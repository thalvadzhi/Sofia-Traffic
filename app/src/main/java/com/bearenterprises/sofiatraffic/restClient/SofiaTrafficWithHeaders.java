package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Log;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;

import retrofit2.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class SofiaTrafficWithHeaders {

    private SofiaTrafficApi sofiaTrafficApi;
    private Context context;
    private final String KEY_COOKIES = "PREF_COOKIES";
    private final String KEY_LAST_UPDATE_COOKIES = "LAST_UPDATE_COOKIES";
    private final String KEY_LAST_UPDATE_ROUTES = "LAST_UPDATE_ROUTES";
    private final String KEY_LAST_UPDATE_LINES = "LAST_UPDATE_LINES";
    private final String KEY_LINES = "LINES";
    private final String KEY_ROUTES = "ROUTES";
    private final long TWO_HOURS_IN_MS = 2 * 60 * 60 * 1000;
    private final long TEN_MINUTES_IN_MS = 10 * 60 * 1000;
    private final long EIGHT_HOURS_IN_MS = 8 * 60 * 60 * 1000;

    public SofiaTrafficWithHeaders(Context context){
        this.sofiaTrafficApi = MainActivity.retrofit.create(SofiaTrafficApi.class);
        this.context = context;
    }

    public void saveCookieSessionXsrfToken() {
        long lastUpdate = PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_LAST_UPDATE_COOKIES, 0);
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastUpdate) > (TWO_HOURS_IN_MS - TEN_MINUTES_IN_MS)){
            Call<Void> response = this.sofiaTrafficApi.getCookies();
            Response<Void> resp = null;
            try {
                resp = response.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Headers headers = resp.headers();
            Map<String, List<String>> headerMapList = headers.toMultimap();
            List<String> allCookies = headerMapList.get("Set-Cookie");
            HashSet<String> cookies = new HashSet<>(allCookies);

            SharedPreferences.Editor cookiesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            cookiesEditor.putStringSet(KEY_COOKIES, cookies).apply();

            SharedPreferences.Editor lastUpdateEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            lastUpdateEditor.putLong(KEY_LAST_UPDATE_COOKIES, currentTime).apply();
        }
    }

    public List<Line> getLines(){
        saveCookieSessionXsrfToken();
        long lastUpdated = PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_LAST_UPDATE_LINES, 0);
        long currentTime = System.currentTimeMillis();
        Gson gson = new Gson();

        if ((currentTime - lastUpdated) > EIGHT_HOURS_IN_MS){
            Call<List<Line>> linesCall = sofiaTrafficApi.getLines();

            SharedPreferences.Editor lastUpdatedEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            lastUpdatedEditor.putLong(KEY_LAST_UPDATE_LINES, currentTime).apply();

            try {
                List<Line> lines = linesCall.execute().body();
                SharedPreferences.Editor linesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();

                String linesString = gson.toJson(lines);
                linesEditor.putString(KEY_LINES, linesString).apply();
                return lines;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            SharedPreferences linesEditor = PreferenceManager.getDefaultSharedPreferences(context);
            String linesString = linesEditor.getString(KEY_LINES, "");
            return gson.fromJson(linesString, new TypeToken<List<Line>>(){}.getType());
        }
    }

    public Routes getRoutes(String lineId){
        saveCookieSessionXsrfToken();

        Gson gson = new Gson();
        String lastUpdatedString = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LAST_UPDATE_ROUTES, "{}");
        HashMap<String, Long> lastlyUpdatedRoutes = gson.fromJson(lastUpdatedString, new TypeToken<HashMap<String, Long>>(){}.getType());
        Long cachedUpdate = lastlyUpdatedRoutes.get(lineId);
        long lastUpdate = cachedUpdate != null ? cachedUpdate : 0;
        long currentTime = System.currentTimeMillis();
        String keyRoutes = KEY_ROUTES + "_" + lineId;

        if ((currentTime - lastUpdate) > EIGHT_HOURS_IN_MS){
            RouteInput ri = new RouteInput(Integer.parseInt(lineId));
            Call<Routes> routesCall = sofiaTrafficApi.getRoutes(ri);

            lastlyUpdatedRoutes.put(lineId, currentTime);
            String lastlyUpdatedJson = gson.toJson(lastlyUpdatedRoutes);

            SharedPreferences.Editor lastUpdatedEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            lastUpdatedEditor.putString(KEY_LAST_UPDATE_ROUTES, lastlyUpdatedJson).apply();

            try {
                SharedPreferences.Editor routesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                Routes routes = routesCall.execute().body();
                String routesString = gson.toJson(routes);
                routesEditor.putString(keyRoutes, routesString).apply();
                return routes;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            String routesPreference = PreferenceManager.getDefaultSharedPreferences(context).getString(keyRoutes, "{}");
            return gson.fromJson(routesPreference, new TypeToken<Routes>(){}.getType());
        }
    }
}
