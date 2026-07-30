package com.example.crisistech;

import android.net.wifi.p2p.WifiP2pInfo;

public interface ConnectionInfoListener {
    void onConnectionInfoAvailable(WifiP2pInfo info);
}