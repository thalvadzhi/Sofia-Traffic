package com.bearenterprises.sofiatraffic.restClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    public static final String PREF_COOKIES = "PREF_COOKIES";
    public static final String XSRF_TOKEN = "XSRF_TOKEN";
    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        HashSet<String> cookies = (HashSet<String>) defaultSharedPreferences.getStringSet(PREF_COOKIES, new HashSet<String>());
        String xsrfToken = null;
        for (String cookie : cookies){
            Log.i("Mr COOKIE", cookie);
            if (cookie.contains("XSRF-TOKEN")){
                String cookie_only = cookie.split(";")[0].replace("XSRF-TOKEN=", "");
                xsrfToken = java.net.URLDecoder.decode(cookie_only, StandardCharsets.UTF_8.name());
            }
        }

        for (String cookie : cookies) {
            Log.i("Mr COOKIE", cookie);

            builder.addHeader("cookie", cookie);
        }
        if (xsrfToken != null){
            Log.i("Mr XSRF", xsrfToken);

            builder.addHeader("x-xsrf-token", xsrfToken);

        }
        return chain.proceed(builder.build());
    }
}
