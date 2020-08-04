package com.vantageclient.dvrclient;

import java.net.Socket;

import android.app.Application;

public class MVCApplication extends Application {
	private String m_session;
	private Socket m_sock;
	private String m_Ip;
	private String m_servername;
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

	public void setServerIp(String value) {
		m_Ip = value;
	}

	public void setServerPort(int value) {
		m_port = value;
	}

	public String getServerIp() {
		return m_Ip;
	}

	public int getServerPort() {
		return m_port;
	}

	public String getServerName() {
		return m_servername;
	}

	public void setServerName(String value) {
		m_servername = value;
	}
}
