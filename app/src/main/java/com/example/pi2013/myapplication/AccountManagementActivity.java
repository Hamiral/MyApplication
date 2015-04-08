package com.example.pi2013.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.InetAddress;

/**
 * Management of the account
 *
 */
public class AccountManagementActivity extends BaseActivity {
    public static AccountManagementActivity AccountManagementActivity=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);
        AccountManagementActivity=this;

        getActionBar().setTitle(getResources().getString(R.string.title_activity_account_management));


        WifiManager networkd = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo details = networkd.getDhcpInfo();
        String gateway = "Default Gateway: "+ intToIp(details.gateway);
        TextView textView=(TextView) findViewById(R.id.Textview_id);
        textView.setText(gateway);
    }
    public String intToIp(int i) {

        return (( i & 0xFF) + "." +
               ((i >> 8 ) & 0xFF) +  "." +
                ((i >> 16 ) & 0xFF)  + "." +
                ((i >> 24 ) & 0xFF ));
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
