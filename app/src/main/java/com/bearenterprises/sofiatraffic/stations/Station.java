package com.bearenterprises.sofiatraffic.stations;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thalv on 01-Jul-16.
 */
public class Station implements Serializable{
    private String name, code, latitude, longtitute, description;

    public Station(String name, String code, String latitude, String longtitute) {
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longtitute = longtitute;
    }

    public Station(String name, String code, String latitude, String longtitute, String description) {
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longtitute = longtitute;
        this.description = description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongtitute() {
        return longtitute;
    }

    public String getDescription(){
        return this.description;
    }

    public String getDirection(){
        if(this.description == null){
            return null;
        }
        String patternDirection = "ПОСОКА(.*)";
        String dir = match(patternDirection);
        if(dir != null){
            return dir;
        }
        String patternFirstStop = "НАЧАЛНА СПИРКА(.*)";
        String firstStop = match(patternFirstStop);
        if(firstStop != null){
            return firstStop;
        }

        String patternLastStop = "КРАЙНА СПИРКА(.*)";
        String lastStop = match(patternLastStop);
        if(lastStop != null){
            return lastStop;
        }

        return null;

    }

    private String match(String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(this.description);
        if(m.find()){
            return m.group();
        }else{
            return null;
        }
    }

}
