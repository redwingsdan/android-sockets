package com.vantageclient.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "devices.db";
    public static final String TABLE_NAME = "devices";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DEVICENAME = "devicename";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_PORT = "port";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    private DBHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DBHandler open(Context context) throws SQLException {
        mDbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public Cursor getDevices() {
        String[] columns = {COLUMN_ID, COLUMN_DEVICENAME, COLUMN_IP, COLUMN_PORT, COLUMN_USERNAME, COLUMN_PASSWORD};
        return mDb.query(TABLE_NAME, columns, null, null, null, null, COLUMN_ID + " DESC");
    }

    public DeviceItem findDevice(String devicename) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_DEVICENAME + " =  \"" + devicename + "\"";

        Cursor cursor = mDb.rawQuery(query, null);

        DeviceItem item = new DeviceItem();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            item.setID(Integer.parseInt(cursor.getString(0)));
            item.setDeviceName(cursor.getString(1));
            item.setIP(cursor.getString(2));
            item.setPort(Integer.parseInt(cursor.getString(3)));
            item.setUsername(cursor.getString(4));
            item.setPassword(cursor.getString(5));
        } else {
            item = null;
        }
        cursor.close();
        return item;
    }

    public Long addDevice(DeviceItem item) {
        return mDbHelper.addDevice(item, mDb);
    }

    public boolean deleteDevice(String devicename) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_DEVICENAME + " =  \"" + devicename + "\"";
        Cursor cursor = mDb.rawQuery(query, null);

        DeviceItem device = new DeviceItem();

        if (cursor.moveToFirst()) {
            device.setID(Integer.parseInt(cursor.getString(0)));
            mDb.delete(TABLE_NAME, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(device.getID())});
            result = true;
        }
        cursor.close();
        return result;
    }

    public void editDevice(DeviceItem device, String olddevice) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_DEVICENAME, device.getDeviceName());
        values.put(COLUMN_IP, device.getIP());
        values.put(COLUMN_PORT, device.getPort());
        values.put(COLUMN_USERNAME, device.getUsername());
        values.put(COLUMN_PASSWORD, device.getPassword());

        if (!mDb.isOpen()){
            mDb = mDbHelper.getWritableDatabase();
        }
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_DEVICENAME + " =  \"" + olddevice + "\"";
        Cursor cursor = mDb.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            device.setID(Integer.parseInt(cursor.getString(0)));
            mDb.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(device.getID())});
        }
        cursor.close();
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_DEVICES_TABLE = "CREATE TABLE " +
                    TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_DEVICENAME
                    + " TEXT," + COLUMN_IP + " TEXT," + COLUMN_PORT
                    + " INTEGER," + COLUMN_USERNAME
                    + " TEXT," + COLUMN_PASSWORD
                    + " TEXT" + ")";
            db.execSQL(CREATE_DEVICES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public Long addDevice(DeviceItem device, SQLiteDatabase db) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_DEVICENAME, device.getDeviceName());
            values.put(COLUMN_IP, device.getIP());
            values.put(COLUMN_PORT, device.getPort());
            values.put(COLUMN_USERNAME, device.getUsername());
            values.put(COLUMN_PASSWORD, device.getPassword());

            return db.insert(TABLE_NAME, COLUMN_ID, values);
        }

    }
}