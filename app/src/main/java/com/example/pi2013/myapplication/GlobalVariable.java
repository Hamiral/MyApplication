package com.example.pi2013.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * GlobalVariable
 */
public class GlobalVariable extends Application {
    private boolean wifi=false;
    private boolean Logged=false;
    public static final String PREFS_NAME = "com.example.pi2013.myapplication.PREFERENCE_FILE_KEY";

    /**
     * Gets the value of the global variable wifi
     * @return The value of wifi
     */
    public boolean getWifi() {
        return wifi;
    }

    /**
     * Sets the value of the global variable wifi
     * @param wifi The value of the Wifi State
     */
    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    /**
     * Returns The value of Logged global variable
     * @return The value of Logged
     */
    public boolean getLogged() {
        return Logged;
    }

    /**
     * Sets the value of Logged gloabl variable
     * @param logged The value we are setting Logged to
     */
    public void setLogged(boolean logged) {
        Logged = logged;
    }

    /**
     * Puts a string  in the preferences PREFS_NAME file, under the key Key
     * @param Key Entry in witch we want to store/add a string
     * @param value The string we want to store/add
     * @param context The context of the application
     */
    public void putPref(String Key, String value, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Key, value);
        editor.commit();
    }

    /**
     * Puts a boolean in the preferences PREFS_NAME file, under the key Key
     * @param Key Entry in witch we want to store/add a boolean
     * @param value The boolean we want to store
     * @param context The context of the application
     */
    public void putPrefBool(String Key,boolean value, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Key,value);
        editor.commit();
    }

    /**
     * Gets the String stored in preferences PREFS_NAME under the key Key
     * @param Key Name of the key under which is stored the string we want to get
     * @param context The context of the application
     * @return The String stored in preferences PREFS_NAME under the key Key
     */
    public String getPref(String Key, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(Key, null);
    }

    /**
     * Gets the Boolean stored in preferences PREFS_NAME under the key Key
     * @param Key Name of the key under which is stored the boolean we want to get
     * @param context The context of the application
     * @return The boolean stored in preferences PREFS_NAME under the key Key
     */
    public boolean getPrefBool(String Key, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(Key, false);
    }

    /**
     * Removes everything that has been stored under the key Key in preferences PREFS_NAME
     * @param Key Name of teh key under which is stored what we erase
     * @param context The context of the application
     */
    public void removePref(String Key, Context context)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Key);
        editor.commit();
    }

}

