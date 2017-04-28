
package com.bearenterprises.sofiatraffic.restClient;

import java.io.Serializable;
import java.util.ArrayList;

public class Stop implements Serializable{

    private Integer favouriteIndex; // the index of appearance in Favourites tab
    private Integer id;
    private Integer code;
    private String name;
    private String longtitude;
    private String latitude;
    private String description;
    private String alias;
    private ArrayList<Line> lines;


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


    public Integer getFavouriteIndex() {
        return favouriteIndex;
    }

    public void setFavouriteIndex(Integer favouriteIndex) {
        this.favouriteIndex = favouriteIndex;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

}
