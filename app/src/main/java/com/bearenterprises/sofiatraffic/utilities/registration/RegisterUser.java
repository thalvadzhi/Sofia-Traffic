package com.bearenterprises.sofiatraffic.utilities.registration;

import android.content.Context;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.Registration;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;

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
        SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
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
