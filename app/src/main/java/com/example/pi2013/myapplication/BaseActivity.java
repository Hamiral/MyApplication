package com.example.pi2013.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;


public class  BaseActivity extends ActionBarActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if(appState.getLogged())
        {
            inflater.inflate(R.menu.main_activity_actions_logged, menu);
        }
        else
        {
            inflater.inflate(R.menu.main_activity_actions_unlogged, menu);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_ID:
                openID();
                return true;
            case R.id.action_navigate:
                openNavigate();
                return true;
            case R.id.action_MAPS:
                openMAPS();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openMAPS() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void openID() {
        Intent intent = new Intent(this, AccountManagementActivity.class);
        startActivity(intent);
    }

    private void openNavigate() {
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void onRestart()
    {
        super.onRestart();
        invalidateOptionsMenu();
    }


}



