package com.example.pi2013.myapplication;

import android.os.Bundle;


/**
 * Use of Google Maps activity
 */

public class MapActivity extends BaseActivity {
    public static MapActivity MapActivity=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapActivity=this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_map));
    }
    @Override
    public void finish()
    {
        super.finish();
        MapActivity=null;
    }
}
