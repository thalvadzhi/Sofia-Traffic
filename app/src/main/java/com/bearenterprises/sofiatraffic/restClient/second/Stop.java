
package com.bearenterprises.sofiatraffic.restClient.second;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stop implements Serializable{

    private Integer favouriteIndex; // the index of appearance in Favourites tab
    private Integer id;
    private Integer code;
    private String name;
    private String longtitude;
    private String latitude;
    private String description;
    private String alias;

    public Stop(Integer id, Integer code, String name, String latitude, String longtitude, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.description = description;
    }

    public Stop(Integer code, String name, String latitude, String longtitude, String description) {
        this.code = code;
        this.name = name;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.description = description;
    }


    public Stop(Integer code, String name, String description) {

        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Stop(Integer code, String name,  String latitude, String longtitude) {
        this.name = name;
        this.code = code;
        this.longtitude = longtitude;
        this.latitude = latitude;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getDescription() {
        return description;
    }


    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 
     * @param code
     *     The code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
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

    public Integer getFavouriteIndex() {
        return favouriteIndex;
    }

    public void setFavouriteIndex(Integer favouriteIndex) {
        this.favouriteIndex = favouriteIndex;
    }
}
