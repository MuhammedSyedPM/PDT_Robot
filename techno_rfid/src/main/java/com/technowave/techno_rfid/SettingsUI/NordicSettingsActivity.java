package com.technowave.techno_rfid.SettingsUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.technowave.techno_rfid.Beeper;
import com.technowave.techno_rfid.NordicId.AccessoryExtension;
import com.technowave.techno_rfid.NordicId.NurApi;
import com.technowave.techno_rfid.NordicId.NurApiListener;
import com.technowave.techno_rfid.NordicId.NurEventAutotune;
import com.technowave.techno_rfid.NordicId.NurEventClientInfo;
import com.technowave.techno_rfid.NordicId.NurEventDeviceInfo;
import com.technowave.techno_rfid.NordicId.NurEventEpcEnum;
import com.technowave.techno_rfid.NordicId.NurEventFrequencyHop;
import com.technowave.techno_rfid.NordicId.NurEventIOChange;
import com.technowave.techno_rfid.NordicId.NurEventInventory;
import com.technowave.techno_rfid.NordicId.NurEventNxpAlarm;
import com.technowave.techno_rfid.NordicId.NurEventProgrammingProgress;
import com.technowave.techno_rfid.NordicId.NurEventTagTrackingChange;
import com.technowave.techno_rfid.NordicId.NurEventTagTrackingData;
import com.technowave.techno_rfid.NordicId.NurEventTraceTag;
import com.technowave.techno_rfid.NordicId.NurEventTriggeredRead;
import com.technowave.techno_rfid.NordicId.NurRespReaderInfo;
import com.technowave.techno_rfid.NurApi.BleScanner;
import com.technowave.techno_rfid.NurApi.NurApiAutoConnectTransport;
import com.technowave.techno_rfid.NurApi.NurDeviceListActivity;
import com.technowave.techno_rfid.NurApi.NurDeviceSpec;
import com.technowave.techno_rfid.R;
import com.technowave.techno_rfid.SettingsUI.Adapters.ViewPagerAdapter;
import com.technowave.techno_rfid.SettingsUI.Fragments.AntennaTuningFragment;
import com.technowave.techno_rfid.SettingsUI.Fragments.RFIDReaderSettingsFragment;
import com.technowave.techno_rfid.SettingsUI.Fragments.ReaderFragment;

import java.util.ArrayList;

