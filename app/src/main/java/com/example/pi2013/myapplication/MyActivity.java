package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

    //For "Remember me"
    public static final String PREFS_NAME = "PreferencesFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "RememberMe";
    private boolean RememberMeChecked;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MyActivity=this;

        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);

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
        affichageRememberMe();
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
        if (status && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else if (!status && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }


    public void login(View view)
    {

        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
       if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
            //correct password
            appState.setLogged(true);
            Intent intent = new Intent(this, MyActivity.class);
            rememberMe();
            startActivity(intent);
        } else {
            //wrong password
            appState.setLogged(false);
            Toast.makeText(getApplicationContext(),"WrongPassword", Toast.LENGTH_SHORT).show();
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

    // UPDATES

    public void updateWifiState()
    {
        WifiManager wifiManager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
        WifiSwitch = (Switch)  findViewById(R.id.switchWiFi);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (wifiManager.isWifiEnabled() ||appState.getWifi())
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
//            textView.setTextColor(Color.GREEN);
        }
        else if (appState.getLogged() && !appState.getHotspot())
        {
            button_dynamic.setText(R.string.button_main_dynamic_FindHotspot);
            textView.setText(R.string.textview_main_NoHotSpotFound);
//            textView.setTextColor(Color.RED);
        }
        else
        {
            button_dynamic.setText(R.string.button_main_dynamic_Signin);
            textView.setText(R.string.textview_main_NotSignedIn);
//            textView.setTextColor(Color.RED);
        }

    }

    public void updateAll()
    {
        updateComponents();
        updateWifiState();
        updateLayoutVisibility();
        invalidateOptionsMenu();
    }

    //REMEMBER ME

    public void onClickRememberMe(View view)
    {
        this.RememberMeChecked = ((CheckBox) view).isChecked();
        putPrefBool(PREF_REMEMBER,RememberMeChecked,getApplicationContext());
    }

    public void putPref(String key, String value, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void putPrefBool(String key,boolean value, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public String getPref(String key, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public boolean getPrefBool(String key, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public void removePref(String key, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }


    public void rememberMe()
    {
        if (RememberMeChecked)
        {
            if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
            {
                //Remember the username
                putPref(PREF_USERNAME, username.getText().toString(), getApplicationContext());
                //Remember the password
                putPref(PREF_PASSWORD, password.getText().toString(), getApplicationContext());
            }
            else{}
        }
        else
        {
            //remove the username that was memorized
            removePref(PREF_USERNAME, getApplicationContext());
            //remove the password that was memorized
            removePref(PREF_PASSWORD,getApplicationContext());
            //remove the value of remember me memorized
            removePref(PREF_REMEMBER, getApplicationContext());
        }
    }

    public void affichageRememberMe()
    {
        RememberMeChecked = getPrefBool(PREF_REMEMBER, getApplicationContext());
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_Remember_Me);
        checkBox.setChecked(RememberMeChecked);
        //Fill the TextViews (will be null if not necessary)
            username.setText(getPref(PREF_USERNAME, getApplicationContext()));
            password.setText(getPref(PREF_PASSWORD, getApplicationContext()));
    }
}