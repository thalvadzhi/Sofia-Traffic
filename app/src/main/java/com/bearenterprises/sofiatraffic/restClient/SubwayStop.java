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
    public String getSubwayLine() {
        return null;
    }

    @Override
    List<String> getCoordinates() {
        return Arrays.asList(latitude, longitude);
    }

    @Override
    String getName() {
        return name;
    }

    @Override
    List<Integer> getCodes() {
        return Arrays.asList(code1, code2);
    }

    @Override
    Integer getStopType() {
        return Constants.SUBWAY;
    }
}
