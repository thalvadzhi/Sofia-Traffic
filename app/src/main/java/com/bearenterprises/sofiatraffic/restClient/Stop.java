
package com.bearenterprises.sofiatraffic.restClient;


import com.bearenterprises.sofiatraffic.constants.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stop extends IStop implements Serializable{

    private Integer code;
    private String description;
    private ArrayList<Line> lines;
    private ArrayList<Integer> lineTypes;

    public Stop(){
    }

    public Stop(Integer id, Integer code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Stop(Integer id, Integer code, String name, String latitude, String longitude, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.lineTypes = new ArrayList<>();
    }

    public Stop(Integer code, String name, String latitude, String longitude, String description) {
        this.code = code;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.lineTypes = new ArrayList<>();

    }


    public Stop(Integer code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.lineTypes = new ArrayList<>();
    }

    public Stop(Integer code, String name,  String latitude, String longitude) {
        this.name = name;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lineTypes = new ArrayList<>();

    }

    public ArrayList<Integer> getLineTypes() {
        return lineTypes;
    }

    public void addLineType(Integer lineType){
        this.lineTypes.add(lineType);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public List<String> getCoordinates() {
        return Arrays.asList(this.latitude, this.longitude);
    }

    public String getName() {
        return name;
    }

    @Override
    public List<Integer> getCodes() {
        return Arrays.asList(this.code);
    }

    @Override
    public Integer getStopType() {
        return Constants.NORMAL;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLineTypes(ArrayList<Integer> lineTypes) {
        this.lineTypes = lineTypes;
    }
}
