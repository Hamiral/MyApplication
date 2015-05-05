package com.example.pi2013.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by pi2013 on 04/05/2015.
 */
public class NetWatcherBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //here, check that the network connection is available. If yes, start your service. If not, stop your service.
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                if (wifiManager.isWifiEnabled()) {
                    //start service
                    Intent ServiceIntent = new Intent(context, CheckConnectivityService.class);
                    context.startService(ServiceIntent);
                }
                else {
                    //stop service
                    Intent ServiceIntent = new Intent(context, CheckConnectivityService.class);
                    context.stopService(ServiceIntent);
                }
            }
    }
}
