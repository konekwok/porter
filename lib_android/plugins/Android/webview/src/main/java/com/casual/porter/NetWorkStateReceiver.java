package com.casual.porter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkStateReceiver extends BroadcastReceiver {

    MainActivity m_main;
    public NetWorkStateReceiver(MainActivity mainActivity) {
        this.m_main = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String strNetStatus = "none" ;

        try
        {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        strNetStatus = "connected" ;
                    }
                }
            }
        }
        catch (Exception e) {}
        if(null != this.m_main)
        {
            this.m_main.OnConnectChanged(strNetStatus);
        }
    }
}
