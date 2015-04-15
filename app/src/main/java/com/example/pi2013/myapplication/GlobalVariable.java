package com.example.pi2013.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pi2013 on 26/03/2015.
 */
public class GlobalVariable extends Application {
    private boolean wifi=false;
    private boolean Hotspot=false;
    private boolean Logged=false;
    public static final String PREFS_NAME = "com.example.pi2013.myapplication.PREFERENCE_FILE_KEY";

    public boolean getWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean getHotspot() {
        return Hotspot;
    }

    public void setHotspot(boolean hotspot) {
        Hotspot = hotspot;
    }

    public boolean getLogged() {
        return Logged;
    }

    public void setLogged(boolean logged) {
        Logged = logged;
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



}

