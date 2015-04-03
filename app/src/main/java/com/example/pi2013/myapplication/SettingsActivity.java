package com.example.pi2013.myapplication;

import android.content.Context;
import android.content.Intent;
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
    private Switch HotSpotSwitch;
    private Switch NotifSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Spinner ChoixLangue = (Spinner) findViewById(R.id.language_choice);
        ChoixLangue.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_choice, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ChoixLangue.setAdapter(adapter);

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
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        WifiSwitch.setChecked(appState.getWifi());
        HotSpotSwitch.setChecked(appState.getHotspot());
        getActionBar().setTitle(getResources().getString(R.string.title_activity_settings));
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (pos==1)
        {
            Toast.makeText(parent.getContext(),
                    R.string.toast_settings_spinner_english, Toast.LENGTH_SHORT)
                .show();
            setLocale("en");
        }
        if (pos==2)
        {
            Toast.makeText(parent.getContext(),
                    R.string.toast_settings_spinner_french, Toast.LENGTH_SHORT)
                    .show();
            setLocale("fr");
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

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

    public void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        if (status && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        else if (!status && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }
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
