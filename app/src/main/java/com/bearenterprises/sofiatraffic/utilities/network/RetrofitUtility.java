package com.bearenterprises.sofiatraffic.utilities.network;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.ApiError;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.utilities.parsing.ParseApiError;
import com.bearenterprises.sofiatraffic.utilities.registration.RegistrationUtility;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by thalv on 05-Mar-17.
 */

public class RetrofitUtility {
    public static <T> T handleUnauthorizedQuery(Call<T> call, MainActivity mainActivity) throws IOException {
        Response<T> response = call.execute();
        if(!response.isSuccessful()){
            ApiError error = ParseApiError.parseError(response);
            if(error.getCode() != null && error.getCode().equals(Constants.UNAUTHOROZIED_USER_ID)){
                RegistrationUtility.reRegister(mainActivity);
                Call<T> cll = call.clone();
                response = cll.execute();
            }

        }
        return response.body();
    }

    public static ArrayList<Line> getLinesByStationCode(String code, MainActivity activity){
        SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
        Call<Station> station = sofiaTransportApi.getStation(code);
        try {
            return RetrofitUtility.handleUnauthorizedQuery(station, activity).getLines();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}