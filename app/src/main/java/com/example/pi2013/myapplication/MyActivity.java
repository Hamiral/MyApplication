package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
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
    public static Activity MyActivity = null;
    public static String URL_link;
    public static String URL_cmd;
    public JSONObject JSONContent;

    private static final String DEBUG_TAG = "Example";
    private static EditText username;
    private static EditText password;

    private boolean AutomaticConnectionChecked=false;
    private boolean RememberMeChecked=false;
    private boolean mLoginButton;
    private Switch WifiSwitch;
    //Keys to register in the application
    private String PREF_USERNAME = "username";
    private String PREF_PASSWORD = "password";
    private String PREF_REMEMBER = "RememberMe";
    private String PREF_AUTOMATIC = "Automatic";

    Timer myTimer;
    final Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MyActivity = this;
        createListenerforWifiSwitch();

        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                CheckStatus();
            }
        }, 0, 5000);
        updateAll();
    }

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

    final Runnable myRunnable = new Runnable() {
        public void run() {
            GlobalVariable appState = ((GlobalVariable) getApplicationContext());
            if(JSONContent==null){
                updateAll();
                return;
            }

            if(URL_cmd!="/status" || appState.getLogged())
                updateAll();
        }
    };

    private void CheckStatus() {
        URL_cmd="/status";
        new RequestContentTask(URL_cmd).execute();
        myHandler.post(myRunnable);
    }

    public void createListenerforWifiSwitch() {

        WifiSwitch=(Switch) findViewById(R.id.switchWiFi);
        WifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
           /**
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
        }
        else if (!status && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * The button will open the default browser of the device
     * @param view
     */
    public void onClickButtonDynamic(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(browserIntent);
    }

    /**
     * Listener Disconnect button
     * Call RequestContentTask to request the disconnection
     * @param view
     */
    public void onClickButtonDisconnect(View view){
        URL_cmd="/logout";
        new RequestContentTask(URL_cmd).execute();
    }

    /**
     * Listener Login button
     * @param view
     */
    public void onClickLogin(View view){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if(!appState.getWifi()){
            Toast.makeText(getApplicationContext(),getString(R.string.toast_main_impossibletoconnect), Toast.LENGTH_SHORT).show();
            return;
        }

        URL_cmd="/login";
        mLoginButton=true;
        new RequestContentTask(URL_cmd).execute();
    }

    /**
     * Listener RememberMe button
     * Save the boolean RememberMe value in the application
     * @param view
     */
    public void onClickRememberMe(View view){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        this.RememberMeChecked = ((CheckBox) view).isChecked();
        appState.putPrefBool(PREF_REMEMBER,RememberMeChecked,getApplicationContext());
    }

    public void onClickAutomaticConnection(View view){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        appState.putPrefBool(PREF_AUTOMATIC,((CheckBox) view).isChecked(),getApplicationContext());
    }

    /**
     * Called after the request to disconnect has been made
     * Check the result of the request
     */
    public void onDisconnectRequested(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        try {
            if(JSONContent.getString("process").equals("success")) {
                appState.putPrefBool(PREF_AUTOMATIC, false, getApplicationContext());
                updateAutomaticConnection();
                appState.setLogged(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateAll();
    }

    /**
     * Called after the request to check the status has been made
     */
    public void onStatusRequested()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());

        // The variable used to store the username or password may not be initialized (first use of the application for example)
        // Without this check, the application will crash when trying to get an username (getPref(....).isEmpty())
        if(appState.getPref(PREF_USERNAME,getApplicationContext())==null){
            return;
        }

        try {
            //Tries to reconnect if the user had been connected but for some reason has been deconnected
             if(JSONContent.getString("authentified").equals("false")    &&   !appState.getPref(PREF_USERNAME, getApplicationContext()).isEmpty() && AutomaticConnectionChecked ) {
                URL_cmd="/login";
                new RequestContentTask(URL_cmd).execute();
            }
            else if(JSONContent.getString("authentified").equals("false")){
                appState.setLogged(false);
                 AutomaticConnectionChecked=false;
            }
            else if(JSONContent.getString("authentified").equals("true")){
                appState.setLogged(true);
                 AutomaticConnectionChecked=true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called after the request to login has been made (after a check status or a click on the login)
     */
    public void onLoginRequested() {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        rememberMe();
        try {
            if (JSONContent.getString("process").equals("error")) {
                appState.setLogged(false);

                /*The Toast is only visible when the button Log In has been clicked
                The status request may have called the login request and thus, would "spam" the user with the toast without this condition
                */
                if(mLoginButton)
                Toast.makeText(getApplicationContext(),getString(R.string.toast_main_wrongpassword), Toast.LENGTH_SHORT).show();
            }
            else if(JSONContent.getString("process").equals("success")) {
                appState.setLogged(true);
                AutomaticConnectionChecked=true;
                Intent intent = new Intent(this, MyActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(),getString(R.string.toast_main_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLoginButton=false;
    }


    public void rememberMe(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());

        if (RememberMeChecked && !username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            //Remember the username
            appState.putPref(PREF_USERNAME, username.getText().toString(), getApplicationContext());
            //Remember the password
            appState.putPref(PREF_PASSWORD, password.getText().toString(), getApplicationContext());
        }
        else{
            //remove the username that was memorized
            appState.removePref(PREF_USERNAME, getApplicationContext());
            //remove the password that was memorized
            appState.removePref(PREF_PASSWORD,getApplicationContext());
            //remove the value of remember me memorized
            appState.removePref(PREF_REMEMBER, getApplicationContext());
        }
    }

    /**
     * update the WifiSwitch state if changes has been made outside of the activity
     */
    // UPDATE
    public void updateWifiState(){
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        WifiSwitch = (Switch)  findViewById(R.id.switchWiFi);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (wifiManager.isWifiEnabled()){
            appState.setWifi(true);
            WifiSwitch.setChecked(true);
        }
        else{
            appState.setWifi(false);
            appState.setLogged(false);
            WifiSwitch.setChecked(false);
        }
    }

    /**
     * Update the layout
     * What is updated is the layout for the login, the dynamic button and the disconnect button
     */
    public void updateLayoutVisibility(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        LinearLayout login_layout = (LinearLayout) findViewById(R.id.Login_Layout);
        Button button_dynamic = (Button) findViewById(R.id.button_dynamic);
        Button button_disconnect = (Button) findViewById(R.id.button_disconnect);

        if(!appState.getLogged()){
            login_layout.setVisibility(View.VISIBLE);
            button_dynamic.setVisibility(View.GONE);
            button_disconnect.setVisibility(View.GONE);
        }
        else if (appState.getWifi() || appState.getLogged()){
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.VISIBLE);
            button_disconnect.setVisibility(View.VISIBLE);
        }
        else{
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.GONE);
            button_disconnect.setVisibility(View.GONE);
        }

    }

    /**
     * The texts of the activity are updated according to the state of the different global variable
     */
    public void updateTexts(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        Button button_dynamic = (Button) findViewById(R.id.button_dynamic);
        TextView state_textView=(TextView) findViewById(R.id.text_accueil);

        if(appState.getLogged()) {
            button_dynamic.setText(R.string.button_main_dynamic_startBrowsing);
            state_textView.setText(R.string.textview_main_HotSpotFound);
        }
        else{
            button_dynamic.setText(R.string.button_main_dynamic_Signin);
            state_textView.setText(R.string.textview_main_NotSignedIn);
        }

    }

    public void updateRememberMe(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        RememberMeChecked = appState.getPrefBool(PREF_REMEMBER, getApplicationContext());
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_Remember_Me);
        checkBox.setChecked(RememberMeChecked);
        //Fill the TextViews (will be null if not necessary)
        username.setText(appState.getPref(PREF_USERNAME, getApplicationContext()));
        password.setText(appState.getPref(PREF_PASSWORD, getApplicationContext()));
    }

    public void updateAutomaticConnection(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_automatic_connection);
        checkBox.setChecked( appState.getPrefBool(PREF_AUTOMATIC, getApplicationContext()));
    }

    /**
     * Update everything
     */
    public void updateAll() {
        updateTexts();
        updateWifiState();
        updateLayoutVisibility();
        invalidateOptionsMenu();
        updateAutomaticConnection();
        updateRememberMe();
    }

    /**
     * Asynchrone thread to request a status/login/logout
     */
    public class RequestContentTask extends AsyncTask<Void, Integer, Void> {
        String cmd;
        public RequestContentTask(String URL_cmd){
            cmd=URL_cmd;
        }
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            getDefaultGateway();
            JSONContent = null;
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONContent = requestWebService(URL_link,cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            GlobalVariable appState = (GlobalVariable) getApplicationContext();
            if (JSONContent==null && (AutomaticConnectionChecked ||cmd=="/login") ){
                if(cmd=="/login")
                Toast.makeText(getApplicationContext(),getString(R.string.toast_main_impossibletoconnect), Toast.LENGTH_SHORT).show();

                appState.setLogged(false);
                updateAll();
                return;
            }
            else if(JSONContent==null){
                return;
            }
            if (cmd=="/login") {
                onLoginRequested();
            }
            else if(cmd=="/logout") {
                onDisconnectRequested();
            }
            else if(cmd=="/status"){
                onStatusRequested();
            }
            if(cmd!="/status")
            updateAll();
        }
    }

    /**
     * Request to the Login API
     * @param serviceUrl
     * @param cmdUrl
     * @return
     * @throws IOException
     */
    public static JSONObject requestWebService(String serviceUrl, String cmdUrl) throws IOException{
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL url = new URL(serviceUrl + cmdUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            if (cmdUrl=="/status" || cmdUrl=="/logout") {

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

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                urlConnection.connect();

                out = urlConnection.getOutputStream();
                OutputStreamWriter wr= new OutputStreamWriter(out);
                String urlParameters =
                        "username=" + URLEncoder.encode(String.valueOf(username.getText()), "UTF-8") +
                                "&password=" + URLEncoder.encode(String.valueOf(password.getText()), "UTF-8");
                wr.write(urlParameters);
                wr.flush();

                //display what returns the POST request
                int HttpResult =urlConnection.getResponseCode();

                if(HttpResult ==HttpURLConnection.HTTP_OK){

                    in = urlConnection.getInputStream();
                    String input = readIt(in, 500);
                    JSONObject jsonObject = new JSONObject(input);
                    return jsonObject;

                }else{
                    System.out.println(urlConnection.getResponseMessage());
                }
            }


        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {

        } catch (JSONException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * Read the InputStream and convert it to a String
     * @param stream
     * @param len
     * @return
     * @throws IOException
     */
    public static String readIt(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * Get the DefaultGateway
     * The defaultGateway is used to get the http address to communicate with the Login API
     */
    public void getDefaultGateway(){
        WifiManager networkd = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo details = networkd.getDhcpInfo();
        String gateway =intToIp(details.gateway);
        URL_link="http://"+gateway;
    }

    /**
     * Convert the gateway value into an usable address
     * For more information to understand the convertion : http://stackoverflow.com/questions/5387036/programmatically-getting-the-gateway-and-subnet-mask-details
     * @param i
     * @return
     */
    public String intToIp(int i) {
        return (( i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) +  "." +
                ((i >> 16 ) & 0xFF)  + "." +
                ((i >> 24 ) & 0xFF ));
    }

}