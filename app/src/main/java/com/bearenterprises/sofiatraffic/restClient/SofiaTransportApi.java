package com.bearenterprises.sofiatraffic.restClient;

import android.content.Context;

import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.restClient.second.Routes;
import com.bearenterprises.sofiatraffic.utilities.GenerateClient;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by thalv on 29-Aug-16.
 */
public interface SofiaTransportApi {
    @Headers("@: userID")
    @GET("stops/{code}")
    Call<Station> getStation(@Path("code") String code);

    @Headers("@: userID")
    @GET("beta/stops/{code}")
    Call<Station> getStationWithTimes(@Path("code") String code);

    @Headers("@: userID")
    @GET("stops/{code}/lines/{lineId}")
    Call<List<Time>> getTimes(@Path("code") String code, @Path("lineId") String lineId);

    @Headers("@: userID")
    @GET("lines/{lineType}")
    Call<List<Line>> getLines(@Path("lineType") String lineType);

    @Headers("@: userID")
    @GET("lines/{lineType}/{lineId}")
    Call<Routes> getRoutes(@Path("lineType") String lineType, @Path("lineId") String lineId);

    @POST("users")
    Call<Registration> registerUser();
}
