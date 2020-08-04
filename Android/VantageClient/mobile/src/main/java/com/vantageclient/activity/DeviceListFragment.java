package com.vantageclient.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vantageclient.R;
import com.vantageclient.adapter.DeviceListAdapter;
import com.vantageclient.adapter.ItemClickAdapter;
import com.vantageclient.data.DBHandler;
import com.vantageclient.data.DeviceItem;

public class DeviceListFragment extends Fragment {

    private DeviceListAdapter adapter;
    private DBHandler dbHandler;
    private RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {

        View view = inflater.inflate(R.layout.devicelist, container, false);

        rv = (RecyclerView)view.findViewById(R.id.rv);
        dbHandler = new DBHandler().open(this.getActivity());

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceAddFragment dialog = DeviceAddFragment.newInstance();
                dialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AventuraTheme);
                dialog.show(getChildFragmentManager(), "deviceAddDialog");
            }
        });

        ItemClickAdapter.addTo(rv).setOnItemClickListener(new ItemClickAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                TextView tv = (TextView) v.findViewById(R.id.device_name);
                String devicename = tv.getText().toString();

            }
        });

        ItemClickAdapter.addTo(rv).setOnItemLongClickListener(new ItemClickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                TextView tv = (TextView) v.findViewById(R.id.device_name);
                String devicename = tv.getText().toString();

                DeviceListDialogFragment dialog = DeviceListDialogFragment.newInstance(devicename);
                dialog.show(getActivity().getFragmentManager(), "deviceListDialog");
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new UpdateRV().execute();
    }

    public void addDevice(DeviceItem item) {
        dbHandler.addDevice(item);
        new UpdateRV().execute();
        Toast.makeText(getActivity(), "Device Added!", Toast.LENGTH_SHORT).show();
    }

    public void editDevice(DeviceItem item, String oldDevice) {
        dbHandler.editDevice(item, oldDevice);
        new UpdateRV().execute();
        Toast.makeText(getActivity(), "Device Changes Saved!", Toast.LENGTH_SHORT).show();
    }

    public void deleteDevice(String devicename) {
        dbHandler.deleteDevice(devicename);
        new UpdateRV().execute();
        Toast.makeText(getActivity(), "Device Deleted!", Toast.LENGTH_SHORT).show();
    }

    private class UpdateRV extends AsyncTask<Void, Void, Void> {

        protected void onPostExecute(Void result) {
            //adapter.notifyDataSetChanged();
            adapter.notifyItemChanged(0, adapter.getItemCount());
            rv.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                adapter = new DeviceListAdapter(getContext(), dbHandler.getDevices());
                return null;

            } catch (Exception e) {
                Log.e("ServerList", e.getLocalizedMessage());
                return null;
            }
        }
    }

}