package com.vantageclient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vantageclient.NetStatusChecker;
import com.vantageclient.R;
import com.vantageclient.adapter.CameraListAdapter;
import com.vantageclient.adapter.ItemClickAdapter;
import com.vantageclient.data.DeviceItem;
import com.vantageclient.net.CGIHelper;

import java.util.ArrayList;
import java.util.List;

public class CameraListDialog extends DialogFragment {

    CameraListAdapter adapter;
    private RecyclerView rv;
    ProgressDialog m_progressDialog;
    private String _session;
    private List<CameraInfo> camList;
    CameraListListener activityCallback;
    CameraListDialog cameraListDialog;

    public interface CameraListListener {
        void onCameraListClick(int position);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.dialog_cameralist, null);

        rv = (RecyclerView) view.findViewById(R.id.cameralist_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        m_progressDialog = ProgressDialog.show(this.getActivity(), "", "Loading, Please wait...", true);
        _session = ((DeviceItem) getActivity().getApplicationContext()).getSession();

        GetCamList gcl = new GetCamList();
        gcl.add(this);
        gcl.execute();

        Button refresh = (Button) view.findViewById(R.id.RefreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Refresh();
                                       }
                                   });


                ItemClickAdapter.addTo(rv).setOnItemClickListener(new ItemClickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        if (!NetStatusChecker.checkNetWorkStatus(getActivity().getApplicationContext())) {
                            return;
                        }
                        activityCallback.onCameraListClick(position);
                        getDialog().dismiss();
                    }
                });

        return new AlertDialog.Builder(getActivity())
        .setView(view)
        .setTitle(R.string.btnLoginText)
        .create();
    }

    private class GetCamList extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... args) {
            boolean flag = false;
            try {
                String[] cameras = CGIHelper.getCameraList(
                                ((DeviceItem) getActivity().getApplicationContext()).getIP(),
                                ((DeviceItem) getActivity().getApplicationContext()).getPort(), _session).split("\r\n");
                camList = new ArrayList<>();

                for (int i = 0; i < cameras.length; i++) {
                    if (cameras[i].length() < 20) {
                        continue;
                    }
                    if ("AnalogChannelNumber".equals(cameras[i]
                            .substring(0, 19))
                            || ("DigitalChannelNumber"
                            .equalsIgnoreCase(cameras[i].substring(0,
                                    20)))) {

                        int equalsLocation = cameras[i + 1].indexOf("=");
                        String desc = cameras[i + 1]
                                .substring(equalsLocation + 1);
                        equalsLocation = cameras[i].indexOf("=");
                        short num = Short
                                .parseShort(cameras[i]
                                        .substring(equalsLocation + 1,
                                                cameras[i].length()).trim());

                        camList.add(new CameraInfo(desc, num));
                        i++;
                    }
                }

                // sock.close();
                flag = true;
            } catch (Exception e) {
                Log.d("CameraList", e.getMessage());
            }

            return flag;
        }

        public void add(CameraListDialog cameraListA) {
            cameraListDialog = cameraListA;
            // TODO Auto-generated method stub

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (m_progressDialog != null)
                m_progressDialog.dismiss();

            if (result) {
                adapter = new CameraListAdapter(camList);
                rv.setAdapter(adapter);

            } else
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();

        }
    }

    public final class CameraInfo {
        private String m_description;
        private short m_camNum;

        public CameraInfo(String desc, short num) {
            m_description = desc;
            m_camNum = num;
        }

        @Override
        public String toString() {
            return (m_camNum + 1) + " - " + m_description;
        }

    }

    private void Refresh() {
        m_progressDialog = ProgressDialog.show(this.getActivity(), "", "Loading, Please wait...", true);
        GetCamList gcl = new GetCamList();
        gcl.add(this);
        gcl.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            activityCallback = (CameraListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement CameraListListener");
        }
    }
}