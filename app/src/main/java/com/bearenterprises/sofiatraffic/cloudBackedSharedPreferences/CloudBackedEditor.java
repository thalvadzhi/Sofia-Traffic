package com.bearenterprises.sofiatraffic.cloudBackedSharedPreferences;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by thalv on 12-Jul-16.
 */
public class CloudBackedEditor implements SharedPreferences.Editor {

    private SharedPreferences.Editor editor;
    private BackupManager manager;

    public CloudBackedEditor(SharedPreferences.Editor editor, BackupManager manager) {
        this.editor = editor;
        this.manager = manager;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        editor.putString(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        editor.putLong(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        editor.putFloat(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        editor.remove(key);
        return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
        editor.clear();
        return this;
    }

    @Override
    public boolean commit() {
        boolean commit = editor.commit();
        manager.dataChanged();
        return commit;
    }

    @Override
    public void apply() {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
