package com.example.pi2013.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String message = "Settings";
        setContentView(R.layout.activity_settings);
    }


}
