package com.zebra.demo.rfidreader.settings.certificates;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.util.List;

public class CertificatesAdapter extends RecyclerView.Adapter<CertificatesAdapter.ViewHolder> {

    private List<String> certificateList;
    private static final String TAG = "WiFiCertificatesAdapter";

    // data is passed into the constructor
    public CertificatesAdapter(List<String> certificates) {
        this.certificateList = certificates;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.content_certificates, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCertName.setText(certificateList.get(position));
        holder.btRemove.setOnClickListener(v -> {
            try {
                RFIDResults rfidResults = RFIDController.mConnectedReader.Config.removeCertificate(certificateList.get(position));
                if(rfidResults.equals(RFIDResults.RFID_API_SUCCESS)){
                    certificateList.clear();
                    certificateList = RFIDController.mConnectedReader.Config.getCertificates();
                    notifyDataSetChanged();
                }
            } catch (InvalidUsageException e) {
                Log.d(TAG, "Returned SDK Exception wifiGetCertificates");
            } catch (OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                    Log.d(TAG,e.getVendorMessage());
                }
                Log.d(TAG, "Returned SDK Exception wifiGetCertificates");
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return certificateList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCertName;
        Button btRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCertName = itemView.findViewById(R.id.tv_cert_name);
            btRemove = itemView.findViewById(R.id.bt_cert_remove);
        }
    }

    public void updateList (List<String> items) {
        certificateList.clear();
        certificateList.addAll(items);
        notifyDataSetChanged();

    }

}