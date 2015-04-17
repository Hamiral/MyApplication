package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main activity
 * The layout is changed dynamically according to the different states possible of the application
 * To get more information about the layout display, please check the updatelayout() function or the document
 */
public class MyActivity extends BaseActivity {
    public final static String EXTRA_MESSAGE = "com.example.pi2013.myapplication.MESSAGE";
    private Switch WifiSwitch;
    public static Activity MyActivity = null;

    //For "Remember me"
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "RememberMe";
    private static final String PREF_AUTOMATIC = "Automatic";
    private static final String DEBUG_TAG = "Example";
    private boolean AutomaticConnectionChecked;
    private boolean RememberMeChecked;
    private static EditText username;
    private static EditText password;
    public static String URL_link;
    public JSONObject JSONContent;
    public static String URL_cmd;
    public static String RequestResult;
    public TextView text_debug;
    Timer myTimer;
    public int i=0;
    final Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MyActivity = this;
        createListenerforWifiSwitch();

        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);

        text_debug=(TextView) findViewById(R.id.textview_debug);

        WifiSwitch = (Switch)  findViewById(R.id.switchWiFi);
        WifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                GlobalVariable appState = ((GlobalVariable) getApplicationContext());
                if (isChecked) {
                    appState.setWifi(true);
                    toggleWiFi(true);
                    updateAll();
                } else {
                    appState.setWifi(false);
                    toggleWiFi(false);
                    updateAll();
                }

            }
        });
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                CheckStatus();
            }
        }, 0, 5000);
        updateAll();

    }


    private void CheckStatus() {
        i++;
        URL_cmd="/status";
        new LoadViewTask().execute();
        myHandler.post(myRunnable);
    }

