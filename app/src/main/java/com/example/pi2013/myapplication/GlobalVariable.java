package com.example.pi2013.myapplication;

import android.app.Activity;
import android.app.Application;

/**
 * Created by pi2013 on 26/03/2015.
 */
public class GlobalVariable extends Application {
    private boolean wifi=false;
    private boolean LanguageChanged=false;
    private boolean Hotspot=false;
    private boolean Logged=false;
    private MyActivity myActivity;


    public boolean getLanguageChanged() {
        return LanguageChanged;
    }

    public void setLanguageChanged(boolean languageChanged) {
        LanguageChanged = languageChanged;
    }

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

}
