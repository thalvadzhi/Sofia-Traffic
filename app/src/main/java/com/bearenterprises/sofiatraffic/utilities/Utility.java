package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Stop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thalv on 08-Jul-16.
 */
public class Utility {

    public interface RouteGettingFunction<T, K, R>{
        public R getRoutes(SofiaTransportApi sofiaTransportApi, T t, K k) throws IOException;
    }

    public interface RouteStopsFunction<T>{
        public ArrayList<ArrayList<Stop>> getStops(T routeShowerArguments);
    }


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
        if (message == null || mainActivity == null){
            return;
        }
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
        if(fragment == null || activity == null){
            return;
        }
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.station_name_fragment, fragment)
                .commitAllowingStateLoss();

    }
    public static void changeFragment(int id, Fragment fragment, AppCompatActivity activity){
        if(fragment == null || activity == null){
            return;
        }
       activity.getSupportFragmentManager().
                beginTransaction().
                replace(id, fragment).
                commitAllowingStateLoss();
    }


    public static void detachFragment(Fragment fragment, AppCompatActivity activity){
        if(fragment == null || activity == null){
            return;
        }
        activity.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    public static Bitmap replaceColor(Bitmap src, int[] colorsToReplace, int colorThatWillReplace) {
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int pixel;

        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                int index = y * width + x;
                pixel = pixels[index];
                for(int i = 0; i < colorsToReplace.length; i++){
                    if(pixel != Color.parseColor("#FFFFFF") && pixel != 0){
                        pixels[index] = colorThatWillReplace;
                    }

                }
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public static String getScheduleDayType() throws Exception {
        Calendar cal = Calendar.getInstance();
        if(cal != null){
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek >= 0 && dayOfWeek < 5){
                return Constants.WORKDAY;
            }else if(dayOfWeek == 5){
                return Constants.PRE_NON_WORKING_DAY;
            }else{
                return Constants.NON_WORKING_DAY;
            }
        }else{
            throw new Exception("Couldn't get the current day.");
        }
    }

    /**
     * for sorting purposes
     */
    public static int compareLineNames(Line l1, Line l2){
        String lineAName = l1.getName().replaceAll("[А-ЯA-Z]", "");
        String lineBName = l2.getName().replaceAll("[А-ЯA-Z]", "");

        String[] lineASplit = lineAName.split("-");
        String[] lineBSplit = lineBName.split("-");

        if(lineASplit.length > 0){
            lineAName = lineASplit[0];
        }

        if(lineBSplit.length > 0){
            lineBName = lineBSplit[0];
        }

        if(lineAName.length() == lineBName.length()){
            return lineAName.compareTo(lineBName);
        }else{
            return lineAName.length() - lineBName.length();
        }
    }
}
