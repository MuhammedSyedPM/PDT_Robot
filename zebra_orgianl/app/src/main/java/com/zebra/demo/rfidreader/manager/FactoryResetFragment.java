package com.zebra.demo.rfidreader.manager;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.zebra.ASCII_SDK.ASCIIProcessor;
import com.zebra.ASCII_SDK.Command;
import com.zebra.ASCII_SDK.Command_ChangeConfig;
import com.zebra.ASCII_SDK.Command_Reset;
import com.zebra.ASCII_SDK.Response_Status;
import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.DeviceDiscoverActivity;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.common.CustomProgressDialog;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.RfidStatusEvents;

public class FactoryResetFragment extends Fragment {

    private static final String TAG = "FactoryResetFragment" ;
    private CustomProgressDialog progressDialog;
    private TextView textView;
    private int resetType = R.id.radio_factory;
    private TextView headerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.factoryreset_fragment, container, false);
        Button resetbutton = (Button) rootview.findViewById(R.id.resetbutton);
        textView = (TextView)rootview.findViewById(R.id.factoryresettextview);
        headerView = (TextView)rootview.findViewById(R.id.factoryresetheading);
        RadioButton radioButton = rootview.findViewById(R.id.radio_factory);
        radioButton.setChecked(true);
        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Command_ChangeConfig command_changeConfig = new Command_ChangeConfig();
                //command_changeConfig.setMode(ENUM_CHANGE_CONFIG_MODE.RESTORE_FACTORY_DEFAULTS);


                if(RFIDController.mConnectedReader == null )
                {
                    Toast.makeText(getActivity(), "Cannot perform the action, Reader not connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                if((!RFIDController.mConnectedReader.getHostName().startsWith("RFD40"))
                    && (!RFIDController.mConnectedReader.getHostName().startsWith("RFD90"))
                    && (!RFIDController.mConnectedReader.getHostName().startsWith("RFID"))
                    && (!RFIDController.mConnectedReader.getHostName().startsWith("TC27R"))
                    && (!RFIDController.mConnectedReader.getHostName().startsWith("TC22R"))
                    && (!RFIDController.mConnectedReader.getHostName().contains("FXP20")))
                {
                    Toast.makeText(getActivity(), "Cannot perform the action, not supported for " + RFIDController.mConnectedReader.getHostName() , Toast.LENGTH_SHORT).show();
                    return;
                }
                if(RFIDController.mIsInventoryRunning || RFIDController.isLocatingTag){
                    Toast.makeText(getActivity(), "Operation In Progress-Command not Allowed", Toast.LENGTH_SHORT).show();
                    return;

                }
                switch (resetType){
                    case R.id.radio_factory:
                        progressDialog = new CustomProgressDialog(getContext(), "Factory Reset");
                        progressDialog.setMessage("Factory Reset done device rebooting..");
                        progressDialog.show();
                        startFactoryReset( getString(R.string.resetFactory_progress_title));

                        break;
                    case R.id.radio_device:
                        progressDialog = new CustomProgressDialog(getContext(), "Device Reset");
                        progressDialog.setMessage("Reader reset is in progress");
                        progressDialog.show();
                        Thread deviceReset = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendResetCommand(new Command_Reset());
                            }
                        });
                        deviceReset.start();
                         break;
                }

            }
        });
        return rootview;
    }


    public void sendResetCommand(Command cmd) {

        try {
            if(RFIDController.mConnectedReader.getHostName().startsWith("RFD40")
                    ||RFIDController.mConnectedReader.getHostName().startsWith("RFD90")
                    || RFIDController.mConnectedReader.getHostName().startsWith("RFID")
                    || RFIDController.mConnectedReader.getHostName().startsWith("TC27R")
                    || RFIDController.mConnectedReader.getHostName().startsWith("TC22R"))  {
                if (!((ActiveDeviceActivity) requireActivity()).deviceReset(ASCIIProcessor.getCommandString(cmd))) {
                    Log.d(TAG, "operation_not_allowed_reader_detached");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }else {
                if (((ActiveDeviceActivity) requireActivity()).deviceReset(null)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("device Reset done.");
                        }
                    });
                    if(progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }
        } catch (InvalidUsageException | OperationFailureException e) {
           Log.d(TAG,  "Returned SDK Exception");
        }
    }

    public static FactoryResetFragment newInstance() {
        FactoryResetFragment fragment = new FactoryResetFragment();
        return fragment;
    }

    public void changeResetMode( View view )
    {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_factory:
                if (checked)
                    //factoryResetClicked(view);
                    resetType = R.id.radio_factory;
                    headerView.setText("Reset to Factory Defaults");
                    textView.setText(R.string.factory_reset);
                    break;
            case R.id.radio_device:
                if (checked)
                    //deviceResetClicked(view);
                    resetType = R.id.radio_device;
                headerView.setText("Device Reset");
                textView.setText("Device reset will reboot RFDXX device");
                    break;
        }
    }
    public void startFactoryReset( String title) {
       // if (Application.StatusReaderConnection == ENUM_SERVICE_STATUS.READER_ATTACHED.ordinal())

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {

                        //timerDelayRemoveDialog(com.zebra.rfid.api3.Constants.RESPONSE_TIMEOUT, progressDialog, getString(R.string.status_failure_message), true);

                    }

                    @Override
                    protected Void doInBackground(Void... params) {

                        {
                            try {
                                ((ActiveDeviceActivity) getActivity()).resetFactoryDefault();
                                RFIDController.mConnectedReader = null;
                                {
                                    //Toast.makeText((ManageDeviceActivity) getActivity(), "Operation not allowed when reader is busy/off", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "operation_not_allowed_reader_detached");
                                    //if(progressDialog.isShowing())
                                    //    progressDialog.dismiss();
                                }
                            } catch (InvalidUsageException e) {
                               Log.d(TAG,  "Returned SDK Exception");
                            } catch (OperationFailureException e) {
                               Log.d(TAG,  "Returned SDK Exception");
                                if(e.getResults().equals(RFIDResults.RFID_API_COMMAND_TIMEOUT)) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            textView.setText("Factory Reset initiated successfully...");
                                        }
                                    });


                                }else{
                                    if((progressDialog != null) && progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    String eMsg = e.getVendorMessage();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.setText(eMsg);
                                        }
                                    });
                                    return null;
                                }
                            }
                        }

                        if (getActivity() == null)
                            return null;
                        int loopCount = 0;
                        do{
                            try {
                                Thread.sleep(1000);
                                loopCount++;
                            } catch (InterruptedException e) {
                               Log.d(TAG,  "Returned SDK Exception");
                                break;
                            }

                            Log.d(TAG, "Reset in Progress, Reader not attached" );

                        }while(loopCount < 5);
                        if(getActivity() == null){
                            return null;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                textView.setText("Factory Reset done.");
                                if (progressDialog != null) {
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    progressDialog = null;
                                }

                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (getActivity() == null) return;
                        //if (progressDialog != null)
                        //    progressDialog.dismiss();
                        if (RFIDController.mConnectedDevice != null) {
                            //Toast.makeText(getActivity(), getActivity().getString(R.string.operation_success_message), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Operation successfull");
                            //FOr BT transport type refresh Bt Pairing again

                        }else
                        {
                            //progressDialog.cancel();
                            Intent intent;
                            intent = new Intent(getActivity(), DeviceDiscoverActivity.class);
                            intent.putExtra("enable_toolbar", false);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void onDataReceived(String data) {
        if (data.contains("Command:changeconfig")) {
            handleStatusResponse(data);
        }
    }

    public void RFIDReaderDisappeared(final ReaderDevice readerDevice) {
        //Intent intent;
        //intent = new Intent(getActivity(), DeviceDiscoverActivity.class);
        //intent.putExtra("enable_toolbar", false);
        //startActivity(intent);
        RFIDController.readersList.remove(readerDevice);

        if (getActivity() == null) return;
        if (progressDialog != null) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void handleStatusResponse(final String data) {
        if(getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Response_Status statusData = Response_Status.FromString(data);
                if (statusData.command.trim().equalsIgnoreCase(Command_ChangeConfig.commandName)) {
                    if (statusData.Status.trim().equalsIgnoreCase("OK")) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                if (getActivity() == null)
                                    return null;
                                int loopCount = 0;
                                do{
                                    try {
                                        Thread.sleep(1000);
                                        loopCount++;
                                    } catch (InterruptedException e) {
                                       Log.d(TAG,  "Returned SDK Exception");
                                        break;
                                    }

                                    Log.d(TAG, "Reset in Progress, Reader not attached" );

                                }while((RFIDController.mConnectedDevice != null) && (loopCount < 5));
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                if (getActivity() == null) return;
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                                if (RFIDController.mConnectedDevice != null) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.operation_success_message), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Operation successfull");

                                }
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.operation_failure_message) + statusData.Status, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, getActivity().getString(R.string.operation_failure_message) + statusData.Status);
                    }
                }
            }
        });
    }


    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
    }

    public void RFIDReaderAppeared(ReaderDevice readerDevice) {

        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }

    }
}
