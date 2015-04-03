package com.example.pi2013.myapplication;

import android.os.Bundle;


public class SignUpActivity extends BaseActivity {
    public static SignUpActivity SignUpActivity=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SignUpActivity=this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_sign_up));
    }

    public void finish()
    {
        super.finish();
        SignUpActivity=null;
    }
}
