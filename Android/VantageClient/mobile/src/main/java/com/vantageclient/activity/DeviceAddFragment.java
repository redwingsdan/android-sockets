package com.vantageclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.vantageclient.R;
import com.vantageclient.data.DeviceItem;

public class DeviceAddFragment extends DialogFragment {

    AddDeviceListener activityCallback;

    public interface AddDeviceListener {
        void onAddDeviceClick(DeviceItem item);
    }

    private EditText inputDeviceName, inputIP, inputPort, inputUsername, inputPassword;
    private TextInputLayout inputLayoutDeviceName, inputLayoutIP, inputLayoutPort, inputLayoutUsername, inputLayoutPassword;
    Button btnAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_deviceadd, container, false);

        inputLayoutDeviceName = (TextInputLayout) view.findViewById(R.id.input_layout_devicename);
        inputLayoutIP = (TextInputLayout) view.findViewById(R.id.input_layout_ip);
        inputLayoutPort = (TextInputLayout) view.findViewById(R.id.input_layout_port);
        inputLayoutUsername = (TextInputLayout) view.findViewById(R.id.input_layout_username);
        inputLayoutPassword = (TextInputLayout) view.findViewById(R.id.input_layout_password);

        inputDeviceName = (EditText) view.findViewById(R.id.input_devicename);
        inputIP = (EditText) view.findViewById(R.id.input_ip);
        inputPort = (EditText) view.findViewById(R.id.input_port);
        inputUsername = (EditText) view.findViewById(R.id.input_username);
        inputPassword = (EditText) view.findViewById(R.id.input_password);
        btnAdd = (Button) view.findViewById(R.id.btn_add);

        inputDeviceName.addTextChangedListener(new MyTextWatcher(inputDeviceName));
        inputIP.addTextChangedListener(new MyTextWatcher(inputIP));
        inputPort.addTextChangedListener(new MyTextWatcher(inputPort));
        inputUsername.addTextChangedListener(new MyTextWatcher(inputUsername));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        return view;
    }

    /**
     * Validating form
     */
    private void submitForm() {
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

        addDevice();
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
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

    public void addDevice () {

        DeviceItem item = new DeviceItem();

        item.setDeviceName(inputDeviceName.getText().toString());
        item.setIP(inputIP.getText().toString());
        item.setPort(Integer.parseInt(inputPort.getText().toString()));
        item.setUsername(inputUsername.getText().toString());
        item.setPassword(inputPassword.getText().toString());

        inputDeviceName.setText("");
        inputIP.setText("");
        inputPort.setText("");
        inputUsername.setText("");
        inputPassword.setText("");

        activityCallback.onAddDeviceClick(item);
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            activityCallback = (AddDeviceListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement AddDeviceListener");
        }
    }

    public static DeviceAddFragment newInstance() {
        return new DeviceAddFragment();
    }
}
