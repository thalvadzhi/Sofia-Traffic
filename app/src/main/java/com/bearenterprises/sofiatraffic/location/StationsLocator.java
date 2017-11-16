package com.bearenterprises.sofiatraffic.location;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;

import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.db.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.db.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Used to locate stops in {@code maxDistance} radius of {@code location}
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

    public Comparator<Stop> comparator = new Comparator<Stop>(){

        @Override
        public int compare(Stop c1, Stop c2) {

            Location l1 = new Location("");
            l1.setLatitude(Float.parseFloat(c1.getLatitude()));
            l1.setLongitude(Float.parseFloat(c1.getLongitude()));
            Location l2 = new Location("");
            l2.setLatitude(Float.parseFloat(c2.getLatitude()));
            l2.setLongitude(Float.parseFloat(c2.getLongitude()));

            float distance1 = location.distanceTo(l1);
            float distance2 = location.distanceTo(l2);
            return (int)(distance1 - distance2);
        }
    };

    private boolean determineViabilityOfStation(Stop station){

        if(station.getLatitude().equals("") || station.getLongitude().equals("")){
            return false;
        }
        Location stationLocation = new Location("");

        stationLocation.setLatitude(Float.parseFloat(station.getLatitude()));
        stationLocation.setLongitude(Float.parseFloat(station.getLongitude()));

        float distance = this.location.distanceTo(stationLocation);

        return distance <= this.maxDistance;
    }

    private ArrayList<Stop> getAllStations(){
        DbManipulator dbManipulator = new DbManipulator(this.context);
        ArrayList<Stop> stations = null;
        try(Cursor cursor = dbManipulator.read()) {
            stations = new ArrayList<>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String stationName = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
                String stationCode = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
                String latitude = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
                String longtitude = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
                String description = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_DESCRIPTION));
                String lineTypes = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LINE_TYPES));
                Stop s = new Stop(Integer.parseInt(stationCode), stationName, latitude, longtitude, description);
                DbUtility.addLineTypes(s, lineTypes);
                stations.add(s);
                cursor.moveToNext();
            }
        }finally {
            dbManipulator.closeDb();
        }
        return stations;
    }

    public ArrayList<Stop> getClosestStationsFast(){
        DbManipulator dbManipulator = new DbManipulator(this.context);
        ArrayList<Stop> stations = null;
        String query = "SELECT * FROM "+DbHelper.FeedEntry.TABLE_NAME_STATIONS+ " ORDER BY (ABS("+DbHelper.FeedEntry.COLUMN_NAME_LAT +"+0.0 - ?)+ABS("+DbHelper.FeedEntry.COLUMN_NAME_LON+"+0.0 - ?)) ASC LIMIT " + numberOfStations;
        try(Cursor cursor = dbManipulator.readRawQuery(query, new String[]{Double.toString(location.getLatitude()), Double.toString(location.getLongitude())})) {
            stations = new ArrayList<>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String stationName = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
                String stationCode = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
                String latitude = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
                String longtitude = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
                String description = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_DESCRIPTION));
                String lineTypes = cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LINE_TYPES));
                Stop s = new Stop(Integer.parseInt(stationCode), stationName, latitude, longtitude, description);
                DbUtility.addLineTypes(s, lineTypes);
                stations.add(s);
                cursor.moveToNext();
            }
        }finally {
            dbManipulator.closeDb();
        }
        return stations;
    }

    public ArrayList<Stop> getClosestStations(){
        ArrayList<Stop> closestStations = new ArrayList<>();
        ArrayList<Stop> allStations = getAllStations();
        if (allStations == null || allStations.size() == 0){
            return null;
        }
        PriorityQueue<Stop> priorityQueue = new PriorityQueue<>(allStations.size(), comparator);
        if(allStations == null){
            return null;
        }
        for(Stop st : allStations){
            if(st.getLatitude().equals("") || st.getLongitude().equals("")){
                continue;
            }
            priorityQueue.add(st);
        }
        int count = 0;
        for(int k = 0; k < priorityQueue.size(); k ++){
            Stop st = priorityQueue.poll();
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
