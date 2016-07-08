package com.bearenterprises.sofiatraffic;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import static com.bearenterprises.sofiatraffic.utilities.Utility.toastOnUiThread;
import com.bearenterprises.sofiatraffic.Constants.Constants;
import com.bearenterprises.sofiatraffic.stations.DbHelper;
import com.bearenterprises.sofiatraffic.stations.DbManipulator;

/**
 * Created by thalv on 03-Jul-16.
 */
public class FavouriteSaver {
    public static void save(String code, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        DbManipulator manipulator = new DbManipulator(context);
        Cursor c = manipulator.readRawQuery("SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_CODE + "= ?", new String[]{code});
        if(c != null && c.getCount() > 0){
            c.moveToFirst();
        }else{
            toastOnUiThread("Няма такава спирка", context);
            return;
        }
        String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
        editor.putString(code, stationName);
        editor.commit();
        toastOnUiThread("Запазено в любими!", context);
    }
}
