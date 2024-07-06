package com.bearenterprises.sofiatraffic.restClient;

import android.content.Context;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleLineTimes;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.network.RetrofitUtility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by thalvadzhiev on 10/10/17.
 */

public class StopInformationGetter {
    private int stopCode;
    private Stop stop;
    private Stop scheduleStop;
//    private SofiaTransportApi sofiaTransportApi;
    private SofiaTrafficApi sofiaTrafficApi;
    private Context context;
    private List<ScheduleLineTimes> scheduleLineTimesList;

    private RequestAndCacheScheduleStop requestAndCacheScheduleStop;
    private OnScheduleLinesReceived onScheduleLinesReceived;
    private static final String TAG = StopInformationGetter.class.getName();

    public StopInformationGetter(int stopCode, Context context) {
        this.stopCode = stopCode;
        sofiaTrafficApi = MainActivity.retrofit.create(SofiaTrafficApi.class);
//        this.requestAndCacheScheduleStop = new RequestAndCacheScheduleStop(stopCode, sofiaTransportApi);
        this.context = context;
    }

//    public Stop getScheduleStopWithTimes(String code) throws Exception {
//        scheduleStop = this.getScheduleStop(code);
//        scheduleLineTimesList = this.getScheduleLineTimes(code);
//        populateScheduleStopLineTimes(scheduleStop, scheduleLineTimesList);
//        return scheduleStop;
//    }

    /**
     * Get schedule stop should be called before this. This will populate the line times.
     */
//    public void getScheduleTimesAsync(){
//        Call<List<ScheduleLineTimes>> call = sofiaTransportApi.getScheduleLineTimes(Integer.toString(this.stopCode));
//        call.enqueue(new Callback<List<ScheduleLineTimes>>() {
//            @Override
//            public void onResponse(Call<List<ScheduleLineTimes>> call, Response<List<ScheduleLineTimes>> response) {
//                if(response.code() == 200){
//                    scheduleLineTimesList = response.body();
//
//                    try {
//                        populateScheduleStopLineTimes(scheduleStop, scheduleLineTimesList);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    if (scheduleStop == null){
//                        return;
//                    }
//
//                    if (scheduleStop.getLines() == null){
//                        return;
//                    }
//
//                    for (Line l : scheduleStop.getLines()){
//                        onScheduleLinesReceived.scheduleReceived(l);
//                    }
//                    onScheduleLinesReceived.allLinesProcessed();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ScheduleLineTimes>> call, Throwable t) {
//
//            }
//        });
//    }

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
        if (code.length() < 4){
            code = String.format("%04d", Integer.parseInt(code));
        }

        SofiaTrafficWithHeaders stwh = new SofiaTrafficWithHeaders(this.context);

        stwh.saveCookieSessionXsrfToken();

        Call<HashMap<String, VirtualTableForStop>> call = sofiaTrafficApi.getVirtualTables(new VirtualTablesInput(code));
        HashMap<String, VirtualTableForStop> vtsStop =  RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) context);

        ArrayList<VirtualTableForStop> vts = new ArrayList<>(vtsStop.values());

        ArrayList<Line> lines = new ArrayList<>();
        Calendar timeNow = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm", Locale.US);
        ArrayList<Integer> lineTypes = new ArrayList<>();

        for (VirtualTableForStop s : vts){
            Line l = new Line(Utility.newLineTypeToOldLineType(s.type), s.id, s.name);
            l.setRouteName(s.routeName.toUpperCase().toUpperCase());
            ArrayList<Time> times = new ArrayList<>();

            for (VirtualTableTime vtt : s.times){
                Calendar current_time = (Calendar) timeNow.clone();
                current_time.add(Calendar.MINUTE, vtt.time);

                Time t = new Time(format1.format(current_time.getTime()), vtt.ac, vtt.wheelchairs);
                times.add(t);
            }
            l.setTimes(times);
            lines.add(l);
            if (!lineTypes.contains(l.getType())){
                lineTypes.add(l.getType());
            }

        }

        StopBuilder sb = new StopBuilder();
        stop = sb.setCode(Integer.parseInt(code)).setLines(lines).setLineTypes(lineTypes).build();

        return stop;

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
