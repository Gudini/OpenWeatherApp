package by.syakimchik.openweatherapp.loaders.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.syakimchik.openweatherapp.R;
import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.database.tables.WeatherTable;
import by.syakimchik.openweatherapp.ui.activity.MainActivity;
import by.syakimchik.openweatherapp.utils.DateConverter;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class CityAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public CityAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root = mInflater.inflate(R.layout.city_list_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.cityTxt = (TextView)root.findViewById(R.id.cityNameTxt);
        holder.tempTxt = (TextView)root.findViewById(R.id.tempTxt);
        holder.cloudsTxt = (TextView)root.findViewById(R.id.cloudsTxt);
        holder.windSpeedTxt = (TextView)root.findViewById(R.id.windSpeedTxt);
        holder.lastUpdateTxt = (TextView)root.findViewById(R.id.lastUpdateTxt);
        holder.tempVarTxt = (TextView) root.findViewById(R.id.tempVarTxt);
        root.setTag(holder);
        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(CityTable.Columns.ID));
        long code = cursor.getLong(cursor.getColumnIndex(CityTable.Columns.CITY_CODE));
        String cityName = cursor.getString(cursor.getColumnIndex(CityTable.Columns.NAME));
        String countryName = cursor.getString(cursor.getColumnIndex(CityTable.Columns.COUNTRY));
        float temp = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.TEMP));
        float clouds = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.CLOUDS));
        float windSpeed = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.WIND_SPEED));
        String lastUpdateDate = DateConverter.convertToLocalTime(cursor.getString(cursor.getColumnIndex(WeatherTable.Columns.LAST_UPDATE_DATE)));
        ViewHolder holder = (ViewHolder) view.getTag();
        if(holder != null) {
            holder.cityTxt.setText(cityName+", "+countryName);
            holder.cityName = cityName;
            holder.cityId = id;
            holder.cityCode = code;
            String tempVar = ((MainActivity)context).getTempVar();
            if(tempVar.equals("K"))
                temp+=273.15;
            else if(tempVar.equals("F"))
                temp+=32;
            holder.tempTxt.setText(String.format("%.2f",temp));
            holder.cloudsTxt.setText(String.valueOf(clouds));
            holder.windSpeedTxt.setText(String.valueOf(windSpeed));
            holder.lastUpdateTxt.setText(lastUpdateDate);
            holder.tempVarTxt.setText(" "+context.getString(R.string.temp_var)+tempVar);
        }
    }

    public static class ViewHolder {
        public TextView cityTxt, tempTxt, cloudsTxt, windSpeedTxt, lastUpdateTxt, tempVarTxt;
        public long cityId;
        public long cityCode;
        public String cityName;
    }
}
