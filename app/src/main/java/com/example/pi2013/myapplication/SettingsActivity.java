package com.example.pi2013.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private Switch WifiSwitch;
    public static final String PREFS_NAME = "com.example.pi2013.myapplication.PREFERENCE_FILE_KEY";


    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActionBar().setTitle(getResources().getString(R.string.title_activity_settings));

        initSpinner_ChoixLangue();
        initSwitch_WifiSwitch();

        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        WifiSwitch.setChecked(appState.getWifi());

    }

    /**
     * Called after your activity has been stopped, prior to it being started again.
     */
    @Override
    public void onRestart(){
        super.onRestart();

        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        WifiSwitch = (Switch)  findViewById(R.id.state_wifi);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (wifiManager.isWifiEnabled() || appState.getWifi()){
            WifiSwitch.setChecked(true);
        }
        else {
            WifiSwitch.setChecked(false);
        }
    }

    /**
     * Initialize the Spinner to get all the possible language from the string.xml
     * See the onItemSelected() function below
     */
    public void initSpinner_ChoixLangue(){
        Spinner ChoixLangue = (Spinner) findViewById(R.id.language_choice);
        ChoixLangue.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_choice, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ChoixLangue.setAdapter(adapter);
    }

    /**
     * Listener to the language change spinner
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch(pos){
            case 1 :{
                Toast.makeText(parent.getContext(),R.string.toast_settings_spinner_english, Toast.LENGTH_SHORT).show();
                setLocale("en");
                break;
            }
            case 2 :{
                Toast.makeText(parent.getContext(),R.string.toast_settings_spinner_french, Toast.LENGTH_SHORT).show();
                setLocale("fr");
                break;
            }
        }

    }

    /**
     * Initialize the listener for the Wifi Switch
     */
    public void initSwitch_WifiSwitch()
    {
        WifiSwitch = (Switch)  findViewById(R.id.state_wifi);
        WifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GlobalVariable appState = ((GlobalVariable)getApplicationContext());
                if(isChecked){
                    appState.setWifi(true);
                    toggleWiFi(true);
                }
                else{
                    appState.setWifi(false);
                    toggleWiFi(false);
                }
            }
        });
    }



    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    /**
     * Change the language of the application and register it in the SharedPreferences for the future
     * uses of the application
     * @param lang language
     */
    public void setLocale(String lang) {
        //Set the application's language
        Locale myLocale;
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getBaseContext().getResources().updateConfiguration(conf,
        getBaseContext().getResources().getDisplayMetrics());

        //Close all activities to reset them to the right language
        onConfigurationChanged(conf);

        //Register the default language of the application
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("language", lang);
        editor.commit();
    }

    /**
     * Starts the user agreement activity
     * @param view
     */
    public void start_user_agreement_activity(View view){
        Intent intent = new Intent(this, UserAgreementActivity.class);
        startActivity(intent);
    }

    /**
     * Enable or Disable the wifi state of the device
     * @param status status of the Wi-Fi switch
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
     * End all activities if the language has changed
     * @param newConfig new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
       if(MyActivity.MyActivity!=null){
           MyActivity.MyActivity.finish();
           Intent intent = new Intent(getApplicationContext(), MyActivity.class);
           startActivity(intent);
       }

       if(UserAgreementActivity.UserAgreementActivity!=null){
           UserAgreementActivity.UserAgreementActivity.finish();
       }

       Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
       finish();
       startActivity(intent);
       super.onConfigurationChanged(newConfig);
    }

}
