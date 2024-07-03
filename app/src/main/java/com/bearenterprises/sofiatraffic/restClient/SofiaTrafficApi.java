package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleLineTimes;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleRoute;

import java.util.HashMap;
import java.util.List;

import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SofiaTrafficApi {

    @POST("trip/getAllStops")
    Call<List<SegmentStop>> getAllStops();

    @POST("trip/getVirtualTable")
    Call<HashMap<String, VirtualTableForStop>> getVirtualTables(@Body VirtualTablesInput virtualTablesInput);


    @POST("trip/getLines")
    Call<Stop> getStop(String code);

    @Headers("@: userID")
    @GET("beta/stops/{code}")
    Call<Stop> getStopWithTimes(@Path("code") String code);

    @Headers("@: userID")
    @GET("stops/{code}/lines/{lineId}")
    Call<List<Time>> getTimes(@Path("code") String code, @Path("lineId") String lineId);

    @POST("trip/getLines")
    Call<List<Line>> getLines();

    @GET("/")
    Call<Void> getCookies();

    @POST("trip/getSchedule")
    Call<Routes> getRoutes(@Body RouteInput ri);

    @Headers("@: userID")
    @GET("schedules/lines/{lineType}")
    Call<List<Line>> getScheduleLines(@Path("lineType") String lineType);

    @Headers("@: userID")
    @GET("schedules/lines/{lineType}/{lineName}")
    Call<List<ScheduleRoute>> getScheduleRoutes(@Path("lineType") String lineType, @Path("lineName") String lineName);

    @Headers("@: userID")
    @GET("schedules/stops/{stopCode}")
    Call<Stop> getScheduleStop(@Path("stopCode") String stopCode);

    @Headers("@: userID")
    @GET("schedules/stops/{stopCode}/lines")
    Call<List<ScheduleLineTimes>> getScheduleLineTimes(@Path("stopCode") String stopCode);

    @POST("users")
    Call<Registration> registerUser();
}
