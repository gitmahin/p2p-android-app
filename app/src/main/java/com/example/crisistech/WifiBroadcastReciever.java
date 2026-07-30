package com.example.crisistech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

public class WifiBroadcastReciever extends BroadcastReceiver {

    private final MainActivity mainActivity;

    public WifiBroadcastReciever(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Toast.makeText(context,
                    state == WifiP2pManager.WIFI_P2P_STATE_ENABLED ? "Wifi On" : "Wifi Off",
                    Toast.LENGTH_SHORT).show();

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            mainActivity.requestPeers();

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.d("WifiDirect", "CONNECTION_STATE_CHANGE received");
            mainActivity.requestConnectionInfo();

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // no-op for now
        }
    }
}