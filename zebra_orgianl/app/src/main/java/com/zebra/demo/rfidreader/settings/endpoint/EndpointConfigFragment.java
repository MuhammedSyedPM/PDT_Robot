package com.zebra.demo.rfidreader.settings.endpoint;

import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedReader;
import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ENDPOINT_CONFIG_SETTINGS_PAGE;
import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ENDPOINT_MAIN_PAGE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.BackPressedFragment;
import com.zebra.demo.rfidreader.settings.SettingsDetailActivity;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.IotConfigInfo;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.util.ArrayList;

public class EndpointConfigFragment extends BackPressedFragment {

    private static final String TAG = "EndpointConfigMainFragment";
    private static FragmentActivity fragmentActivity = null;
    ArrayList<String> endpointNames = new ArrayList<>();
    private static EndpointConfigFragment endpointConfigFragment = null;


    public static EndpointConfigFragment newInstance() {
        return new EndpointConfigFragment();
    }

    public EndpointConfigFragment() {
        // Required empty public constructor
//        fragmentActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentActivity = getActivity();
        final View rootview = inflater.inflate(R.layout.fragment_endpoint_main, container, false);
        RecyclerView rvEndpoints = rootview.findViewById(R.id.rv_epConfig);

        // get all endpoint show in the RecyclerView
        try {
            endpointNames = RFIDController.mConnectedReader.Config.getEndpointNames();

        } catch (InvalidUsageException | OperationFailureException e) {
            Log.d(TAG, "Returned SDK Exception endpoint config");
        }
        int selectedPosition = getActiveEndPointPosition();
        EndpointAdapter endpointAdapter = new EndpointAdapter(endpointNames, selectedPosition);
        rvEndpoints.setLayoutManager(new LinearLayoutManager(rootview.getContext()));
        rvEndpoints.setAdapter(endpointAdapter);
        return rootview;
    }

    public static EndpointConfigFragment getInstance() {
        if (endpointConfigFragment == null)
            endpointConfigFragment = new EndpointConfigFragment();
        return endpointConfigFragment;
    }

    @Override
    public void onBackPressed() {
        if(getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if(getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(ENDPOINT_MAIN_PAGE);
        }
    }

    public void editEndpoint(int position){
        RFIDController.editEndpointPos = position;
        if(fragmentActivity instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) fragmentActivity).loadNextFragment(ENDPOINT_CONFIG_SETTINGS_PAGE);
        }
    }

    private int getActiveEndPointPosition() {
        try {
            IotConfigInfo iotConfigInfo = mConnectedReader.Config.getActiveEndpoints();
            for(int i=0;i<endpointNames.size();i++) {
                String endPointName = endpointNames.get(i);
                if(iotConfigInfo.getActivemgmtep() != null && iotConfigInfo.getActivemgmtep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getActivemgmtevtep() != null && iotConfigInfo.getActivemgmtevtep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getActivectrlep() != null && iotConfigInfo.getActivectrlep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getActivedat1ep() != null && iotConfigInfo.getActivedat1ep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getActivedat2ep() != null && iotConfigInfo.getActivedat2ep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getBackupmgmtep() != null && iotConfigInfo.getBackupmgmtep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getBackupmgmtevtep() != null && iotConfigInfo.getBackupmgmtevtep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getBackupctrlep() != null && iotConfigInfo.getBackupctrlep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getBackupdat1ep() != null && iotConfigInfo.getBackupdat1ep().equals(endPointName)){
                    return i;
                } else if(iotConfigInfo.getBackupdat2ep() != null && iotConfigInfo.getBackupdat2ep().equals(endPointName)){
                    return i;
                }
            }
        } catch (InvalidUsageException e) {
            Log.d(TAG, "Returned SDK Exception endpointAdapter");
        }catch ( OperationFailureException e){
            if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }
            Log.d(TAG, "Returned SDK Exception endpointAdapter "+ e.getVendorMessage());
        }
        return -1;
    }
}
