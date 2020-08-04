package com.vantageclient.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.vantageclient.R;
import com.vantageclient.data.DBHandler;
import com.vantageclient.data.DeviceItem;
import com.vantageclient.net.CGIHelper;

import java.util.Timer;
import java.util.TimerTask;

public class LiveViewFragment extends Fragment
        implements View.OnTouchListener, View.OnClickListener {

    DBHandler dbHandler;
    private LoginToHost loginHost;
    private ProgressDialog _progressDialog;
    DialogFragment deviceListDialog = null;
    DialogFragment cameraListDialog = null;

    Button btnSplit1;
    Button btnSplit4;
    Button btnSplit9;
    Button btnSplit12;
    Button smallerbiggerbutton;

    private LinearLayout channelContainer, linearLayoutRow1, linearLayoutRow2, linearLayoutRow3, linearLayoutRow4;
    LayoutParams LLParams;

    //YOU MUST CHANGE THIS IF YOU ADD MORE CHANNELS (+1)
    private int channelCount = 13;
    private int currentSplit = 0;
    private View[] channels = new View[channelCount];
    private SurfaceView[] surfaces = new SurfaceView[channelCount];
    private ProgressBar[] progressBars = new ProgressBar[channelCount];
    private Button[] addDevice = new Button[channelCount];
    private Button[] removeDevice = new Button[channelCount];

    private int id;
    private String deviceName;
    private int position[] = new int[channelCount];
    public String[] _session = new String[channelCount];
    private String[] deviceIP = new String[channelCount];
    private int[] devicePort = new int[channelCount];
    private String[] deviceUsername = new String[channelCount];
    private String[] devicePassword = new String[channelCount];

    private SurfaceHolder[] surfaceHolder = new SurfaceHolder[channelCount];
    private CameraView[] cameraView = new CameraView[channelCount];

    private static final String TILT_UP = "Up";
    private static final String TILT_DOWN = "Down";
    private static final String TURN_RIGHT = "Right";
    private static final String TURN_LEFT = "Left";
    private static final String LEFT_UP = "LeftUp";
    private static final String RIGHT_UP = "RightUp";
    private static final String LEFT_DOWN = "LeftDown";
    private static final String RIGHT_DOWN = "RightDown";
    private static final String STOP = "StopAction";
    private static final String FOCUS_NEAR = "FocusNear";
    private static final String FOCUS_FAR = "FocusFar";
    private static final String IRIS_OPEN = "IrisOpen";
    private static final String IRIS_CLOSE = "IrisClose";

    private float _oldX = 0;
    private float _oldY = 0;
    private float _oldDist = 0;

    private boolean _isZoom = false;
    private boolean _isPan = false;
    private long doubleClick = 200;
    private long timestampLastClick;
    boolean isFullScreen = false;

    private int zoomState = -1;
    private String _currentCommand = "";

    public LiveViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.liveview, container, false);
        //View v = inflater.inflate(R.layout.channels, container, true);

        channelContainer = (LinearLayout) view.findViewById(R.id.ChannelContainer);

        for (int i = 1; i < channelCount; i++) {
            int resId = getResources().getIdentifier("liveview_channel" + (i), "id", "com.vantageclient");
            channels[i] =  view.findViewById(resId);
            surfaces[i] = (SurfaceView) channels[i].findViewById(R.id.SurfaceView);
            surfaces[i].setOnTouchListener(this);
            surfaces[i].setTag(i);
            progressBars[i] = (ProgressBar) channels[i].findViewById(R.id.pgbLoading);
            progressBars[i].setVisibility(View.INVISIBLE);
            addDevice[i] = (Button) channels[i].findViewById(R.id.channel_add);
            addDevice[i].setOnClickListener(this);
            addDevice[i].setTag(i);
            removeDevice[i] = (Button) channels[i].findViewById(R.id.channel_clear);
            removeDevice[i].setOnClickListener(this);
            removeDevice[i].setTag(i);
        }

        btnSplit1 = (Button) view.findViewById(R.id.btn_1split);
        btnSplit1.setOnClickListener(this);
        btnSplit4 = (Button) view.findViewById(R.id.btn_4split);
        btnSplit4.setOnClickListener(this);
        btnSplit9 = (Button) view.findViewById(R.id.btn_9split);
        btnSplit9.setOnClickListener(this);
        btnSplit12 = (Button) view.findViewById(R.id.btn_12split);
        btnSplit12.setOnClickListener(this);

        ButtonTouchListener listener = new ButtonTouchListener();
        (view.findViewById(R.id.btn_focus_up)).setOnTouchListener(listener);
        (view.findViewById(R.id.btn_focus_down)).setOnTouchListener(listener);
        (view.findViewById(R.id.dvr_iris_up)).setOnTouchListener(listener);
        (view.findViewById(R.id.dvr_iris_down)).setOnTouchListener(listener);

        smallerbiggerbutton = (Button)view.findViewById(R.id.BiggerSmallerButton);
        smallerbiggerbutton.setOnTouchListener(listener);
        smallerbiggerbutton.setText("<");

        liveViewLayout();

        return view;
    }

    public void onClick(View v) {
        // Perform action on click
        switch (v.getId()) {
            case R.id.channel_add:
                id = (Integer) v.getTag();
                addDevice[id].setVisibility(View.INVISIBLE);
                removeDevice[id].setVisibility(View.VISIBLE);
                surfaces[id].setVisibility(View.VISIBLE);
                surfaceHolder[id] = surfaces[id].getHolder();
                getDevice();
                break;
            case R.id.channel_clear:
                id = (Integer) v.getTag();
                try {
                    cameraView[id].closeChannel();
                }
                catch (Exception e) {
                }
                addDevice[id].setVisibility(View.VISIBLE);
                removeDevice[id].setVisibility(View.INVISIBLE);
                surfaces[id].setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_1split:
                split1(currentSplit);
                break;
            case R.id.btn_4split:
                split4(currentSplit);
                break;
            case R.id.btn_9split:
                split9(currentSplit);
                break;
            case R.id.btn_12split:
                split12(currentSplit);
                break;
        }
    }

    public boolean onTouch(View v, MotionEvent event) {

        id = (Integer) v.getTag();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                _isPan = true;
                _oldX = event.getX();
                _oldY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                if (zoomState != -1 || !"".equalsIgnoreCase(_currentCommand)) {
                    cameraView[id].ptzCommand(_session[id], position[id], STOP);
                }
                else if (zoomState == -1 && "".equalsIgnoreCase(_currentCommand)) {
                    if ((System.currentTimeMillis() - timestampLastClick) < doubleClick) {
                        {
                            if (!isFullScreen) {
                                this.makeFullScreen(id);
                            } else {
                                this.returnSplit(currentSplit);
                            }
                            //Toast.makeText(getApplicationContext(),"ENLARGE CAMERA  " + _channel, Toast.LENGTH_SHORT).show();
                        }
                    }
                    timestampLastClick = System.currentTimeMillis();
                }
                _currentCommand = "";
                _isZoom = false;
                zoomState = -1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // if (zoomState != -1 || !"".equalsIgnoreCase(_currentCommand)) {
                cameraView[id].ptzCommand(_session[id], position[id], STOP);
                // }
                _isPan = false;
                _isZoom = false;
                zoomState = -1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                _oldDist = spacing(event);
                _isPan = false;
                _isZoom = true;
                _currentCommand = "";
                break;
            case MotionEvent.ACTION_MOVE:
                if (_isZoom) {
                    float newDist = spacing(event);
                    if (newDist - 10 > _oldDist) {
                        if (zoomState != 0) {
                            zoomState = 0;
                            cameraView[id].ptzCommand(_session[id], position[id], "ZoomIn");
                        }
                    } else if (newDist + 10 < _oldDist) {
                        if (zoomState != 1) {
                            zoomState = 1;
                            cameraView[id].ptzCommand(_session[id], position[id], "ZoomOut");
                        }
                    }
                } else if (_isPan) {
                    float x = event.getX();
                    float y = event.getY();

                    float disX = x - _oldX;
                    float disY = y - _oldY;

                    if (Math.abs(disX) > 20 || Math.abs(disY) > 20) {
                        String command;

                        double radians = Math.atan((disY * disY) / (disX * disX));
                        double degrees = Math.toDegrees(radians);

                        int degreeType;
                        if (degrees > 20 && degrees < 70) {
                            degreeType = 1;
                        } else if (degrees <= 20) {
                            degreeType = 0;
                        } else {
                            degreeType = 2;
                        }

                        if (disX > 0) {
                            if (degreeType == 0) {
                                command = TURN_RIGHT;
                            } else if (degreeType == 2) {
                                command = disY < 0 ? TILT_UP : TILT_DOWN;
                            } else {
                                command = disY < 0 ? RIGHT_UP : RIGHT_DOWN;
                            }
                        } else {
                            if (degreeType == 0) {
                                command = TURN_LEFT;
                            } else if (degreeType == 2) {
                                command = disY < 0 ? TILT_UP : TILT_DOWN;
                            } else {
                                command = disY < 0 ? LEFT_UP : LEFT_DOWN;
                            }
                        }

                        if (!command.equalsIgnoreCase(_currentCommand)) {
                            try {
                                cameraView[id].ptzCommand(_session[id], position[id], command);
                                _currentCommand = command;
                            }
                            catch (Exception e) {

                            }
                        }
                    }
                }
        }
        return true;
    }

    public void makeFullScreen(int viewNumber) {

        for (int i = 1; i < channelCount; i++) {
            channels[i].setVisibility(View.INVISIBLE);
        }

        linearLayoutRow1.setVisibility(View.INVISIBLE);
        linearLayoutRow2.setVisibility(View.INVISIBLE);
        linearLayoutRow3.setVisibility(View.INVISIBLE);
        linearLayoutRow4.setVisibility(View.INVISIBLE);

        if (viewNumber == 1 || viewNumber == 2 || viewNumber == 3) {
            if (viewNumber == 3 && currentSplit == 4){
                linearLayoutRow2.setLayoutParams(
                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
                linearLayoutRow2.setVisibility(View.VISIBLE);
            }
            else {
                linearLayoutRow1.setLayoutParams(
                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
                linearLayoutRow1.setVisibility(View.VISIBLE);
            }
        }
        if (viewNumber == 4 || viewNumber == 5 || viewNumber == 6) {

            linearLayoutRow2.setLayoutParams(
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
            linearLayoutRow2.setVisibility(View.VISIBLE);
        }
        if (viewNumber == 7 || viewNumber == 8 || viewNumber == 9) {

            linearLayoutRow3.setLayoutParams(
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
            linearLayoutRow3.setVisibility(View.VISIBLE);
        }
        if (viewNumber == 10 || viewNumber == 11 || viewNumber == 12) {

            linearLayoutRow4.setLayoutParams(
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
            linearLayoutRow4.setVisibility(View.VISIBLE);
        }

        channels[viewNumber].setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
        channels[viewNumber].setVisibility(View.VISIBLE);
        isFullScreen = true;
    }

    public void returnSplit(int currentSplit) {
        int split = 50;
        if (currentSplit == 1) {
            split1(split);
        }
        else if (currentSplit == 4) {
            split4(split);
        }
        else if (currentSplit == 9) {
            split9(split);
        }
        else {
            split12(split);
        }

    }

    boolean canEnlargeOrHide = true;
    class ButtonTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String command = "";

            switch (v.getId()) {
                case R.id.dvr_iris_down:
                    command = IRIS_CLOSE;
                    break;
                case R.id.dvr_iris_up:
                    command = IRIS_OPEN;
                    break;
                case R.id.btn_focus_down:
                    command = FOCUS_FAR;
                    break;
                case R.id.btn_focus_up:
                    command = FOCUS_NEAR;
                    break;
                //	case R.id.ptzrefresh:
                //		_cgihelper.destory();
                //		_cgihelper = new TcpCgiHelper(_serverIp, 5100);
                //		_cgihelper.init();
                //		break;
                case R.id.BiggerSmallerButton:
                    if(canEnlargeOrHide)
                    {
                        if((getView().findViewById(R.id.linearLayout2)).getVisibility() == View.VISIBLE)
                        {

                            (getView().findViewById(R.id.linearLayout2)).setVisibility(View.GONE);
                            smallerbiggerbutton.setText("<");
                        }
                        else
                        {
                            (getView().findViewById(R.id.linearLayout2)).setVisibility(View.VISIBLE);
                            smallerbiggerbutton.setText(">");
                        }
                        canEnlargeOrHide = false;
                        final Handler handler = new Handler();
                        Timer timer = new Timer(false);
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        canEnlargeOrHide = true;
                                        // Do whatever you want
                                    }
                                });
                            }
                        };
                        timer.schedule(timerTask, 500); // 1000 = 1 second.
                    }
            }

            if (event.getAction() == MotionEvent.ACTION_UP)
                command = STOP;

            if (!"".equalsIgnoreCase(command) && cameraView[id] != null) {
                cameraView[id].ptzCommand(_session[id], id, command);
            }
            return false;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    public void getDevice() {
        deviceListDialog = new DeviceListDialog();
        deviceListDialog.show(getChildFragmentManager(), "deviceListDialog");
    }

    public void onDismiss(String devicename) {
        deviceListDialog = null;
        showDialog();
        findDevice(devicename);
        loginHost = new LoginToHost();
        loginHost.execute(false);
    }

    public void findDevice(String devicename) {
        dbHandler = new DBHandler().open(this.getActivity());
        DeviceItem item = dbHandler.findDevice(devicename);

        deviceName = String.valueOf(item.getDeviceName());
        deviceIP[id] = String.valueOf(item.getIP());
        devicePort[id] = item.getPort();
        deviceUsername[id] = String.valueOf(item.getUsername());
        devicePassword[id] = String.valueOf(item.getPassword());
    }

    private void showDialog() {
        _progressDialog = ProgressDialog.show(getActivity(), "",
                "Loading, Please wait...", true);
        _progressDialog.setCancelable(true);
        _progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                if (loginHost.getStatus() != AsyncTask.Status.FINISHED)
                    loginHost.cancel(true);
            }
        });
    }

    public void cameraView(int Position) {
        position[id] = Position;
        cameraListDialog = null;
        cameraView[id] = new CameraView();
        cameraView[id].setValues(surfaceHolder[id], progressBars[id], position[id], deviceIP[id], devicePort[id], _session[id]);
        cameraView[id].setProgressBar(progressBars[id]);
        cameraView[id].start();
    }

    private class LoginToHost extends AsyncTask<Boolean, Void, String> {

        public int status = 0;
        Toast _toast1 = Toast.makeText(getActivity().getApplicationContext(),
                "Can't connect to the server.", Toast.LENGTH_SHORT);
        Toast _toast2 = Toast.makeText(getActivity().getApplicationContext(),
                "No SDK License.", Toast.LENGTH_SHORT);

        @Override
        protected void onPostExecute(String result) {
            try {
                _progressDialog.dismiss();

                if (result == null) {
                    _toast1.cancel();
                    _toast1.show();
                } else if (result.equals("NOSDK")) {
                    {
                        status = 1;
                        _toast2.cancel();
                        _toast2.show();
                    }
                } else {
                    _session[id] = result;

                    ((DeviceItem) getActivity().getApplicationContext()).setSession(_session[id]);
                    ((DeviceItem) getActivity().getApplicationContext()).setDeviceName(deviceName);
                    ((DeviceItem) getActivity().getApplicationContext()).setIP(deviceIP[id]);
                    ((DeviceItem) getActivity().getApplicationContext()).setPort(devicePort[id]);

                    if (cameraListDialog == null) {
                        cameraListDialog = new CameraListDialog();
                        cameraListDialog.show(getChildFragmentManager(), "cameraListDialog");
                    }

                }
            } catch (Exception e) {
            }
        }

        @Override
        protected String doInBackground(Boolean... arg0) {
            try {

                return CGIHelper.login(deviceIP[id], devicePort[id], deviceUsername[id], devicePassword[id]);

            } catch (Exception e) {
                Log.e("ServerList", e.getLocalizedMessage());
                return null;
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen for landscape and portrait
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    private void liveViewLayout() {

        channelContainer.removeAllViews();
        linearLayoutRow1 = new LinearLayout(this.getActivity());
        linearLayoutRow2 = new LinearLayout(this.getActivity());
        linearLayoutRow3 = new LinearLayout(this.getActivity());
        linearLayoutRow4 = new LinearLayout(this.getActivity());

        LLParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, .5f);

        linearLayoutRow1.setLayoutParams(LLParams);
        linearLayoutRow2.setLayoutParams(LLParams);
        linearLayoutRow3.setLayoutParams(LLParams);
        linearLayoutRow4.setLayoutParams(LLParams);

        linearLayoutRow1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutRow2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutRow3.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutRow4.setOrientation(LinearLayout.HORIZONTAL);

        channelContainer.addView(linearLayoutRow1);
        channelContainer.addView(linearLayoutRow2);
        channelContainer.addView(linearLayoutRow3);
        channelContainer.addView(linearLayoutRow4);

        linearLayoutRow1.addView(channels[1]);
        linearLayoutRow1.addView(channels[2]);
        linearLayoutRow1.addView(channels[3]);
        linearLayoutRow2.addView(channels[4]);
        linearLayoutRow2.addView(channels[5]);
        linearLayoutRow2.addView(channels[6]);
        linearLayoutRow3.addView(channels[7]);
        linearLayoutRow3.addView(channels[8]);
        linearLayoutRow3.addView(channels[9]);
        linearLayoutRow4.addView(channels[10]);
        linearLayoutRow4.addView(channels[11]);
        linearLayoutRow4.addView(channels[12]);

        split4(currentSplit);
    }

    public void resetLayout() {
        for (int i = 1; i < channelCount; i++) {
            channels[i].setVisibility(View.GONE);
        }
        linearLayoutRow1.setVisibility(View.GONE);
        linearLayoutRow2.setVisibility(View.GONE);
        linearLayoutRow3.setVisibility(View.GONE);
        linearLayoutRow4.setVisibility(View.GONE);

        linearLayoutRow1.setLayoutParams(LLParams);
        linearLayoutRow2.setLayoutParams(LLParams);
        linearLayoutRow3.setLayoutParams(LLParams);
        linearLayoutRow4.setLayoutParams(LLParams);

        if (currentSplit == 4){
            linearLayoutRow2.removeView(channels[3]);
            linearLayoutRow1.addView(channels[3]);
            if (cameraView[3] != null) {
                cameraView[3].closeChannel();
                cameraView[3].start();
            }
        }

        try {
            channels[id].setLayoutParams(LLParams);
        }
        catch (Exception e) {
        }

        isFullScreen = false;
    }

    public void split1(int split) {
        if (split != 1) {
            resetLayout();
            linearLayoutRow1.setVisibility(View.VISIBLE);
            channels[1].setVisibility(View.VISIBLE);
        }
        currentSplit = 1;
    }

    public void split4(int split) {
        if (split != 4) {
            resetLayout();

            linearLayoutRow1.setVisibility(View.VISIBLE);
            linearLayoutRow2.setVisibility(View.VISIBLE);

            linearLayoutRow1.removeView(channels[3]);
            linearLayoutRow2.addView(channels[3]);
            if (cameraView[3] != null) {
                cameraView[3].closeChannel();
                cameraView[3].start();
            }

            channels[1].setVisibility(View.VISIBLE);
            channels[2].setVisibility(View.VISIBLE);
            channels[3].setVisibility(View.VISIBLE);
            channels[4].setVisibility(View.VISIBLE);
        }
        currentSplit = 4;
    }

    public void split9(int split) {
        if (split != 9) {
            resetLayout();

            linearLayoutRow1.setVisibility(View.VISIBLE);
            linearLayoutRow2.setVisibility(View.VISIBLE);
            linearLayoutRow3.setVisibility(View.VISIBLE);

            channels[1].setVisibility(View.VISIBLE);
            channels[2].setVisibility(View.VISIBLE);
            channels[3].setVisibility(View.VISIBLE);
            channels[4].setVisibility(View.VISIBLE);
            channels[5].setVisibility(View.VISIBLE);
            channels[6].setVisibility(View.VISIBLE);
            channels[7].setVisibility(View.VISIBLE);
            channels[8].setVisibility(View.VISIBLE);
            channels[9].setVisibility(View.VISIBLE);
        }
        currentSplit = 9;
    }

    public void split12(int split) {
        if (split != 12) {
            resetLayout();
            linearLayoutRow1.setVisibility(View.VISIBLE);
            linearLayoutRow2.setVisibility(View.VISIBLE);
            linearLayoutRow3.setVisibility(View.VISIBLE);
            linearLayoutRow4.setVisibility(View.VISIBLE);

            channels[1].setVisibility(View.VISIBLE);
            channels[2].setVisibility(View.VISIBLE);
            channels[3].setVisibility(View.VISIBLE);
            channels[4].setVisibility(View.VISIBLE);
            channels[5].setVisibility(View.VISIBLE);
            channels[6].setVisibility(View.VISIBLE);
            channels[7].setVisibility(View.VISIBLE);
            channels[8].setVisibility(View.VISIBLE);
            channels[9].setVisibility(View.VISIBLE);
            channels[10].setVisibility(View.VISIBLE);
            channels[11].setVisibility(View.VISIBLE);
            channels[12].setVisibility(View.VISIBLE);
        }
        currentSplit = 12;
    }

}
