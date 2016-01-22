package by.syakimchik.openweatherapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.database.tables.WeatherTable;

/**
 * Created by Sergey on 10/31/2015.
 */
public class WeatherContentProvider extends ContentProvider {

    static final int URI_CITY_WEATHER = 1;
    static final int URI_CITY_HISTORY_WEATHER = 2;
    static final int URI_CITY = 3;
    static final int URI_WEATHER = 4;

    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(SqliteHelper.AUTHORITY, "city", URI_CITY);
        URI_MATCHER.addURI(SqliteHelper.AUTHORITY, "city_weather", URI_CITY_WEATHER);
        URI_MATCHER.addURI(SqliteHelper.AUTHORITY, "city_history", URI_CITY_HISTORY_WEATHER);
        URI_MATCHER.addURI(SqliteHelper.AUTHORITY, "weather", URI_WEATHER);
    }

    private SqliteHelper mSqliteHelper;

    private static final HashMap<String, String> mColumnMap = buildColumnMap();

    @Override
    public boolean onCreate() {
        mSqliteHelper = new SqliteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else{
            String _seletion = selection;
            queryBuilder.setTables(table);
            queryBuilder.setProjectionMap(mColumnMap);
            switch (URI_MATCHER.match(uri)){
                case URI_CITY_WEATHER:
                    sortOrder = CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.DEFAULT+" DESC";
                    _seletion = null;
                    break;
            }
            Cursor cursor = queryBuilder.query(mSqliteHelper.getWritableDatabase(), projection, _seletion, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case URI_CITY:
                return CityTable.Requests.TABLE_NAME;
            case URI_WEATHER:
                return WeatherTable.Requests.TABLE_NAME;
            case URI_CITY_WEATHER:
                return CityTable.Requests.TABLE_NAME+" LEFT JOIN "+"(SELECT * FROM "+WeatherTable.Requests.TABLE_NAME+" group by "+WeatherTable.Columns.CITY_ID+" order by "+WeatherTable.Columns.LAST_UPDATE_DATE+" DESC) "+WeatherTable.Requests.TABLE_NAME+" ON ("+CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.ID+" = "+WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.CITY_ID+")";
            case URI_CITY_HISTORY_WEATHER:
                return CityTable.Requests.TABLE_NAME+" LEFT JOIN "+"(SELECT * FROM "+WeatherTable.Requests.TABLE_NAME+" order by "+WeatherTable.Columns.LAST_UPDATE_DATE+" DESC) "+WeatherTable.Requests.TABLE_NAME+" ON ("+CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.ID+" = "+WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.CITY_ID+")";
            default:
                return "";
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @NonNull ContentValues values) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            long id = database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            getContext().getContentResolver().notifyChange(CityTable.CONTENT_WEATHER_URI_BASE, null);
            return ContentUris.withAppendedId(uri, id);
        }
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            int numInserted = 0;
            database.beginTransaction();
            try {
                for (ContentValues contentValues : values) {
                    long id = database.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if (id > 0) {
                        numInserted++;
                    }
                }
                database.setTransactionSuccessful();
            }
            finally {
                database.endTransaction();
            }
            getContext().getContentResolver().notifyChange(CityTable.CONTENT_WEATHER_URI_BASE, null);
            return numInserted;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            int cnt =  database.delete(table, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(CityTable.CONTENT_WEATHER_URI_BASE, null);
            return cnt;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            int cnt = database.update(table, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(CityTable.CONTENT_WEATHER_URI_BASE, null);
            return cnt;
        }
    }

    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(CityTable.Columns.ID, CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.ID);
        map.put(CityTable.Columns.NAME, CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.NAME);
        map.put(CityTable.Columns.COUNTRY, CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.COUNTRY);
        map.put(CityTable.Columns.DEFAULT, CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.DEFAULT);
        map.put(CityTable.Columns.CITY_CODE, CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.CITY_CODE);
        map.put(WeatherTable.Columns.TEMP, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.TEMP);
        map.put(WeatherTable.Columns.MIN_TEMP, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.MIN_TEMP);
        map.put(WeatherTable.Columns.MAX_TEMP, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.MAX_TEMP);
        map.put(WeatherTable.Columns.CLOUDS, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.CLOUDS);
        map.put(WeatherTable.Columns.WEATHER_MAIN, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.WEATHER_MAIN);
        map.put(WeatherTable.Columns.WEATHER_DESCRIPTION, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.WEATHER_DESCRIPTION);
        map.put(WeatherTable.Columns.WIND_SPEED, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.WIND_SPEED);
        map.put(WeatherTable.Columns.LAST_UPDATE_DATE, WeatherTable.Requests.TABLE_NAME+"."+WeatherTable.Columns.LAST_UPDATE_DATE);

        return map;
    }

}
