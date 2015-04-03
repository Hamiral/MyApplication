package com.example.pi2013.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class UserAgreementActivity extends ActionBarActivity {
    public static UserAgreementActivity UserAgreementActivity=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        UserAgreementActivity=this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_user_agreement));
    }

    public void finish()
    {
        super.finish();
        UserAgreementActivity=null;
    }
}
