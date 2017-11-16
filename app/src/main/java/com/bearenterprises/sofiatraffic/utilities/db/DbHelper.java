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
        public static final String TABLE_NAME_STATIONS = "stations";
        public static final String TABLE_NAME_DESCRIPTIONS = "descriptions";
        public static final String TABLE_NAME_SUBWAY = "subway";

        //column names for STATIONS db
        public static final String COLUMN_NAME_STATION_NAME = "stationName";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_LAT = "latitude";
        public static final String COLUMN_NAME_LON = "longtitude";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINE_TYPES = "line_types";

        //column names for SUBWAY db
        public static final String COLUMN_NAME_STOP_NAME_SUB = "stopName";
        public static final String COLUMN_NAME_CODE1 = "code1";
        public static final String COLUMN_NAME_CODE2 = "code2";
        public static final String COLUMN_NAME_LAT_SUB = "latitude";
        public static final String COLUMN_NAME_LON_SUB = "longitude";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_LINE = "line";


        //column names for DESCRIPTIONS db
        public static final String COLUMN_NAME_TR_TYPE = "transportationType";
        public static final String COLUMN_NAME_LINE_NAME = "lineName";
        public static final String COLUMN_NAME_STOP_CODE = "stopCode";
        public static final String COLUMN_NAME_DIRECTION = "direction";

        public static final String COLUMN_UNIQUE = " UNIQUE";
        public static final String SQL_DROP_STATIONS =
                "DROP TABLE IF EXISTS " + TABLE_NAME_STATIONS;
        public static final String SQL_DROP_DESCRIPTIONS =
                "DROP TABLE IF EXISTS " + TABLE_NAME_DESCRIPTIONS;
        public static final String SQL_DELETE_ALL_STATIONS = "DELETE FROM " + TABLE_NAME_STATIONS;
        public static final String SQL_DELETE_ALL_DESCRIPTIONS = "DELETE FROM " + TABLE_NAME_DESCRIPTIONS;
        public static final String SQL_DELETE_ALL_SUBWAY = "DELETE FROM " + TABLE_NAME_SUBWAY;

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String CREATE_TABLE_STATIONS =
            "CREATE TABLE " + FeedEntry.TABLE_NAME_STATIONS + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_STATION_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CODE + TEXT_TYPE + FeedEntry.COLUMN_UNIQUE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LAT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LON + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE +  COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LINE_TYPES + TEXT_TYPE +
                    " )";

    private static final String CREATE_TABLE_DESCRIPTIONS =
            "CREATE TABLE " + FeedEntry.TABLE_NAME_DESCRIPTIONS + " (" +
                    FeedEntry.COLUMN_NAME_TR_TYPE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LINE_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_STOP_CODE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DIRECTION + TEXT_TYPE +
                    " )";

    private static final String CREATE_TABLE_SUBWAY =
            "CREATE TABLE " + FeedEntry.TABLE_NAME_SUBWAY + " ("+
                    FeedEntry.COLUMN_NAME_STOP_NAME_SUB + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CODE1 + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CODE2 + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LAT_SUB + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LON_SUB + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LINE + TEXT_TYPE +
                    " )";

    private static final String DROP_TABLE_STATIONS =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_STATIONS;

    private static final String DROP_TABLE_DESCRIPTIONS =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_DESCRIPTIONS;

    private  static final String DROP_TABLE_SUBWAY =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_SUBWAY;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "stationsInfo.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STATIONS);
        db.execSQL(CREATE_TABLE_DESCRIPTIONS);
        db.execSQL(CREATE_TABLE_SUBWAY);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DROP_TABLE_STATIONS);
        db.execSQL(DROP_TABLE_DESCRIPTIONS);
        db.execSQL(DROP_TABLE_SUBWAY);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
