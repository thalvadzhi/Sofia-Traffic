package com.bearenterprises.sofiatraffic.restClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    public static final String PREF_COOKIES = "PREF_COOKIES";
    public static final String XSRF_TOKEN = "XSRF-TOKEN";
    public static final String SOFIA_TRAFFIC_SESSION = "sofia_traffic_session";

    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        HashSet<String> cookies = (HashSet<String>) defaultSharedPreferences.getStringSet(PREF_COOKIES, new HashSet<String>());
        HashMap<String, String> cookiesMap = new HashMap<>();

        String xsrfToken = null;
        for (String cookie : cookies){
            if (cookie.contains(XSRF_TOKEN)){
                String cookie_only = cookie.split(";")[0].replace(XSRF_TOKEN+"=", "");
                xsrfToken = java.net.URLDecoder.decode(cookie_only, StandardCharsets.UTF_8.name());
                cookiesMap.put(XSRF_TOKEN, cookie);
            }else if (cookie.contains(SOFIA_TRAFFIC_SESSION)){
                cookiesMap.put(SOFIA_TRAFFIC_SESSION, cookie);
            }
        }
        builder.addHeader("cookie", cookiesMap.get(SOFIA_TRAFFIC_SESSION));
        builder.addHeader("cookie", cookiesMap.get(XSRF_TOKEN));


        if (xsrfToken != null){
            builder.addHeader("x-xsrf-token", xsrfToken);

        }
        return chain.proceed(builder.build());
    }
}
