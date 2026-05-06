package com.zebra.demo.rfidreader.settings.endpoint;

import static android.text.TextUtils.isEmpty;
import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedReader;
import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ENDPOINT_CONFIGURATION_PAGE;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.BuildConfig;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.common.Constants;
import com.zebra.demo.rfidreader.common.CustomProgressDialog;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.BackPressedFragment;
import com.zebra.demo.rfidreader.settings.SettingsDetailActivity;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.ENUM_EP_OPERATION;
import com.zebra.rfid.api3.ENUM_EP_PROTOCOL_TYPE;
import com.zebra.rfid.api3.ENUM_EP_TYPE;
import com.zebra.rfid.api3.ENUM_HOST_VERIFY;
import com.zebra.rfid.api3.EndpointConfigurationInfo;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.ArrayList;

public class EndpointConfigSettingsFragment extends BackPressedFragment {

    private static final String TAG = "EndpointConfigSettingsFragment";
    private FragmentActivity fragmentActivity = null;
    private Spinner typeSpinner;
    private EditText epName;
    private Spinner protocolSpinner;
    private EditText url;
    private EditText port;
    private EditText keepAlive;
    private EditText tenantId;
    private CheckBox cleanSession;
    private EditText minReconnectDelay;
    private EditText maxReconnectDelay;
    private Spinner hostVerify;
    private EditText userName;
    private EditText password;
    private Context context;
    private ArrayList<String> endpointNames = new ArrayList<>();
    private EndpointConfigurationInfo activeEp;
    private boolean addNew;
    private Spinner caCertSpinner, clientCertSpinner, privateKeySpinner;
    private EditText etCmdTopic, etResponseTopic, etEventTopic;
    private String[] caCerts, clientCerts, keyCerts;



    public static EndpointConfigSettingsFragment newInstance() {
        return new EndpointConfigSettingsFragment();
    }

    public EndpointConfigSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_endpoint_config_settings, container, false);
