package com.zebra.demo.wifi;


import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.ENUM_WIFI_PROTOCOL_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.WifiProfile;
import com.zebra.rfid.api3.WifiScanData;
import com.zebra.rfid.api3.WifiSecureConfig;

import java.util.ArrayList;
import java.util.List;

public class AvailableWifiNetworkAdapter extends RecyclerView.Adapter<AvailableWifiNetworkAdapter.ViewHolder> {

    String TAG = "AvailableWifiNetworkAdapter";
    Context mContext;
    List<WifiScanData> results;
    private boolean isErrorDialogShowing = false;

    public AvailableWifiNetworkAdapter(Context mContext, List<WifiScanData> results) {
        this.mContext = mContext;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.other_wifi_networks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WifiScanData scanResult = results.get(position);

        if (scanResult == null || isEmpty(scanResult.getssid())) {
            Log.w(TAG, "Scan result or SSID is null/empty at position: " + position);
            return;
        }
        holder.tv_connectedWifiName.setText(scanResult.getssid());
        if("Add Profile Manually".equals(scanResult.getssid())) {
            holder.iv_connectedWifiIcon.setVisibility(View.GONE);
            holder.iv_wifiLock.setVisibility(View.VISIBLE);
            holder.iv_wifiLock.setImageResource(R.drawable.ic_arrow_right);
            return;
        }
        holder.iv_connectedWifiIcon.setVisibility(View.VISIBLE);
        if (scanResult.getLevel() >= -30) {
            holder.iv_connectedWifiIcon.setImageResource(R.drawable.ic_wifi_nw);
        } else if (scanResult.getLevel() >= -67) {
            holder.iv_connectedWifiIcon.setImageResource(R.drawable.ic_wifi_3);
        } else if (scanResult.getLevel() >= -70) {
            holder.iv_connectedWifiIcon.setImageResource(R.drawable.ic_wifi_1);
        } else {
            holder.iv_connectedWifiIcon.setImageResource(R.drawable.ic_wifi_0);
        }

        if (scanResult.getProtocol() != null &&
                scanResult.getProtocol().size() > 0 &&
                scanResult.getProtocol().get(0).name().contains("WPA")) {
            holder.iv_wifiLock.setVisibility(View.VISIBLE);
            holder.iv_wifiLock.setImageResource(R.drawable.ic_lock);
        } else {
            holder.iv_wifiLock.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_connectedWifiIcon, iv_wifiLock;
        TextView tv_connectedWifiName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_connectedWifiIcon = itemView.findViewById(R.id.connected_wifi_icon);
            tv_connectedWifiName = itemView.findViewById(R.id.connected_wifi_name);
            iv_wifiLock = itemView.findViewById(R.id.lock);

            itemView.setOnClickListener(v -> {
                if (RFIDController.mConnectedReader != null) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetTheme);

                    View sheetView = LayoutInflater.from(mContext).inflate(R.layout.wifi_bottom_sheet_add_profile, (ViewGroup) itemView.findViewById(R.id.bottom_sheet_add_profile));


                    WifiScanData selectedData = results.get(getAbsoluteAdapterPosition());

                    if(selectedData == null ||selectedData.getProtocol() == null ||selectedData.getProtocol().isEmpty()){
                        Toast.makeText(mContext, "Feature not supported", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        return;
                    }

                    WifiProfile wifiProfile = new WifiProfile();
                    WifiSecureConfig wifiSecureConfig = new WifiSecureConfig();

                    TextView tvWifiName = sheetView.findViewById(R.id.tv_wifi_name);
                    EditText etWifiName = sheetView.findViewById(R.id.et_wifi_name);
                    CheckBox preferredWifi = sheetView.findViewById(R.id.preferredWifi);
                    if("Add Profile Manually".equals(selectedData.getssid())) {
                        etWifiName.setVisibility(View.VISIBLE);
                        etWifiName.setHint("Enter Wifi Name");
                        tvWifiName.setVisibility(View.GONE);
                    } else {
                        etWifiName.setVisibility(View.GONE);
                        tvWifiName.setVisibility(View.VISIBLE);
                        tvWifiName.setText(selectedData.getssid());
                    }

                    //TableRow tableRowProtocol = sheetView.findViewById(R.id.tbl_row_protocol);
                    TableRow tableRowEap = sheetView.findViewById(R.id.tbl_row_eap);
                    TableRow tableRowCaCert = sheetView.findViewById(R.id.tbl_row_ca_cert);
                    TableRow tableRowIdentity = sheetView.findViewById(R.id.tbl_row_identity);
                    TableRow tableRowAnonymous = sheetView.findViewById(R.id.tbl_row_anonymous);
                    TableRow tableRowClientCert = sheetView.findViewById(R.id.tbl_row_client_cert);
                    TableRow tableRowPassword = sheetView.findViewById(R.id.tbl_row_password);
                    TableRow tableRowKey = sheetView.findViewById(R.id.tbl_row_key);
                    TableRow tableRowPrivatePassword = sheetView.findViewById(R.id.tbl_row_private_password);

                    EditText editTextPassword = sheetView.findViewById(R.id.et_password);
                    EditText editTextPrivatePassword = sheetView.findViewById(R.id.et_private_password);
                    EditText editTextAnonymousIdentity = sheetView.findViewById(R.id.et_anonymous_identity);
                    EditText editTextIdentity = sheetView.findViewById(R.id.et_identity);

                    Button btAddProfile = sheetView.findViewById(R.id.wifi_add_profile);


                    ArrayAdapter<ENUM_WIFI_PROTOCOL_TYPE> protocolArrayAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, selectedData.getProtocol());
                    protocolArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerProtocol = sheetView.findViewById(R.id.spinner_protocol);
                    spinnerProtocol.setAdapter(protocolArrayAdapter);

                    ArrayAdapter<CharSequence> eapArrayAdapter = ArrayAdapter.createFromResource(mContext,
                            R.array.eap_values, android.R.layout.simple_spinner_item);
                    eapArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerEap = sheetView.findViewById(R.id.spinner_eap);
                    spinnerEap.setAdapter(eapArrayAdapter);
                    String[] eapArray = mContext.getResources().getStringArray(R.array.eap_values);

                    spinnerProtocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            switch (selectedData.getProtocol().get(position)) {
                                case No_Encryption:
                                    tableRowEap.setVisibility(View.GONE);
                                    tableRowCaCert.setVisibility(View.GONE);
                                    tableRowIdentity.setVisibility(View.GONE);
                                    tableRowAnonymous.setVisibility(View.GONE);
                                    tableRowClientCert.setVisibility(View.GONE);
                                    tableRowPassword.setVisibility(View.GONE);
                                    tableRowPrivatePassword.setVisibility(View.GONE);
                                    tableRowKey.setVisibility(View.GONE);
                                    btAddProfile.setClickable(true);
                                    break;
                                case WPA_Personal_TKIP:
                                case WPA2_Personal_CCMP:
                                case WPA3_Personal_SAE:
                                    tableRowEap.setVisibility(View.GONE);
                                    tableRowCaCert.setVisibility(View.GONE);
                                    tableRowIdentity.setVisibility(View.GONE);
                                    tableRowAnonymous.setVisibility(View.GONE);
                                    tableRowClientCert.setVisibility(View.GONE);
                                    tableRowPrivatePassword.setVisibility(View.GONE);
                                    tableRowKey.setVisibility(View.GONE);
                                    tableRowPassword.setVisibility(View.VISIBLE);
                                    btAddProfile.setClickable(true);
                                    break;
                                case WPA2_Enterprise_CCMP:
                                case WPA3_Enterprise_CCMP:
                                case WPA3_Enterprise_CCMP_256:
                                case WPA3_Enterprise_GCMP_128:
                                case WPA3_Enterprise_GCMP_256_SHA256:
                                case WPA3_Enterprise_GCMP_256_SUITEB_192:
                                    tableRowEap.setVisibility(View.VISIBLE);
                                    tableRowCaCert.setVisibility(View.VISIBLE);
                                    tableRowIdentity.setVisibility(View.VISIBLE);
                                    tableRowPassword.setVisibility(View.VISIBLE);
                                    if (spinnerEap.getSelectedItem().toString().equals("TLS")) {
                                        tableRowPrivatePassword.setVisibility(View.VISIBLE);
                                        tableRowAnonymous.setVisibility(View.GONE);
                                        tableRowClientCert.setVisibility(View.VISIBLE);
                                        tableRowKey.setVisibility(View.VISIBLE);
                                        tableRowPassword.setVisibility(View.GONE);
                                    } else if (spinnerEap.getSelectedItem().toString().equals("TTLS") ||
                                            spinnerEap.getSelectedItem().toString().equals("PEAP")) {
                                        tableRowPrivatePassword.setVisibility(View.GONE);
                                        tableRowAnonymous.setVisibility(View.VISIBLE);
                                        tableRowClientCert.setVisibility(View.GONE);
                                        tableRowKey.setVisibility(View.GONE);
                                        tableRowPassword.setVisibility(View.VISIBLE);
                                    }
                                    btAddProfile.setClickable(true);
                                    break;
                                case UNSUPPORTED:
                                    btAddProfile.setClickable(false);
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    ArrayList<String> wpaCertificates = new ArrayList<>();
                    try {
                        wpaCertificates = RFIDController.mConnectedReader.Config.getCertificates();
                        wpaCertificates.add(0, "");
                    } catch (InvalidUsageException e) {
                        Log.d(TAG, "Returned SDK Exception wifiGetCertificates");
                        if (e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }catch ( OperationFailureException e) {
                        Log.d(TAG, "Returned SDK Exception wifiGetCertificates");
                        if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                            Log.d(TAG,"Authentication error : "+ e.getVendorMessage());
                            ErrorLoginDialogFragment.handleAdminAuthenticationError(
                                    ((androidx.fragment.app.FragmentActivity) mContext).getSupportFragmentManager());
//                            ErrorLoginDialogFragment errorLoginDialogFragment = ErrorLoginDialogFragment.newInstance();
//                            errorLoginDialogFragment.show(((androidx.fragment.app.FragmentActivity) mContext).getSupportFragmentManager(), "ErrorLoginDialogFragment");
                        } else {
                            Log.e(TAG, e.getVendorMessage());
                        }
                        if (e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }

                    ArrayAdapter<String> caArrayAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, wpaCertificates);
                    caArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerCa = sheetView.findViewById(R.id.spinner_ca_cert);
                    spinnerCa.setAdapter(caArrayAdapter);

                    ArrayAdapter<String> ClientCertArrayAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, wpaCertificates);
                    ClientCertArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerClientCert = sheetView.findViewById(R.id.spinner_client_cert);
                    spinnerClientCert.setAdapter(ClientCertArrayAdapter);

                    ArrayAdapter<String> PrivateKeyArrayAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, wpaCertificates);
                    PrivateKeyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerPrivateKey = sheetView.findViewById(R.id.spinner_private_key);
                    spinnerPrivateKey.setAdapter(PrivateKeyArrayAdapter);


                    spinnerEap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (eapArray[position].equals("TLS")) {
                                tableRowPrivatePassword.setVisibility(View.VISIBLE);
                                tableRowAnonymous.setVisibility(View.GONE);
                                tableRowClientCert.setVisibility(View.VISIBLE);
                                tableRowKey.setVisibility(View.VISIBLE);
                                tableRowPassword.setVisibility(View.GONE);
                            } else if (spinnerEap.getSelectedItem().toString().equals("TTLS") ||
                                    spinnerEap.getSelectedItem().toString().equals("PEAP")) {
                                tableRowPrivatePassword.setVisibility(View.GONE);
                                tableRowAnonymous.setVisibility(View.VISIBLE);
                                tableRowClientCert.setVisibility(View.GONE);
                                tableRowKey.setVisibility(View.GONE);
                                tableRowPassword.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    btAddProfile.setOnClickListener(v2 -> {
                        boolean isWifiPreferred = preferredWifi.isChecked();
                        if(tvWifiName.getVisibility() == View.VISIBLE) {
                            wifiProfile.setssid(selectedData.getssid());
                        }else {
                            String ssid = etWifiName.getText().toString();
                            if(isEmpty(ssid)) {
                                Toast.makeText(mContext, "Enter a valid ssid to proceed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            wifiProfile.setssid(ssid);
                            wifiProfile.setHiddenProfile(true);
                        }
                        wifiProfile.setprotocol((ENUM_WIFI_PROTOCOL_TYPE) spinnerProtocol.getSelectedItem());
                        wifiProfile.setpassword(editTextPassword.getText().toString());
                        wifiSecureConfig.seteap(spinnerEap.getSelectedItem().toString());
                        if (spinnerCa.getSelectedItem() != null) {
                            wifiSecureConfig.setcacert(spinnerCa.getSelectedItem().toString());
                        }
                        wifiSecureConfig.setidentity(editTextIdentity.getText().toString());
                        if (spinnerEap.getSelectedItem().toString().equals("TLS")) {
                            if (spinnerClientCert.getSelectedItem() != null) {
                                wifiSecureConfig.setclientcert(spinnerClientCert.getSelectedItem().toString());
                            }
                            if (spinnerPrivateKey.getSelectedItem() != null) {
                                wifiSecureConfig.setPrivateKey(spinnerPrivateKey.getSelectedItem().toString());
                            }
                            wifiSecureConfig.setPrivatePassword(editTextPrivatePassword.getText().toString());
                        } else {
                            wifiSecureConfig.setAnonymousIdentity(editTextAnonymousIdentity.getText().toString());
                            wifiSecureConfig.setPassword(editTextPassword.getText().toString());
                        }
                        wifiProfile.setconfig(wifiSecureConfig);
                        if (wifiProfile.getprotocol() != null && wifiProfile.getprotocol().toString().contains("Personal") &&
                                wifiProfile.getpassword().length() < 8) {
                            Toast.makeText(mContext, "Min 8 char password required", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ReaderWifiSettingsFragment.getInstance().addProfile(wifiProfile, isWifiPreferred);
                        bottomSheetDialog.dismiss();
                    });

                    sheetView.findViewById(R.id.bottom_sheet_close).setOnClickListener(v3 -> bottomSheetDialog.dismiss());
                    bottomSheetDialog.setContentView(sheetView);
                    //  setupFullHeight(bottomSheetDialog);
                    bottomSheetDialog.show();
                }else{
                    Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
                }
            });


        }

        private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
            FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

            int windowHeight = getWindowHeight();
            if (layoutParams != null) {
                layoutParams.height = windowHeight;
            }
            bottomSheet.setLayoutParams(layoutParams);
            //behavior.setState(BottomSheetBehavior.STATE_DRAGGING);
        }

        private int getWindowHeight() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }


}
