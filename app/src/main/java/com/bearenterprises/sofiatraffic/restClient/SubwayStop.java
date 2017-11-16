package com.bearenterprises.sofiatraffic.restClient;

import com.bearenterprises.sofiatraffic.constants.Constants;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by thalvadzhiev on 11/3/17.
 */

public class SubwayStop extends ISubwayStop {


    public SubwayStop(String name, String longitude, String latitude, String subwayLine, String subwayId, Integer code1, Integer code2) {
        super(name, longitude, latitude, subwayLine, subwayId, code1, code2);
    }

    @Override
    public List<String> getCoordinates() {
        return Arrays.asList(latitude, longitude);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Integer> getCodes() {
        return Arrays.asList(code1, code2);
    }

    @Override
    public Integer getStopType() {
        return Constants.SUBWAY;
    }
}
