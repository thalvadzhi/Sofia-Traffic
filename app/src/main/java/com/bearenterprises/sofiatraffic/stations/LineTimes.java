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
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by thalv on 02-Jul-16.
 */
public class LineTimes implements Serializable{
    private Line line;
    private String type;
    private String times;
    private String routeName;
    private boolean isSchedule;
    private final static String TIME_TEMPLATE = "%d:%d";
    private final static String TIME_TEMPLATE_O_BEFORE_MINUTE = "%d:0%d";


    public boolean isSchedule() {
        return isSchedule;
    }

    public void setSchedule(boolean schedule) {
        isSchedule = schedule;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

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
            Long timeDiff = getTimeDiff(date, timeNow);
            timeDiffs.add(timeDiff);
        }
        return timeDiffs;
    }

    private String formatHoursAndMinutes(int hours, int minutes){
        String time;
        if(minutes < 10){
            time = String.format(TIME_TEMPLATE_O_BEFORE_MINUTE, hours, minutes);
        }else{
            time = String.format(TIME_TEMPLATE, hours, minutes);
        }
        return time;
    }

    private Long getTimeDiff(Date date, Calendar timeNow){
        Calendar targetTime = createPresentDayCalendarAndSetHourAndMinute(date, timeNow);
        long timeDiff = Math.abs(targetTime.getTimeInMillis() - timeNow.getTimeInMillis());
        return timeDiff;
    }

    private Calendar createPresentDayCalendarAndSetHourAndMinute(Date date, Calendar timeNow){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.YEAR, timeNow.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, timeNow.get(Calendar.MONTH));
        int hourOfDaySchedule = cal.get(Calendar.HOUR_OF_DAY);
        int hourOfDayNow = timeNow.get(Calendar.HOUR_OF_DAY);
        cal.set(Calendar.DAY_OF_MONTH, timeNow.get(Calendar.DAY_OF_MONTH));
        if(hourOfDaySchedule < hourOfDayNow){
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }else if (hourOfDaySchedule == hourOfDayNow){
            int minuteOfDaySchedule = cal.get(Calendar.MINUTE);
            int minuteOfDayNow = timeNow.get(Calendar.MINUTE);
            if(minuteOfDaySchedule < minuteOfDayNow){
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return cal;
    }

    public ArrayList<String> getRemainingTimesList(){
        if (vehicleTimes == null){
            return new ArrayList<>();
        }
        ArrayList<Date> dates = timesToDates(vehicleTimes);
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
        if(times.size() < 10){
            return times;
        }else{
            return new ArrayList<>(times.subList(0, 10));
        }
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
        ArrayList<Date> dates = timesToDates(vehicleTimes);
        final Calendar timeNow = Calendar.getInstance();
        Collections.sort(dates, new Comparator<Date>() {
            @Override
            public int compare(Date date1, Date date2) {
                Long timeDiffWithDate1 = getTimeDiff(date1, timeNow);
                Long timeDiffWithDate2 = getTimeDiff(date2, timeNow);
                return (int)(timeDiffWithDate1 - timeDiffWithDate2);
            }
        });
        for(Date date : dates){
            Calendar cal = createPresentDayCalendarAndSetHourAndMinute(date, timeNow);
            int hours = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);
            String time = formatHoursAndMinutes(hours, minutes);
            times.add(time);
        }
        if(times.size() < 10){
            return times;
        }else{
            return new ArrayList<>(times.subList(0, 10));
        }
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
