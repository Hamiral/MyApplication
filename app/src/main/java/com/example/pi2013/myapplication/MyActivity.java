package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MyActivity extends BaseActivity{
    public final static String EXTRA_MESSAGE = "com.example.pi2013.myapplication.MESSAGE";
    private Switch WifiSwitch;
    public static Activity MyActivity=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MyActivity=this;

        WifiSwitch = (Switch)  findViewById(R.id.switchWiFi);
        WifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                GlobalVariable appState = ((GlobalVariable)getApplicationContext());
                if(isChecked)
                {
                    appState.setWifi(true);
                    toggleWiFi(true);
                    updateAll();
                }
                else
                {
                    appState.setWifi(false);
                    toggleWiFi(false);
                    updateAll();
                }

            }
        });
        updateAll();
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        updateAll();
    }
    @Override
    public void finish()
    {
        super.finish();
        MyActivity=null;
    }

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

    public void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        if (status == true && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else if (status == false && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

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

    public void updateWifiState()
    {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        WifiSwitch = (Switch)  findViewById(R.id.switchWiFi);
        if (wifiManager.isWifiEnabled())
        {
            WifiSwitch.setChecked(true);
        }
        else
        {
            WifiSwitch.setChecked(false);
        }
    }

    public void updateLayoutVisibility()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        LinearLayout layout = (LinearLayout) findViewById(R.id.Login_Layout);
        Button button = (Button) findViewById(R.id.button_dynamic);
        if(!appState.getLogged())
        {
            layout.setVisibility(View.VISIBLE);
            button.setVisibility(View.GONE);
        }
        else if (!appState.getHotspot() && appState.getLogged())
        {
            layout.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }
        else if (appState.getWifi())
        {
            layout.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }
        else
        {
            layout.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }

    }

    public void updateComponents()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        Button button_dynamic = (Button) findViewById(R.id.button_dynamic);
        TextView textView=(TextView) findViewById(R.id.text_accueil);
        if(appState.getLogged() && appState.getHotspot()) {
            button_dynamic.setText(R.string.button_main_dynamic_startBrowsing);
            textView.setText(R.string.textview_main_HotSpotFound);
            textView.setTextColor(Color.GREEN);
        }
        else if (appState.getLogged() && !appState.getHotspot())
        {
            button_dynamic.setText(R.string.button_main_dynamic_FindHotspot);
            textView.setText(R.string.textview_main_NoHotSpotFound);
            textView.setTextColor(Color.RED);
        }
        else
        {
            button_dynamic.setText(R.string.button_main_dynamic_Signin);
            textView.setText(R.string.textview_main_NotSignedIn);
            textView.setTextColor(Color.RED);
        }

    }

    public void updateAll()
    {
        updateComponents();
        updateWifiState();
        updateLayoutVisibility();
        invalidateOptionsMenu();
    }
}