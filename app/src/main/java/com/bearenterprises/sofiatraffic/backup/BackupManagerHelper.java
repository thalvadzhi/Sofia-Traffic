package com.bearenterprises.sofiatraffic.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.util.Log;

import com.bearenterprises.sofiatraffic.constants.Constants;

/**
 * Created by thalv on 12-Jul-16.
 */
public class BackupManagerHelper extends BackupAgentHelper {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("WHY WHY WHY", "DIULAILA");
        // A Helper for our Preferences, this name is the same name we use when saving SharedPreferences
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, Constants.SHARED_PREFERENCES_FAVOURITES);
        addHelper(Constants.SHARED_PREFERENCES_HELPER, helper);
    }
}
