package com.example.cristiana.weatherapp.database;

import android.provider.BaseColumns;

/**
 * Created by Cristiana on 5/6/2017.
 */

public interface DbContract {
    interface Day extends BaseColumns {
        /* The SQLite table */
        String TABLE = "day";

        /* The SQLite columns */
        String ID = "id";
        String LOCATION = "location";
        String DATE = "date";
        String TEMPERATURE = "temperature";
        String DESCRIPTIOn = "descrition";
        String PRESSURE = "pressure";
        String WIND_SPEED = "wind_speed";
    }
}
