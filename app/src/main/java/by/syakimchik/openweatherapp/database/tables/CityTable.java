package by.syakimchik.openweatherapp.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import by.syakimchik.openweatherapp.database.SqliteHelper;
import by.syakimchik.openweatherapp.models.CityWeather;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class CityTable {

    public static final String PATH_CITY_ID = "/city/";
    public static final String PATH_CITY_WEATHER = "/city_weather/";
    public static final String PATH_CITY_HISTORY_WEATHER = "/city_history/";
    public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SqliteHelper.SCHEME + SqliteHelper.AUTHORITY+PATH_CITY_ID);
    public static final Uri CONTENT_WEATHER_URI_BASE = Uri.parse(SqliteHelper.SCHEME + SqliteHelper.AUTHORITY+PATH_CITY_WEATHER);
    public static final Uri CONTENT_HISTORY_WEATHER_URI_BASE = Uri.parse(SqliteHelper.SCHEME + SqliteHelper.AUTHORITY+PATH_CITY_HISTORY_WEATHER);

    public static String save(Context context, @NonNull CityWeather city) {
        return context.getContentResolver().insert(CONTENT_ID_URI_BASE, toContentValues(city)).getLastPathSegment();
    }

    @NonNull
    public static ContentValues toContentValues(@NonNull CityWeather city) {
        ContentValues values = new ContentValues();
        values.put(Columns.NAME, city.getCityName());
        values.put(Columns.COUNTRY, city.getCountryName());
        values.put(Columns.CITY_CODE, city.getCityCode());
        return values;
    }

    public interface Columns {
        String ID = "_id";
        String NAME = "name";
        String COUNTRY = "country";
        String DEFAULT = "default_city";
        String CITY_CODE = "city_code";
    }

    public interface Requests {

        String TABLE_NAME = CityTable.class.getSimpleName();

        String CREATION_REQUEST = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Columns.NAME + " VARCHAR(200), " +
                Columns.COUNTRY +  " VARCHAR(200), " +
                Columns.DEFAULT + " INTEGER, "+
                Columns.CITY_CODE+" INTEGER);";

        String DROP_REQUEST = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final String[] DEFAULT_PROJECTION = new String[] {
            Columns.ID,
            Columns.NAME,
            Columns.COUNTRY,
            Columns.DEFAULT,
            Columns.CITY_CODE
    };
}
