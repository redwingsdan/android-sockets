package com.vantageclient.data;

import android.app.Application;
import android.database.Cursor;

import java.net.Socket;

public class DeviceItem extends Application {
    private int _id;
    private String _devicename;
    private String _ip;
    private int _port;
    private String _username;
    private String _password;
    private String m_session;
    private Socket m_sock;

    public DeviceItem() {
    }

    public DeviceItem(int id, String devicename, String ip, int port, String username, String password) {
        this._id = id;
        this._devicename = devicename;
        this._ip = ip;
        this._port = port;
        this._username = username;
        this._password = password;
    }

    public DeviceItem(String devicename, String ip, int port, String username, String password) {
        this._devicename = devicename;
        this._ip = ip;
        this._port = port;
        this._username = username;
        this._password = password;
    }

    public void setID(int id) {
        this._id = id;
    }

    public int getID() {
        return this._id;
    }

    public void setDeviceName(String devicename) {
        this._devicename = devicename;
    }

    public String getDeviceName() {
        return this._devicename;
    }

    public void setIP(String ip) {
        this._ip = ip;
    }

    public String getIP() {
        return this._ip;
    }

    public void setPort(int port) {
        this._port = port;
    }

    public int getPort() {
        return this._port;
    }

    public void setUsername(String username) {
        this._username = username;
    }

    public String getUsername() {
        return this._username;
    }

    public void setPassword(String password) {
        this._password = password;
    }

    public String getPassword() {
        return this._password;
    }

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

    public static DeviceItem fromCursor(Cursor cursor) {
        DeviceItem item = new DeviceItem();
        for (int c=0; c<cursor.getColumnCount(); c++) {
            String columnName = cursor.getColumnName(c);
            if (columnName.equals(DBHandler.COLUMN_ID)) {
                item.setID(cursor.getInt(c));
            } else if (columnName.equals(DBHandler.COLUMN_DEVICENAME)) {
                item.setDeviceName(cursor.getString(c));
            } else if (columnName.equals(DBHandler.COLUMN_IP)) {
                item.setIP(cursor.getString(c));
            } else if (columnName.equals(DBHandler.COLUMN_PORT)) {
                item.setPort(cursor.getInt(c));
            } else if (columnName.equals(DBHandler.COLUMN_USERNAME)) {
                item.setUsername(cursor.getString(c));
            }
        }
        return item;
        //TODO return your MyListItem from cursor.
    }
}