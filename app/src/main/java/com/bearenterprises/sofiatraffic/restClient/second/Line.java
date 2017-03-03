
package com.bearenterprises.sofiatraffic.restClient.second;

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
    private List<Time> times = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Line(Integer type, Integer id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
