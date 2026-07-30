package com.example.crisistech;

import android.net.wifi.p2p.WifiP2pDevice;
import java.util.List;

public interface PeerListUpdateListener {
    void onPeersUpdated(List<WifiP2pDevice> peers);
}