package com.zebra.demo.rfidreader.settings.endpoint;


import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedReader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.ENUM_EP_OPERATION;
import com.zebra.rfid.api3.EndpointConfigurationInfo;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.IotConfigInfo;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.util.ArrayList;

public class EndpointAdapter extends RecyclerView.Adapter<EndpointAdapter.ViewHolder> {

    private static final String TAG = "EndpointAdapter";
    ArrayList<String> endpointNames;
    private int selectedPosition;

    public EndpointAdapter(ArrayList<String> endpointNames, int selectedPosition){
        this.endpointNames = endpointNames;
        this.selectedPosition = selectedPosition;
    }


    @NonNull
    @Override
    public EndpointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.content_endpoint, parent, false);
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull EndpointAdapter.ViewHolder holder, int position) {
        holder.setData(endpointNames.get(position), position);
    }

    @Override
    public int getItemCount() {
        return endpointNames.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvendpointName;
        private final CheckBox cbActivate;
        private int viewPosition = -1;

        public ViewHolder(View itemView) {
            super(itemView);
            tvendpointName = itemView.findViewById(R.id.tv_ep_name);
            ImageButton btRemove = itemView.findViewById(R.id.bt_ep_remove);
            ImageButton btEdit = itemView.findViewById(R.id.bt_ep_edit);
            cbActivate = itemView.findViewById(R.id.activate);
            btRemove.setOnClickListener(v -> {
                try {
                    EndpointConfigurationInfo endpointConfigurationInfo = new EndpointConfigurationInfo();
                    endpointConfigurationInfo.setOperation(ENUM_EP_OPERATION.DELETE);
                    endpointConfigurationInfo.setEpname(endpointNames.get(viewPosition));

                    RFIDResults rfidResults = mConnectedReader.Config.setEndpointConfiguration(endpointConfigurationInfo);
                    Log.d(TAG, "delete EndpointConfiguration res = " + rfidResults);

                    if(rfidResults.equals(RFIDResults.RFID_API_SUCCESS)){
                        if(cbActivate.isChecked() == true ) {
                            cbActivate.setChecked(false);
                            selectedPosition = -1;
                        }
                        else if (viewPosition < selectedPosition) {
                            selectedPosition--;
                        }
                        endpointNames.clear();
                        endpointNames = RFIDController.mConnectedReader.Config.getEndpointNames();
                        notifyDataSetChanged();
                    }

                } catch (InvalidUsageException | OperationFailureException e) {
                    Log.d(TAG, "Returned SDK Exception endpointAdapter");
                }
            });

            btEdit.setOnClickListener(v -> EndpointConfigFragment.getInstance().editEndpoint(viewPosition));

            cbActivate.setOnClickListener(v -> {
                if(cbActivate.isChecked()){
                    try {
                        IotConfigInfo iotConfigInfo = new IotConfigInfo();
                        iotConfigInfo.setActivemgmtep(endpointNames.get(viewPosition));
                        mConnectedReader.Config.activateEndpoint(iotConfigInfo);
                        int previouslySelectedPosition = selectedPosition;
                        selectedPosition = viewPosition;
                        notifyItemChanged(previouslySelectedPosition);
                        notifyItemChanged(selectedPosition);
                    } catch (InvalidUsageException | OperationFailureException e) {
                        Log.d(TAG, "Returned SDK Exception endpointAdapter");
                        Toast.makeText(v.getContext(), "Endpoint Activation Failed", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    try {
                        IotConfigInfo iotConfigInfo = new IotConfigInfo();
                        iotConfigInfo.setActivemgmtep("");
                        mConnectedReader.Config.activateEndpoint(iotConfigInfo);
                        //int previouslySelectedPosition = selectedPosition;
                        //selectedPosition = viewPosition;
                        //notifyItemChanged(previouslySelectedPosition);
                        //notifyItemChanged(selectedPosition);
                    } catch (InvalidUsageException | OperationFailureException e) {
                        Log.d(TAG, "Returned SDK Exception endpointAdapter");
                        Toast.makeText(v.getContext(), "Endpoint Activation Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        public void setData(String endPointName, int position) {
            tvendpointName.setText(endPointName);
            viewPosition = position;
            cbActivate.setChecked(position == selectedPosition);
        }
    }
}
