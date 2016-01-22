package by.syakimchik.openweatherapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.database.tables.WeatherTable;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public static final String AUTHORITY = "by.syakimchik.openweatherapp.database.WeatherContentProvider";

    private static final String DATABASE_NAME = "by.syakimchik.database.db";

    public static final String SCHEME = "content://";

    private static final int DATABASE_VERSION = 1;

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CityTable.Requests.CREATION_REQUEST);
        db.execSQL(WeatherTable.Requests.CREATION_REQUEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CityTable.Requests.DROP_REQUEST);
        db.execSQL(WeatherTable.Requests.DROP_REQUEST);
        onCreate(db);
    }
}
