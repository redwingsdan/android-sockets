package com.vantageclient.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import android.widget.Toast;

public final class CGIHelper {
	public static final String TAG = "CGIHelper";

	public static byte[] GetLoginPacket(String username, String pass) {
		return packageCommand(String.format("Login?UserName=%s&Password=%s",
				username, pass));
	}

	public static String GetLoginString(String username, String pass) {
		return String.format("Login?UserName=%s&Password=%s&plain=1", username,
				pass);
	}

	public static byte[] getCameraListPacket(String sessionID) {
		return packageCommand(String.format(
				"Get?SessionID=%s&Function=CameraNames", sessionID));
	}

	public static String GetCameraListString(String sessionID) {
		return String
				.format("Get?SessionID=%s&Function=CameraNames", sessionID);
	}

	public static byte[] getCameraStream(String sessionID, int channel) {
		return packageCommand(String.format("Stream?SessionID=%s&&Channel=%s",
				sessionID, channel));
	}

	public static String getCameraStreamString(String sessionID, int channel) {
		return String.format("Stream?SessionID=%s&&Channel=%s", sessionID,
				channel);
	}

	public static byte[] getSetPTZSpeedPacket(String sessionID, int channel,
			int value) {
		return packageCommand(String.format(
				"Set?SessionID=%s&Function=PTZSpeed&Channel=%d&Value=%d",
				sessionID, channel, value));
	}

	public static String getSetPTZSpeedString(String sessionID, int channel,
			int value) {
		return String.format(
				"Set?SessionID=%s&Function=PTZSpeed&Channel=%d&Value=%d",
				sessionID, channel, value);
	}

	public static byte[] getPtzCommandPacket(String sessionID, int channel,
			String command) {
		return packageCommand(String.format(
				"PTZ?SessionID=%s&Channel=%d&Command=%s", sessionID, channel,
				command));
	}

	public static String getPtzCommandString(String sessionID, int channel,
			String command) {
		return String.format("PTZ?SessionID=%s&Channel=%d&Command=%s",
				sessionID, channel, command);
	}

	private static final int REQUEST_TIMEOUT = 10 * 1000;
	private static final int SO_TIMEOUT = 10 * 1000;

	public static String httpCgiSender(String ip, int port, String command) {

		Log.e("PTZTAG", command);
		String ret = "";

		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);

		HttpClient client = new DefaultHttpClient(httpParams);

		HttpUriRequest request = new HttpGet(String.format("http://%s:%s/%s",
				ip, port, command));
		try {

			HttpResponse response = client.execute(request);

			ret = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Log.e("PTZTAG", ret.replace("\r\n", ""));
		if(ret.indexOf("Result=Check SDK license Failed") > -1)
		{
			Log.e("PTZTAG", "HIT!!");
	
		}
		return ret;
	}

	public static String login(String ip, int port, String userName,
			String password) {

		String ret = CGIHelper.httpCgiSender(ip, port,
				GetLoginString(userName, password));

		if (ret.contains("SDK"))
		{
			
			return "NOSDK";
		}
		int sessionLocation = ret.indexOf("ID=");

		if (sessionLocation == -1)
			return null;
		else
			return ret.substring(sessionLocation + 3).trim();
	}

	public static String getCameraList(String ip, int port, String sessionID) {
		return CGIHelper
				.httpCgiSender(ip, port, GetCameraListString(sessionID));
	}

	public static String getCameraStreamString(String ip, int port,
			String sessionID, int channel) {
		return CGIHelper.httpCgiSender(ip, port,
				getCameraStreamString(sessionID, channel));
	}

	public static String ptzCommand(String ip, int port, String sessionID,
			int channel, String command) {
		return CGIHelper.httpCgiSender(ip, port,
				getPtzCommandString(sessionID, channel, command));
	}

	public static String ptzSpeed(String ip, int port, String sessionID,
			int channel, int speed) {
		return CGIHelper.httpCgiSender(ip, port,
				getSetPTZSpeedString(sessionID, channel, speed));
	}

	private static byte[] packageCommand(String command) {
		try {
			String cgiCommand = String.format("cgi://%s", command);
			byte[] data = cgiCommand.getBytes("US-ASCII");
			byte[] flag = "NetFlag2".getBytes("US-ASCII");

			ByteBuffer buff = ByteBuffer.allocate(4);
			buff.order(ByteOrder.LITTLE_ENDIAN);
			buff.putInt(data.length + 1);
			byte[] length = buff.array();

			byte[] sendBuffer = new byte[data.length + 17];
			System.arraycopy(length, 0, sendBuffer, 0, length.length);
			System.arraycopy(flag, 0, sendBuffer, 4, flag.length);
			System.arraycopy(data, 0, sendBuffer, 16, data.length);

			return sendBuffer;
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UnsupportedEncodingExcpeption: " + e.getMessage());
		}

		return new byte[1];
	}
}
