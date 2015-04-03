package com.example.pi2013.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setTitle(getResources().getString(R.string.title_activity_about));
    }


}
