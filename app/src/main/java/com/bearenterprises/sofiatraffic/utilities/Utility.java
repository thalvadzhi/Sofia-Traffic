package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.bearenterprises.sofiatraffic.MainActivity;

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
}
