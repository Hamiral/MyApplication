package com.nomosphere.app.Nomosphere;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Service that checks if the wifi we are connected to is Nomosphere's Wifi
 */
public class ConnectivityService extends Service {

    /**
     * Debug tag for the service
     */
    private static final String DEBUG_TAG ="ConnectivityService : " ;
    /**
     * Default Gateway URL
     */
    private String URL_link;
    /**
     * Result of the request to the login API
     */
    private JSONObject JSONContent;

    /**
     * On activation of the broadcast receiveer, starts a connectivity test.
     */
    private BroadcastReceiver ConnectivityChangedReceiver
            = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadViewTask().execute();

        }};

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter Filter = new IntentFilter();
        Filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        Filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.registerReceiver(this.ConnectivityChangedReceiver,
               Filter);
        // Get the HandlerThread's Looper and use it for our Handler
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job


        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(ConnectivityChangedReceiver);
    }


    /**
     * Handle
     *
     * @throws JSONException
     */
    public void HandleNotification() throws JSONException {
        if(JSONContent==null)
            return;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_icon_client)
                        .setContentTitle(getString(R.string.notification_service_title))
                        .setContentText(getString(R.string.notification_service_content))
                        .setAutoCancel(true);
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, MyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(JSONContent.getString("authentified").equals("false")) {
            nManager.notify(NOTIFICATION_ID, builder.build());
            return;
        }
        nManager.cancel(NOTIFICATION_ID);
    }
    /**
     * Asynchronous thread to request the default language of the application
     */
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            getDefaultGateway();
            JSONContent = null;
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONContent = requestWebService(URL_link,"status");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            try {

                HandleNotification();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Request to the Login API
     * @param serviceUrl Default gateway
     * @param cmdUrl Command : login/status/logout
     * @return JSON returned by the request or null
     * @throws java.io.IOException Signals a general, I/O-related error.
     */
    public JSONObject requestWebService(String serviceUrl, String cmdUrl) throws IOException {
        InputStream in;
        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL url = new URL(serviceUrl + cmdUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(2000);

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoInput(true);

            urlConnection.connect();
            // handle issues
            int statusCode = urlConnection.getResponseCode();
            Log.d(DEBUG_TAG, "HTTP Status Code" +
                    ": " + statusCode);

            // create JSON object from content
            in = urlConnection.getInputStream();
            String input = readIt(in, 500);
            return new JSONObject(input);



        } catch (MalformedURLException e) {
            Log.d(DEBUG_TAG, "MalformedURLExeception : " + e);
        } catch (SocketTimeoutException e) {
            Log.d(DEBUG_TAG, "SocketTimeoutException : " + e);
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "IOException : " + e);
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, "JSONException : " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
    public static String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
    public void getDefaultGateway(){
        WifiManager networkd = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo details = networkd.getDhcpInfo();
        String gateway =intToIp(details.gateway);
        URL_link="http://"+gateway+"/";
    }
    /**
     * Convert the gateway value into an usable address
     * <br>For more information to understand the convertion : http://stackoverflow.com/questions/5387036/programmatically-getting-the-gateway-and-subnet-mask-details
     * @param i Integer to convert into an ip
     * @return String - Ip address converted :x.x.x.x
     */
    public String intToIp(int i) {
        return (( i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) +  "." +
                ((i >> 16 ) & 0xFF)  + "." +
                ((i >> 24 ) & 0xFF ));
    }
}
