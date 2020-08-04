package com.vantageclient.dvrclient;

public class Dvr {

	public static final String TABLE_NAME = "DVR";

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	private int id;
	private String name;
	private String ip;
	private int port;
	private String userName;
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
