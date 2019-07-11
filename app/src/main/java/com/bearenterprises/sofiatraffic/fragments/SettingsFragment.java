package com.bearenterprises.sofiatraffic.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.appcompat.app.AlertDialog;

import com.bearenterprises.sofiatraffic.R;

public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
//        SUMC has removed the fast method
//        Preference preferenceQueryMethod = findPreference(getString(R.string.key_choose_query_method));
//        preferenceQueryMethod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                MainActivity.setQueryMethod((String)newValue);
//                return true;
//            }
//        });
        Preference preference = findPreference(getString(R.string.key_choose_theme));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String key = preference.getKey();
                if(key.equals(getActivity().getResources().getString(R.string.key_choose_theme))){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Искате ли да рестартирате приложението?")
                            .setMessage("За да влязат в сила промените е нужно приложението да се рестартира.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = getActivity().getBaseContext().getPackageManager()
                                            .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                }
                return true;
            }
        });
    }
}
