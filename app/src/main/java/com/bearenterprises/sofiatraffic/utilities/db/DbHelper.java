package com.bearenterprises.sofiatraffic.utilities.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by thalv on 01-Jul-16.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "stations";
        public static final String COLUMN_NAME_STATION_NAME = "stationName";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_LAT = "latitude";
        public static final String COLUMN_NAME_LON = "longtitude";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_UNIQUE = " UNIQUE";
        public static final String SQL_DROP_DB =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String SQL_DELETE_ALL = "DELETE FROM " + TABLE_NAME;
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_STATION_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CODE + TEXT_TYPE + FeedEntry.COLUMN_UNIQUE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LAT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LON + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "stationsInfo.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