//        context = getActivity();

        typeSpinner = (Spinner) rootView.findViewById(R.id.epType);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.eptype, R.layout.custom_spinner_layout);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        epName = (EditText) rootView.findViewById(R.id.et_name);

        protocolSpinner = (Spinner) rootView.findViewById(R.id.epProtocol);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> protocolAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.protocol_array, R.layout.custom_spinner_layout);
        // Specify the layout to use when the list of choices appears
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        protocolSpinner.setAdapter(protocolAdapter);

        url = (EditText) rootView.findViewById(R.id.et_url);
        port = (EditText) rootView.findViewById(R.id.et_port);
        keepAlive = (EditText) rootView.findViewById(R.id.et_keepAlive);
        tenantId = (EditText) rootView.findViewById(R.id.et_TenantId);
        cleanSession = (CheckBox) rootView.findViewById(R.id.cleanSession);
        minReconnectDelay = (EditText) rootView.findViewById(R.id.et_minReconnectDelay);
        maxReconnectDelay = (EditText) rootView.findViewById(R.id.et_maxReconnectDelay);

        hostVerify = (Spinner) rootView.findViewById(R.id.hostVerify);
        ArrayAdapter<CharSequence> hostVerifyAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.host_verify, R.layout.custom_spinner_layout);
        hostVerifyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hostVerify.setAdapter(hostVerifyAdapter);

        userName = (EditText) rootView.findViewById(R.id.et_userName);
        password = (EditText) rootView.findViewById(R.id.et_password);

        caCertSpinner = rootView.findViewById(R.id.caCertificateSpinner);
        clientCertSpinner = rootView.findViewById(R.id.clientCertificateSpinner);
        privateKeySpinner = rootView.findViewById(R.id.privateKeySpinner);

        etCmdTopic = rootView.findViewById(R.id.et_cmdTopic);
        etResponseTopic = rootView.findViewById(R.id.et_responseTopic);
        etEventTopic = rootView.findViewById(R.id.et_eventTopic);

        // defaults
        protocolSpinner.setSelection(0, false);

        if (RFIDController.addEndpointPos == -1) {
            RFIDController.endpointConfiguration = null;
        }

        if (RFIDController.editEndpointPos != -1) {
            try {
                endpointNames = mConnectedReader.Config.getEndpointNames();
                RFIDController.endpointConfiguration = mConnectedReader.Config.getEndpointConfigByName(endpointNames.get(RFIDController.editEndpointPos));

            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            } catch (OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }

                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        }

        try{
            ArrayList<String> wpaCertificates = mConnectedReader.Config.getCertificates();
            if(wpaCertificates!=null && !wpaCertificates.isEmpty()) {
                ArrayList<String> caCertificates = new ArrayList<>();
                caCertificates.add("");
                ArrayList<String> clientCertificates = new ArrayList<>();
                clientCertificates.add("");
                ArrayList<String> privateKeys = new ArrayList<>();
                privateKeys.add("");
                for(String certificate:wpaCertificates) {
                    if(certificate.endsWith("ca_cert")) {
                        caCertificates.add(certificate);
                    } else if (certificate.endsWith("client_cert")) {
                        clientCertificates.add(certificate);
                    } else if (certificate.endsWith("client_key.key")
                            || certificate.endsWith("client_key")) {
                        privateKeys.add(certificate);
                    }
                }
                caCerts = caCertificates.toArray(new String[0]);
                ArrayAdapter<CharSequence> caCertAdapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner_layout, caCerts);
                caCertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                caCertSpinner.setAdapter(caCertAdapter);
                clientCerts = clientCertificates.toArray(new String[0]);
                ArrayAdapter<CharSequence> clientCertAdapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner_layout, clientCerts);
                clientCertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                clientCertSpinner.setAdapter(clientCertAdapter);
                keyCerts = privateKeys.toArray(new String[0]);
                ArrayAdapter<CharSequence> keysAdapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner_layout, keyCerts);
                keysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                privateKeySpinner.setAdapter(keysAdapter);
            } else {
                caCerts = new String[]{""};
                clientCerts = new String[]{""};
                keyCerts = new String[]{""};
            }
        } catch (OperationFailureException e) {
            if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }
            Log.e(TAG,"getCertificate failed with Exception", e);
        }catch ( InvalidUsageException e){
            Log.e(TAG,"getCertificate failed with Exception", e);
        }

        if (mConnectedReader != null && mConnectedReader.isConnected() && mConnectedReader.isCapabilitiesReceived()) {
            if(RFIDController.endpointConfiguration != null) {
                epName.setText(String.format("%s", RFIDController.endpointConfiguration.getEpname()));

                if (ENUM_EP_TYPE.MDM == RFIDController.endpointConfiguration.getType()) {
                    typeSpinner.setSelection(1);
                } else {
                    typeSpinner.setSelection(0);
                    updateMDMViews(rootView, View.GONE);
                }

                protocolSpinner.setSelection(RFIDController.endpointConfiguration.getProtocol().ordinal());
                if(RFIDController.endpointConfiguration.getUrl() != null)
                    url.setText(String.format("%s", RFIDController.endpointConfiguration.getUrl()));
                port.setText(String.format("%s", RFIDController.endpointConfiguration.getPort()));
                keepAlive.setText(String.format("%s", RFIDController.endpointConfiguration.getKeepalive()));
                if(RFIDController.endpointConfiguration.getTenantid() != null)
                    tenantId.setText(String.format("%s", RFIDController.endpointConfiguration.getTenantid()));

                if(RFIDController.endpointConfiguration.getEncleanss())
                    cleanSession.setChecked(true);

                minReconnectDelay.setText(String.format("%s", RFIDController.endpointConfiguration.getRcdelaymin()));
                maxReconnectDelay.setText(String.format("%s", RFIDController.endpointConfiguration.getRcdelaymax()));
                hostVerify.setSelection(RFIDController.endpointConfiguration.getHostvfy().getEnumValue());
                if(RFIDController.endpointConfiguration.getUsername() != null)
                    userName.setText(String.format("%s", RFIDController.endpointConfiguration.getUsername()));
                //if(RFIDController.endpointConfiguration.getPassword() != null)
                //    password.setText(String.format("%s", RFIDController.endpointConfiguration.getPassword()));

                if(!isEmpty(RFIDController.endpointConfiguration.getCacertname())
                        && caCertSpinner.getVisibility() == View.VISIBLE && caCerts.length > 1) {
                    int selectedPosition = 0;
                    for(int i = 0; i< caCerts.length; i++) {
                        if(!isEmpty(caCerts[i]) && caCerts[i].startsWith(RFIDController.endpointConfiguration.getCacertname())) {
                            selectedPosition = i;
                            break;
                        }
                    }
                    caCertSpinner.setSelection(selectedPosition);
                }
                if(!isEmpty(RFIDController.endpointConfiguration.getCertname())
                        && clientCertSpinner.getVisibility() == View.VISIBLE && clientCerts.length > 1) {
                    int selectedPosition = 0;
                    for(int i = 0; i< clientCerts.length; i++) {
                        if(!isEmpty(clientCerts[i]) && clientCerts[i].startsWith(RFIDController.endpointConfiguration.getCertname())) {
                            selectedPosition = i;
                            break;
                        }
                    }
                    clientCertSpinner.setSelection(selectedPosition);
                }
                if(!isEmpty(RFIDController.endpointConfiguration.getKeyname())
                        && privateKeySpinner.getVisibility() == View.VISIBLE && keyCerts.length > 1) {
                    int selectedPosition = 0;
                    for(int i = 0; i< keyCerts.length; i++) {
                        if(!isEmpty(keyCerts[i]) && keyCerts[i].startsWith(RFIDController.endpointConfiguration.getKeyname())) {
                            selectedPosition = i;
                            break;
                        }
                    }
                    privateKeySpinner.setSelection(selectedPosition);
                }

                if(!isEmpty(RFIDController.endpointConfiguration.getSubname())) {
                    etCmdTopic.setText(RFIDController.endpointConfiguration.getSubname());
                }
                if(!isEmpty(RFIDController.endpointConfiguration.getPub1name())) {
                    etResponseTopic.setText(RFIDController.endpointConfiguration.getPub1name());
                }
                if(!isEmpty(RFIDController.endpointConfiguration.getPub2name())) {
                    etEventTopic.setText(RFIDController.endpointConfiguration.getPub2name());
                }
            }
        }

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               int viewStatus = (position ==1)? View.VISIBLE : View.GONE;
               updateMDMViews(rootView, viewStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        updateUI();

        return rootView;
    }


    private void updateMDMViews(View rootView, int viewStatus) {
        rootView.findViewById(R.id.EndpointConfigRow14).setVisibility(viewStatus);
        rootView.findViewById(R.id.configRow14Divider).setVisibility(viewStatus);
        rootView.findViewById(R.id.EndpointConfigRow15).setVisibility(viewStatus);
        rootView.findViewById(R.id.configRow15Divider).setVisibility(viewStatus);
        rootView.findViewById(R.id.EndpointConfigRow16).setVisibility(viewStatus);
        rootView.findViewById(R.id.configRow16Divider).setVisibility(viewStatus);
        rootView.findViewById(R.id.EndpointConfigRow17).setVisibility(viewStatus);
        rootView.findViewById(R.id.configRow17Divider).setVisibility(viewStatus);
        rootView.findViewById(R.id.EndpointConfigRow18).setVisibility(viewStatus);
        rootView.findViewById(R.id.configRow18Divider).setVisibility(viewStatus);
        rootView.findViewById(R.id.EndpointConfigRow19).setVisibility(viewStatus);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(RFIDController.mConnectedReader != null) {
            updateUI();
        }
    }

    private void updateUI(){

    }

    @Override
    public void onBackPressed() {
        if (RFIDController.endpointConfiguration == null && !epName.getText().toString().isEmpty()) {
            Task_SaveEndpointConfig saveEndpointConfig = new Task_SaveEndpointConfig(epName.getText().toString(), String.valueOf(typeSpinner.getSelectedItemPosition()), ENUM_EP_OPERATION.NEW, protocolSpinner.getSelectedItemPosition(), url.getText().toString(), port.getText().toString(), keepAlive.getText().toString(), tenantId.getText().toString(), cleanSession.isChecked(), minReconnectDelay.getText().toString(), maxReconnectDelay.getText().toString(), String.valueOf(hostVerify.getSelectedItemPosition()), userName.getText().toString(), password.getText().toString(), context);
            String selectedType = (String) typeSpinner.getSelectedItem();
            if(!isEmpty(selectedType) && selectedType.equals("MDM")) {
                String caCertName = null;
                Object selectedItem = caCertSpinner.getSelectedItem();
                if(selectedItem != null) {
                    caCertName = selectedItem.toString();
                }
                String clientCertName = null;
                selectedItem = clientCertSpinner.getSelectedItem();
                if(selectedItem != null) {
                    clientCertName = selectedItem.toString();
                }
                String keyName = null;
                selectedItem = privateKeySpinner.getSelectedItem();
                if(selectedItem != null) {
                    keyName = selectedItem.toString();
                }
                saveEndpointConfig.setMDMRelatedFields(caCertName, clientCertName, keyName, etCmdTopic.getText().toString(),etResponseTopic.getText().toString(),etEventTopic.getText().toString());
            }
            saveEndpointConfig.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (isSettingsChanged()) {
            Task_SaveEndpointConfig saveEndpointConfig = new Task_SaveEndpointConfig(epName.getText().toString(), String.valueOf(typeSpinner.getSelectedItemPosition()), ENUM_EP_OPERATION.UPDATE, protocolSpinner.getSelectedItemPosition(), url.getText().toString(), port.getText().toString(), keepAlive.getText().toString(), tenantId.getText().toString(), cleanSession.isChecked(), minReconnectDelay.getText().toString(), maxReconnectDelay.getText().toString(), String.valueOf(hostVerify.getSelectedItemPosition()), userName.getText().toString(), password.getText().toString(), context);
            String selectedType = (String) typeSpinner.getSelectedItem();
            if(!isEmpty(selectedType) && selectedType.equals("MDM")) {
                String caCertName = null;
                Object selectedItem = caCertSpinner.getSelectedItem();
                if(selectedItem != null) {
                    caCertName = selectedItem.toString();
                }
                String clientCertName = null;
                selectedItem = clientCertSpinner.getSelectedItem();
                if(selectedItem != null) {
                    clientCertName = selectedItem.toString();
                }
                String keyName = null;
                selectedItem = privateKeySpinner.getSelectedItem();
                if(selectedItem != null) {
                    keyName = selectedItem.toString();
                }
                saveEndpointConfig.setMDMRelatedFields(caCertName, clientCertName, keyName, etCmdTopic.getText().toString(),etResponseTopic.getText().toString(),etEventTopic.getText().toString());
            }
            saveEndpointConfig.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (getActivity() instanceof SettingsDetailActivity)
                ((SettingsDetailActivity) getActivity()).callBackPressed();
            if (getActivity() instanceof ActiveDeviceActivity) {
                ((ActiveDeviceActivity) getActivity()).callBackPressed();
                ((ActiveDeviceActivity) getActivity()).loadNextFragment(ENDPOINT_CONFIGURATION_PAGE);
            }
        }
    }

    private boolean isSettingsChanged() {
        if (RFIDController.endpointConfiguration != null) {
            String uiEpName = epName.getText().toString();
            String uiUrl = url.getText().toString();
            String uiTenantId = tenantId.getText().toString();
            String uiUserName = userName.getText().toString();
            String uiPassword = password.getText().toString();
            String cmndTopic = etCmdTopic.getText().toString();
            String resTopic = etResponseTopic.getText().toString();
            String evntTopic = etEventTopic.getText().toString();
            String selectedType = (String) typeSpinner.getSelectedItem();
            if (!isEmpty(uiEpName) && !uiEpName.equalsIgnoreCase(RFIDController.endpointConfiguration.getEpname()))
                return true;
            else if (typeSpinner != null && !RFIDController.endpointConfiguration.getType().getEnumValue().equalsIgnoreCase(typeSpinner.getSelectedItem().toString()))
                return true;
            else if (protocolSpinner != null && RFIDController.endpointConfiguration.getProtocol().ordinal() != protocolSpinner.getSelectedItemPosition())
                return true;
            else if (!isEmpty(uiUrl) && !uiUrl.equalsIgnoreCase(RFIDController.endpointConfiguration.getUrl()))
                return true;
            else if (port != null && RFIDController.endpointConfiguration.getPort() != Integer.parseInt(port.getText().toString()))
                return true;
            else if (keepAlive != null && RFIDController.endpointConfiguration.getKeepalive() != Integer.parseInt(keepAlive.getText().toString()))
                return true;
            else if (!isEmpty(uiTenantId) && !uiTenantId.equalsIgnoreCase(RFIDController.endpointConfiguration.getTenantid()))
                return true;
            else if (cleanSession != null && RFIDController.endpointConfiguration.getEncleanss() && !cleanSession.isChecked())
                return true;
            else if (cleanSession != null && RFIDController.endpointConfiguration.getDscleanss() && cleanSession.isChecked())
                return true;
            else if (minReconnectDelay != null && RFIDController.endpointConfiguration.getRcdelaymin() != Integer.parseInt(minReconnectDelay.getText().toString()))
                return true;
            else if (maxReconnectDelay != null && RFIDController.endpointConfiguration.getRcdelaymax() != Integer.parseInt(maxReconnectDelay.getText().toString()))
                return true;
            else if (hostVerify != null && RFIDController.endpointConfiguration.getHostvfy().getEnumValue() != hostVerify.getSelectedItemPosition())
                return true;
            else if (!isEmpty(uiUserName) && !uiUserName.equalsIgnoreCase(RFIDController.endpointConfiguration.getUsername()))
                return true;
            else if (!isEmpty(uiPassword) && !uiPassword.equalsIgnoreCase(RFIDController.endpointConfiguration.getPassword()))
                return true;
            else if (!isEmpty(cmndTopic) && !cmndTopic.equalsIgnoreCase(RFIDController.endpointConfiguration.getSubname()))
                return true;
            else if (!isEmpty(resTopic) && !resTopic.equalsIgnoreCase(RFIDController.endpointConfiguration.getPub1name()))
                return true;
            else if (!isEmpty(evntTopic) && !evntTopic.equalsIgnoreCase(RFIDController.endpointConfiguration.getPub2name()))
                return true;
            else if (!isEmpty(selectedType) && selectedType.equals("MDM")) {
                String caCertName = null;
                Object selectedItem = caCertSpinner.getSelectedItem();
                if (selectedItem != null) {
                    caCertName = selectedItem.toString();
                    if (!isEmpty(caCertName) && !caCertName.equalsIgnoreCase(RFIDController.endpointConfiguration.getCacertname()))
                        return true;
                }
                String clientCertName = null;
                selectedItem = clientCertSpinner.getSelectedItem();
                if (selectedItem != null) {
                    clientCertName = selectedItem.toString();
                    if (!isEmpty(clientCertName) && !clientCertName.equalsIgnoreCase(RFIDController.endpointConfiguration.getCertname()))
                        return true;
                }
                String keyName = null;
                selectedItem = privateKeySpinner.getSelectedItem();
                if (selectedItem != null) {
                    keyName = selectedItem.toString();
                    if (!isEmpty(keyName) && !keyName.equalsIgnoreCase(RFIDController.endpointConfiguration.getKeyname()))
                        return true;
                }

            }

        }
        return false;
    }

    private class Task_SaveEndpointConfig extends AsyncTask<Void, Void, Boolean> {

        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private CustomProgressDialog progressDialog;
        private final int protocol;
        private final String url;
        private final String type;
        private final String name;
        private final ENUM_EP_OPERATION operation;
        private final String port;
        private final String keepAlive;
        private final String tenantId;
        private final boolean cleanSession;
        private final String minReconnectDelay;
        private final String maxReconnectDelay;
        private final String hostVerify;
        private final String userName;
        private final String password;
        Context current_context;
        private String caCertName = null;
        private String clientCertName = null;
        private String keyName = null;
        private String cmdTopic = null;
        private String responseTopic = null;
        private String eventTopic = null;

        public Task_SaveEndpointConfig(String name, String type, ENUM_EP_OPERATION operation, int protocolIndex, String url, String port, String keepAlive, String tenantId, boolean cleanSession, String minReconnectDelay, String maxReconnectDelay, String hostVerify, String userName, String password, Context context) {

            this.protocol = protocolIndex;
            this.url = url;
            this.type = type;
            this.name = name;
            this.operation = operation;
            this.port = port;
            this.keepAlive = keepAlive;
            this.tenantId = tenantId;
            this.cleanSession = cleanSession;
            this.minReconnectDelay = minReconnectDelay;
            this.maxReconnectDelay = maxReconnectDelay;
            this.hostVerify = hostVerify;
            this.userName = userName;
            this.password = password;
            current_context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.endpointConfig_progress_title));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                EndpointConfigurationInfo endpointConfigurationInfo = new EndpointConfigurationInfo();

                //if operation is new/update/delete epname should be given
                endpointConfigurationInfo.setEpname(name);
                switch(type){
                    case "0":
                        endpointConfigurationInfo.setType(ENUM_EP_TYPE.SOTI);
                        break;
                    case "1":
                        endpointConfigurationInfo.setType(ENUM_EP_TYPE.MDM);
                        break;
                    default:
                        endpointConfigurationInfo.setType(ENUM_EP_TYPE.SOTI);
                        break;
                }
                if(protocol == ENUM_EP_PROTOCOL_TYPE.EP_PROTO_TYP_MQTT.ordinal()){
                    endpointConfigurationInfo.setProtocol(ENUM_EP_PROTOCOL_TYPE.EP_PROTO_TYP_MQTT);
                } else if(protocol == ENUM_EP_PROTOCOL_TYPE.EP_PROTO_TYP_MQTT_TLS.ordinal()){
                    endpointConfigurationInfo.setProtocol(ENUM_EP_PROTOCOL_TYPE.EP_PROTO_TYP_MQTT_TLS);
                }

                if(!url.isEmpty())
                    endpointConfigurationInfo.setUrl(url);
                if(!port.isEmpty())
                    endpointConfigurationInfo.setPort((int) Long.parseLong(port));
                if(!keepAlive.isEmpty())
                    endpointConfigurationInfo.setKeepalive((int) Long.parseLong(keepAlive));
                if(!tenantId.isEmpty())
                    endpointConfigurationInfo.setTenantid(tenantId);
                if (cleanSession)
                    endpointConfigurationInfo.setEncleanss(true);
                else
                    endpointConfigurationInfo.setDscleanss(true);

                if(!minReconnectDelay.isEmpty())
                    endpointConfigurationInfo.setRcdelaymin((int) Long.parseLong(minReconnectDelay));
                if(!maxReconnectDelay.isEmpty())
                    endpointConfigurationInfo.setRcdelaymax((int) Long.parseLong(maxReconnectDelay));

                endpointConfigurationInfo.setHostvfy(ENUM_HOST_VERIFY.getEnum(hostVerify));
                if(!userName.isEmpty())
                    endpointConfigurationInfo.setUsername(userName);
                if(!password.isEmpty() && SecureComparison.constantTimeEquals(password, "XXXXXXXX"))
                    endpointConfigurationInfo.setPassword(password);
                if(!isEmpty(caCertName)) {
                    endpointConfigurationInfo.setCacertname(caCertName);
                }
                if(!isEmpty(clientCertName)) {
                    endpointConfigurationInfo.setCertname(clientCertName);
                }
                if(!isEmpty(keyName)) {
                    endpointConfigurationInfo.setKeyname(keyName);
                }

                if(!isEmpty(cmdTopic)) {
                    endpointConfigurationInfo.setSubname(cmdTopic);
                }
                if(!isEmpty(responseTopic)) {
                    endpointConfigurationInfo.setPub1name(responseTopic);
                }
                if(!isEmpty(eventTopic)) {
                    endpointConfigurationInfo.setPub2name(eventTopic);
                }

                endpointConfigurationInfo.setOperation(operation);
                RFIDResults res = mConnectedReader.Config.setEndpointConfiguration(endpointConfigurationInfo);
                Log.d(TAG, "SetEndpointConfiguration res = "+res);

                endpointConfigurationInfo = new EndpointConfigurationInfo();
                endpointConfigurationInfo.setOperation(ENUM_EP_OPERATION.SAVE);
                RFIDResults res1 = mConnectedReader.Config.setEndpointConfiguration(endpointConfigurationInfo);
                Log.d(TAG, "SetEndpointConfiguration save = "+res1);

                RFIDController.endpointConfiguration = endpointConfigurationInfo;

                return true;
            } catch (InvalidUsageException e) {
                if(BuildConfig.DEBUG)
                {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String stackTraceString = sw.toString();
                    Log.e("InvalidUsageException", stackTraceString);
                }
                if(e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
                invalidUsageException = e;
            } catch (OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }

                if(BuildConfig.DEBUG)
                {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String stackTraceString = sw.toString();
                    Log.e("OperationFailureException", stackTraceString);
                }
                if(e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
                operationFailureException = e;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.cancel();
            if (!result ) {
                if (invalidUsageException != null) {
                    if(current_context instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) current_context).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, "Failed to apply settings\n" + invalidUsageException.getVendorMessage());
                }
                if (operationFailureException != null) {
                    if(current_context instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) current_context).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, "Failed to apply settings\n" + operationFailureException.getVendorMessage());
                    if(operationFailureException.getResults() == RFIDResults.ADMIN_CONNECT_ERROR) {
                        ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                    }
                }
            }
            if (invalidUsageException == null && operationFailureException == null)
            {
                Toast.makeText(current_context, R.string.status_success_message, Toast.LENGTH_SHORT).show();
            }
            RFIDController.editEndpointPos = -1;
            ((ActiveDeviceActivity) current_context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ActiveDeviceActivity) current_context).loadNextFragment(ENDPOINT_CONFIGURATION_PAGE);
                }
            });

            super.onPostExecute(result);
        }

        public void setMDMRelatedFields(String caCertName, String certName, String keyName,
                                        String cmdTopic, String responseTopic, String eventTopic) {
            this.caCertName = caCertName;
            this.clientCertName = certName;
            this.keyName = keyName;
            this.cmdTopic = cmdTopic;
            this.responseTopic = responseTopic;
            this.eventTopic = eventTopic;
        }
    }

}

class SecureComparison {

    private SecureComparison(){}
    public static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return true;
        }
        byte[] aBytes = a.getBytes();
        byte[] bBytes = b.getBytes();
        return !MessageDigest.isEqual(aBytes, bBytes);
    }


}