public class NordicSettingsActivity extends AppCompatActivity implements NurApiListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public static final String TAG = "NUR_SAMPLE"; //Can be used for filtering Log's at Logcat
    private final int APP_PERMISSION_REQ_CODE = 41;

    private NurApiAutoConnectTransport hAcTr;
    public NurApi mNurApi;
    private static AccessoryExtension mAccExt; //accessories of reader like barcode scanner, beeper, vibration..

    //Need to keep track connection state with NurApi IsConnected
    private boolean mIsConnected;

    //private Button mConnectButton;
    private TextView mConnectionStatusTextView;

    public NurApi GetNurApi() {
        return mNurApi;
    }

    public AccessoryExtension GetAccessoryExtensionApi() {
        return mAccExt;
    }

    //When connected, this flag is set depending if Accessories like barcode scan, beep etc supported.
    private static boolean mIsAccessorySupported;

    public static boolean IsAccessorySupported() {
        return mIsAccessorySupported;
    }

    //These values will be shown in the UI
    private String mUiConnStatusText;
    private int mUiConnStatusTextColor;
    private String mUiConnButtonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nordic_settings);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        findViewById(R.id.menu).setOnClickListener(v -> {
            connectReader();
        });
        findViewById(R.id.backButton).setOnClickListener(v -> {
            super.onBackPressed();
        });
        // setupViewPager();


        //This app uses portrait orientation only
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        //Init beeper to make some noise at various situations
        Beeper.init(this);
        Beeper.setEnabled(true);

        //Bluetooth LE scanner need to find EXA's near
        BleScanner.init(this);

        mIsConnected = false;

        //Create NurApi handle.
        mNurApi = new NurApi();

        //Accessory extension contains device specific API like barcode read, beep etc..
        //This included in NurApi.jar
        mAccExt = new AccessoryExtension(mNurApi);

        // In this activity, we use mNurApiListener for receiving events
        mNurApi.setListener(this);

        //mConnectButton = (Button)findViewById(R.id.button_connect);
        mConnectionStatusTextView = (TextView) findViewById((R.id.nordicReaderStatusTextView));

        mUiConnStatusText = "Disconnected!";
        mUiConnStatusTextColor = Color.RED;
        mUiConnButtonText = "CONNECT";
        showOnUI();
    }

    private void connectReader() {
        if (mNurApi.isConnected()) {
            hAcTr.dispose();
            hAcTr = null;
        }
        NurDeviceListActivity.startDeviceRequest(this, mNurApi);
        /*else {
            Toast.makeText(this, "Start searching. Make sure device power ON!", Toast.LENGTH_LONG).show();
            NurDeviceListActivity.startDeviceRequest(this, mNurApi);
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NurDeviceListActivity.REQUEST_SELECT_DEVICE: {
                if (data == null || resultCode != NurDeviceListActivity.RESULT_OK)
                    return;

                try {
                    NurDeviceSpec spec = new NurDeviceSpec(data.getStringExtra(NurDeviceListActivity.SPECSTR));

                    if (hAcTr != null) {
                        System.out.println("Dispose transport");
                        hAcTr.dispose();
                    }

                    String strAddress;
                    hAcTr = NurDeviceSpec.createAutoConnectTransport(this, mNurApi, spec);
                    strAddress = spec.getAddress();
                    Log.i(TAG, "Dev selected: code = " + strAddress);
                    hAcTr.setAddress(strAddress);

                    showConnecting();

                    //If you want connect to same device automatically later on, you can save 'strAddress" and use that for connecting at app startup for example.
                    //saveSettings(spec);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void showConnecting() {
        if (hAcTr != null) {
            mUiConnStatusText = "Connecting to " + hAcTr.getAddress();
            mUiConnStatusTextColor = Color.GREEN;
        } else {
            mUiConnStatusText = "Disconnected";
            mUiConnStatusTextColor = Color.RED;
            mUiConnButtonText = "CONNECT";
        }
        showOnUI();
    }

    private void showOnUI() {
        runOnUiThread(() -> {
            mConnectionStatusTextView.setText(mUiConnStatusText);
            mConnectionStatusTextView.setTextColor(mUiConnStatusTextColor);
            //mConnectButton.setText(mUiConnButtonText);
        });
    }

    private void setupViewPager() {
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new RFIDReaderSettingsFragment());
        //fragmentList.add(new ReaderFragment());
        //fragmentList.add(new AntennaTuningFragment());

        viewPager.setAdapter(new ViewPagerAdapter(this, fragmentList));
        //binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        String[] title = new String[]{"RFID"/*, "Reader", "Antenna Tuning"*/};
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(title[position])
        ).attach();
    }

    @Override
    public void logEvent(int p0, String p1) {

    }

    @Override
    public void connectedEvent() {
        //Device is connected.
        // Let's find out is device provided with accessory support (Barcode reader, battery info...) like EXA
        try {
            if (mAccExt.isSupported()) {
                //Yes. Accessories supported
                mIsAccessorySupported = true;
                //Let's take name of device from Accessory api
                mUiConnStatusText = "Connected to " + mAccExt.getConfig().name;
            } else {
                //Accessories not supported. Probably fixed reader.
                mIsAccessorySupported = false;
                NurRespReaderInfo ri = mNurApi.getReaderInfo();
                mUiConnStatusText = "Connected to " + ri.name;
            }
        } catch (Exception ex) {
            mUiConnStatusText = ex.getMessage();
        }

        mIsConnected = true;
        Log.i(TAG, "Connected!");
        Beeper.beep(Beeper.BEEP_100MS);

        mUiConnStatusTextColor = Color.GREEN;
        mUiConnButtonText = "DISCONNECT";
        showOnUI();
        runOnUiThread(this::setupViewPager);
    }

    @Override
    public void disconnectedEvent() {
        mIsConnected = false;
        Log.i(TAG, "Disconnected!");

        runOnUiThread(() -> {
            Toast.makeText(NordicSettingsActivity.this, "Reader disconnected", Toast.LENGTH_SHORT).show();
            showConnecting();
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            // Show smart pair ui
            /*if (!mShowingSmartPair && hAcTr != null) {
                String clsName = hAcTr.getClass().getSimpleName();
                if (clsName.equals("NurApiSmartPairAutoConnect")) {
                    mShowingSmartPair = showSmartPairUI();
                }
            } else {
                mShowingSmartPair = false;
            }*/
        });
    }

    @Override
    public void bootEvent(String p0) {

    }

    @Override
    public void inventoryStreamEvent(NurEventInventory p0) {

    }

    @Override
    public void IOChangeEvent(NurEventIOChange p0) {

    }

    @Override
    public void traceTagEvent(NurEventTraceTag p0) {

    }

    @Override
    public void triggeredReadEvent(NurEventTriggeredRead p0) {

    }

    @Override
    public void frequencyHopEvent(NurEventFrequencyHop p0) {

    }

    @Override
    public void debugMessageEvent(String p0) {

    }

    @Override
    public void inventoryExtendedStreamEvent(NurEventInventory p0) {

    }

    @Override
    public void programmingProgressEvent(NurEventProgrammingProgress p0) {

    }

    @Override
    public void deviceSearchEvent(NurEventDeviceInfo p0) {

    }

    @Override
    public void clientConnectedEvent(NurEventClientInfo p0) {

    }

    @Override
    public void clientDisconnectedEvent(NurEventClientInfo p0) {

    }

    @Override
    public void nxpEasAlarmEvent(NurEventNxpAlarm p0) {

    }

    @Override
    public void epcEnumEvent(NurEventEpcEnum p0) {

    }

    @Override
    public void autotuneEvent(NurEventAutotune p0) {

    }

    @Override
    public void tagTrackingScanEvent(NurEventTagTrackingData p0) {

    }

    @Override
    public void tagTrackingChangeEvent(NurEventTagTrackingChange p0) {

    }


    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
        //Kill connection when app killed
        if (hAcTr != null) {
            hAcTr.onDestroy();
            hAcTr = null;
        }
    }
}