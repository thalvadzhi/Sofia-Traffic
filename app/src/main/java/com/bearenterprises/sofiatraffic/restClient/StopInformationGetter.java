package com.bearenterprises.sofiatraffic.restClient;

import android.content.Context;
import android.util.Log;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleLineTimes;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.network.RetrofitUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thalvadzhiev on 10/10/17.
 */

public class StopInformationGetter {
    private int stopCode;
    private Stop stop;
    private Stop scheduleStop;
    private SofiaTransportApi sofiaTransportApi;
    private Context context;
    private List<ScheduleLineTimes> scheduleLineTimesList;

    private RequestAndCacheScheduleStop requestAndCacheScheduleStop;
    private OnScheduleLinesReceived onScheduleLinesReceived;
    private static final String TAG = StopInformationGetter.class.getName();

    public StopInformationGetter(int stopCode, Context context) {
        this.stopCode = stopCode;
        sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
        this.requestAndCacheScheduleStop = new RequestAndCacheScheduleStop(stopCode, sofiaTransportApi);
        this.context = context;
    }

    public Stop getScheduleStopWithTimes(String code) throws Exception {
        scheduleStop = this.getScheduleStop(code);
        scheduleLineTimesList = this.getScheduleLineTimes(code);
        populateScheduleStopLineTimes(scheduleStop, scheduleLineTimesList);
        return scheduleStop;
    }

    /**
     * Get schedule stop should be called before this. This will populate the line times.
     */
    public void getScheduleTimesAsync(){
        Call<List<ScheduleLineTimes>> call = sofiaTransportApi.getScheduleLineTimes(Integer.toString(this.stopCode));
        call.enqueue(new Callback<List<ScheduleLineTimes>>() {
            @Override
            public void onResponse(Call<List<ScheduleLineTimes>> call, Response<List<ScheduleLineTimes>> response) {
                if(response.code() == 200){
                    scheduleLineTimesList = response.body();

                    try {
                        populateScheduleStopLineTimes(scheduleStop, scheduleLineTimesList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (Line l : scheduleStop.getLines()){
                        onScheduleLinesReceived.scheduleReceived(l);
                    }
                    onScheduleLinesReceived.allLinesProcessed();
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleLineTimes>> call, Throwable t) {

            }
        });
    }

    private void populateScheduleStopLineTimes(Stop scheduleStop, List<ScheduleLineTimes> scheduleLineTimesList) throws Exception {
        String currentDayType = Utility.getScheduleDayType();
        for(Line l : scheduleStop.getLines()){
            for (ScheduleLineTimes slt : scheduleLineTimesList){
                if(l.getName().equals(slt.getName())){
                    for (ScheduleTimes st: slt.getSchedule()){
                        if (st.getScheduleDayTypes().contains(currentDayType)){
                           l.setTimes(st.getTimes());
                        }
                    }
                }
            }
        }
    }

    public Stop getStopSlow(String code) throws IOException {
        Call<Stop> call = sofiaTransportApi.getStop(code);
        stop = RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) context);
        return stop;
    }


    public Stop getScheduleStop(String code) throws IOException {
        Call<Stop> call = sofiaTransportApi.getScheduleStop(code);
        scheduleStop = RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) context);
        return scheduleStop;
    }

    private List<ScheduleLineTimes> getScheduleLineTimes(String code) throws IOException {
        Call<List<ScheduleLineTimes>> call = sofiaTransportApi.getScheduleLineTimes(code);
        return RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) context);
    }



    private ArrayList<Line> lineDifference(ArrayList<Line> linesWithGPS, ArrayList<Line> linesWithSchedules) {
        ArrayList<Line> diff = new ArrayList<>();
        for (Line lineWithSchedule : linesWithSchedules) {
            if (!linesWithGPS.contains(lineWithSchedule)) {
                diff.add(lineWithSchedule);
            }
        }
        return diff;
    }



    public abstract static class OnScheduleLinesReceived{
        public abstract void scheduleReceived(Line line);
        public abstract void allLinesProcessed();
    }

    public void setOnScheduleLinesReceived(OnScheduleLinesReceived onScheduleLinesReceived){
        this.onScheduleLinesReceived = onScheduleLinesReceived;
    }


}
