package com.example.crisistech;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class DevicesFragment extends Fragment implements PeerListUpdateListener {

    ListView devicesListView;
    Button searchActiveDevices;

    private ActivityResultLauncher<String[]> permissionLauncher;

    public DevicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) allGranted = false;
                    }
                    if (allGranted) {
                        startDiscovery();
                    } else {
                        Toast.makeText(requireContext(), "Permissions required to discover devices", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).registerPeerListListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).unregisterPeerListListener(this);
    }

    @Override
    public void onPeersUpdated(List<WifiP2pDevice> peers) {
        Log.d("WifiDirect", "Peers updated, count=" + peers.size());

        if (peers.isEmpty()) {
            Toast.makeText(requireContext(), "No Device found", Toast.LENGTH_SHORT).show();
        }

        String[] deviceNames = new String[peers.size()];
        for (int i = 0; i < peers.size(); i++) {
            deviceNames[i] = peers.get(i).deviceName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext().getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
        devicesListView.setAdapter(adapter);
    }

    private void init(View view) {
        devicesListView = view.findViewById(R.id.device_list_box);
        searchActiveDevices = view.findViewById(R.id.search_active_devices_button);

        searchActiveDevices.setOnClickListener(v -> {
            Log.d("WifiDirect", "Search button clicked");
            if (hasRequiredPermissions()) {
                Log.d("WifiDirect", "Permissions OK, starting discovery");
                startDiscovery();
            } else {
                Log.d("WifiDirect", "Missing permissions, requesting");
                requestRequiredPermissions();
            }
        });
    }

    private boolean hasRequiredPermissions() {
        boolean fineLocation = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean nearbyWifi = ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED;
            return fineLocation && nearbyWifi;
        }
        return fineLocation;
    }

    private void requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
            });
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        }
    }

    private void startDiscovery() {
        MainActivity activity = (MainActivity) requireActivity();
        WifiP2pManager manager = activity.getWifiP2pManager();
        WifiP2pManager.Channel channel = activity.getWifiP2pChannel();

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reason) {
                String reasonStr;
                switch (reason) {
                    case WifiP2pManager.P2P_UNSUPPORTED: reasonStr = "P2P_UNSUPPORTED"; break;
                    case WifiP2pManager.ERROR: reasonStr = "ERROR"; break;
                    case WifiP2pManager.BUSY: reasonStr = "BUSY"; break;
                    default: reasonStr = "UNKNOWN:" + reason;
                }
                Log.e("WifiDirect", "discoverPeers FAILED: " + reasonStr);
                searchActiveDevices.setText("Searching Failed");
            }

            @Override
            public void onSuccess() {
                Log.d("WifiDirect", "discoverPeers onSuccess - discovery initiated");
                searchActiveDevices.setText("Searching...");
            }
        });
    }
}