package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.restClient.second.Routes;

import java.util.List;

import retrofit2.Call;
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
