package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.restClient.second.Routes;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by thalv on 29-Aug-16.
 */
public interface SofiaTransportApi {

    @GET("stops/{code}")
    Call<Station> getStation(@Path("code") String code);

    @GET("stops/{code}/{lineId}")
    Call<List<Time>> getTimes(@Path("code") String code, @Path("lineId") String lineId);

    @GET("lines/{lineType}")
    Call<List<Transport>> getLines(@Path("lineType") String lineType);

    @GET("lines/{lineType}/{lineId}")
    Call<Routes> getRoutes(@Path("lineType") String lineType, @Path("lineId") String lineId);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.sofiatransport.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
