package com.example.pi2013.myapplication;

import android.os.Bundle;


public class LostAccountActivity extends BaseActivity {
    public static LostAccountActivity LostAccountActivity=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_account);
        LostAccountActivity=this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_lost_account));
    }
    @Override
    public void finish()
    {
        super.finish();
        LostAccountActivity=null;
    }
}
