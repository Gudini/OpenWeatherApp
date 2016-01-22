package by.syakimchik.openweatherapp.loaders.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.syakimchik.openweatherapp.R;
import by.syakimchik.openweatherapp.database.tables.WeatherTable;
import by.syakimchik.openweatherapp.ui.activity.MainActivity;
import by.syakimchik.openweatherapp.utils.DateConverter;

/**
 * Created by Sergey on 11/2/2015.
 * @author Sergey Yakimchik
 */
public class HistoryAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public HistoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root = mInflater.inflate(R.layout.history_list_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.tempTxt = (TextView)root.findViewById(R.id.tempTxt);
        holder.minTempTxt = (TextView) root.findViewById(R.id.minTempTxt);
        holder.maxTempTxt = (TextView) root.findViewById(R.id.maxTempTxt);
        holder.weatherTxt = (TextView) root.findViewById(R.id.weatherTxt);
        holder.descTxt = (TextView) root.findViewById(R.id.descriptionTxt);
        holder.cloudsTxt = (TextView)root.findViewById(R.id.cloudsTxt);
        holder.windSpeedTxt = (TextView)root.findViewById(R.id.windSpeedTxt);
        holder.lastUpdateTxt = (TextView)root.findViewById(R.id.lastUpdateTxt);
        holder.tempVarTxt = (TextView) root.findViewById(R.id.tempVarTxt);
        root.setTag(holder);
        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        float temp = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.TEMP));
        float clouds = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.CLOUDS));
        float windSpeed = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.WIND_SPEED));
        String lastUpdateDate = DateConverter.convertToLocalTime(cursor.getString(cursor.getColumnIndex(WeatherTable.Columns.LAST_UPDATE_DATE)));
        float minTemp = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.MIN_TEMP));
        float maxTemp = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.MAX_TEMP));
        String weather = cursor.getString(cursor.getColumnIndex(WeatherTable.Columns.WEATHER_MAIN));
        String desc = cursor.getString(cursor.getColumnIndex(WeatherTable.Columns.WEATHER_DESCRIPTION));
        ViewHolder holder = (ViewHolder) view.getTag();
        StringBuilder sb = new StringBuilder();
        if(cursor.moveToNext()){
            sb.append("(");
            float next_temp = cursor.getFloat(cursor.getColumnIndex(WeatherTable.Columns.TEMP));
            float diff = temp - next_temp;
            if(diff>0){
                sb.append("+");
            }
            sb.append(String.format("%.2f",diff)+")");
            cursor.moveToPrevious();
        }
        String tempVar = ((MainActivity)context).getTempVar();
        if(tempVar.equals("K")) {
            temp += 273.15;
            minTemp+=273.15;
            maxTemp+=273.15;
        }
        else if(tempVar.equals("F")) {
            temp += 32;
            minTemp+=32;
            maxTemp+=32;
        }
        if(holder != null) {
            holder.tempTxt.setText(String.format("%.2f",temp)+sb.toString());
            holder.cloudsTxt.setText(String.valueOf(clouds));
            holder.windSpeedTxt.setText(String.valueOf(windSpeed));
            holder.lastUpdateTxt.setText(lastUpdateDate);
            holder.minTempTxt.setText(String.format("%.2f", minTemp));
            holder.maxTempTxt.setText(String.format("%.2f",maxTemp));
            holder.weatherTxt.setText(weather);
            holder.descTxt.setText(desc);
            holder.tempVarTxt.setText(" "+context.getString(R.string.temp_var)+tempVar);
        }
    }

    public static class ViewHolder {
        public TextView tempTxt, minTempTxt, maxTempTxt, weatherTxt, descTxt, cloudsTxt, windSpeedTxt, lastUpdateTxt, tempVarTxt;
    }
}
