package com.bearenterprises.sofiatraffic;

import com.bearenterprises.sofiatraffic.Constants.Constants;
import com.bearenterprises.sofiatraffic.stations.Line;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thalv on 02-Jul-16.
 */
public class JSONParser {

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
