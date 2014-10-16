package com.donkey.datastorage;

import com.donkey.util.AppKeys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, AppKeys.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + AppKeys.MEMORY_TABLE_NAME
				+ "(memory_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "memory_user_id INTEGER," + "memory_title VARCHAR,"
				+ "memory_content TEXT," + "memory_location VARCHAR,"
				+ "memory_last_modify_time DATETIME" + ")");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + AppKeys.LANDMARK_TABLE_NAME
				+ "(landmark_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "landmark_memory_id INTEGER," + "landmark_name VARCHAR,"
				+ "landmark_type INTEGER," + "landmark_content TEXT,"
				+ "media_type INTEGER," + "media_path VARCHAR,"
				+ "landmark_longitude REAL,"
				+ "landmark_latitude REAL" + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("database", AppKeys.DATABASE_NAME + " version chagned");
	}

}
