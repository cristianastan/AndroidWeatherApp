package com.example.cristiana.weatherapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cristiana on 5/6/2017.
 */

public class MySqlHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "weather.db";
    private static final int DB_VERSION = 1;

    public MySqlHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* Create the Day(Details) table */
        db.execSQL("CREATE TABLE " + DbContract.Day.TABLE + "("
            + DbContract.Day._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DbContract.Day.ID + " INTEGER, "
            + DbContract.Day.LOCATION + " TEXT, "
            + DbContract.Day.DATE + " TEXT, "
            + DbContract.Day.TEMPERATURE + " REAL, "
            + DbContract.Day.DESCRIPTIOn + " TEXT, "
            + DbContract.Day.PRESSURE + " REAL, "
            + DbContract.Day.WIND_SPEED + " REAL"
            + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* Not supported */
        db.execSQL("DROP TABLE " + DbContract.Day.TABLE);
        onCreate(db);
    }
}
