
package com.bearenterprises.sofiatraffic.restClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route {

    private Integer id;
    private String name;
    private Integer type;
    private String stop;
    private List<Segment> segments = new ArrayList<>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     *     The stops
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * 
     * @param segments
     *     The stops
     */
    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
