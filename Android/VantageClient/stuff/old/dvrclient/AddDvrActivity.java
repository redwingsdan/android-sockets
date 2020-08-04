package com.vantageclient.dvrclient;

import java.util.List;

import com.vantageclient.R;
import com.vantageclient.data.Dvr;
import com.vantageclient.data.DvrDbAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddDvrActivity extends Activity {

	private DvrDbAdapter mDbHelper;

	private EditText m_nameEdit;
	private EditText m_addrEdit;
	private EditText m_portEdit;
	private EditText m_userNameEdit;
	private EditText m_passEdit;

	private CheckBox m_chkSavePass;

	private String m_ipAddr;
	private String m_name;
	private String m_userName;
	private String m_password;

	private int m_port;
	private Long m_rowId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_deviceadd);

		
		mDbHelper = new DvrDbAdapter().open(this);

		m_nameEdit = (EditText) findViewById(R.id.nameEdit);
		m_addrEdit = (EditText) findViewById(R.id.addrEdit);
		m_portEdit = (EditText) findViewById(R.id.portEdit);
		//m_userNameEdit = (EditText) findViewById(R.id.userNameEdit);
	//	m_passEdit = (EditText) findViewById(R.id.passEdit);
	//	m_chkSavePass = (CheckBox) findViewById(R.id.chkSavePass);

		//m_passEdit.setEnabled(false);
		//m_passEdit.setCursorVisible(false);
	//	m_passEdit.setFocusable(false);
		//m_passEdit.setFocusableInTouchMode(false);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			setTitle(R.string.editDvrText);
			m_rowId = extras.getLong("Row");
			populateFields();
		}

	}

	private void populateFields() {
		if (m_rowId != null) {
			Dvr dvr = mDbHelper.getDvr(m_rowId.intValue());

			m_nameEdit.setText(dvr.getName());
			m_addrEdit.setText(dvr.getIp());
			m_portEdit.setText(String.valueOf(dvr.getPort()));

		}
	}

	public void ClickHandler(View v) {
			try {
				m_ipAddr = m_addrEdit.getText().toString();
				m_name = m_nameEdit.getText().toString();
				m_port = Integer.parseInt(m_portEdit.getText().toString());
				m_userName = "";// m_userNameEdit.getText().toString();

				m_password = "";// m_chkSavePass.isChecked() ? (m_passEdit.getText()
				//		.toString() == null ? "" : m_passEdit.getText()
				//		.toString()) : null;

				Dvr dvr = new Dvr();
				dvr.setName(m_name);
				dvr.setIp(m_ipAddr);
				dvr.setPort(m_port);
				dvr.setUserName("");///m_userName);
				dvr.setPassword(null);//m_password);
				if (m_rowId != null)
					dvr.setId(m_rowId.intValue());

				int ret = checkExists(m_ipAddr, m_name);
				if (ret > 0) {
					if (_toast != null)
						_toast.cancel();

					_toast = Toast.makeText(getApplicationContext(), String
							.format("Device %s already exists.",
									ret == 1 ? m_ipAddr : m_name),
							Toast.LENGTH_SHORT);
					_toast.show();

					return;
				}
				int error = -1;
				if (isNullOrEmpty(m_ipAddr))
					//	|| !m_ipAddr
						//		.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$"))
					error = R.string.ErrorInvalidIp;
				else if (isNullOrEmpty(m_name))
					error = R.string.ErrorInvalidDvrName;
				//else if (isNullOrEmpty(m_userName))
				//	error = R.string.ErrorInvalidUserName;
				else if (m_rowId == null && mDbHelper.addDvr(dvr) < 0)
					error = R.string.ErrorAddingToDB;
				else if (m_rowId != null && !mDbHelper.updateDvr(dvr))
					error = R.string.ErrorUpdatingDatabase;

				if (error != -1) {
					if (_toast != null)
						_toast.cancel();

					_toast = Toast.makeText(getApplicationContext(), error,
							Toast.LENGTH_SHORT);
					_toast.show();
				}

				if (error != -1)
					return;
				else {
					setResult(RESULT_OK);
					finish();
				}
			} catch (Exception e) {
			}
		
	}

	Toast _toast;

	private int checkExists(String ip, String name) {
		List<Dvr> dvrs = mDbHelper.getDvrs();
		for (int i = 0; i < dvrs.size(); i++) {
			if (m_rowId == null) {
				if (dvrs.get(i).getIp().equalsIgnoreCase(ip)) {
					return 1;
				}
				if (dvrs.get(i).getName().equalsIgnoreCase(name)) {
					return 2;
				}
			} else {
				if (dvrs.get(i).getIp().equalsIgnoreCase(ip)) {
					if (dvrs.get(i).getId() != m_rowId.intValue()) {
						return 1;
					}
				}
				if (dvrs.get(i).getName().equalsIgnoreCase(name)) {
					if (dvrs.get(i).getId() != m_rowId.intValue()) {
						return 2;
					}
				}
			}
		}
		return 0;
	}

	private boolean isNullOrEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}
}
