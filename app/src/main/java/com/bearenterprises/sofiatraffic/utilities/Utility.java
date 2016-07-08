package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
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
}
