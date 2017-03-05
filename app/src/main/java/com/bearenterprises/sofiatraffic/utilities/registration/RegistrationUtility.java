package com.bearenterprises.sofiatraffic.utilities.registration;

import android.content.Context;
import android.content.SharedPreferences;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Registration;
import com.bearenterprises.sofiatraffic.utilities.network.GenerateClient;
import com.google.gson.Gson;

/**
 * Created by thalv on 05-Mar-17.
 */

public class RegistrationUtility {

    /*
        Handles registration in ivkos API
        If already registered just sets the current registration to the one created
        If not registered, registers the user
     */
    public static void handleRegistration(MainActivity mainActivity){
        SharedPreferences sp = mainActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_REGISTRATION, Context.MODE_PRIVATE);
        String registrationString = sp.getString(Constants.REGISTRATION, Constants.SHARED_PREFERENCES_DEFAULT_REGISTRATION);
        if (!registrationString.equals(Constants.SHARED_PREFERENCES_DEFAULT_REGISTRATION)){
            Gson gson = new Gson();
            mainActivity.setRegistration(gson.fromJson(registrationString, Registration.class));
        }else{
            RegisterUser registerUser = new RegisterUser(mainActivity);
            registerUser.start();
            try {
                registerUser.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mainActivity.setRegistration(registerUser.getRegistration());
            SharedPreferences.Editor editor = sp.edit();
            Gson gson = new Gson();
            String registrationStringRepr = gson.toJson(mainActivity.getRegistration());
            editor.putString(Constants.REGISTRATION, registrationStringRepr);
            editor.commit();
        }
    }

    /*
        Remove old registration in IVKOS API and register again
     */
    public static void reRegister(MainActivity mainActivity){
        //move to Utility
        SharedPreferences sp = mainActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_REGISTRATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(Constants.REGISTRATION);
        editor.commit();
        RegistrationUtility.handleRegistration(mainActivity);
        GenerateClient.setRegistration(mainActivity.getRegistration());
    }
}
