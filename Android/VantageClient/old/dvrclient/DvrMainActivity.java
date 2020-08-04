package com.vantageclient.dvrclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.vantageclient.R;
import com.vantageclient.data.Dvr;
import com.vantageclient.data.DvrDbAdapter;
import com.vantageclient.data.MVCApplication;
import com.vantageclient.net.CGIHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DvrMainActivity extends Activity {

	// Socket sock;




	public static boolean useMainStreamBoolean = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvr_main);
		
		TextView textbox = (TextView) findViewById(R.id.textView2);
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int versionNumber = pinfo.versionCode;
			String versionName = pinfo.versionName;
			textbox.setText(versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			textbox.setText("v #");
		}
		
		

		
		final Handler handler = new Handler();
		Timer timer = new Timer(false);
		TimerTask timerTask = new TimerTask() {
		    @Override
		    public void run() {
		        handler.post(new Runnable() {
		            @Override
		            public void run() {
		            	
		        		findViewById(R.id.startlogolayout).setVisibility(View.GONE);
		                // Do whatever you want
		        		Button btnAdd = (Button) findViewById(R.id.btnAdd);
		        		btnAdd.setOnClickListener(new OnClickListener() {

		        			@Override
		        			public void onClick(View arg0) {
		        				showPopup(true, 0);
		        			}
		        		});
		        		_listGroup.setOnItemClickListener(new OnItemClickListener() {

		        			@Override
		        			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		        					long arg3) {
		        				_positaion = arg2;
		        				connect(arg2);
		        			}
		        		});
		            }
		        });
		    }
		};
		timer.schedule(timerTask,2500); // 1000 = 1 second.
		
		
		

		
		_listGroup = (ListView) findViewById(R.id.listView1);
		registerForContextMenu(_listGroup);

		_simadapter = new SimpleAdapter(this, _map,
				R.layout.dvr_listview_group, new String[] { "groupName" },
				new int[] { R.id.txt_groupname });

		_adapter = new DvrDbAdapter().open(this);

		loadGroups();
		_listGroup.setAdapter(_simadapter);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadGroups();
		removeDialog(0);
	}

	private void showDialog() {
		_progressDialog = ProgressDialog.show(this, "",
				"Loading, Please wait...", true);
		_progressDialog.setCancelable(true);
		_progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				if (loginHost.getStatus() != AsyncTask.Status.FINISHED)
					loginHost.cancel(true);
			}
		});
	}

	Dvr dvr;
	private void connect(int server) {

		dvr = (Dvr) _groups.get(server);
		_userName = dvr.getUserName();
		
		this._pass = (dvr.getPassword() != null) ? dvr.getPassword() : "";
		
	
	

		/*
		if (dvr.getPassword() != null) {
			showDialog();

			loginHost = new LoginToHost();
			loginHost.execute(true);
		} else {
			showDialog(0);
		}
		*/
		showDialog(0);
	}

	LoginToHost loginHost;

	 @Override
	    protected void onDestroy() {
	        
	        super.onDestroy();
	    }
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		if (id == 0) {
			EditText nameEdit = (EditText) dialog.getWindow().findViewById(
					R.id.loginBoxUserNameEdit);
			
			nameEdit.setText(_userName);
			
			EditText passEdit = (EditText) dialog.getWindow().findViewById(R.id.loginBoxPassEdit);
			passEdit.setText(_pass);
			
			
			if(_pass != null)
			{
				CheckBox check = (CheckBox) dialog.getWindow().findViewById(R.id.passBox);
				
				if( _pass.length() >= 1)
				{
					check.setChecked(true);
				}
				else
				{
					check.setChecked(false);
				}
			}

			
			
			
		}

		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.dvr_dialog_login, null);// (ViewGroup)findViewById(R.id.loginbox_root));

		final EditText nameEdit = (EditText) layout
				.findViewById(R.id.loginBoxUserNameEdit);
		final EditText passEdit = (EditText) layout
				.findViewById(R.id.loginBoxPassEdit);
		final CheckBox check2 = (CheckBox)layout.findViewById(R.id.useMainOrSubBox);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle(R.string.btnLoginText);
		nameEdit.setText(_userName);

		
	    
		builder.setPositiveButton(R.string.btnLoginText,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						showDialog();
						_userName = nameEdit.getText().toString();
						_pass = passEdit.getText().toString();
						_pass = _pass == null ? "" : _pass;
						dvr.setUserName(_userName);
						
						CheckBox check = (CheckBox) layout.findViewById(R.id.passBox);
						if(check.isChecked())
						{
							dvr.setPassword(_pass);
						}else
						{
							dvr.setPassword(null);
						}
						
						_adapter.updateDvr(dvr);
						loginHost = new LoginToHost();
						loginHost.execute(false);
						
						
						if(check2.isChecked())
						{
							DvrMainActivity.useMainStreamBoolean = true;
						}
						else
						{
							DvrMainActivity.useMainStreamBoolean = false;
						}
						
						removeDialog(0);
						
					}
				});

		return builder.create();
	}

	private void loadGroups() {
		_map.clear();
		_groups = _adapter.getDvrs();

		for (int i = 0; i < _groups.size(); i++) {

			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("groupName", _groups.get(i).getName());
			_map.add(tempMap);
		}

		_simadapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Add DVR");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if ("Add DVR".equalsIgnoreCase(item.getTitle().toString())) {
			showPopup(true, 0);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		menu.add("Edit");
		menu.add("Delete");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int id = _groups.get(info.position).getId();

		if ("Edit".equalsIgnoreCase(item.getTitle().toString())) {

			showPopup(false, id);
		} else if ("Delete".equalsIgnoreCase(item.getTitle().toString())) {

			boolean ret = _adapter.deleteDvr(id);
			Toast.makeText(DvrMainActivity.this, ret ? "Delete failed!"
					: "Delete successed!", Toast.LENGTH_LONG);
			loadGroups();
		}

		return super.onContextItemSelected(item);
	}

	private void showPopup(final boolean addMode, final int id) {

		Intent intent = new Intent(this, AddDvrActivity.class);
		if (!addMode) {
			intent.putExtra("Row", Long.valueOf(String.format("%s", id)));
		}

		startActivity(intent);
	}

	private class LoginToHost extends AsyncTask<Boolean, Void, String> {

		public int status = 0;
		Toast _toast1 = Toast.makeText(getApplicationContext(),
				"Can't connect to the server.", Toast.LENGTH_SHORT);
		Toast _toast2 = Toast.makeText(getApplicationContext(),
				"No SDK License.", Toast.LENGTH_SHORT);

		@Override
		protected void onPostExecute(String result) {
			try {
				_progressDialog.dismiss();

				if (result == null) {
					_toast1.cancel();
					_toast1.show();
				} else if (result == "NOSDK") {
					{
						status = 1;
						_toast2.cancel();
						_toast2.show();
					}
				} else {
					// ((MVCApplication)
					// getApplicationContext()).setSocket(sock);
					((MVCApplication) getApplicationContext())
							.setSession(result);
					Intent i = new Intent(DvrMainActivity.this,
							CameraListActivity.class);
					startActivity(i);
				}
			} catch (Exception e) {
			}
		}

		@Override
		protected String doInBackground(Boolean... arg0) {
			try {

				Dvr dvr = (Dvr) _groups.get(_positaion);

				((MVCApplication) getApplicationContext()).setServerIp(dvr
						.getIp());
				((MVCApplication) getApplicationContext()).setServerPort(dvr
						.getPort());
				((MVCApplication) getApplicationContext()).setServerName(dvr
						.getName());

				String password = arg0[0] ? dvr.getPassword() : _pass;
				String userName = arg0[0] ? dvr.getUserName() : _userName;
				return CGIHelper.login(dvr.getIp(), dvr.getPort(), userName,
						password);

			} catch (Exception e) {
				Log.e("ServerList", e.getLocalizedMessage());
				return null;
			}
		}
		
	}
}