package com.bearenterprises.sofiatraffic.location;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

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

    public Comparator<Station> comparator = new Comparator<Station>(){

        @Override
        public int compare(Station c1, Station c2) {
            Location l1 = new Location("");
            l1.setLatitude(Float.parseFloat(c1.getLatitude()));
            l1.setLongitude(Float.parseFloat(c1.getLongtitute()));
            Location l2 = new Location("");
            l2.setLatitude(Float.parseFloat(c2.getLatitude()));
            l2.setLongitude(Float.parseFloat(c2.getLongtitute()));

            float distance1 = location.distanceTo(l1);
            float distance2 = location.distanceTo(l2);
            return (int)(distance1 - distance2);
        }
    };

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
        PriorityQueue<Station> priorityQueue = new PriorityQueue<>(allStations.size(), comparator);

        for(Station st : allStations){
            if(st.getLatitude().equals("") || st.getLongtitute().equals("")){
                continue;
            }
            priorityQueue.add(st);
        }
        int count = 0;
        for(int k = 0; k < priorityQueue.size(); k ++){
            Station st = priorityQueue.poll();
            if(st != null && determineViabilityOfStation(st)){
                closestStations.add(st);
                count++;
            }else if(st == null){
                break;
            }
            if(count == this.numberOfStations){
                break;
            }
        }

        return closestStations;
    }
}
