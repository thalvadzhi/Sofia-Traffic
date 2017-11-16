package com.bearenterprises.sofiatraffic.restClient;

/**
 * Created by thalvadzhiev on 11/3/17.
 */

public abstract class ISubwayStop extends IStop{
    //could be red or blue for the moment
    protected String subwayLine;
    protected String subwayId;
    protected Integer code1;
    protected Integer code2;

    public ISubwayStop(String name, String longitude, String latitude, String subwayLine, String subwayId, Integer code1, Integer code2) {
        super(name, longitude, latitude);
        this.subwayLine = subwayLine;
        this.subwayId = subwayId;
        this.code1 = code1;
        this.code2 = code2;
    }

    public String getSubwayLine() {
        return subwayLine;
    }

    public void setSubwayLine(String subwayLine) {
        this.subwayLine = subwayLine;
    }

    public String getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(String subwayId) {
        this.subwayId = subwayId;
    }
}
