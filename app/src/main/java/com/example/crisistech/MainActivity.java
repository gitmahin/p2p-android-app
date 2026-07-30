package com.example.crisistech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mainDrawableMenu;
    Button mainMenuOpenButton, mainMenuCloseButton;
    NavigationView mainNavigationView;

    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pChannel;

    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    private PeerListUpdateListener activePeerListener;
    private ConnectionInfoListener activeConnectionListener;

    private final WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Log.d("WifiDirect", "onPeersAvailable, raw count=" + peers.getDeviceList().size());
            List<WifiP2pDevice> deviceList = new ArrayList<>(peers.getDeviceList());
            if (activePeerListener != null) {
                activePeerListener.onPeersUpdated(deviceList);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.init();
        this.mainMenuManager();
        this.mainNavigationViewManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(this, broadcastReceiver, intentFilter,
                    ContextCompat.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void registerPeerListListener(PeerListUpdateListener listener) {
        this.activePeerListener = listener;
    }

    public void unregisterPeerListListener(PeerListUpdateListener listener) {
        if (this.activePeerListener == listener) this.activePeerListener = null;
    }

    public void registerConnectionInfoListener(ConnectionInfoListener listener) {
        this.activeConnectionListener = listener;
    }

    public void unregisterConnectionInfoListener(ConnectionInfoListener listener) {
        if (this.activeConnectionListener == listener) this.activeConnectionListener = null;
    }

    /** Called by the broadcast receiver when peers change. */
    void requestPeers() {
        if (wifiP2pManager != null) {
            wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
        }
    }

    /** Called by the broadcast receiver when connection state changes. */
    void requestConnectionInfo() {
        if (wifiP2pManager == null) return;
        wifiP2pManager.requestConnectionInfo(wifiP2pChannel, info -> {
            Log.d("WifiDirect", "connectionInfo: groupFormed=" + info.groupFormed
                    + " isGroupOwner=" + info.isGroupOwner);
            if (activeConnectionListener != null) {
                activeConnectionListener.onConnectionInfoAvailable(info);
            }
        });
    }

    /** Used by DevicesFragment to initiate a connection to a chosen peer. */
    public void connectToDevice(WifiP2pDevice device) {
        android.net.wifi.p2p.WifiP2pConfig config = new android.net.wifi.p2p.WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        wifiP2pManager.connect(wifiP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("WifiDirect", "connect() onSuccess - waiting for connection broadcast");
            }

            @Override
            public void onFailure(int reason) {
                Log.e("WifiDirect", "connect() FAILED: " + reason);
                Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public WifiP2pManager getWifiP2pManager() {
        return wifiP2pManager;
    }

    public WifiP2pManager.Channel getWifiP2pChannel() {
        return wifiP2pChannel;
    }

    private void init() {
        mainDrawableMenu = findViewById(R.id.mainMenuDrawerLayout);
        mainMenuOpenButton = findViewById(R.id.menu_open_button);
        mainNavigationView = findViewById(R.id.main_navigation_view);

        View headerView = mainNavigationView.getHeaderView(0);
        mainMenuCloseButton = headerView.findViewById(R.id.main_menu_close_button);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiP2pChannel = wifiP2pManager.initialize(this, getMainLooper(), () ->
                Log.e("WifiDirect", "Channel disconnected!"));

        broadcastReceiver = new WifiBroadcastReciever(this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void mainMenuManager() {
        mainMenuOpenButton.setOnClickListener(v -> mainDrawableMenu.open());
        mainMenuCloseButton.setOnClickListener(v -> mainDrawableMenu.close());
    }

    private void mainNavigationViewManager() {
        mainNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Fragment fragment = new HomeFragment();

                if (itemId == R.id.nav_home) fragment = new HomeFragment();
                if (itemId == R.id.nav_profile) fragment = new ProfileFragment();
                if (itemId == R.id.nav_active_devices) fragment = new DevicesFragment();

                if (itemId == R.id.nav_terms_conditions) {
                    Toast.makeText(MainActivity.this, "Terms & Conditions", Toast.LENGTH_SHORT).show();
                }
                if (itemId == R.id.nav_privacy_policy) {
                    Toast.makeText(MainActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();

                mainDrawableMenu.close();
                return true;
            }
        });
    }
}