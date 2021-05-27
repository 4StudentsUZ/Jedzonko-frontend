package com.fourstudents.jedzonko.Adapters.Shared;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.R;

import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolderClass> {
    List<BluetoothDevice> deviceList;
    private final OnDeviceListener onDeviceListener;

    public BluetoothDeviceAdapter(List<BluetoothDevice> deviceList, OnDeviceListener onDeviceListener) {
        this.deviceList = deviceList;
        this.onDeviceListener = onDeviceListener;
//        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list, parent, false);
        return new ViewHolderClass(view, onDeviceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        BluetoothDevice device = deviceList.get(position);
        holder.name.setText(device.getName());
        holder.address.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView address;
        OnDeviceListener onDeviceListener;

        public ViewHolderClass(@NonNull View itemView, OnDeviceListener onDeviceListener) {
            super(itemView);
            name = itemView.findViewById(R.id.device_name);
            address = itemView.findViewById(R.id.device_address);
            this.onDeviceListener = onDeviceListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDeviceListener.onDeviceClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnDeviceListener {
        void onDeviceClick(int position);
    }

}
