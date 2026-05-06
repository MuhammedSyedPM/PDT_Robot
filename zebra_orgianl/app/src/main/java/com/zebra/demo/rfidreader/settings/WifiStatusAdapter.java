package com.zebra.demo.rfidreader.settings;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.demo.R;

import java.util.ArrayList;


public class WifiStatusAdapter extends BaseAdapter {
    Activity context;
    ArrayList<String> wifiStatus;

    public WifiStatusAdapter(Activity context, ArrayList<String> status) {
        super();
        this.context = context;
        this.wifiStatus = status;
    }

    public int getCount() {
        return wifiStatus.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        TextView tvStatusKey;
        TextView tvStatusValue;
    }

    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (view == null) {
            view = inflater.inflate(R.layout.wifi_status_adaptor, parent, false);
            holder = new ViewHolder();
            holder.tvStatusKey = view.findViewById(R.id.status_key);
            holder.tvStatusValue = view.findViewById(R.id.status_value);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String[] statusParts = wifiStatus.get(position).split("  ");
        if (statusParts.length > 0) {
            holder.tvStatusKey.setText(statusParts[0]);
            if (statusParts.length > 1) {
                holder.tvStatusValue.setText(statusParts[1]);
            } else {
                holder.tvStatusValue.setText("");
            }
        }
        return view;
    }

}
