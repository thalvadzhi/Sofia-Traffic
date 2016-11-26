package com.bearenterprises.sofiatraffic.location;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;

/**
 * Created by thalv on 08-Jul-16.
 */
public class StationsLocator {
    private Location location;
    private int numberOfStations;
    private float maxDistance;
    private Context context;

    public StationsLocator(Location location, int numberOfStations, float radius, Context context) {
        this.location = location;
        this.numberOfStations = numberOfStations;
        this.maxDistance = radius;
        this.context = context;
    }

    private boolean determineViabilityOfStation(Station station){

        if(station.getLatitude().equals("") || station.getLongtitute().equals("")){
            return false;
        }
        Location stationLocation = new Location("");

        stationLocation.setLatitude(Float.parseFloat(station.getLatitude()));
        stationLocation.setLongitude(Float.parseFloat(station.getLongtitute()));

        float distance = this.location.distanceTo(stationLocation);

        return distance <= this.maxDistance;
    }

    private ArrayList<Station> getAllStations(){
        DbManipulator dbManipulator = new DbManipulator(this.context);
        ArrayList<Station> stations = new ArrayList<>();
        try(Cursor cursor = dbManipulator.read()) {

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String stationName = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
                String stationCode = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
                String latitude = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
                String longtitude = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
                stations.add(new Station(stationName, stationCode, latitude, longtitude));
                cursor.moveToNext();
            }
        }finally {
            dbManipulator.closeDb();
        }

        return stations;
    }

    public ArrayList<Station> getClosestStations(){
        ArrayList<Station> closestStations = new ArrayList<>();
        ArrayList<Station> allStations = getAllStations();
        int i = 1;
        for(Station station : allStations){
            if(determineViabilityOfStation(station)){
                closestStations.add(station);
            }
            if (i == this.numberOfStations){
                break;
            }
            i++;

        }

        return closestStations;
    }
}
