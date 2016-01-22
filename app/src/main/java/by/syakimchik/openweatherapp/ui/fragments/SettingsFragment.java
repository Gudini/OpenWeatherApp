package by.syakimchik.openweatherapp.ui.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import by.syakimchik.openweatherapp.Constants;
import by.syakimchik.openweatherapp.R;
import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.ui.activity.MainActivity;

/**
 * Created by Sergey on 11/2/2015.
 * @author Sergey Yakimchik
 */
public class SettingsFragment extends Fragment {

    private Button mChangeCityBtn;
    private Button mChangeTimeBtn;
    private Button mChangeTempBtn;
    private TextView mDefaultCityTxt;
    private TextView mDefaultTimeTxt;
    private TextView mDefaultTempTxt;

    private String mCurrentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        mChangeCityBtn = (Button) root.findViewById(R.id.changeCityBtn);
        mChangeTimeBtn = (Button) root.findViewById(R.id.changeTimeBtn);
        mChangeTempBtn = (Button) root.findViewById(R.id.changeTempBtn);
        mChangeCityBtn.setOnClickListener(new ClickListener());
        mChangeTimeBtn.setOnClickListener(new ClickListener());
        mChangeTempBtn.setOnClickListener(new ClickListener());
        mDefaultCityTxt = (TextView) root.findViewById(R.id.defaultCityTxt);
        mDefaultTimeTxt = (TextView) root.findViewById(R.id.defaultTimeTxt);
        mDefaultTempTxt = (TextView) root.findViewById(R.id.defaultTempTxt);
        populateDefaultData();
        return root;
    }

    private void populateDefaultData(){
        Cursor cursor = getContext().getContentResolver().query(CityTable.CONTENT_ID_URI_BASE, CityTable.DEFAULT_PROJECTION, CityTable.Requests.TABLE_NAME + "." + CityTable.Columns.DEFAULT + "=?", new String[]{"1"}, null);
        if(cursor.moveToFirst()){
            mDefaultCityTxt.setText(cursor.getString(cursor.getColumnIndex(CityTable.Columns.NAME)));
            mCurrentId = String.valueOf(cursor.getString(cursor.getColumnIndex(CityTable.Columns.ID)));
        }
        else
            mCurrentId = "0";
        mDefaultTimeTxt.setText(getResources().getStringArray(R.array.time_update_array)[getSettings(Constants.TIME_POSITION)]);
        mDefaultTempTxt.setText(getResources().getStringArray(R.array.temp_array)[getSettings(Constants.TEMP_POSITION)]);
    }

    private class ClickListener implements  Button.OnClickListener{

        @Override
        public void onClick(View view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            switch (view.getId()){
                case R.id.changeCityBtn:
                    builder.setSingleChoiceItems(getContext().getContentResolver().query(CityTable.CONTENT_ID_URI_BASE, CityTable.DEFAULT_PROJECTION, null, null, null), getSettings(Constants.CITY_POSITION), CityTable.Columns.NAME, null);
                    builder.setPositiveButton(getString(R.string.ok),  new CityChangeDialog());
                    break;
                case R.id.changeTimeBtn:
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.time_update_array), getSettings(Constants.TIME_POSITION), null);
                    builder.setPositiveButton(getString(R.string.ok), new TimeChangeDialog());
                    break;
                case R.id.changeTempBtn:
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.temp_array), getSettings(Constants.TEMP_POSITION), null);
                    builder.setPositiveButton(getString(R.string.ok), new TempeChangeDialog());
                    break;
            }
            builder.setTitle(((Button) view).getText());
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        private class CityChangeDialog implements DialogInterface.OnClickListener{

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListView lv = ((AlertDialog) dialogInterface).getListView();
                try {
                    Cursor cursor = (Cursor) lv.getAdapter().getItem(lv.getCheckedItemPosition());
                    mDefaultCityTxt.setText(cursor.getString(cursor.getColumnIndex(CityTable.Columns.NAME)));
                    String newId = cursor.getString(cursor.getColumnIndex(CityTable.Columns.ID));
                    if (!newId.equals(mCurrentId)) {
                        saveSettings(Constants.CITY_POSITION, lv.getCheckedItemPosition());
                        ContentValues values = new ContentValues();
                        values.put(CityTable.Columns.DEFAULT, "0");
                        getContext().getContentResolver().update(CityTable.CONTENT_ID_URI_BASE, values, CityTable.Requests.TABLE_NAME + "." + CityTable.Columns.ID + "=?", new String[]{mCurrentId});
                        values = new ContentValues();
                        values.put(CityTable.Columns.DEFAULT, "1");
                        getContext().getContentResolver().update(CityTable.CONTENT_ID_URI_BASE, values, CityTable.Requests.TABLE_NAME + "." + CityTable.Columns.ID + "=?", new String[]{newId});
                        mCurrentId = newId;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        private class TimeChangeDialog implements DialogInterface.OnClickListener{

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListView lv = ((AlertDialog) dialogInterface).getListView();
                int position = lv.getCheckedItemPosition();
                saveSettings(Constants.TIME_POSITION, position);
                mDefaultTimeTxt.setText(getResources().getStringArray(R.array.time_update_array)[position]);
                ((MainActivity)getActivity()).updateServiceInterval();
            }
        }

        private class TempeChangeDialog implements DialogInterface.OnClickListener{

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListView lv = ((AlertDialog) dialogInterface).getListView();
                int position = lv.getCheckedItemPosition();
                saveSettings(Constants.TEMP_POSITION, position);
                mDefaultTempTxt.setText(getResources().getStringArray(R.array.temp_array)[position]);
            }
        }
    }

    private void saveSettings(String key, int value){
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.USER_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private int getSettings(String key){
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.USER_SETTINGS, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);
    }
}
