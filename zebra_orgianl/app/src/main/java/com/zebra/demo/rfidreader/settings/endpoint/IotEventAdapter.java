package com.zebra.demo.rfidreader.settings.endpoint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.demo.R;

import java.util.List;

public class IotEventAdapter extends RecyclerView.Adapter<IotEventAdapter.IotEventViewHolder> {

    private final List<IotEventData> iotEventList;

    public IotEventAdapter(List<IotEventData> iotEventList) {
        this.iotEventList = iotEventList;
    }

    @NonNull
    @Override
    public IotEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_iot_event, parent, false);
        return new IotEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IotEventViewHolder holder, int position) {
        IotEventData event = iotEventList.get(position);
        holder.causeTextView.setText(event.getCause());
        holder.epTypeTextView.setText(event.getEpType());
        holder.epNameTextView.setText(event.getEpName());
        holder.statusTextView.setText(event.getStatus());
        holder.reasonTextView.setText(event.getReason());
    }

    @Override
    public int getItemCount() {
        return iotEventList.size();
    }

    static class IotEventViewHolder extends RecyclerView.ViewHolder {
        TextView causeTextView, epTypeTextView, epNameTextView, statusTextView, reasonTextView;

        public IotEventViewHolder(@NonNull View itemView) {
            super(itemView);
            causeTextView = itemView.findViewById(R.id.tv_cause);
            epTypeTextView = itemView.findViewById(R.id.tv_ep_type);
            epNameTextView = itemView.findViewById(R.id.tv_ep_name);
            statusTextView = itemView.findViewById(R.id.tv_status);
            reasonTextView = itemView.findViewById(R.id.tv_reason);
        }
    }
}