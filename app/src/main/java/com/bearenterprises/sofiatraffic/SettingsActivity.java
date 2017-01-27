package com.bearenterprises.sofiatraffic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.bearenterprises.sofiatraffic.fragments.SettingsFragment;
import com.bearenterprises.sofiatraffic.utilities.Utility;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setTheme(this);
        setContentView(R.layout.activity_settings);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_container, new SettingsFragment())
                .commit();
    }
}
