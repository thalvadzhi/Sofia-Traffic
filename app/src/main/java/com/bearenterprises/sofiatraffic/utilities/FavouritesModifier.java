package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.cloudBackedSharedPreferences.CloudBackedSharedPreferences;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by thalv on 03-Jul-16.
 */
public class FavouritesModifier {
    public static void save(ArrayList<Station> stations, Context context){
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        MainActivity activity = (MainActivity)context;
        CloudBackedSharedPreferences cloudBackedSharedPreferences = new CloudBackedSharedPreferences(preferences, activity.getBackupManager());
        SharedPreferences.Editor cloudBackedEditor = cloudBackedSharedPreferences.edit();
        Gson gson = new Gson();
        for(Station st : stations){
            String jsonRepresentation = gson.toJson(st);
            cloudBackedEditor.putString(st.getCode(), jsonRepresentation);

        }
        cloudBackedEditor.commit();

    }

    public static void save(Station st, Context context){
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(st);
        save(stations, context);
    }

    public static void remove(String code, Context context){
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        MainActivity activity = (MainActivity)context;
        CloudBackedSharedPreferences cloudBackedSharedPreferences = new CloudBackedSharedPreferences(preferences, activity.getBackupManager());
        SharedPreferences.Editor cloudBackedEditor = cloudBackedSharedPreferences.edit();

        cloudBackedEditor.remove(code);
        cloudBackedEditor.commit();
    }
}
