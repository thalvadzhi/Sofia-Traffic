package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;

import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.stations.Line;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by thalv on 02-Jul-16.
 */
public class JSONParser {

    public static ArrayList<Station> getStationsFromFile(String fileName, Context context){
        String source= null;
        try {
            source = Files.asCharSource(new File(context.getFilesDir(), fileName), Charsets.UTF_8).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getStations(source);
    }

    public static ArrayList<Station> getStations(String json){
        if(json == null){
            return null;
        }
        ArrayList<Station> stations = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length();i++){
                JSONObject stop = jsonArray.getJSONObject(i);
                JSONArray coord = stop.getJSONArray("coordinates");
                stations.add(new Station(stop.getString("stopName"),stop.getString("stopCode"), coord.getString(0), coord.getString(1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stations;
    }

    public static ArrayList<Line> getLines(String json){
        try {
            ArrayList<Line> lineIds = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray lines = jsonObject.getJSONArray(Constants.JSON_LINES);
            for (int i = 0; i < lines.length(); i++){
                JSONObject o = lines.getJSONObject(i);
                lineIds.add(new Line(o.getString(Constants.JSON_TYPE), o.getString(Constants.JSON_NAME), o.getString(Constants.JSON_ID)));
            }
            return lineIds;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStation(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(Constants.JSON_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLineTimes(String json){
        try {
            StringBuilder b = new StringBuilder();
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                b.append(jsonArray.getJSONObject(i).getString(Constants.JSON_TIME) + " ");
            }

            return b.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
