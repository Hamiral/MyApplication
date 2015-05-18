package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tapvalue.beacon.android.sdk.TapvalueSDK;
import com.tapvalue.beacon.android.sdk.TapvalueSDKClient;
import com.tapvalue.beacon.android.sdk.config.TapvalueSDKConfig;
import com.tapvalue.beacon.android.sdk.exception.TapvalueSDKException;
import com.tapvalue.beacon.android.sdk.exception.handler.SDKExceptionHandler;
import com.tapvalue.beacon.android.sdk.model.TapCustomer;

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
 * <br>The layout is changed dynamically according to the different states possible of the application
 * <br>To get more information about the layout display, please check the updatelayout() function or the document
 */
public class MyActivity extends BaseActivity {
    public static Activity MyActivity = null;
    /**
     * Default Gateway URL
     */
    public static String URL_link;

    /**
     * Command to the login API
     */
    public static String URL_cmd;

    /**
     * Result of the request to the login API
     */
    public JSONObject JSONContent;

    private static final String DEBUG_TAG = "Debug";

    /**
     * Edit Text
     * <br>Input Username - E-mail
     */
    private static EditText username;

    /**
     * Edit Text
     * <br>Input Password
     */
    private static EditText password;


    /**
     * Boolean to check if the AutomaticConnection is enabled
     */
    private boolean AutomaticConnectionChecked=false;

    /**
     * Boolean to check if the username and password has to be registered in the application
     */
    private boolean RememberMeChecked=false;

    /**
     * Boolean to remember if the login button has been clicked
     */
    private boolean mLoginButton;

    /**
     * Wifi Switch to enable and disable the wifi
     */
    private Switch WifiSwitch;


    /**
     * Key to register into the SharedPreferences of the application
     */
    private String PREF_USERNAME = "username";

    /**
     * Key to register into the SharedPreferences of the application
     */
    private String PREF_PASSWORD = "password";

    /**
     * Key to register into the SharedPreferences of the application
     */
    private String PREF_REMEMBER = "RememberMe";

    /**
     * Key to register into the SharedPreferences of the application
     */
    private String PREF_AUTOMATIC = "Automatic";

    /**
     * Timer to regularly check the status of the connection with the login API
     */
    Timer myTimer;
    final Handler myHandler = new Handler();

    public Intent ConnectivityServiceIntent;

    private ProgressBar LoadingProgressBar;
    TapvalueSDKClient sdkClient = null;

