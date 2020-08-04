package com.vantageclient.data;

import java.net.Socket;

import android.app.Application;

public class MVCApplication extends Application {
    private String m_session;
    private Socket m_sock;
    private String m_ip;
    private String m_devicename;
    private int m_port;

	public String getSession() {
		return m_session;
	}

	public void setSession(String value) {
		m_session = value;
	}

	public Socket getSocket() {
		return m_sock;
	}

	public void setSocket(Socket value) {
		m_sock = value;
	}

	public void setDeviceName(String devicename) {
		this.m_devicename = devicename;
	}

	public String getDeviceName() {
		return this.m_devicename;
	}

	public void setIP(String ip) {
		this.m_ip = ip;
	}

	public String getIP() {
		return this.m_ip;
	}

	public void setPort(int port) {
		this.m_port = port;
	}

	public int getPort() {
		return this.m_port;
	}

}
