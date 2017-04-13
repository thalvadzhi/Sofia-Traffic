package com.bearenterprises.sofiatraffic.utilities.network;

import android.content.Context;
import android.util.Log;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
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
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                List<String> customAnnotations = original.headers().values("@");
                Request.Builder requestBuilder = original.newBuilder();
                if(customAnnotations.size() != 0 && registration != null){
                    requestBuilder
                            .removeHeader("@")
                            .header("X-User-Id", registration.getId());

                }
                String ivkos_api_key = context.getResources().getString(R.string.ivkos_api_key);
                Request request = requestBuilder
                        .header("X-Api-Key", ivkos_api_key)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();
        return client;
    }
}
