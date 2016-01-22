package by.syakimchik.openweatherapp.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import by.syakimchik.openweatherapp.database.SqliteHelper;
import by.syakimchik.openweatherapp.models.CityWeather;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class WeatherTable {

    public static final String PATH_WEATHER = "/weather/";
    public static final Uri CONTENT_URI_BASE = Uri.parse(SqliteHelper.SCHEME + SqliteHelper.AUTHORITY+PATH_WEATHER);

    public static void save(Context context, @NonNull CityWeather city, String cityId) {
        context.getContentResolver().insert(CONTENT_URI_BASE, toContentValues(city, cityId));
    }

    public static void save(Context context, @NonNull List<CityWeather> citiesWeather, List<String> cityId){
        List<ContentValues> valueList = new ArrayList<>();
        if(citiesWeather!=null) {
            for (int i = 0; i < citiesWeather.size(); i++) {
                valueList.add(toContentValues(citiesWeather.get(i), cityId.get(i)));
            }
            ContentValues[] bulkToInsert = valueList.toArray(new ContentValues[valueList.size()]);
            context.getContentResolver().bulkInsert(CONTENT_URI_BASE, bulkToInsert);
        }

    }

    @NonNull
    public static ContentValues toContentValues(@NonNull CityWeather city, String cityId) {
        ContentValues values = new ContentValues();
        values.put(Columns.TEMP, city.getTemperature()!=null?city.getTemperature().getTemp():null);
        values.put(Columns.MIN_TEMP, city.getTemperature()!=null?city.getTemperature().getMinTemp():null);
        values.put(Columns.MAX_TEMP, city.getTemperature()!=null?city.getTemperature().getMaxTemp():null);
        values.put(Columns.CLOUDS, city.getClouds()!=null?city.getClouds().getClouds():null);
        values.put(Columns.WEATHER_MAIN, city.getWeatherMain());
        values.put(Columns.WEATHER_DESCRIPTION, city.getWeatherDescription());
        values.put(Columns.WIND_SPEED, city.getWind()!=null?city.getWind().getSpeed():null);
        values.put(Columns.CITY_ID, cityId);

        return values;
    }

    public interface Columns {
        String ID = "_id";
        String LAST_UPDATE_DATE = "last_update_date";
        String TEMP = "_temp";
        String MIN_TEMP = "min_temp";
        String MAX_TEMP = "max_temp";
        String CLOUDS = "clouds";
        String WEATHER_MAIN = "weather_main";
        String WEATHER_DESCRIPTION = "weather_description";
        String WIND_SPEED = "wind_speed";
        String CITY_ID = "city_id";
    }

    public interface Requests {

        String TABLE_NAME = WeatherTable.class.getSimpleName();

        String CREATION_REQUEST = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Columns.LAST_UPDATE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                Columns.TEMP + " REAL," +
                Columns.MIN_TEMP + " REAL," +
                Columns.MAX_TEMP + " REAL," +
                Columns.CLOUDS + " REAL," +
                Columns.WEATHER_MAIN + " CHAR(50)," +
                Columns.WEATHER_DESCRIPTION + " CHAR(200)," +
                Columns.WIND_SPEED +" REAL,"+
                Columns.CITY_ID + " INTEGER," +
                " FOREIGN KEY ("+Columns.CITY_ID+") REFERENCES "+CityTable.Requests.TABLE_NAME+"("+CityTable.Columns.ID+"));";

        String DROP_REQUEST = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
