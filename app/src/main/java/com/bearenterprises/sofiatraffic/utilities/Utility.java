package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.bearenterprises.sofiatraffic.MainActivity;
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

    public static void makeSnackbar(String message, CoordinatorLayout coordinatorLayout){
        Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
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
}
