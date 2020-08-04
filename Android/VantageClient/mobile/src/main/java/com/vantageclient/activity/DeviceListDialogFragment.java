package com.vantageclient.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.vantageclient.R;
import com.vantageclient.data.DBHandler;
import com.vantageclient.data.DeviceItem;

public class DeviceListDialogFragment extends DialogFragment {

    DeleteDeviceListener activityCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String devicename = getArguments().getString("devicename");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title)
                .setItems(R.array.dialog_labels, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {
                            editDevice(devicename);
                            dismiss();
                        } else {
                            deleteDevice();
                            dismiss();
                        }
                        dismiss();
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }

    public void deleteDevice() {
        String devicename = getArguments().getString("devicename");
        activityCallback.onDeleteDeviceClick(devicename);
    }

    public void editDevice(String devicename) {
        DeviceEditDialogFragment dialog = DeviceEditDialogFragment.newInstance(devicename);
        dialog.show(getFragmentManager(), "deviceEdit");
    }

    public static final DeviceListDialogFragment newInstance(String devicename) {
        DeviceListDialogFragment df = new DeviceListDialogFragment();
        Bundle args = new Bundle();
        args.putString("devicename", devicename);
        df.setArguments(args);
        return df;
    }

    public interface DeleteDeviceListener {
        void onDeleteDeviceClick(String devicename);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            activityCallback = (DeleteDeviceListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement DeleteDeviceListener");
        }
    }
}