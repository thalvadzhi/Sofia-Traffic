package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;

/**
 * Created by thalv on 08-Jul-16.
 */
public class Utility {
    public static void toastOnUiThread(String message, Context context){
        final String msg = message;
        final MainActivity m = (MainActivity) context;
            m.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(m, msg, Toast.LENGTH_LONG).show();
                }
            });
    }

    public static void makeSnackbar(String message, MainActivity mainActivity){
        Snackbar
                .make(mainActivity.getCoordinatorLayout(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    public static void setTheme(final AppCompatActivity activity){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String theme = sharedPref.getString(activity.getResources().getString(R.string.key_choose_theme), activity.getResources().getString(R.string.light_theme_value));
        String themeDark = activity.getResources().getString(R.string.dark_theme_value);
        if(theme.equals(themeDark)){
            activity.setTheme(R.style.DarkTheme);
        }else{
            activity.setTheme(R.style.LightTheme);
        }
    }

    public static void changeFragmentSlideIn(int id, Fragment fragment, MainActivity activity){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.station_name_fragment, fragment)
                .commitAllowingStateLoss();

    }
    public static void changeFragment(int id, Fragment fragment, AppCompatActivity activity){
       activity.getSupportFragmentManager().
                beginTransaction().
                replace(id, fragment).
                commitAllowingStateLoss();
    }


    public static void detachFragment(Fragment fragment, AppCompatActivity activity){
        activity.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }
}
