package com.vantageclient.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vantageclient.R;
import com.vantageclient.data.DBHandler;
import com.vantageclient.data.DeviceItem;

public class DeviceEditDialogFragment extends DialogFragment {

    private EditText inputDeviceName, inputIP, inputPort, inputUsername, inputPassword;
    private TextInputLayout inputLayoutDeviceName, inputLayoutIP, inputLayoutPort, inputLayoutUsername, inputLayoutPassword;
    Button btnEdit;
    private DBHandler dbHandler;

    EditDeviceListener activityCallback;

    public interface EditDeviceListener {
        void onEditDeviceClick(DeviceItem item, String oldDevice);
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_deviceedit);
        dialog.show();
        dbHandler = new DBHandler().open(getActivity());

        inputLayoutDeviceName = (TextInputLayout) dialog.findViewById(R.id.input_layout_devicename);
        inputLayoutIP = (TextInputLayout)  dialog.findViewById(R.id.input_layout_ip);
        inputLayoutPort = (TextInputLayout)  dialog.findViewById(R.id.input_layout_port);
        inputLayoutUsername = (TextInputLayout)  dialog.findViewById(R.id.input_layout_username);
        inputLayoutPassword = (TextInputLayout)  dialog.findViewById(R.id.input_layout_password);

        inputDeviceName = (EditText) dialog.findViewById(R.id.input_devicename);
        inputIP = (EditText) dialog.findViewById(R.id.input_ip);
        inputPort = (EditText) dialog.findViewById(R.id.input_port);
        inputUsername = (EditText) dialog.findViewById(R.id.input_username);
        inputPassword = (EditText) dialog.findViewById(R.id.input_password);
        btnEdit = (Button) dialog.findViewById(R.id.btn_edit);

        findDevice();

        inputDeviceName.addTextChangedListener(new MyTextWatcher(inputDeviceName));
        inputIP.addTextChangedListener(new MyTextWatcher(inputIP));
        inputPort.addTextChangedListener(new MyTextWatcher(inputPort));
        inputUsername.addTextChangedListener(new MyTextWatcher(inputUsername));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateDeviceName()) {
                    return;
                }

                if (!validateIP()) {
                    return;
                }

                if (!validatePort()) {
                    return;
                }

                if (!validateUsername()) {
                    return;
                }

                editDevice();
            }
        });

        return dialog;
    }

    private boolean validateDeviceName() {
        if (inputDeviceName.getText().toString().trim().isEmpty()) {
            inputLayoutDeviceName.setError(getString(R.string.err_msg_devicename));
            requestFocus(inputDeviceName);
            return false;
        } else {
            inputLayoutDeviceName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateIP() {
        String ip = inputIP.getText().toString().trim();

        if (ip.isEmpty() || !isValidIP(ip)) {
            inputLayoutIP.setError(getString(R.string.err_msg_ip));
            requestFocus(inputIP);
            return false;
        } else {
            inputLayoutIP.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePort() {
        if (inputPort.getText().toString().trim().isEmpty()) {
            inputLayoutPort.setError(getString(R.string.err_msg_port));
            requestFocus(inputPort);
            return false;
        } else {
            inputLayoutPort.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateUsername() {
        if (inputUsername.getText().toString().trim().isEmpty()) {
            inputLayoutUsername.setError(getString(R.string.err_msg_username));
            requestFocus(inputUsername);
            return false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidIP(String ip) {
        return !TextUtils.isEmpty(ip) && Patterns.IP_ADDRESS.matcher(ip).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_devicename:
                    validateDeviceName();
                    break;
                case R.id.input_ip:
                    validateIP();
                    break;
                case R.id.input_port:
                    validatePort();
                    break;
                case R.id.input_username:
                    validateUsername();
                    break;
            }
        }
    }

    public void editDevice() {

        String oldDevice = getArguments().getString("devicename");
        String devicename = inputDeviceName.getText().toString();
        String ip = inputIP.getText().toString();
        int port = Integer.parseInt(inputPort.getText().toString());
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        inputDeviceName.setText("");
        inputIP.setText("");
        inputPort.setText("");
        inputUsername.setText("");
        inputPassword.setText("");

        DeviceItem item = new DeviceItem(devicename, ip, port, username, password);
        activityCallback.onEditDeviceClick(item, oldDevice);
        getDialog().dismiss();
    }

    public void findDevice() {

        String devicename = getArguments().getString("devicename");
        DeviceItem item = dbHandler.findDevice(devicename);

        inputDeviceName.setText(devicename);
        inputIP.setText(String.valueOf(item.getIP()));
        inputPort.setText(String.valueOf(item.getPort()));
        inputUsername.setText(String.valueOf(item.getUsername()));
        inputPassword.setText(String.valueOf(item.getPassword())
    );
    }

    public static final DeviceEditDialogFragment newInstance(String devicename) {
        DeviceEditDialogFragment df = new DeviceEditDialogFragment();
        Bundle args = new Bundle();
        args.putString("devicename", devicename);
        df.setArguments(args);
        return df;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            activityCallback = (EditDeviceListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement AddDeviceListener");
        }
    }
}

