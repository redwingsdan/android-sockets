package com.vantageclient.adapter;


import com.vantageclient.R;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.vantageclient.activity.CameraListDialog.CameraInfo;

import java.util.List;

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.ViewHolder> {

    private List<CameraInfo> cameraList;

    public CameraListAdapter(List<CameraInfo> cameraList){
        this.cameraList = cameraList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cameraName.setText(cameraList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView cameraName;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            cameraName = (TextView) itemView.findViewById(R.id.camera_name);
        }
    }
}

