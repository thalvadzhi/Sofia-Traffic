package com.bearenterprises.sofiatraffic.utilities.parsing;

import android.content.Context;
import android.util.Log;

import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.SubwayStop;
import com.bearenterprises.sofiatraffic.stations.GeoLine;
import com.bearenterprises.sofiatraffic.utilities.ReadWholeFileAsStringKt;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thalv on 02-Jul-16.
 */
public class JSONParser {
    public static String TAG = JSONParser.class.toString();

    public static String readFileAsString(String fileName, Context context){
        String source= null;
        try {
//            source = Files.asCharSource(new File(context.getFilesDir(), fileName), Charsets.UTF_8).read();
            source = ReadWholeFileAsStringKt.readFileAsString(new File(context.getFilesDir(), fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    public static List<SubwayStop> getSubwayStopsFromFile(String fileName, Context context){
        String source = readFileAsString(fileName, context);
        return getSubwayStopFromString(source);
    }

    public static List<GeoLine> getGeoLinesFromFile(String fileName, Context context){
        String source = readFileAsString(fileName, context);
        return getGeoLines(source);
    }

    private static List<GeoLine> getGeoLines(String json){
        List<GeoLine> geoLines = new ArrayList<>();
        if (json == null){
            return null;
        }

        try{
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject line = jsonArray.getJSONObject(i);
                String name = line.getString("name");
                int type = line.getInt("type");
                String first = line.getString("first_stop");
                String last = line.getString("last_stop");
                String geo = line.getString("geo");
                GeoLine gl = new GeoLine(type, first, last, geo, name);
                geoLines.add(gl);
            }
        }catch (JSONException e){
            Log.d(TAG, "error parsing geo json", e);
        }
        return geoLines;
    }

    public static List<SubwayStop> getSubwayStopFromString(String json){
        List<SubwayStop> stops = new ArrayList<>();
        if(json == null){
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject stop = jsonArray.getJSONObject(i);
                String name = stop.getString("stopName");
                String line = stop.getString("line");
                String id = stop.getString("id");
                String code1 = (String) stop.getJSONArray("stopCodes").get(0);
                String code2 = (String) stop.getJSONArray("stopCodes").get(1);
                String lat = Double.toString((Double) stop.getJSONArray("coordinates").get(0));
                String lon = Double.toString((Double) stop.getJSONArray("coordinates").get(1));
                stops.add(new SubwayStop(name, lon, lat, line, id, Integer.parseInt(code1), Integer.parseInt(code2)));
            }
        } catch (JSONException e) {
            Log.d(TAG, "error parsing subway json", e);
        }
        return stops;
    }


    public static ArrayList<Stop> getStationsFromFile(String fileName, Context context){
        String source = readFileAsString(fileName, context);
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
                JSONArray lineTypes = stop.getJSONArray("lineTypes");
                JSONArray lineNames = stop.getJSONArray("lineNames");
                ArrayList<Line> lines = new ArrayList<>();
                for (int k = 0; k < lineNames.length(); k++){
                    String nameAndType = lineNames.getString(k);

                    String[] nameAndTypeParsed = nameAndType.replace("[", "").replace("]", "").split(",");
                    Line line = new Line(Integer.parseInt(nameAndTypeParsed[1]), null, nameAndTypeParsed[0]);
                    lines.add(line);
                }

                Stop s = new Stop(Integer.parseInt(stop.getString("stopCode")),stop.getString("stopName"), coord.getString(0), coord.getString(1));
                s.setLines(lines);
                for (int j = 0; j < lineTypes.length(); j++){
                    s.addLineType(lineTypes.getInt(j));
                }
                stations.add(s);
            }
        } catch (JSONException e) {
            Log.d(TAG, "jsonexception", e);
        } catch (Exception e){
            Log.d(TAG, "exception", e);
        }
        return stations;
    }
}
