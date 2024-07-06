package com.bearenterprises.sofiatraffic.utilities.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.parsing.Description;
import com.bearenterprises.sofiatraffic.utilities.parsing.JSONParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Utility methods for manipulating DB
 */

public class DbUtility {

    public static ArrayList<Stop> getStationsByCode(String code, MainActivity mainActivity) throws SQLiteDatabaseLockedException {
        String codeNoZeroes = code.replaceFirst("^0+(?!$)", "");
        String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_STATIONS + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_CODE + " =?";
        String[] args = new String[]{codeNoZeroes};
        return DbUtility.getStationsFromDatabase(query, args, mainActivity);
    }

    public static Stop getStationByCode(String code, MainActivity mainActivity) throws SQLiteDatabaseLockedException{
        ArrayList<Stop> stationsByCode = getStationsByCode(code, mainActivity);
        if(stationsByCode != null && stationsByCode.size() > 0){
            return stationsByCode.get(0);
        }else{
            return new Stop();
        }
    }

    public static <T extends Stop> void addLineTypes(T s, String lineTypes){
        String[] split = lineTypes.split(",");
        for(int i = 0; i < split.length; i++){
            s.addLineType(Integer.parseInt(split[i]));
        }
    }

    public static <T extends Stop> void addLineNames(T s, String lineNames) throws JSONException {
        JSONArray lineNamesAndType = new JSONArray(lineNames);
        ArrayList<Line> lines = new ArrayList<>();
        for (int k = 0; k < lineNamesAndType.length(); k++){
            String nameAndType = lineNamesAndType.getString(k);

            String[] nameAndTypeParsed = nameAndType.replace("[", "").replace("]", "").split(",");
            int type = Integer.parseInt(nameAndTypeParsed[1]);
            String name = nameAndTypeParsed[0].replace("\"", "").replace("\\", "");
            int lineId = Integer.parseInt(nameAndTypeParsed[2]);
            Line line = new Line(type, lineId, name);
            lines.add(line);
        }
        s.setLines(lines);
    }

    public static<T extends Stop> void addLineTypes(T s, MainActivity mainActivity){
        ArrayList<Stop> stationByCode = getStationsByCode(Integer.toString(s.getCode()), mainActivity);
        if(stationByCode != null && stationByCode.size() > 0){
            s.setLineTypes(stationByCode.get(0).getLineTypes());
        }
    }

    public static <T extends Stop>void addLineTypes(ArrayList<T> stops, MainActivity activity){
        for(Stop s : stops){
            addLineTypes(s, activity);
        }
    }
    public static Description getDescription(String trType, String lineName, String stopCode, Context context){
        String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_DESCRIPTIONS + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_TR_TYPE + "=? AND " + DbHelper.FeedEntry.COLUMN_NAME_LINE_NAME + "=? AND " + DbHelper.FeedEntry.COLUMN_NAME_STOP_CODE + "=?";
        String[] args = new String[]{trType, lineName, stopCode};
        return DbUtility.getDescriptionFromDatabase(query, args, context);

    }

    public static Description getDescriptionFromDatabase(String query, String[] codes, Context context) throws SQLiteDatabaseLockedException{
        DbManipulator manipulator = null;
        try{
            manipulator = new DbManipulator(context);
        }catch (SQLiteDatabaseLockedException e){
            if(manipulator != null){
                manipulator.closeDb();
            }
            throw e;
        }
        Description desc = null;
        try(Cursor c = manipulator.readRawQuery(query, codes)){
            if (c != null && c.getCount() > 0){
                c.moveToFirst();
            }else{
                return null;
            }
            String trType = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_TR_TYPE));
            String lineName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LINE_NAME));
            String stopCode = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STOP_CODE));
            String direction = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_DIRECTION));
            desc = new Description(trType, lineName, stopCode, direction);
        }finally {
            if(manipulator != null){
                manipulator.closeDb();
            }
        }
        return desc;
    }


    public static ArrayList<Stop> getStationsFromDatabase(String query, String[] codes, MainActivity mainActivity) throws SQLiteDatabaseLockedException{
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
                if(c.moveToFirst()){
                    do {
                        String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
                        String stationCode = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
                        String lat = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
                        String lon = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
                        String description = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_DESCRIPTION));
                        String lineTypes = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LINE_TYPES));
                        String lineNames = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LINE_NAMES));
                        Stop s = new Stop(Integer.parseInt(stationCode), stationName, lat, lon, description);
                        addLineTypes(s, lineTypes);
                        addLineNames(s, lineNames);
                        stations.add(s);
                    }while(c.moveToNext());
                }

            } else {
                return stations;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            if(manipulator != null){
                manipulator.closeDb();

            }
        }
        return stations;
    }
}
