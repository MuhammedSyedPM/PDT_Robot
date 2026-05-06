package com.zebra.demo.rfidreader.settings;


import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.WIFI_MAIN_PAGE;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.R;
import com.zebra.demo.application.Application;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import com.zebra.rfid.api3.ENUM_WIFI_BAND;


public class WifiFragment extends BackPressedFragment {
    Context context;
    private CheckBox checkBoxWifi;
    private CheckBox checkbox24Gz;
    private CheckBox checkbox5GzNoDfs;
    private CheckBox checkbox5GzDfs;
    private ProgressDialog progressDialog;
    private ListView lvStatus;
    private static final String TAG = "WifiFragment";
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    HashMap<String, String> status = new HashMap<>();
    AtomicReference<RFIDResults> rfidResults = new AtomicReference<>();
    ArrayList<String> wifiStatusArray = new ArrayList<>();
    WifiStatusAdapter wifiStatusAdapter;

    private WifiFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WifiFragment.
     */
    public static WifiFragment newInstance() {
        return new WifiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootview = inflater.inflate(R.layout.fragment_wifi, container, false);

        context = rootview.getContext();
        checkBoxWifi = rootview.findViewById(R.id.checkboxwifi);
        checkbox24Gz = rootview.findViewById(R.id.checkbox24Gz);
        checkbox5GzNoDfs = rootview.findViewById(R.id.checkbox5GzNoDfs);
        checkbox5GzDfs = rootview.findViewById(R.id.checkbox5GzDfs);
        lvStatus = rootview.findViewById(R.id.lv_status);

        checkBoxWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (RFIDController.mConnectedReader != null) {
                    try {
                        if (isChecked) {
                            RFIDController.mConnectedReader.Config.wifiEnable();
                        } else {
                            RFIDController.mConnectedReader.Config.wifiDisable();
                        }
                    } catch (InvalidUsageException e) {
                        if (e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }catch ( OperationFailureException e) {
                        if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                        }
                        if (e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }
                    updateUI();
                } else {
                    Toast.makeText(getActivity(), "Reader not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //  updateUI();
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (RFIDController.mConnectedReader != null) {
//            HashMap<String, String> status = new HashMap<>();
//            try {
//                RFIDResults rfidResults = RFIDController.mConnectedReader.Config.wifiGetStatus(status);
//                if (rfidResults == RFIDResults.RFID_API_SUCCESS
//                        && Objects.equals(status.get("wifi"), "ENABLE")) {
//                    checkBoxWifi.setChecked(true);
//                } else {
//                    updateUI();
//                }
//            } catch (InvalidUsageException | OperationFailureException e) {
//                e.printStackTrace();
//            }
            updateUI();
        }
    }

    @Override
    public void onBackPressed() {
        if (saveWifiChannelband()== RFIDResults.RFID_API_SUCCESS){
            Toast.makeText(getActivity(), getResources().getString(R.string.status_success_message), Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getActivity(), getResources().getString(R.string.status_failure_message), Toast.LENGTH_SHORT).show();
        }
        if (getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if (getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(WIFI_MAIN_PAGE);
        }
    }
    public RFIDResults saveWifiChannelband(){
        RFIDResults result = null;
        int enumValue = 0;

        if (checkbox24Gz.isChecked()) {
            enumValue |= ENUM_WIFI_BAND.B2_4GHz.getEnumValue();
        }
        if (checkbox5GzNoDfs.isChecked()) {
            enumValue |= ENUM_WIFI_BAND.B5_GHzNONDFS.getEnumValue();
        }
        if (checkbox5GzDfs.isChecked()) {
            enumValue |= ENUM_WIFI_BAND.B5_GHzDFS.getEnumValue();
        }
        try{
            result = RFIDController.mConnectedReader.Config.enableWifiChannelBand(enumValue);

        } catch (InvalidUsageException e) {
            if (e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }catch ( OperationFailureException e) {
            if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }
            if (e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }
        return result;
    }

    private void updateUI() {
        executor.execute(() -> {
            if (!RFIDController.mIsInventoryRunning && !RFIDController.isLocatingTag && !Application.mIsMultiTagLocatingRunning) {
                try {
                    rfidResults.set(RFIDController.mConnectedReader.Config.wifiGetStatus(status));
                } catch (InvalidUsageException e) {
                    if (e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }catch ( OperationFailureException e) {
                    if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                            ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                    }
                    if (e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }
            }
            handler.post(() -> {
                wifiStatusArray.clear();
                if (RFIDController.mIsInventoryRunning) {
                    wifiStatusArray.add("Wifi Status cannot be obtained  : Inventory operation is running");
                }
                else if (RFIDController.isLocatingTag || Application.mIsMultiTagLocatingRunning){
                    wifiStatusArray.add("Wifi Status cannot be obtained  : TagLocate is running");
                }
                else {
                    if (rfidResults.get() == RFIDResults.RFID_API_SUCCESS) {
                        for (String key : new ArrayList<>(status.keySet())) {

                            if (key.equals("PreferredSSID") || key.equals("2_4GHzAllowedChannelListMask") ||
                                    key.equals("5GHzNonDFSAllowedChannelListMask") || key.equals("5GHzDFSAllowedChannelListMask")
                                    || key.equals("ChannelListMask") ) {

                                continue;
                            }
                            if (key.equals("wifi")) {
                                String keys = status.get(key);
                                key = "Wi-Fi";
                                wifiStatusArray.add(key + "  " + ": " + keys);

                            } else if (key.equals("WifiRegion")) {
                                String keys = status.get(key);
                                key = "Wi-Fi Region";
                                wifiStatusArray.add(key + "  " + ": " + keys);

                            }else if (key.equals("ChannelListEnum")) {
                                String keys = status.get(key);
                                switch (keys) {
                                    case "1":
                                        checkbox24Gz.setChecked(true);
                                        break;
                                    case "2":
                                        checkbox5GzNoDfs.setChecked(true);
                                        break;
                                    case "3":
                                        checkbox24Gz.setChecked(true);
                                        checkbox5GzNoDfs.setChecked(true);
                                        break;
                                    case "4":
                                        checkbox5GzDfs.setChecked(true);
                                        break;
                                    case "5":
                                        checkbox5GzDfs.setChecked(true);
                                        checkbox24Gz.setChecked(true);
                                        break;
                                    case "6":
                                        checkbox5GzDfs.setChecked(true);
                                        checkbox5GzNoDfs.setChecked(true);
                                        break;
                                    case "7":
                                        checkbox24Gz.setChecked(true);
                                        checkbox5GzNoDfs.setChecked(true);
                                        checkbox5GzDfs.setChecked(true);
                                        break;
                                }

                            } else {
                                wifiStatusArray.add(key + "  " + ": " + status.get(key));
                            }
                        }
                    }
                }

                if (wifiStatusAdapter == null) {
                    wifiStatusAdapter = new WifiStatusAdapter(getActivity(), wifiStatusArray);
                    lvStatus.setAdapter(wifiStatusAdapter);
                } else {
                    wifiStatusAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    public void readWifiScanNotification(String scanStatus, String ssid) {

        Log.d(TAG, " readWifiScanNotification " + scanStatus);

        requireActivity().runOnUiThread(() -> {
            switch (scanStatus) {
                case "ScanStart":
                case "ScanStop":
                    break;
                case "Connect":
                case "Disconnect":
                    updateUI();
                    break;
            }
        });
    }
}
