package com.bearenterprises.sofiatraffic.utilities.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.Utility;

import java.util.ArrayList;

/**
 * Utility methods for manipulating DB
 */

public class DbUtility {

    public static ArrayList<Stop> getStationByCode(String code, MainActivity mainActivity) throws SQLiteDatabaseLockedException {
        //move to Utility
        String codeNoZeroes = code.replaceFirst("^0+(?!$)", "");
        String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_CODE + " =?";
        String[] args = new String[]{codeNoZeroes};
        return DbUtility.getStationsFromDatabase(query, args, mainActivity);

    }

    public static ArrayList<Stop> getStationsFromDatabase(String query, String[] codes, MainActivity mainActivity) throws SQLiteDatabaseLockedException{
        // move to Utility
        DbManipulator manipulator=null;
        try {
            manipulator = new DbManipulator(mainActivity);
        }catch (SQLiteDatabaseLockedException e){
            if(manipulator != null){
                manipulator.closeDb();
            }
            throw e;

        }

        ArrayList<Stop> stations = new ArrayList<>();
        try(Cursor c = manipulator.readRawQuery(query, codes)){
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
            } else {
                Utility.makeSnackbar("Няма такава спирка", mainActivity);
                return null;
            }
            String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
            String stationCode = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
            String lat = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
            String lon = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
            String description = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_DESCRIPTION));

            stations.add(new Stop(Integer.parseInt(stationCode), stationName, lat, lon, description));
        }finally {
            if(manipulator != null){
                manipulator.closeDb();

            }
        }
        return stations;
    }
}
