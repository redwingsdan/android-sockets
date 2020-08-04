package com.vantageclient.dvrclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vantageclient.R;
import com.vantageclient.data.MVCApplication;
import com.vantageclient.net.CGIHelper;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CameraListActivity extends ListActivity {

	public ArrayAdapter<CameraInfo> adapter;
	ListView listCamera;
	ProgressDialog m_progressDialog;
	private String _session;
	List<CameraInfo> m_camList;

	
	public void onClick(View view) {
		
		if(view.getId() == R.id.DoneButton)
		{
		  List<CameraInfo> li = ((InteractiveArrayAdapter)adapter).list;
		   
		   String blah = "";
		   int found = 0;
		   int[] camNums = {-1,-1,-1,-1};
		   
		   for(int i = 0; i < li.size();i++)
		   {
			   if(li.get(i).isSelected())
			   {
				   blah += li.get(i).toString() + "  ";

				   if(found < 4)
				   {
					   camNums[found] = i;
				   }
				   found++;
			   }
		   }
		   if(found > 4)
		   {
			   Toast.makeText(getApplicationContext(), "Too many cameras selected. Please limit to 4 or less.", Toast.LENGTH_SHORT).show();
		   }
		   else if(found < 1)
		   {
			   Toast.makeText(getApplicationContext(), "Too few cameras selected. Please select atleast 1.", Toast.LENGTH_SHORT).show();
		   }
		   else
		   {
			   if (!NetStatusChecker
						.checkNetWorkStatus(CameraListActivity.this
								.getApplicationContext())) {
					return;
				}
				Intent intent = new Intent(CameraListActivity.this,
						CameraViewActivity.class);
			//	int camAddr = m_camList.get(arg2).getCamNum();
				//intent.putExtra("Cam", camAddr);
				intent.putExtra("CamNums",camNums);
				intent.putExtra("NumberOfCams", found);

				startActivity(intent);
				//this.finish();
			
		   }
		}
		
		if(view.getId() == R.id.RefreshButton)
		{
			m_progressDialog = ProgressDialog.show(this, "",
					"Loading, Please wait...", true);
			GetCamList gcl = new GetCamList();
			gcl.add(this);
			gcl.execute();
		}
		
		
	}
	
	 // Check screen orientation or screen rotate event here
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen for landscape and portrait
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
          
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
           
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dvr_window_cameralist);	


			m_progressDialog = ProgressDialog.show(this, "",
					"Loading, Please wait...", true);
		    _session = ((MVCApplication) getApplicationContext()).getSession();
		    
		    TextView text = ((TextView) findViewById(R.id.txtTitle));
			text.setText(((MVCApplication) getApplicationContext()).getServerName());
			
			GetCamList gcl = new GetCamList();
			gcl.add(this);
			gcl.execute();
	}
	
	private class GetCamList extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... args) {
			boolean flag = false;
			try {
				String[] cameras = CGIHelper
						.getCameraList(
								((MVCApplication) getApplicationContext())
										.getServerIp(),
								((MVCApplication) getApplicationContext())
										.getServerPort(), _session).split(
								"\r\n");
				m_camList = new ArrayList<CameraInfo>();

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

						m_camList.add(new CameraInfo(desc, num));
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

		public void add(CameraListActivity cameraListA) {
			cameraListActivity = cameraListA;
			// TODO Auto-generated method stub
			
		}
		
		CameraListActivity cameraListActivity;
		@Override
		protected void onPostExecute(Boolean result) {
			if (m_progressDialog != null)
				m_progressDialog.dismiss();
			if (result) {
			

				adapter = new InteractiveArrayAdapter(cameraListActivity,
				        m_camList);
				    setListAdapter(adapter);
			} else
				finish();
		}
	}
	
	public final class CameraInfo {
		private String m_description;
		private short m_camNum;
		private boolean selected;

		public short getCamNum() {
			return m_camNum;
		}

		public CameraInfo(String desc, short num) {
			m_description = desc;
			m_camNum = num;
			selected = false;
		}

		@Override
		public String toString() {
			return (m_camNum + 1) + " - " + m_description;
		}

		public boolean isSelected() {
			// TODO Auto-generated method stub
			return selected;
		}

		public void setSelected(boolean checked) {
			selected = checked;
			
		}

	}

	
	

}
