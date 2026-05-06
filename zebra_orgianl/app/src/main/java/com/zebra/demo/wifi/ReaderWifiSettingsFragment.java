package com.zebra.demo.wifi;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.demo.R;
import com.zebra.demo.rfidreader.common.CustomProgressDialog;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.ENUM_WIFI_PROTOCOL_TYPE;
import com.zebra.rfid.api3.ENUM_WIFI_STATE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.WifiProfile;
import com.zebra.rfid.api3.WifiScanData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ReaderWifiSettingsFragment extends Fragment {

    String TAG = "ReaderWifiSettingsFragment";
    RecyclerView rvOtherAvailableNetworks, rvSavedWifiNetworks;
    public static Context mContext;
    AvailableWifiNetworkAdapter availableNetworkAdapter;
    public static SavedWifiNetworksAdapter savedWifiNetworksAdapter;
    List<WifiScanData> results = new ArrayList<>();
    ImageButton btn_scannetworks;
    public static RelativeLayout rl_connectedWiFi;
    public static TextView tv_connected_wifi_name;
    ImageView ivWifiOptions, ivConnectedWifiIcon;
    List<WifiProfile> profilelist = new ArrayList<>();
    private static ReaderWifiSettingsFragment readerWifiSettingsFragment = null;
    private static CustomProgressDialog progressDialog;
    private static boolean isConnectRequest = false;
    private boolean isErrorDialogShowing = false;

    public ReaderWifiSettingsFragment() {
        // Required empty public constructor
    }

    public static ReaderWifiSettingsFragment newInstance() {

        return new ReaderWifiSettingsFragment();
    }

    public static ReaderWifiSettingsFragment getInstance() {
        if (readerWifiSettingsFragment == null)
            readerWifiSettingsFragment = new ReaderWifiSettingsFragment();
        return readerWifiSettingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reader_wifi_settings, container, false);
        rvOtherAvailableNetworks = view.findViewById(R.id.rv_other_available_networks);
        rvSavedWifiNetworks = view.findViewById(R.id.rv_saved_wifi_networks);
        rl_connectedWiFi = view.findViewById(R.id.connected_wifi_network);
        btn_scannetworks = view.findViewById(R.id.btn_scannetworks);
        tv_connected_wifi_name = view.findViewById(R.id.connected_wifi_name);
        ivWifiOptions = view.findViewById(R.id.connected_wifi_options);
        ivConnectedWifiIcon = view.findViewById(R.id.connected_wif_icon);
        mContext = getActivity();
        progressDialog = new CustomProgressDialog(mContext, "Loading...");

//        animation = new RotateAnimation(0.0f, 360.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
//        animation.setRepeatCount(-1);
//        animation.setDuration(2000);

        ivWifiOptions.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(getActivity(), ivWifiOptions);
            menu.inflate(R.menu.wifi_connect_menu);
            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.wifi_disconnect:
                        disconnectWifi();
                        break;
                }
                return false;
            });
            menu.show();
        });
        rl_connectedWiFi.setOnClickListener(v -> new AlertDialog.Builder(requireActivity())
                .setMessage("Do you want to Disconnect?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> disconnectWifi())
                .setNegativeButton(android.R.string.no, null).show());

        try {
            if (RFIDController.mConnectedReader != null) {
                HashMap<String, String> status = new HashMap<>();
                RFIDResults rfidResults = RFIDController.mConnectedReader.Config.wifiGetStatus(status);
                if (rfidResults == RFIDResults.RFID_API_SUCCESS && status.containsKey("wifi")) {
                    Log.d(TAG, " " + status.get("wifi"));
                    if (Objects.equals(status.get("wifi"), "DISABLE")) {
                     //   RFIDController.mConnectedReader.Config.wifiEnable();
                    }
                }
            }
        } catch (InvalidUsageException e) {
            Log.d(TAG, "Returned SDK Exception wifi_getStatus, wifi_enable");
        } catch ( OperationFailureException e) {
            if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }
            Log.d(TAG, "Returned SDK Exception wifi_getStatus, wifi_enable");
        }

        availableNetworkAdapter = new AvailableWifiNetworkAdapter(mContext, results);
        rvOtherAvailableNetworks.setAdapter(availableNetworkAdapter);

        loadSavedData();
        savedWifiNetworksAdapter = new SavedWifiNetworksAdapter(mContext, profilelist);
        rvSavedWifiNetworks.setAdapter(savedWifiNetworksAdapter);


        btn_scannetworks.setOnClickListener(v -> {
            if (RFIDController.mConnectedReader != null) {
                try {
                    results.clear();
                    availableNetworkAdapter.notifyDataSetChanged();
                    RFIDController.mConnectedReader.Config.wifiScan();
                } catch (InvalidUsageException e) {
                    Log.d(TAG, "Returned SDK Exception wifi_scan");
                } catch ( OperationFailureException e){
                    if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                        ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                    }
                    Log.d(TAG, "Returned SDK Exception wifi_scan");
                }
            }else{
                Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            if (RFIDController.mConnectedReader != null) {
                results.clear();
                RFIDController.mConnectedReader.Config.wifiScan();
            }
        }catch (InvalidUsageException e) {
            Log.d(TAG, "Returned SDK Exception wifi_scan");
        } catch ( OperationFailureException e) {
            if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }
            Log.d(TAG, "Returned SDK Exception wifi_scan");
        }

        return view;
    }

    private void addHiddenProfile() {
        if(results.isEmpty()) {
            WifiScanData manualScanData = new WifiScanData();
            manualScanData.setssid("Add Profile Manually");
            ArrayList<ENUM_WIFI_PROTOCOL_TYPE> protocolTypes = new ArrayList<>();
            Collections.addAll(protocolTypes, ENUM_WIFI_PROTOCOL_TYPE.values());
            manualScanData.setProtocol(protocolTypes);
            results.add(0,manualScanData);
        } else {
            if(!"Add Profile Manually".equals(results.get(0).getssid())) {
                WifiScanData manualScanData = new WifiScanData();
                manualScanData.setssid("Add Profile Manually");
                ArrayList<ENUM_WIFI_PROTOCOL_TYPE> protocolTypes = new ArrayList<>();
                Collections.addAll(protocolTypes, ENUM_WIFI_PROTOCOL_TYPE.values());
                manualScanData.setProtocol(protocolTypes);
                results.add(0,manualScanData);
            }
        }
    }

    @Override
    public void onResume() {
        mContext = getActivity();
        super.onResume();
    }

    private void disconnectWifi() {
        if(RFIDController.mConnectedReader != null) {
            try {
                showProgressDialog();
                isConnectRequest = false;
                RFIDController.mConnectedReader.Config.wifiDisconnect();
            } catch (InvalidUsageException e) {
                Toast.makeText(mContext, "Could not disconnect", Toast.LENGTH_SHORT).show();
            } catch ( OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }else{
                    Toast.makeText(mContext, "Could not disconnect", Toast.LENGTH_SHORT).show();
                }

            }
            rl_connectedWiFi.setVisibility(View.GONE);
            loadSavedData();
        }else{
            Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
        }

    }

    private synchronized void loadSavedData() {

        try {
            rl_connectedWiFi.setVisibility(View.GONE);
            if (RFIDController.mConnectedReader != null) {
                profilelist = RFIDController.mConnectedReader.Config.wifiListProfile();
                WifiProfile connectedProfile = null;
                for (WifiProfile profile : profilelist) {
                    if (profile.getstate() != null && profile.getstate().equals(ENUM_WIFI_STATE.STATE_CONNECTED)) {
                        rl_connectedWiFi.setVisibility(View.VISIBLE);
                        tv_connected_wifi_name.setText(profile.getssid());
                        connectedProfile = profile;
                    }
                }
                if(connectedProfile != null) {
                    Log.d(TAG, "Removing connected Profile-"+connectedProfile.getssid()+" from the Saved Profile List");
                    profilelist.remove(connectedProfile);
                }
            }else{
                Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
            }
        } catch (InvalidUsageException e) {
            Log.d(TAG, "Returned SDK Exception wifi_listProfile");
        } catch ( OperationFailureException e) {
            if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }
            Log.d(TAG, "Returned SDK Exception wifi_listProfile");
        }

        if (savedWifiNetworksAdapter != null) {
            savedWifiNetworksAdapter.updateList(profilelist);

        }

    }

    public void ConnectWifi(int pos, List<WifiProfile> savedWifiInfo) {
        if (RFIDController.mConnectedReader != null) {
            try {
                showProgressDialog();
                isConnectRequest = true;
                RFIDController.mConnectedReader.Config.wifiConnectNonRoaming(savedWifiInfo.get(pos).getssid());
            } catch (InvalidUsageException e) {
                Toast.makeText(mContext, "Connection Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Returned SDK Exception wifi_connectNonroaming");
            } catch ( OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }
                Log.d(TAG, "Returned SDK Exception wifi_connectNonroaming");
            }
        }else{
            Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void readWifiScanNotification(String status, String ssid) {
        Log.d(TAG, " readWifiScanNotification " + status);

        requireActivity().runOnUiThread(() -> {
            switch (status) {
                case "ScanStart":
                    showProgressDialog();
                    break;
                case "Connect":
                    loadSavedData();
                    dismissProgressDialog();
                    isConnectRequest = false;
                    break;
                case "ScanStop":
                    addHiddenProfile();
                    loadSavedData();
                    dismissProgressDialog();
                    break;
                case "Disconnect":
                    if(!isConnectRequest){
                        loadSavedData();
                        dismissProgressDialog();
                    }
                    break;
                case "Operation Failed":
                    Toast.makeText(mContext, status + " ssid: " + ssid, Toast.LENGTH_SHORT).show();
                    loadSavedData();
                    dismissProgressDialog();
                    break;
            }
            availableNetworkAdapter.notifyDataSetChanged();
        });
    }

    public void DeleteProfile(int pos, List<WifiProfile> wifiInfo) {
        if (RFIDController.mConnectedReader != null) {
            try {
                RFIDController.mConnectedReader.Config.wifiDeleteProfile(wifiInfo.get(pos).getssid());
            } catch (InvalidUsageException e) {
                Toast.makeText(mContext, "Could not delete selected profile", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Returned SDK Exception wifi_deleteProfile");
            } catch ( OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }
                Toast.makeText(mContext, "Could not delete selected profile", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Returned SDK Exception wifi_deleteProfile");
            }

            loadSavedData();
        } else {
            Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateScanResult(WifiScanData wifiscandata) {
        results.add(wifiscandata);
    }


    public void addProfile(WifiProfile wifiProfile, boolean isWifiPreferred) {
        if(RFIDController.mConnectedReader != null) {
            try {
                RFIDController.mConnectedReader.Config.wifiAddProfile(wifiProfile, isWifiPreferred);
                loadSavedData();
            } catch (InvalidUsageException | OperationFailureException e) {
                if (e instanceof OperationFailureException &&
                        ((OperationFailureException) e).getStatusDescription().equals("RFID_WPA_MAX_SAVED_PROFILE_EXCEEDED")) {
                    Toast.makeText(mContext, "Max Saved Profile Exceeded", Toast.LENGTH_LONG).show();
                }else if (e instanceof OperationFailureException && ((OperationFailureException) e).getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }
                else {
                    Toast.makeText(mContext, "Could not add new profile", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "Returned SDK Exception wifi_addProfile");
            }
        }else{
            Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressDialog() {
        progressDialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(this::dismissProgressDialog, 20 * 1000);
    }

    private void dismissProgressDialog() {
        if (progressDialog != null ) {
            progressDialog.dismiss();
        }
    }

    public void deviceDisconnected() {
        dismissProgressDialog();
    }

}