    private BroadcastReceiver WifiStateChangedReceiver
            = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            GlobalVariable appState =(GlobalVariable) getApplicationContext();
            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE ,
                    WifiManager.WIFI_STATE_UNKNOWN);
            switch(extraWifiState){
                case WifiManager.WIFI_STATE_DISABLED:
                    WifiSwitch.setTextColor(getResources().getColor(R.color.bleuNomosphere));
                    WifiSwitch.setChecked(false);
                    WifiSwitch.setClickable(true);
                    appState.setWifi(false);
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    WifiSwitch.setChecked(false);
                    WifiSwitch.setTextColor(getResources().getColor(R.color.grisPinchard));
                    WifiSwitch.setClickable(false);
                    appState.setWifi(false);
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    WifiSwitch.setChecked(true);
                    WifiSwitch.setTextColor(getResources().getColor(R.color.bleuNomosphere));
                    WifiSwitch.setClickable(true);
                    appState.setWifi(true);
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    WifiSwitch.setChecked(true);
                    WifiSwitch.setTextColor(getResources().getColor(R.color.grisPinchard));
                    WifiSwitch.setClickable(false);
                    appState.setWifi(true);
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:

                    break;
            }

        }};
    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one. Always followed by onStart().
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
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

        this.registerReceiver(this.WifiStateChangedReceiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        initTapValue();
        updateAll();
    }

    /**
     * Called after your activity has been stopped, prior to it being started again. Always followed by onStart()
     */
    @Override
    public void onRestart() {
        super.onRestart();
        this.registerReceiver(this.WifiStateChangedReceiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        updateAll();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(ConnectivityServiceIntent!=null){

            stopService(ConnectivityServiceIntent);
        }

    }


    /**
     * Called when your activity is done and should be closed
     */

    @Override
    public void finish() {
        super.finish();
        MyActivity = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(this.WifiStateChangedReceiver);
        ConnectivityServiceIntent = new Intent(this, ConnectivityService.class);
        this.startService(ConnectivityServiceIntent);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            GlobalVariable appState = ((GlobalVariable) getApplicationContext());
            /*if(JSONContent==null){
                updateAll();
                return;
            }*/

            if(!URL_cmd.equals("status") || appState.getLogged())
                updateAll();
        }
    };

    /**
     * Creates a thread that checks the status of the login API
     */
    private void CheckStatus() {
        URL_cmd="status";
        new RequestContentTask(URL_cmd).execute();
        myHandler.post(myRunnable);
    }

    /**
     * Listener Wi-Fi Switch
     */
    public void createListenerforWifiSwitch() {

        WifiSwitch=(Switch) findViewById(R.id.switchWiFi);
        WifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
           /**
           * if the state of the switch change, toggleWifi will be called to change the wifi of the device
            * and update the layout activity according to the changes
           * @param buttonView The compound button view whose state has changed.
           * @param isChecked The new checked state of buttonView.
           */
            public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){
                GlobalVariable appState = ((GlobalVariable) getApplicationContext());
                if (isChecked) {
                    appState.setWifi(true);
                    toggleWiFi(true);
                } else {
                    appState.setWifi(false);
                    toggleWiFi(false);
                }
            }
        }
    );
}

    /**
     * Change the Wifi state of the device
     * @param status Status of the Wi-Fi
     */
    public void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        //int Wifi=wifiManager.WIFI_STATE_DISABLING;
        if (status && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        else if (!status && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * The button will open the default browser of the device
     * @param view The view that was clicked.
     */
    public void onClickButtonDynamic(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(browserIntent);
    }

    /**
     * Listener Disconnect button
     * <br>Call RequestContentTask to request the disconnection
     * @param view The view that was clicked.
     */
    public void onClickButtonDisconnect(View view){
        URL_cmd="logout";
        new RequestContentTask(URL_cmd).execute();
    }

    /**
     * Listener Login button
     * <br> Call RequestContentTask to request a login
     * @param view The view that was clicked.
     */
    public void onClickLogin(View view){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if(!appState.getWifi()){
            Toast.makeText(getApplicationContext(),getString(R.string.toast_main_impossibletoconnect), Toast.LENGTH_SHORT).show();
            return;
        }

        rememberMe();
        URL_cmd="login";
        mLoginButton=true;
        new RequestContentTask(URL_cmd).execute();
    }

    /**
     * Listener RememberMe button
     * <br>Save the boolean RememberMeChecked value in the application
     * @param view The view that was clicked.
     */
    public void onClickRememberMe(View view){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        this.RememberMeChecked = ((CheckBox) view).isChecked();
        appState.putPrefBool(PREF_REMEMBER,RememberMeChecked,getApplicationContext());
    }

    /**
     * Listener AutomaticConnection button
     * <br>Save the boolean value in the application
     * @param view The view that was clicked.
     */
    public void onClickAutomaticConnection(View view){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        appState.putPrefBool(PREF_AUTOMATIC,((CheckBox) view).isChecked(),getApplicationContext());
    }

    /**
     * Called after the request to disconnect has been made
     * <br>Check the result of the request
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
    public void onStatusRequested(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());

        // The variable used to store the username or password may not be initialized (first use of the application for example)
        // Without this check, the application will crash when trying to get an username (getPref(....).isEmpty())
        if(appState.getPref(PREF_USERNAME,getApplicationContext())==null){
            return;
        }

        try {
            //Tries to reconnect if the user had been connected but for some reason has been deconnected
             if(JSONContent.getString("authentified").equals("false")    &&   !appState.getPref(PREF_USERNAME, getApplicationContext()).isEmpty() && AutomaticConnectionChecked ) {
                URL_cmd="login";
                new RequestContentTask(URL_cmd).execute();
            }
            else if(JSONContent.getString("authentified").equals("true")){
                appState.setLogged(true);
                 AutomaticConnectionChecked=true;
            }
            else{
                 appState.setLogged(false);
                 AutomaticConnectionChecked=false;
             }

        } catch (JSONException e) {
            e.printStackTrace();
            appState.setLogged(false);
            AutomaticConnectionChecked=false;
        }
    }

    /**
     * Called after the request to login has been made (after a check status or a click on the login)
     */
    public void onLoginRequested() {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());

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
                sdkClient.updateCustomer(new TapCustomer.Builder()
                        .email(username.getText().toString())
                        .build());
            }
            else{
                Toast.makeText(getApplicationContext(),getString(R.string.toast_main_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLoginButton=false;
    }

    /**
     * Save the username and the password of the user if the RememberMe checkbox is checked
     */
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
     * Update the WifiSwitch state if changes has been made outside of the activity
     */
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
     * <br>What is updated is the layout for the login, the dynamic button and the disconnect button
     */
    public void updateLayoutVisibility(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        LinearLayout login_layout = (LinearLayout) findViewById(R.id.LoginLayout);
        Button button_dynamic = (Button) findViewById(R.id.button_dynamic);
        Button button_disconnect = (Button) findViewById(R.id.button_disconnect);

        if(!appState.getLogged()){
            login_layout.setVisibility(View.VISIBLE);
            button_dynamic.setVisibility(View.GONE);
            button_disconnect.setVisibility(View.GONE);
        }
        else {
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.VISIBLE);
            button_disconnect.setVisibility(View.VISIBLE);
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
            button_dynamic.setText(R.string.button_main_startBrowsing);
            state_textView.setText(R.string.textview_main_HotSpotFound);
        }
        else{
            state_textView.setText(R.string.textview_main_NotSignedIn);
        }


    }

    /**
     * Updates the Textviews(login and password) using the values stored in preferences PREFS_NAME
     */
    public void updateRememberMe(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        RememberMeChecked = appState.getPrefBool(PREF_REMEMBER, getApplicationContext());
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_Remember_Me);
        checkBox.setChecked(RememberMeChecked);
        //Fill the TextViews (will be null if not necessary)
        username.setText(appState.getPref(PREF_USERNAME, getApplicationContext()));
        password.setText(appState.getPref(PREF_PASSWORD, getApplicationContext()));
    }

    /**
     * Update the AutomaticConnection checkbox using the value stored in preferences PREFS_NAME
     */
    public void updateAutomaticConnection(){
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        AutomaticConnectionChecked = appState.getPrefBool(PREF_AUTOMATIC, getApplicationContext());
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_automatic_connection);
        checkBox.setChecked(AutomaticConnectionChecked);
    }

    /**
     * Updates everything
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
     * Asynchronous thread to request a status/login/logout
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
            LoadingProgressBar = (ProgressBar) findViewById(R.id.loadingprogressBar);
            if((cmd.equals("login")&&AutomaticConnectionChecked) || cmd.equals("logout")) {
                LoadingProgressBar.setVisibility(View.VISIBLE);
            }
        }

        /**
         * The code to be executed in a background thread.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONContent = requestWebService(URL_link,cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After executing the code in the thread
         * @param result Void
         */
        @Override
        protected void onPostExecute(Void result) {
            if(cmd.equals("login") || cmd.equals("logout")) {
                LoadingProgressBar.setVisibility(View.GONE);
            }
            GlobalVariable appState = (GlobalVariable) getApplicationContext();
            if (JSONContent==null && (AutomaticConnectionChecked ||cmd.equals("login")) ){
                if(cmd.equals("login"))
                Toast.makeText(getApplicationContext(),getString(R.string.toast_main_wronghotspot), Toast.LENGTH_SHORT).show();
                AutomaticConnectionChecked = false;
                if(appState.getLogged()) {
                    appState.setLogged(false);
                    updateAll();
                }
                return;
            }
            else if(JSONContent==null ){
                AutomaticConnectionChecked=false;
                if(appState.getLogged()) {
                    appState.setLogged(false);
                    updateAll();
                }
                return;
            }
            switch(cmd)
            {
                case "login" :
                    onLoginRequested();
                    break;
                case"logout" :
                    onDisconnectRequested();
                    break;
                case"status" :
                    onStatusRequested();
            }
            if(!cmd.equals("status") && !AutomaticConnectionChecked)
            updateAll();
        }
    }

    /**
     * Request to the Login API
     * @param serviceUrl Default gateway
     * @param cmdUrl Command : login/status/logout
     * @return JSON returned by the request or null
     * @throws IOException Signals a general, I/O-related error.
     */
    public static JSONObject requestWebService(String serviceUrl, String cmdUrl) throws IOException{
        InputStream in;
        OutputStream out;
        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL url = new URL(serviceUrl + cmdUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(2000);
            if (cmdUrl.equals("status") || cmdUrl.equals("logout")) {

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
            }
            else if(cmdUrl.equals("login")){

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
                    return new JSONObject(input);

                }else{
                    System.out.println(urlConnection.getResponseMessage());
                }
            }


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

    /**
     * Read the InputStream and convert it to a String
     * @param stream Stream received from the API
     * @param len Length, maximum stream length to be read
     * @return String - InputStream converted into a String of length len
     * @throws IOException Signals a general, I/O-related error.
     */
    public static String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
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

    public void initTapValue()
    {

        Long AppID= Long.valueOf(100009);
        Long UserID= Long.valueOf(100055);
        String Token="29c89da7-bcce-40be-82f3-f0c63c838da5";
        TapvalueSDKConfig config = TapvalueSDKConfig.create(this,Token,UserID,AppID);
        try {
            sdkClient = TapvalueSDK.getClient(config);
        } catch (TapvalueSDKException e) {
        }
        sdkClient.start();

        sdkClient.setSDKExceptionHandler(new SDKExceptionHandler() {
            @Override
            public void onException(Exception e) {
                Log.d(DEBUG_TAG, "The response is: " + e);
            }
        });

    }
}