package by.syakimchik.openweatherapp.loaders;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import by.syakimchik.openweatherapp.Constants;
import by.syakimchik.openweatherapp.database.tables.CityTable;

/**
 * Created by Sergey on 11/2/2015.
 * @author Sergey Yakimchik
 */
public class AutoUpdateService extends Service {

    long mInterval = 3600000;

    private Timer mTimer;
    TimerTask mTask;

    UpdateBinder mBinder = new UpdateBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        updateInterval();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void schedule(){
        if(mTask!=null) {
            mTask.cancel();
        }
        if(mInterval>0){
            mTask = new TimerTask() {

                @Override
                public void run() {
                    executeUpdate();
                }
            };
            mTimer.schedule(mTask, 1000, mInterval);
        }
    }

    private void executeUpdate(){
        Cursor cursor = getApplicationContext().getContentResolver().query(CityTable.CONTENT_ID_URI_BASE, CityTable.DEFAULT_PROJECTION, null, null, null);
        StringBuilder citiesCode = new StringBuilder();
        List<String> citiesId = new ArrayList<>();
        while (cursor.moveToNext()){
            citiesId.add(String.valueOf(cursor.getLong(cursor.getColumnIndex(CityTable.Columns.ID))));
            citiesCode.append(cursor.getLong(cursor.getColumnIndex(CityTable.Columns.CITY_CODE)) + ",");
        }
        WeatherInfoLoader.updateCitiesData(getApplicationContext(), citiesCode.toString(), citiesId, null);
    }

    public void updateInterval(){
        Long settingsInterval = Long.valueOf(getApplicationContext().getSharedPreferences(Constants.USER_SETTINGS, MODE_PRIVATE).getInt(Constants.TIME_POSITION, 0));
        if(settingsInterval==0)
            mInterval = 0;
        else if(settingsInterval==1)
            mInterval = 3600000;
        else if(settingsInterval==2){
            mInterval = 86400000;
        }
        else if(settingsInterval==3){
            mInterval = 900000;
        }
        schedule();
    }

    public class UpdateBinder extends Binder{
        public AutoUpdateService getService(){
            return AutoUpdateService.this;
        }
    }

}
