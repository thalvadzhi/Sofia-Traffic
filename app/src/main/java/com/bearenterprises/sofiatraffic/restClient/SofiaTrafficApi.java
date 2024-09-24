package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleLineTimes;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleRoute;

import java.util.HashMap;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
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
    Call<List<Line>> getLines();

    @POST("trip/getLines")
    Call<ResponseBody> getLinesRaw();

    @GET("/")
    Call<Void> getCookies();

    @POST("trip/getSchedule")
    Call<Routes> getRoutes(@Body RouteInput ri);

    @POST("trip/getSchedule")
    Call<ResponseBody> getRoutesRaw(@Body RouteInput ri);

    @POST("trip/getSchedule")
    Call<Schedule> getSchedule(@Body RouteInput ri);
}
