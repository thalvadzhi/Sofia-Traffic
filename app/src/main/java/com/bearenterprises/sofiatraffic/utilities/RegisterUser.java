package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Registration;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by thalv on 19-Dec-16.
 */

public class RegisterUser extends Thread{
    private Context context;
    private Registration registration;
    public RegisterUser(Context context){
        this.context = context;

    }

    @Override
    public void run(){
        SofiaTransportApi sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
        Call<Registration> registrationCall = sofiaTransportApi.registerUser();
        try {
            registration = registrationCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Registration getRegistration() {
        return registration;
    }
}