final Runnable myRunnable = new Runnable() {
    public void run() {
        if(JSONContent==null)
        {return;}
        text_debug.setText(i+ JSONContent.toString());
        updateAll();
    }
};
    @Override
    public void onRestart() {
        super.onRestart();
        updateAll();
    }

    @Override
    public void finish() {
        super.finish();
        MyActivity = null;
    }

    public void createListenerforWifiSwitch() {
    WifiSwitch=(Switch) findViewById(R.id.switchWiFi);
    WifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

    {
       /**
       *
       * if the state of the switch change, toggleWifi will be called to change the wifi of the device
        * and update the layout activity according to the changes
       * @param buttonView
       * @param isChecked
       */
        public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){

        GlobalVariable appState = ((GlobalVariable) getApplicationContext());
        if (isChecked) {
            appState.setWifi(true);
            toggleWiFi(true);
            updateAll();
        } else {
            appState.setWifi(false);
            toggleWiFi(false);
            updateAll();
        }

    }
    }

    );
}

    /**
     * Change the Wifi state of the device
     * @param status
     */
    public void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        if (status && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else if (!status && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * The button will either open the default browser of the device or call MapActivity to find a Hotspot
     * @param view
     */
    public void onClickButtonDynamic(View view)
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if(appState.getLogged() && appState.getHotspot()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(browserIntent);
        }
        else if (appState.getLogged() && !appState.getHotspot())
        {
            Intent findHotspotIntent = new Intent(this, MapActivity.class);
            startActivity(findHotspotIntent);
        }
    }

    public void onClickButtonDisconnect(View view)
    {
        URL_cmd="/logout";
        new LoadViewTask().execute();

    }

    public void onDisconnectRequested()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        try {
            if(JSONContent.getString("process").equals("success"))
            appState.setLogged(false);
            appState.setHotspot(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateAll();
        /*if(myTimer!=null)
        {
            myTimer.cancel();
            myTimer=null;
        }*/
    }

    public void onStatusRequested()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        try {
             if(JSONContent.getString("authentified").equals("false") && !appState.getPref(PREF_USERNAME, getApplicationContext()).isEmpty() &&AutomaticConnectionChecked)
            {
                URL_cmd="/login";
                new LoadViewTask().execute();
            }
            else if(JSONContent.getString("authentified").equals("false"))
            {
                appState.setLogged(false);
            }

            else if(JSONContent.getString("authentified").equals("true"))
            {
                appState.setLogged(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Login function
     * @param view
     */
    public void login(View view)
    {
        URL_cmd="/login";
        new LoadViewTask().execute();
    }

    public void onLoginRequested()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (RequestResult.equals("false")) {
            //wrong password
            appState.setLogged(false);
            appState.setHotspot(false);
            Toast.makeText(getApplicationContext(),"WrongPassword or Username", Toast.LENGTH_SHORT).show();
        }
        else if(RequestResult.equals("success")) {
            //correct password
            appState.setLogged(true);
            appState.setHotspot(true);
            Intent intent = new Intent(this, MyActivity.class);
            rememberMe();
            startActivity(intent);

        }
        else
        {
            Toast.makeText(getApplicationContext(),"Vous n'êtes pas sur le bon wifi", Toast.LENGTH_SHORT).show();
        }

        updateAll();
    }

    public void onClickSignUp(View view)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onClickLostMyPswd(View view)
    {
        Intent intent = new Intent(this, LostAccountActivity.class);
        startActivity(intent);
    }

    /**
     * update the WifiSwitch state if changes has been made outside of the activity
     */
    // UPDATE
    public void updateWifiState()
    {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        WifiSwitch = (Switch)  findViewById(R.id.switchWiFi);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (wifiManager.isWifiEnabled())
        {
            appState.setWifi(true);
            WifiSwitch.setChecked(true);
        }
        else
        {
            appState.setWifi(false);
            appState.setLogged(false);
            appState.setHotspot(false);
            WifiSwitch.setChecked(false);
        }
    }

    /**
     * Update the layout
     * What is updated is the layout for the login and the dynamic button
     */
    public void updateLayoutVisibility()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        LinearLayout login_layout = (LinearLayout) findViewById(R.id.Login_Layout);
        Button button_dynamic = (Button) findViewById(R.id.button_dynamic);
        Button button_disconnect = (Button) findViewById(R.id.button_disconnect);
        if(!appState.getLogged())
        {
            login_layout.setVisibility(View.VISIBLE);
            button_dynamic.setVisibility(View.GONE);
            button_disconnect.setVisibility(View.GONE);
        }
        else if (!appState.getHotspot() && appState.getLogged())
        {
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.VISIBLE);
            button_disconnect.setVisibility(View.VISIBLE);
        }
        else if (appState.getWifi())
        {
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.VISIBLE);
            button_disconnect.setVisibility(View.VISIBLE);
        }
        else
        {
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.GONE);
            button_disconnect.setVisibility(View.GONE);
        }

    }

    /**
     * The texts of the activity are updated according to the state of the different global variable
     */
    public void updateComponents()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        Button button_dynamic = (Button) findViewById(R.id.button_dynamic);
        TextView state_textView=(TextView) findViewById(R.id.text_accueil);
        if(appState.getLogged() && appState.getHotspot()) {
            button_dynamic.setText(R.string.button_main_dynamic_startBrowsing);
            state_textView.setText(R.string.textview_main_HotSpotFound);
        }
        else if (appState.getLogged() && !appState.getHotspot())
        {
            button_dynamic.setText(R.string.button_main_dynamic_FindHotspot);
            state_textView.setText(R.string.textview_main_NoHotSpotFound);
        }
        else
        {
            button_dynamic.setText(R.string.button_main_dynamic_Signin);
            state_textView.setText(R.string.textview_main_NotSignedIn);
        }

    }

    /**
     * Update everything
     */
    public void updateAll()
    {
        affichageRememberMe();
        affichageAutomaticConnection();
        updateComponents();
        updateWifiState();
        updateLayoutVisibility();
        invalidateOptionsMenu();
    }

    //REMEMBER ME

    public void onClickRememberMe(View view)
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        this.RememberMeChecked = ((CheckBox) view).isChecked();
        appState.putPrefBool(PREF_REMEMBER,RememberMeChecked,getApplicationContext());
    }

    public void rememberMe()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());

        if (RememberMeChecked)
        {
            if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
            {
                //Remember the username
                appState.putPref(PREF_USERNAME, username.getText().toString(), getApplicationContext());
                //Remember the password
                appState.putPref(PREF_PASSWORD, password.getText().toString(), getApplicationContext());
            }
            else{}
        }
        else
        {
            //remove the username that was memorized
            appState.removePref(PREF_USERNAME, getApplicationContext());
            //remove the password that was memorized
            appState.removePref(PREF_PASSWORD,getApplicationContext());
            //remove the value of remember me memorized
            appState.removePref(PREF_REMEMBER, getApplicationContext());
        }
    }

    public void affichageRememberMe()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        RememberMeChecked = appState.getPrefBool(PREF_REMEMBER, getApplicationContext());
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_Remember_Me);
        checkBox.setChecked(RememberMeChecked);
        //Fill the TextViews (will be null if not necessary)
            username.setText(appState.getPref(PREF_USERNAME, getApplicationContext()));
            password.setText(appState.getPref(PREF_PASSWORD, getApplicationContext()));
    }

    public void affichageAutomaticConnection()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        AutomaticConnectionChecked = appState.getPrefBool(PREF_AUTOMATIC, getApplicationContext());
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_automatic_connection);
        checkBox.setChecked(AutomaticConnectionChecked);
       /* if(AutomaticConnectionChecked)
        {
            if(myTimer==null) {
                myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CheckStatus();
                    }
                }, 0, 5000);
            }
        }
        else
        {
            if(myTimer!=null)
            {
                myTimer.cancel();
                myTimer=null;
            }
        }*/
    }

    public void onClickAutomaticConnection(View view)
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        this.AutomaticConnectionChecked = ((CheckBox) view).isChecked();
        appState.putPrefBool(PREF_AUTOMATIC,AutomaticConnectionChecked,getApplicationContext());
    }


    public class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            getDefaultGateway();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONContent = requestWebService(URL_link,URL_cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            TextView textView = (TextView) findViewById(R.id.text_accueil);
            textView.setText(URL_link +URL_cmd+ JSONContent);
            if (JSONContent==null)
            {
               // Toast.makeText(getApplicationContext(),"Connexion impossible, vérifiez que vous êtes connecté sur le bon wifi", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                if (URL_cmd=="/login") {
                    RequestResult = JSONContent.getString("process");
                    onLoginRequested();
                }
                else if(URL_cmd=="/logout") {
                    RequestResult = JSONContent.getString("process");
                    onDisconnectRequested();
                }
                else if(URL_cmd=="/status"){
                    RequestResult = JSONContent.getString("authentified");
                    onStatusRequested();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateAll();
        }
    }

    public static JSONObject requestWebService(String serviceUrl, String cmdUrl) throws IOException{
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection urlConnection = null;
        try {
            // create connection
            if (cmdUrl=="/status" || cmdUrl=="/logout") {
                URL url = new URL(serviceUrl + cmdUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoInput(true);

                urlConnection.connect();
                // handle issues
                int statusCode = urlConnection.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + statusCode);

                // create JSON object from content
                in = urlConnection.getInputStream();
                String input = readIt(in, 500);
                JSONObject jsonObject = new JSONObject(input);
                return jsonObject;
            }
            else if(cmdUrl=="/login"){
                URL url = new URL(serviceUrl + cmdUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                // urlConnection.setDoInput(true);

                urlConnection.connect();
                // create JSON object from content

              /*  JSONObject cred = new JSONObject();
                cred.put("password", "momota");
                cred.put("username", "lor.alex.f@gmail.com");*/
                out = urlConnection.getOutputStream();
                OutputStreamWriter wr= new OutputStreamWriter(out);
                String urlParameters =
                        "username=" + URLEncoder.encode(String.valueOf(username.getText()), "UTF-8") +
                                "&password=" + URLEncoder.encode(String.valueOf(password.getText()), "UTF-8");
                wr.write(urlParameters);
                wr.flush();

                //display what returns the POST request

                StringBuilder sb = new StringBuilder();

                int HttpResult =urlConnection.getResponseCode();

                if(HttpResult ==HttpURLConnection.HTTP_OK){

                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));

                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    br.close();

                    JSONObject jsonObject= new JSONObject(sb.toString());
                    return jsonObject;

                }else{
                    System.out.println(urlConnection.getResponseMessage());
                }
            }
            //return jsonObject.getString("authentified");


        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {
            String blabla = e.getMessage();


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
        URL_link="http://"+gateway;
    }
    public String intToIp(int i) {

        return (( i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) +  "." +
                ((i >> 16 ) & 0xFF)  + "." +
                ((i >> 24 ) & 0xFF ));
    }

}