package com.bearenterprises.sofiatraffic.utilities;

import android.util.Log;

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
    public static OkHttpClient getClient(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                List<String> customAnnotations = original.headers().values("@");
                Request.Builder requestBuilder = original.newBuilder();
                if(customAnnotations.size() != 0){
                    requestBuilder
                            .removeHeader("@")
                            .header("X-User-Id", registration.getId());
                    Log.i("X-Used-Id", registration.getId());
                }

                Request request = requestBuilder
                        .header("X-Api-Key", Constants.IVKOS_API_KEY)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();
        return client;
    }
}
