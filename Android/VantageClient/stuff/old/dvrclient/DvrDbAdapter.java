package com.vantageclient.dvrclient;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DvrDbAdapter {

	private static final String DATABASE_NAME = "dvrclient";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	public DvrDbAdapter open(Context context) throws SQLException {
		mDbHelper = new DatabaseHelper(context, DATABASE_NAME, null, 7);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public List<Dvr> getDvrs() {
		List<Dvr> dvrs = new ArrayList<Dvr>();

		Cursor cursor = mDb.query(Dvr.TABLE_NAME, new String[] { Dvr.ID,
				Dvr.NAME, Dvr.IP, Dvr.PORT, Dvr.USERNAME, Dvr.PASSWORD }, null,
				null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			Dvr dvr = new Dvr();
			dvr.setId(cursor.getInt(0));
			dvr.setName(cursor.getString(1));
			dvr.setIp(cursor.getString(2));
			dvr.setPort(cursor.getInt(3));
			dvr.setUserName(cursor.getString(4));
			dvr.setPassword(cursor.getString(5));

			dvrs.add(dvr);
			cursor.moveToNext();
		}

		cursor.close();
		return dvrs;
	}

	public Dvr getDvr(int id) {
		Cursor cursor = mDb.query(Dvr.TABLE_NAME, new String[] { Dvr.ID,
				Dvr.NAME, Dvr.IP, Dvr.PORT, Dvr.USERNAME, Dvr.PASSWORD },
				String.format("%s = %s", Dvr.ID, id), null, null, null, null);

		cursor.moveToFirst();

		Dvr dvr = new Dvr();
		dvr.setId(cursor.getInt(0));
		dvr.setName(cursor.getString(1));
		dvr.setIp(cursor.getString(2));
		dvr.setPort(cursor.getInt(3));
		dvr.setUserName(cursor.getString(4));
		dvr.setPassword(cursor.getString(5));

		cursor.close();
		return dvr;
	}

	public Long addDvr(Dvr dvr) {
		return mDbHelper.addDvr(dvr, mDb);
	}

	public boolean deleteDvr(int id) {
		return mDb.delete(Dvr.TABLE_NAME, String.format("id = %s", id), null) == 1;
	}

	public boolean updateDvr(Dvr dvr) {
		ContentValues values = new ContentValues();

		values.put(Dvr.NAME, dvr.getName());
		values.put(Dvr.IP, dvr.getIp());
		values.put(Dvr.PORT, dvr.getPort());
		values.put(Dvr.USERNAME, dvr.getUserName());
		values.put(Dvr.PASSWORD, dvr.getPassword());

		return mDb.update(Dvr.TABLE_NAME, values,
				String.format("id = %s", dvr.getId()), null) == 1;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			String sql = String
					.format("create table %s (%s integer primary key autoincrement, %s text not null, %s text not null, %s integer not null, %s text not null, %s text )",
							Dvr.TABLE_NAME, Dvr.ID, Dvr.NAME, Dvr.IP, Dvr.PORT,
							Dvr.USERNAME, Dvr.PASSWORD);

			db.execSQL(sql);
		}

		public Long addDvr(Dvr dvr, SQLiteDatabase db) {
			ContentValues values = new ContentValues();

			values.put(Dvr.NAME, dvr.getName());
			values.put(Dvr.IP, dvr.getIp());
			values.put(Dvr.PORT, dvr.getPort());
			values.put(Dvr.USERNAME, dvr.getUserName());
			values.put(Dvr.PASSWORD,
					dvr.getPassword() == null ? null : dvr.getPassword());

			Long ret = db.insert(Dvr.TABLE_NAME, Dvr.ID, values);
			return ret;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + Dvr.TABLE_NAME);
			onCreate(db);
		}
	}
}
