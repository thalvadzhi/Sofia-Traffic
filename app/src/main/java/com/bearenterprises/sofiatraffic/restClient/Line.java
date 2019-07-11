
package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.restClient.Time;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Line implements Serializable {

    private Integer type;
    private Integer routeId;
    private Integer id;
    private String name;
    private String routeName;


    public Line(){

    }
    private List<Time> times = null;

//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Line(Integer type, Integer id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public void setTimes(List<Time> times) {
        this.times = times;
    }

    public Integer getRouteId() {

        return routeId;
    }

    public List<Time> getTimes() {
        return times;
    }

    /**
     * 
     * @return
     *     The type
     */
    public Integer getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(Integer type) {
        this.type = type;
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

//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        Line line = (Line) o;

        //in the two apis there are lines of the type 20-TM and 20TM which are the same
        //also in the e.g. above one TM is in cyrillic while the other is in latin script

        String lineAName = line.getName().replaceAll("-", "").replaceAll("TM", "ТМ");
        String lineBName = getName().replaceAll("-", "").replaceAll("TM", "ТМ");


        if (getType() != null ? !getType().equals(line.getType()) : line.getType() != null){
            return false;
        }
        return lineBName != null ? lineBName.equals(lineAName) : lineAName == null;

    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
