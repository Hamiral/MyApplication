package com.example.pi2013.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private Switch HotSpotSwitch;
    private Switch NotifSwitch;
    public static final String PREFS_NAME = "com.example.pi2013.myapplication.PREFERENCE_FILE_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActionBar().setTitle(getResources().getString(R.string.title_activity_settings));

        initSpinner_ChoixLangue();
        initSwitch_HotSpotSwitch();
        initSwitch_WifiSwitch();

        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        WifiSwitch.setChecked(appState.getWifi());
        HotSpotSwitch.setChecked(appState.getHotspot());

    }
    @Override
    public void onRestart()
    {
        super.onRestart();

        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        WifiSwitch = (Switch)  findViewById(R.id.state_wifi);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (wifiManager.isWifiEnabled() || appState.getWifi())
        {
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
    public void initSpinner_ChoixLangue()
    {
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
        switch(pos)
        {
            case 1 :
            {
                Toast.makeText(parent.getContext(),
                        R.string.toast_settings_spinner_english, Toast.LENGTH_SHORT)
                        .show();
                setLocale("en");
            }
            case 2 :
            {
                Toast.makeText(parent.getContext(),
                        R.string.toast_settings_spinner_french, Toast.LENGTH_SHORT)
                        .show();
                setLocale("fr");
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
                if(isChecked)
                {
                    appState.setWifi(true);
                    toggleWiFi(true);
                    Toast.makeText(getApplicationContext(),R.string.toast_settings_wifi_enabled, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    appState.setWifi(false);
                    toggleWiFi(false);
                    Toast.makeText(getApplicationContext(), R.string.toast_settings_wifi_disabled, Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    /**
     * Initialize the listener for the HotSpot Switch
     */
    public void initSwitch_HotSpotSwitch()
    {

        HotSpotSwitch = (Switch)  findViewById(R.id.Hotspot_switch);
        HotSpotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                GlobalVariable appState = ((GlobalVariable)getApplicationContext());
                if(isChecked)
                {
                    appState.setHotspot(true);
                    Toast.makeText(getApplicationContext(), R.string.toast_settings_hotspot_enabled, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    appState.setHotspot(false);
                    Toast.makeText(getApplicationContext(), R.string.toast_settings_hotspot_disabled, Toast.LENGTH_SHORT).show();
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
     * @param lang
     */
    public void setLocale(String lang) {
        Locale myLocale;
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getBaseContext().getResources().updateConfiguration(conf,
        getBaseContext().getResources().getDisplayMetrics());
        onConfigurationChanged(conf);

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("language", lang);
        editor.commit();
    }

    public void start_user_agreement_activity(View view)
    {
        Intent intent = new Intent(this, UserAgreementActivity.class);
        startActivity(intent);
    }
    public void start_about_activity(View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Enable or Disable the wifi state of the device
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
     * End all activities if the language has changed
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
       if(AccountManagementActivity.AccountManagementActivity!=null)
       {
           AccountManagementActivity.AccountManagementActivity.finish();
       }

       if(LostAccountActivity.LostAccountActivity!=null)
       {
           LostAccountActivity.LostAccountActivity.finish();
       }

       if(MyActivity.MyActivity!=null)
       {
           MyActivity.MyActivity.finish();
       }

       if(MapActivity.MapActivity!=null)
       {
           MapActivity.MapActivity.finish();
       }

       if(SignUpActivity.SignUpActivity!=null)
       {
           SignUpActivity.SignUpActivity.finish();
       }

       if(UserAgreementActivity.UserAgreementActivity!=null)
       {
           UserAgreementActivity.UserAgreementActivity.finish();
       }
       Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
       finish();
       startActivity(intent);
       super.onConfigurationChanged(newConfig);
    }


}
