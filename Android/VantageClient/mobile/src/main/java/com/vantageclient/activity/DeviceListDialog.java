package com.vantageclient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vantageclient.R;
import com.vantageclient.adapter.DeviceListAdapter;
import com.vantageclient.adapter.ItemClickAdapter;
import com.vantageclient.data.DBHandler;

public class DeviceListDialog extends DialogFragment {

    private DeviceListAdapter adapter;
    private DBHandler dbHandler;
    private RecyclerView rv;
    private DialogDismissListener activityCallback;
    private String devicename;

    public interface DialogDismissListener {
        void onDismissDialog(String devicename);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.dialog_devicelist, null);

        rv = (RecyclerView) view.findViewById(R.id.devicelist_rv);
        dbHandler = new DBHandler().open(this.getActivity());
        initializeAdapter();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);



        ItemClickAdapter.addTo(rv).setOnItemClickListener(new ItemClickAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                devicename = ((TextView) v.findViewById(R.id.device_name)).getText().toString();
                activityCallback.onDismissDialog(devicename);
                getDialog().dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
        .setView(view)
        .setTitle(R.string.btnLoginText)
        .create();
    }

    public void initializeAdapter() {
        try {

            adapter = new DeviceListAdapter(getActivity(), dbHandler.getDevices());
            rv.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("ServerList, null", e.getLocalizedMessage());
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            activityCallback = (DialogDismissListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement AddDeviceListener");
        }
    }
}