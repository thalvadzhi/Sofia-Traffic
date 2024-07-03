package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.activities.MainActivity;

import android.content.Context;
import android.util.Log;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    public SofiaTrafficWithHeaders(Context context){
        this.sofiaTrafficApi = MainActivity.retrofit.create(SofiaTrafficApi.class);
        this.context = context;
    }

    public void saveCookieSessionXsrfToken() throws IOException {
        Call<Void> response = this.sofiaTrafficApi.getCookies();
        Response<Void> resp = response.execute();
        Headers headers = resp.headers();
        Map<String, List<String>> headerMapList = headers.toMultimap();
        List<String> allCookies = headerMapList.get("Set-Cookie");
        assert allCookies != null;
        PreferenceManager.getDefaultSharedPreferences(context).getStringSet("PREF_COOKIES", new HashSet<String>());

        HashSet<String> cookies = new HashSet<>(allCookies);


        SharedPreferences.Editor cookies_editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        cookies_editor.putStringSet("PREF_COOKIES", cookies).apply();
        cookies_editor.commit();
//        for (String cookie : allCookies){
//            String[] splitx = cookie.split(";");
//            for (String k : splitx){
//                if (k.contains("XSRF-TOKEN=")){
//                    String result = java.net.URLDecoder.decode(k, StandardCharsets.UTF_8.name());
//                    Log.i("Decoded xsrf", result);
//                }
//                Log.i("MR cookie", k);
//            }
//            Log.i("End of cookie", "########");
//        }
//        String s = Objects.requireNonNull(resp.headers().get("set-cookie"));


        Log.i("THE HEADERS", Objects.requireNonNull(resp.headers().get("set-cookie")));
//        Log.i("THE TOKEN", Objects.requireNonNull(resp.headers().get("set-x-xsrf-token")));

    }
}
