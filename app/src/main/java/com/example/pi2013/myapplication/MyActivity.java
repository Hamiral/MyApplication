package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity
 * The layout is changed dynamically according to the different states possible of the application
 * To get more information about the layout display, please check the updatelayout() function or the document
 */
public class MyActivity extends BaseActivity {
    public final static String EXTRA_MESSAGE = "com.example.pi2013.myapplication.MESSAGE";
    private Switch WifiSwitch;
    public static Activity MyActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MyActivity = this;
        createListenerforWifiSwitch();

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


    /**
     * Login function
     * @param view
     */

    public void login(View view) {
        EditText username = (EditText)findViewById(R.id.editText1);
        EditText password = (EditText)findViewById(R.id.editText2);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
            //correct password
            appState.setLogged(true);
            Intent intent = new Intent(this, MyActivity.class);
            startActivity(intent);
        } else {
            //wrong password
            appState.setLogged(false);
            Toast.makeText(getApplicationContext(),"WrongPassword", Toast.LENGTH_SHORT).show();
        }
        updateAll();
    }

    public void onClickRememberMe(View view)
    {
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
    public void updateWifiState()
    {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        WifiSwitch = (Switch)  findViewById(R.id.switchWiFi);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (wifiManager.isWifiEnabled() ||appState.getWifi())
        {
            appState.setWifi(true);
            WifiSwitch.setChecked(true);
        }
        else
        {
            appState.setWifi(false);
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
        if(!appState.getLogged())
        {
            login_layout.setVisibility(View.VISIBLE);
            button_dynamic.setVisibility(View.GONE);
        }
        else if (!appState.getHotspot() && appState.getLogged())
        {
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.VISIBLE);
        }
        else if (appState.getWifi())
        {
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.VISIBLE);
        }
        else
        {
            login_layout.setVisibility(View.GONE);
            button_dynamic.setVisibility(View.GONE);
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
        updateComponents();
        updateWifiState();
        updateLayoutVisibility();
        invalidateOptionsMenu();
    }
}