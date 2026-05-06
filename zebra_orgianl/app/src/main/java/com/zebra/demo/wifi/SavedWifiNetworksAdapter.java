package com.zebra.demo.wifi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.WifiProfile;
import com.zebra.rfid.api3.WifiScanData;

import java.util.List;

public class SavedWifiNetworksAdapter extends RecyclerView.Adapter<SavedWifiNetworksAdapter.ViewHolder> {

    Context mContext;
    TextView tv_wifiConnectDetails;
    String readerName;
    List<WifiProfile> wifiInfo;
    public SavedWifiNetworksAdapter(Context mContext, List<WifiProfile> wifiInfo) {
        this.mContext = mContext;
        this.wifiInfo = wifiInfo;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saved_wifi_network,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WifiProfile savedWifiInfo = wifiInfo.get(position);

        holder.savedWifiIcon.setImageResource(R.drawable.ic_wifi_nw);
        holder.savedWifiName.setText(savedWifiInfo.getssid());
        holder.savedWifiDescription.setText("Tap to share WiFi access from network");
        //SSSif(savedWifiInfo.getprotocol().lock == true) {
            holder.savedWifiLock.setImageResource(R.drawable.ic_lock);
        //SSS}

        readerName = savedWifiInfo.getssid();
    }

    public void updateList (List<WifiProfile> items) {
        wifiInfo.clear();
        wifiInfo.addAll(items);
        notifyDataSetChanged();

    }




    @Override
    public int getItemCount() {
        return wifiInfo.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView savedWifiIcon ,savedWifiLock;
        TextView savedWifiName,savedWifiDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            savedWifiIcon = itemView.findViewById(R.id.saved_wif_icon);
            savedWifiName = itemView.findViewById(R.id.saved_wifi_name);
            savedWifiDescription = itemView.findViewById(R.id.saved_wifi_description);
            savedWifiLock = itemView.findViewById(R.id.saved_wifi_lock);
            itemView.setOnClickListener(v -> {
                if(RFIDController.mConnectedReader != null) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetTheme);
                    View sheetView = LayoutInflater.from(mContext).inflate(R.layout.wifi_bottom_sheet_layout, (ViewGroup) itemView.findViewById(R.id.bottom_sheet));

                    tv_wifiConnectDetails = sheetView.findViewById(R.id.wificonnectdetails);
                    tv_wifiConnectDetails.setText(String.format(mContext.getResources().getString(R.string.wificonnectdetails), wifiInfo.get(getAbsoluteAdapterPosition()).getssid(), RFIDController.mConnectedReader.getHostName()));

                    sheetView.findViewById(R.id.wifi_share_access).setOnClickListener(v1 -> {
                        bottomSheetDialog.cancel();
                        ReaderWifiSettingsFragment.getInstance().ConnectWifi(getAbsoluteAdapterPosition(), wifiInfo);
                    });
                    sheetView.findViewById(R.id.wifi_delete_profile).setOnClickListener(v2 -> {
                        bottomSheetDialog.dismiss();
                        ReaderWifiSettingsFragment.getInstance().DeleteProfile(getAbsoluteAdapterPosition(), wifiInfo);
                    });

                    sheetView.findViewById(R.id.bottom_sheet_close).setOnClickListener(v3 -> bottomSheetDialog.dismiss());
                    bottomSheetDialog.setContentView(sheetView);
                    bottomSheetDialog.show();
                }else{
                    Toast.makeText(mContext, "Reader not connected", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




}
