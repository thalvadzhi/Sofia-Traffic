package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;

import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thalv on 02-Jul-16.
 */
public class JSONParser {

    public static ArrayList<Stop> getStationsFromFile(String fileName, Context context){
        String source= null;
        try {
            source = Files.asCharSource(new File(context.getFilesDir(), fileName), Charsets.UTF_8).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getStations(source);
    }

    public static ArrayList<Stop> getStations(String json){
        if(json == null){
            return null;
        }
        ArrayList<Stop> stations = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length();i++){
                JSONObject stop = jsonArray.getJSONObject(i);
                JSONArray coord = stop.getJSONArray("coordinates");
                stations.add(new Stop(Integer.parseInt(stop.getString("stopCode")),stop.getString("stopName"), coord.getString(0), coord.getString(1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stations;
    }
}
