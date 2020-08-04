package com.vantageclient.dvrclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetStatusChecker {

	public static boolean checkNetWorkStatus(Context context) {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) {
			result = true;
			Log.i("NET", "network connected");
		} else {
			result = false;
			Log.i("NET", "network disconnected");
		}
		return result;
	}
}
