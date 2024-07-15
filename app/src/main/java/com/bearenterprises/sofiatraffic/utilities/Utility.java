package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.bearenterprises.sofiatraffic.restClient.SegmentStop;
import com.bearenterprises.sofiatraffic.restClient.SofiaTrafficApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
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
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by thalv on 08-Jul-16.
 */
public class Utility {

    public interface RouteGettingFunction<T, K, R> {
        public R getRoutes(SofiaTrafficApi sofiaTrafficApi, T t, K k) throws IOException;
    }

    public interface RouteStopsFunction<T> {
        public ArrayList<ArrayList<Stop>> getStops(T routeShowerArguments);
    }


    public static void toastOnUiThread(String message, Context context) {
        final String msg = message;
        final MainActivity m = (MainActivity) context;
        m.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(m, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void makeSnackbar(String message, MainActivity mainActivity) {
        if (message == null || mainActivity == null) {
            return;
        }
        Snackbar
                .make(mainActivity.getCoordinatorLayout(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    public static void setTheme(final AppCompatActivity activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String themeDark = activity.getResources().getString(R.string.dark_theme_value);
        String themeLight = activity.getResources().getString(R.string.light_theme_value);
        String themeAuto = activity.getResources().getString(R.string.auto_theme_value);
        String theme = sharedPref.getString(activity.getResources().getString(R.string.key_choose_theme), themeAuto);

        if (theme.equals(themeDark)) {
            activity.setTheme(R.style.DarkTheme);
        } else if (theme.equals(themeLight)) {
            activity.setTheme(R.style.LightTheme);
        }else if (theme.equals(themeAuto)){
            switch (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    activity.setTheme(R.style.DarkTheme);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    activity.setTheme(R.style.LightTheme);
                    break;
            }
        }
    }

    public static void changeFragmentSlideIn(int id, Fragment fragment, MainActivity activity) {
        if (fragment == null || activity == null) {
            return;
        }
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.station_name_fragment, fragment)
                .commitAllowingStateLoss();

    }

    public static void changeFragment(int id, Fragment fragment, AppCompatActivity activity) {
        if (fragment == null || activity == null) {
            return;
        }
        activity.getSupportFragmentManager().
                beginTransaction().
                replace(id, fragment).
                commitAllowingStateLoss();
    }


    public static void detachFragment(Fragment fragment, AppCompatActivity activity) {
        if (fragment == null || activity == null) {
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
                for (int i = 0; i < colorsToReplace.length; i++) {
                    if (pixel != Color.parseColor("#FFFFFF") && pixel != 0) {
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
        if (cal != null) {
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                return Constants.WORKDAY;
            } else if (dayOfWeek == Calendar.SATURDAY) {
                return Constants.PRE_NON_WORKING_DAY;
            } else {
                return Constants.NON_WORKING_DAY;
            }
        } else {
            throw new Exception("Couldn't get the current day.");
        }
    }

    public static String getNextScheduleDayType(String scheduleDayType) throws Exception {
        if (scheduleDayType.equals(Constants.WORKDAY)) {
            return Constants.PRE_NON_WORKING_DAY;
        } else if (scheduleDayType.equals(Constants.PRE_NON_WORKING_DAY)) {
            return Constants.NON_WORKING_DAY;
        } else if (scheduleDayType.equals(Constants.NON_WORKING_DAY)) {
            return Constants.WORKDAY;
        } else {
            return null;
        }
    }

    /**
     * for sorting purposes
     */
    public static int compareLineNames(Line l1, Line l2) {
        String lineAName = l1.getName();
        String lineBName = l2.getName();

        //handle night lines
        //replace N with padding of zeros so that the night lines appear at the bottom
        lineAName = lineAName.replaceFirst("^N", "000");
        lineBName = lineBName.replaceFirst("^N", "000");

        lineAName = lineAName.replaceAll("[А-ЯA-Z]", "");
        lineBName = lineBName.replaceAll("[А-ЯA-Z]", "");


        String[] lineASplit = lineAName.split("-");
        String[] lineBSplit = lineBName.split("-");

        if (lineASplit.length > 0) {
            lineAName = lineASplit[0];
        }

        if (lineBSplit.length > 0) {
            lineBName = lineBSplit[0];
        }

        if (lineAName.length() == lineBName.length()) {
            return lineAName.compareTo(lineBName);
        } else {
            return lineAName.length() - lineBName.length();
        }
    }

    public static Stop mergeStops(Stop s1, Stop s2) {
        if(s1 == null && s2 == null){
            return null;
        }

        HashSet<Line> allLines;
        if(s1 != null && s1.getLines() != null){
            allLines = new HashSet<>(s1.getLines());
        }else{
            allLines = new HashSet<>();
        }

        if(s2 != null && s2.getLines() != null){
            allLines.addAll(s2.getLines());
        }

        ArrayList<Line> allLinesList = new ArrayList<>(allLines);
        Collections.sort(allLinesList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        if (s1 != null){
            s1.setLines(allLinesList);
            return s1;
        }else{
            s2.setLines(allLinesList);
            return s2;
        }
    }

    public static int newLineTypeToOldLineType(int newLineType){
        return switch (newLineType) {
            case 1 -> 1;
            case 2 -> 0;
            case 4 -> 2;
            default -> newLineType;
        };
    }

//    public static Stop SegmentStopToStop(SegmentStop segmentStop){
//        Stop()
//    }

}

