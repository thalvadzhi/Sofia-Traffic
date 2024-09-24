
package com.bearenterprises.sofiatraffic.restClient;


import java.io.Serializable;


public class RouteInput implements Serializable{
    public int line_id;
    public String ext_id;

    public RouteInput(String ext_id){
//        this.line_id = line_id;
        this.ext_id = ext_id;
    }
}
