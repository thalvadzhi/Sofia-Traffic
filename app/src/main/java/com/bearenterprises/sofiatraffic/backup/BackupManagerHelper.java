package com.bearenterprises.sofiatraffic.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.util.Log;

import com.bearenterprises.sofiatraffic.constants.Constants;

/**
 * Backup helper for the favourites shared preferences.
 */
public class BackupManagerHelper extends BackupAgentHelper {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, Constants.SHARED_PREFERENCES_FAVOURITES);
        addHelper(Constants.SHARED_PREFERENCES_HELPER, helper);
    }


}
