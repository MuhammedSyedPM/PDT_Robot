package com.zebra.demo.rfidreader.settings.endpoint;

import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ENDPOINT_MAIN_PAGE;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.BackPressedFragment;
import com.zebra.demo.rfidreader.settings.SettingsDetailActivity;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.util.ArrayList;
import java.util.List;

public class EndpointStatusFragment extends BackPressedFragment implements IotEventListener {

    public static EndpointStatusFragment newInstance() {
        return new EndpointStatusFragment();
    }
    private RecyclerView recyclerView;
    private TextView noDataTextView;
    private IotEventAdapter adapter;
    private List<IotEventData> iotEventList = new ArrayList<>();
    private IotEventManager iotEventManager;
    private static final String TAG = "EndpointStatusFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iotEventManager != null) {
            iotEventManager.unregisterListener(this); // Unregister this instance
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        IotEventManager.getInstance().registerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        IotEventManager.getInstance().unregisterListener(this); // Unregister this instance
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_endpoint_status, container, false);

        recyclerView = view.findViewById(R.id.rv_iot_events);
        noDataTextView = view.findViewById(R.id.tv_no_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IotEventAdapter(iotEventList);
        recyclerView.setAdapter(adapter);

        fetchIotEvents();

        return view;
    }


    private void fetchIotEvents() {
        try {
            RFIDResults rfidResults =  RFIDController.mConnectedReader.Config.getEndpointStatus();
            if (rfidResults != RFIDResults.RFID_API_SUCCESS){

            }
        } catch (InvalidUsageException e) {
            if(e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
        } catch (OperationFailureException e) {
            if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }
            if(e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
        }
    }

    @Override
    public void onIotEvent(IotEventData iotEventData) { 
        if(iotEventData == null) {
            Log.e("EndpointStatusFragment", "iotEventData is null");
            return;
        }
        if (iotEventData != null) {
            Log.i("EndpointStatusFragment", "iotEventData is not  null" + iotEventData.getEpType());
            iotEventList.add(iotEventData);

         //   adapter.notifyDataSetChanged();
            getActivity().runOnUiThread(() -> {
                if (recyclerView.getAdapter() != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataTextView.setVisibility(View.GONE);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
        } else if (iotEventList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noDataTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if (getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(ENDPOINT_MAIN_PAGE);
        }
    }
}