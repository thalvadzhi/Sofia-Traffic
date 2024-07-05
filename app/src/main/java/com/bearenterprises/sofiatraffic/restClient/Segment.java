
package com.bearenterprises.sofiatraffic.restClient;


import com.bearenterprises.sofiatraffic.constants.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Segment implements Serializable{
    public int id;
    @SerializedName("end_stop_id")
    public int endStopId;
    public String length;
    public String polyline;
    public int priority;
    @SerializedName("route_id")
    public int routeId;
    public int sequence;
    @SerializedName("start_stop_id")
    public int startStopId;
    @SerializedName("stop")
    public SegmentStop startStop;
    @SerializedName("end_stop")
    public SegmentStop endStop;


}
