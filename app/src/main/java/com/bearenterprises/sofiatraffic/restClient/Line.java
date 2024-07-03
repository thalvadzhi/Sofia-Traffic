
package com.bearenterprises.sofiatraffic.restClient;


import java.io.Serializable;
import java.util.List;


public class Line implements Serializable {

    private Integer type;
    private Integer routeId;
    private Integer line_id;
    private String name;
    private String extId;

    private String routeName;


    public Line(){

    }
    private List<Time> times = null;

//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Line(Integer type, Integer line_id, String name) {
        this.type = type;
        this.line_id = line_id;
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
    public Integer getLine_id() {
        return line_id;
    }

    /**
     * 
     * @param line_id
     *     The id
     */
    public void setLine_id(Integer line_id) {
        this.line_id = line_id;
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
        //also in the e.g. above one TM is in cyrillic while the other is in latin script, same for E

        String lineAName = line.getName().replaceAll("-", "").replaceAll("TM", "ТМ").replace("E", "Е");
        String lineBName = getName().replaceAll("-", "").replaceAll("TM", "ТМ").replace("E", "Е");


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
