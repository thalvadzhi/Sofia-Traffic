package com.bearenterprises.sofiatraffic.restClient;

import android.util.Log;

import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleLineTimes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAndCacheScheduleStop {
    private int stopCode;
    private List<ScheduleLineTimes> scheduleLineTimesList;
    private List<ScheduleReceivedObserver> observers;
    private boolean hasRequestedSchedule;
    private SofiaTransportApi sofiaTransportApi;

    public RequestAndCacheScheduleStop(int stopCode, SofiaTransportApi sofiaTransportApi) {
        this.stopCode = stopCode;
        this.hasRequestedSchedule = false;
        this.sofiaTransportApi = sofiaTransportApi;
        observers = new ArrayList<>();
    }

    public void addObserver(ScheduleReceivedObserver observer) {
        synchronized (observers){
            observers.add(observer);
        }
        if(scheduleLineTimesList != null){
            notifyObservers(scheduleLineTimesList);
        }
    }

    private void notifyObservers(List<ScheduleLineTimes> scheduleLineTimes) {
        synchronized (observers){
            for (ScheduleReceivedObserver observer : observers) {
                observer.onScheduleReceived(scheduleLineTimes);
            }
            observers.clear();
        }
    }

    public void getSchedule() {
        if (!hasRequestedSchedule) {
            hasRequestedSchedule = true;
            final Call<List<ScheduleLineTimes>> scheduleLineTimes = sofiaTransportApi.getScheduleLineTimes(Integer.toString(stopCode));
            final long requested = System.currentTimeMillis();
            scheduleLineTimes.enqueue(new Callback<List<ScheduleLineTimes>>() {
                @Override
                public void onResponse(Call<List<ScheduleLineTimes>> call, Response<List<ScheduleLineTimes>> response) {
                    scheduleLineTimesList = response.body();
                    Log.i("time for response", ""  + (System.currentTimeMillis() - requested));
                    notifyObservers(scheduleLineTimesList);
                }

                @Override
                public void onFailure(Call<List<ScheduleLineTimes>> call, Throwable t) {

                }
            });
        }

    }


    public static abstract class ScheduleReceivedObserver {
        public abstract void onScheduleReceived(List<ScheduleLineTimes> scheduleLineTimesList);
    }
}