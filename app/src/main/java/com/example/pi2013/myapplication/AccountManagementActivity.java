package com.example.pi2013.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Scanner;

/**
 * Management of the account
 *
 */
public class AccountManagementActivity extends BaseActivity {
    public static AccountManagementActivity AccountManagementActivity=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountManagementActivity = this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_account_management));

        setContentView(R.layout.activity_account_management);

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
