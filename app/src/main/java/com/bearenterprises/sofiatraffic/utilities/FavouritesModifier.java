package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.cloudBackedSharedPreferences.CloudBackedSharedPreferences;
import com.bearenterprises.sofiatraffic.constants.Constants;

/**
 * Created by thalv on 03-Jul-16.
 */
public class FavouritesModifier {
    public static void save(String code, Context context){
        CoordinatorLayout coordinatorLayout = ((MainActivity)context).getCoordinatorLayout();
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        MainActivity activity = (MainActivity)context;
        CloudBackedSharedPreferences cloudBackedSharedPreferences = new CloudBackedSharedPreferences(preferences, activity.getBackupManager());
        SharedPreferences.Editor cloudBackedEditor = cloudBackedSharedPreferences.edit();

        DbManipulator manipulator = new DbManipulator(context);
        try(Cursor c = manipulator.readRawQuery("SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_CODE + "= ?", new String[]{code})) {
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
            } else {
                Utility.makeSnackbar("Няма такава спирка", coordinatorLayout);
                return;
            }
            String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
            cloudBackedEditor.putString(code, stationName);
            cloudBackedEditor.commit();
            Utility.makeSnackbar("Запазено в любими!", coordinatorLayout);
        }finally {
            manipulator.closeDb();
        }
    }

    public static void remove(String code, Context context){
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        MainActivity activity = (MainActivity)context;
        CloudBackedSharedPreferences cloudBackedSharedPreferences = new CloudBackedSharedPreferences(preferences, activity.getBackupManager());
        SharedPreferences.Editor cloudBackedEditor = cloudBackedSharedPreferences.edit();

        cloudBackedEditor.remove(code);
        cloudBackedEditor.commit();
    }
}
