
package com.bearenterprises.sofiatraffic.restClient;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class SegmentStop implements Serializable{
    public int id;
    public String code;
    @SerializedName("ext_id")
    public String extId;
    @SerializedName("is_active")
    public Integer isActive;
    public String latitude;
    public String longitude;
    public Integer type;
    public String name;
    public String description;

}
