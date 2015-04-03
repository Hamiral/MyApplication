package com.example.pi2013.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class AccountManagementActivity extends BaseActivity {
    public static AccountManagementActivity AccountManagementActivity=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);
        AccountManagementActivity=this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_account_management));
    }

    public void onClickDisconnect(View view)
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        appState.setLogged(false);
        Intent intent = new Intent(this,MyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish()
    {
        super.finish();
        AccountManagementActivity=null;
    }
}
