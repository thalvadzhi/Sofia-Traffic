package com.bearenterprises.sofiatraffic.utilities.network;

import android.content.Context;
import android.util.Log;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.AddCookiesInterceptor;
import com.bearenterprises.sofiatraffic.restClient.Registration;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thalv on 19-Dec-16.
 */

public class GenerateClient {
    private static Registration registration;

    /**
     * This method should be called from MainActivity after registration has been settled
     */
    public static void setRegistration(Registration reg){
        registration = reg;
    }

    public static OkHttpClient getClient(final Context context){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new AddCookiesInterceptor(context));
        OkHttpClient client = httpClient.build();
        return client;
    }
}
