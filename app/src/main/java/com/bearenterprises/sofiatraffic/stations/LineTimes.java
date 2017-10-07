package com.bearenterprises.sofiatraffic.stations;

import android.text.TextUtils;

import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.restClient.Line;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by thalv on 02-Jul-16.
 */
public class LineTimes implements Serializable{
    private Line line;
    private String type;
    private String times;
    private ArrayList<Time> vehicleTimes;

    public LineTimes(Line line, String type, String times, ArrayList<Time> vehicleTimes) {
        this.line = line;
        this.type = type;
        this.times = times;
        this.vehicleTimes = vehicleTimes;
    }

    public LineTimes(Line line, String type) {
        this.line = line;
        this.type = type;
    }

    public Line getLine() {
        return line;
    }

    public String getType() {
        return type;
    }

    public String getTimes() {
        if (vehicleTimes == null){
            return null;
        }
//        if(times == null){
//            times = generateTimes();
//        }
        return generateTimes();
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Time> getVehicleTimes() {
        return vehicleTimes;
    }

    private ArrayList<Date> timesToDates(ArrayList<Time> vehicleTimes){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        ArrayList<Date> dates = new ArrayList<>();
        for(Time t : vehicleTimes){
            try {
                dates.add(sdf.parse(t.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    private ArrayList<Date> mockDates(){
        ArrayList<Date> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        for(int i=0; i < 10; i++){

            try {
                dates.add(sdf.parse("00:50"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return dates;
    }

    private ArrayList<Long> datesToTimeDiffs(ArrayList<Date> dates){
        ArrayList<Long> timeDiffs = new ArrayList<>();
        Calendar timeNow = Calendar.getInstance();
        for(Date date : dates){
            Calendar helperCalendar = Calendar.getInstance();
            Calendar targetTime = Calendar.getInstance();
            helperCalendar.setTime(date);
            targetTime.set(Calendar.HOUR_OF_DAY, helperCalendar.get(Calendar.HOUR_OF_DAY));
            targetTime.set(Calendar.MINUTE, helperCalendar.get(Calendar.MINUTE));
            int hourOfDayTarget = targetTime.get(Calendar.HOUR_OF_DAY);
            int hourOfDayNow = timeNow.get(Calendar.HOUR_OF_DAY);
            if(hourOfDayTarget < hourOfDayNow){
                targetTime.add(Calendar.DAY_OF_MONTH, 1);
            }
            long timeDiff = Math.abs(targetTime.getTimeInMillis() - timeNow.getTimeInMillis());
            timeDiffs.add(timeDiff);
        }
        return timeDiffs;
    }

    public ArrayList<String> getRemainingTimesList(){
        if (vehicleTimes == null){
            return new ArrayList<>();
        }
        ArrayList<Date> dates = timesToDates(vehicleTimes);
//        ArrayList<Date> dates = mockDates();
        ArrayList<Long> timeDiffs = datesToTimeDiffs(dates);
        Collections.sort(timeDiffs);
        ArrayList<String> times = new ArrayList<>();
        for(Long timeDiff : timeDiffs){

            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
            long diffHours = minutes / 60;
            long diffMinutes = minutes % 60;

            if(diffHours != 0){
                String paddedDiffMinutes = null;
                if (diffMinutes < 10){
                    paddedDiffMinutes = "0" + diffMinutes;

                }else{
                    paddedDiffMinutes = diffMinutes + "";
                }
                times.add(diffHours+"ч"+paddedDiffMinutes+"м");
            }else{
                times.add(""+diffMinutes+"м");
            }
        }
        return times;
    }
    public String getRemainingTimes(){
        if (vehicleTimes == null){
            return "";
        }
        ArrayList<String> remainingTimesList = getRemainingTimesList();

        return TextUtils.join(" ", remainingTimesList);
    }

    public ArrayList<String> getTimesList(){
        if(vehicleTimes == null){
            return new ArrayList<>();
        }
        ArrayList<String> times = new ArrayList<>();
        for(Time t : vehicleTimes){
            times.add(t.getTime());
        }
        return times;
    }
    private String generateTimes(){
        if (vehicleTimes == null){
            return "";
        }
        ArrayList<String> timesList = getTimesList();
        return TextUtils.join(" ", timesList);
    }

    public void setVehicleTimes(ArrayList<Time> vehicleTimes) {
        this.vehicleTimes = vehicleTimes;
    }
}
