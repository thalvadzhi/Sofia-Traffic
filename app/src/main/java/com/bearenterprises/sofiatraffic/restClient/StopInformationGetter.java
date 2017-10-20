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
    private OnPreciseTimeReceivedListener onPreciseTimeReceivedListener;
    private OnScheduleReceivedListener onScheduleReceivedListener;
    private OnPreciseTimeScheduleMixReceivedListener onPreciseTimeScheduleMixReceivedListener;
    private RequestAndCacheScheduleStop requestAndCacheScheduleStop;
    private static final String TAG = StopInformationGetter.class.getName();

    public StopInformationGetter(int stopCode, Context context) {
        this.stopCode = stopCode;
        sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
        this.requestAndCacheScheduleStop = new RequestAndCacheScheduleStop(stopCode, sofiaTransportApi);
        this.context = context;
    }

    public Stop getStopSlow(String code) throws IOException {
        Call<Stop> call = sofiaTransportApi.getStop(code);
        stop = RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) context);
        return stop;
    }

    public Stop getStopFast(String code) throws IOException {
        Call<Stop> call = sofiaTransportApi.getStopWithTimes(code);
        stop = RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) context);
        return stop;
    }

    private Stop getScheduleStop(String code) throws IOException {
        Call<Stop> call = sofiaTransportApi.getScheduleStop(code);
        scheduleStop = RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) context);
        return scheduleStop;
    }

    private void getScheduleLineTimesList(String code, final Line line) throws IOException {
        if (scheduleLineTimesList == null) {
            requestAndCacheScheduleStop.addObserver(new RequestAndCacheScheduleStop.ScheduleReceivedObserver() {
                @Override
                public void onScheduleReceived(List<ScheduleLineTimes> scheduleLineTimesList) {
                    handleScheduleTimes(scheduleLineTimesList, line);
                }
            });
        } else {
            handleScheduleTimes(scheduleLineTimesList, line);
        }
    }

    private void handleScheduleTimes(List<ScheduleLineTimes> scheduleLineTimesList, Line line) {
        List<Time> times;
        TimesWithDirection timesWithDirection;
        try {
            timesWithDirection = scheduleLineTimesToTimesOfLine(scheduleLineTimesList, line);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't transform scheduleLineTimes to times", e);
            return;
        }

        if (timesWithDirection != null) {
            times = timesWithDirection.getTimes();
            if (times.size() != 0) {
//                onPreciseTimeScheduleMixReceivedListener.received(line, times, OnPreciseTimeScheduleMixReceivedListener.SCHEDULE);
                onPreciseTimeScheduleMixReceivedListener.receivedSchedule(line, timesWithDirection);
            } else {
                onPreciseTimeScheduleMixReceivedListener.received(line, null, OnPreciseTimeScheduleMixReceivedListener.NONE);
            }
        } else {
            onPreciseTimeScheduleMixReceivedListener.received(line, null, OnPreciseTimeScheduleMixReceivedListener.NONE);
        }
    }

    public Stop getStopWithAllLines(String code) throws IOException {
        return getScheduleStop(code);
    }

    public void getLineTimeWithSchedulesAsync() throws IOException {
        if (stop != null) {
            getTimesMixedWithSchedules();
        } else {
//            getStopSlow(Integer.toString(stopCode));
            getTimesMixedWithSchedules();
        }
    }

    private TimesWithDirection scheduleLineTimesToTimesOfLine(List<ScheduleLineTimes> slt, Line line) throws Exception {
        //TODO rewrite this in functional style
        for (ScheduleLineTimes s : slt) {
            //the list contains the times for all the lines so we need to filter it
            if (s.getType() == line.getType() && s.getName().equals(line.getName())) {
                String scheduleDayType = Utility.getScheduleDayType();
                for (ScheduleTimes t : s.getSchedule()) {

                    //we only need the schedule for the current day
                    if (t.getScheduleDayTypes().contains(scheduleDayType)) {
                        return new TimesWithDirection(t.getTimes(), t.getRouteName());
                    }
                }
                break;
            }
        }
        return null;
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

    private void getTimesMixedWithSchedules() {
        requestAndCacheScheduleStop.getSchedule();
        if (stop != null) {
            for (int i = 0; i < stop.getLines().size(); i++) {
                final Line line = stop.getLines().get(i);
                Call<List<Time>> call = sofiaTransportApi.getTimes(Integer.toString(stop.getCode()), Integer.toString(line.getId()));
                call.enqueue(new Callback<List<Time>>() {
                    @Override
                    public void onResponse(Call<List<Time>> call, Response<List<Time>> response) {
                        List<Time> times = response.body();
                        if (onPreciseTimeReceivedListener != null) {
                            if (times != null) {
                                onPreciseTimeReceivedListener.received(line, times);
                            }
                        }
                        if (onPreciseTimeScheduleMixReceivedListener != null) {
                            if (times != null) {
                                onPreciseTimeScheduleMixReceivedListener.received(line, times, OnPreciseTimeScheduleMixReceivedListener.PRECISE);
                            }
                        }
                        if (times == null) {
                            try {
                                getScheduleLineTimesList(Integer.toString(scheduleStop.getCode()), line);
                            } catch (IOException e) {
                                Log.d(TAG, "Couldn't get schedulelinetimes", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Time>> call, Throwable t) {

                    }
                });

            }
        }
        ArrayList<Line> lineDiff;
        if (stop == null) {
            lineDiff = scheduleStop.getLines();
        } else {
            lineDiff = lineDifference(stop.getLines(), scheduleStop.getLines());
        }
        for (Line line : lineDiff) {
            try {
                getScheduleLineTimesList(Integer.toString(scheduleStop.getCode()), line);
            } catch (IOException e) {
                Log.d(TAG, "Couldn't get schedulelinetimes", e);
            }
        }
    }

    public abstract class OnScheduleReceivedListener {
        public abstract void received(List<ScheduleLineTimes> scheduleLineTimes);
    }

    public abstract class OnPreciseTimeReceivedListener {
        public abstract void received(Line line, List<Time> times);
    }

    public abstract static class OnPreciseTimeScheduleMixReceivedListener {
        public static final String SCHEDULE = "schedule";
        public static final String PRECISE = "precise";
        public static final String NONE = "no_info";

        //means is either schedule or precise
        public abstract void received(Line line, List<Time> times, String means);

        public abstract void receivedSchedule(Line line, TimesWithDirection timesWithDirection);
    }

    public void setOnPreciseTimeReceivedListener(OnPreciseTimeReceivedListener onPreciseTimeReceivedListener) {
        this.onPreciseTimeReceivedListener = onPreciseTimeReceivedListener;
    }

    public void setOnScheduleReceivedListener(OnScheduleReceivedListener onScheduleReceivedListener) {
        this.onScheduleReceivedListener = onScheduleReceivedListener;
    }

    public void setOnPreciseTimeScheduleMixReceivedListener(OnPreciseTimeScheduleMixReceivedListener onPreciseTimeScheduleMixReceivedListener) {
        this.onPreciseTimeScheduleMixReceivedListener = onPreciseTimeScheduleMixReceivedListener;
    }


    public static class TimesWithDirection {
        private ArrayList<Time> times;
        private String direction;

        public ArrayList<Time> getTimes() {
            return times;
        }

        public void setTimes(ArrayList<Time> times) {
            this.times = times;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public TimesWithDirection(ArrayList<Time> times, String direction) {
            this.times = times;
            this.direction = direction;
        }
    }
}
