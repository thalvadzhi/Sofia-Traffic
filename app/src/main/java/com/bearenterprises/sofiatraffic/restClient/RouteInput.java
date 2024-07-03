
package com.bearenterprises.sofiatraffic.restClient;


import java.io.Serializable;


public class RouteInput implements Serializable{
    public int line_id;

    public RouteInput(int line_id){
        this.line_id = line_id;
    }
}
