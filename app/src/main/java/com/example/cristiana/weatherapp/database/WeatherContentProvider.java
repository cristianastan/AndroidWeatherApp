package com.example.cristiana.weatherapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.cristiana.weatherapp.model.Weather;

/**
 * Created by Cristiana on 5/6/2017.
 */

public class WeatherContentProvider extends ContentProvider {
    private MySqlHelper mySqlHelper;
    private static final String AUTHORITY = "com.example.cristiana.weatherapp.provider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Entities {
        int DAYS = 0;
        int DAY = 1;
    }

    /* Build the URI path for the days */
    public static final Uri DAY_URI = CONTENT_URI.buildUpon()
            .appendPath("days")
            .build();

    /* Matcher to bind the URIs to the Entities */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /* Rules for the matching */
    static {
        sUriMatcher.addURI(AUTHORITY, "days", Entities.DAYS);
        sUriMatcher.addURI(AUTHORITY, "day", Entities.DAY);
    }

    /* Required empty constructor */
    public WeatherContentProvider() {

    }

    @Override
    public boolean onCreate() {
        mySqlHelper = new MySqlHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        /* If there is no selection */
        if (TextUtils.isEmpty(selection))
            selection = "1";

        switch (sUriMatcher.match(uri)) {
            case Entities.DAYS:
                /* Requesting all days */
                return mySqlHelper.getReadableDatabase().query(DbContract.Day.TABLE, projection,
                        selection, selectionArgs, null, null, sortOrder);

            case Entities.DAY:
                /* Requesting only the selection correspondent */
                String sqlSelection = " AND " + DbContract.Day.ID + " = " + uri.getLastPathSegment();
                return mySqlHelper.getReadableDatabase().query(DbContract.Day.TABLE, projection,
                        selection + sqlSelection, selectionArgs, null, null, sortOrder);
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case Entities.DAYS:
                return "vnd.android.cursor.dir/vnd.examaple.cristiana.weatgerapp.days";
            case Entities.DAY:
                return "vnd.android.cursor.item/vnd.examaple.cristiana.weatgerapp.day";
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
        long id;
        Uri modifiedUri = WeatherContentProvider.CONTENT_URI;

        switch (sUriMatcher.match(uri)) {
            case Entities.DAYS:
            case Entities.DAY:
                /* Insert the data */
                id = database.insertOrThrow(DbContract.Day.TABLE, null, values);

                /* Build the specified entity */
                modifiedUri = modifiedUri.buildUpon().appendPath(DbContract.Day.TABLE).build();
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        /* Check if the operation was successful */
        if (id >= 0) {
            modifiedUri = ContentUris.withAppendedId(modifiedUri, id);

            /* Notify that something has changed */
            getContext().getContentResolver().notifyChange(modifiedUri, null);
            return modifiedUri;
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
        int rowsAffected;

        if (TextUtils.isEmpty(selection))
            selection = "1";

        switch (sUriMatcher.match(uri)) {
            case Entities.DAY:
                selection += " AND " + DbContract.Day.TABLE + " = " + uri.getLastPathSegment();
            case Entities.DAYS:
                rowsAffected = database.delete(DbContract.Day.TABLE, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        /* Notify the change */
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
        int rowsAffected;

        switch (sUriMatcher.match(uri)) {
            case Entities.DAY:
                selection += " AND " + DbContract.Day.TABLE + " = " + uri.getLastPathSegment();
            case Entities.DAYS:
                rowsAffected = database.update(DbContract.Day.TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        /* Notify the change */
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
}
