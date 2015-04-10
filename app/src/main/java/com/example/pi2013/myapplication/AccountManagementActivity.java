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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

/**
 * Management of the account
 *
 */
public class AccountManagementActivity extends BaseActivity {
    private static final String DEBUG_TAG = "Example";
    public static AccountManagementActivity AccountManagementActivity=null;
    public static String URL_link;
    public  JSONObject JSONContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountManagementActivity = this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_account_management));

        setContentView(R.layout.activity_account_management);

        new LoadViewTask().execute();

    }

        private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
            //Before running code in separate thread
            @Override
            protected void onPreExecute() {
                getDefaultGateway();

            }

            //The code to be executed in a background thread.
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONContent = requestWebService(URL_link);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            //after executing the code in the thread
            @Override
            protected void onPostExecute(Void result) {
                TextView textView = (TextView) findViewById(R.id.Textview_id);
                textView.setText(URL_link + JSONContent);
            }
        }

   public static JSONObject requestWebService(String serviceUrl) throws IOException{
        InputStream in = null;
       HttpURLConnection urlConnection = null;
        try {
            // create connection
           URL url = new URL(serviceUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            urlConnection.connect();
            // handle issues
            int statusCode = urlConnection.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + statusCode);

            // create JSON object from content
           in =  urlConnection.getInputStream();
            String input = readIt(in,500);
            JSONObject jsonObject = new JSONObject(input);
            return jsonObject;
            //return jsonObject.getString("authentified");


        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
        } catch (JSONException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    return null;
    }


    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
    public void getDefaultGateway()
    {
        WifiManager networkd = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo details = networkd.getDhcpInfo();
        String gateway =intToIp(details.gateway);
        URL_link="http://"+gateway+"/status";
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
