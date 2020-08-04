package com.vantageclient.dvrclient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class TcpCgiHelper {

	private String _ip;
	private int _port;

	private Socket _socket;
	private OutputStream _outStream;

	public TcpCgiHelper(String ip, int port) {
		_ip = ip;
		_port = port;
	}

	public boolean init() {
		boolean ret = false;
		if (_ip != null && _port != -1) {
			try {
				_socket = new Socket(_ip, _port);
				_outStream = _socket.getOutputStream();
				ret = true;
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			}
		}
		return ret;
	}

	public void ptzCommand(String sessionID, int channel, String command) {
		tcpCgiHelper(CGIHelper.getPtzCommandPacket(sessionID, channel, command));
	}

	public void ptzSpeed(String sessionID, int channel, int speed) {
		tcpCgiHelper(CGIHelper.getSetPTZSpeedPacket(sessionID, channel, speed));
	}

	public void tcpCgiHelper(byte[] command) {
		if (_outStream != null) {
			try {
				_outStream.write(command);
			} catch (Exception e) {
				Log.e("CameraControl", e.getMessage());
			}
		}
	}

	public void destory() {
		if (_socket != null && _socket.isConnected())
			try {
				_socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
