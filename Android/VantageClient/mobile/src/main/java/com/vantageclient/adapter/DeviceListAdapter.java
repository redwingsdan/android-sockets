package com.vantageclient.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.vantageclient.R;
import com.vantageclient.data.DBHandler;
import com.vantageclient.data.DeviceItem;

public class DeviceListAdapter extends CursorRecyclerViewAdapter<DeviceListAdapter.DeviceViewHolder> {

    public DeviceListAdapter(Context context,Cursor cursor){
        super(context, cursor);
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView deviceName;
        TextView deviceType;
        ImageView deviceImage;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceType = (TextView) itemView.findViewById(R.id.device_type);
            deviceImage = (ImageView) itemView.findViewById(R.id.device_image);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_item, viewGroup, false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder deviceViewHolder, Cursor cursor) {
        DeviceItem deviceItem = DeviceItem.fromCursor(cursor);
        deviceViewHolder.deviceName.setText(deviceItem.getDeviceName());
        deviceViewHolder.deviceType.setText(deviceItem.getIP());

    }
}