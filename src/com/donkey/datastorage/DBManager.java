package com.donkey.datastorage;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.donkey.entity.Landmark;
import com.donkey.entity.Memory;
import com.donkey.util.AppKeys;

public class DBManager {
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		dbHelper = new DBHelper(context);
		if (db == null || !db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
	}

	/**
	 * insert a memory item to "memory" table
	 * 
	 * @param m
	 *            memory entity
	 */
	public void insertOneMemory(Memory m) {
		if (!db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		db.beginTransaction();
		try {
			db.execSQL(
					"insert into " + AppKeys.MEMORY_TABLE_NAME + " values ("
							+ "NULL,?,?,?,?,?" + ")",
					new Object[] { String.valueOf(m.getMemoryUserId()),
							m.getMemoryTitle(), m.getMemoryContent(),
							m.getMemoryLocation(), m.getMemoryLastModifyTime() });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * insert a landmark item to "landmark" table
	 * 
	 * @param landmark
	 *            landmark entity
	 * @author zouliping
	 */
	public void insertOneLandmark(Landmark landmark) {
		if (!db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		db.beginTransaction();
		try {
			db.execSQL(
					"insert into " + AppKeys.LANDMARK_TABLE_NAME + " values ("
							+ "NULL,?,?,?,?,?,?,?" + ")",
					new Object[] { String.valueOf(landmark.getLandmarkId()),
							landmark.getLandmarkName(),
							String.valueOf(landmark.getLandmarkType()),
							landmark.getLandmarkContent(),
							String.valueOf(landmark.getMediaType()),
							landmark.getMediaPath(),
							String.valueOf(landmark.getLongitude()),
							String.valueOf(landmark.getLatitude()) });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * return the specific memory list by the given userid
	 * 
	 * @param uid
	 *            userid
	 * @return
	 */
	public ArrayList<Memory> getSpecMemoryList(int uid) {
		if (!db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		ArrayList<Memory> memoryList = new ArrayList<Memory>();
		Memory m = null;
		String sqlString = "select * from " + AppKeys.MEMORY_TABLE_NAME
				+ " where memory_user_id = ?";
		Cursor cursor = db.rawQuery(sqlString,
				new String[] { String.valueOf(uid) });
		while (cursor.moveToNext()) {
			m = new Memory();
			m.setMemoryContent(cursor.getString(cursor
					.getColumnIndex("memory_content")));
			m.setMemoryId(cursor.getInt(cursor.getColumnIndex("memory_id")));
			m.setMemoryLastModifyTime(cursor.getString(cursor
					.getColumnIndex("memory_last_modify_time")));
			m.setMemoryLocation(cursor.getString(cursor
					.getColumnIndex("memory_location")));
			m.setMemoryTitle(cursor.getString(cursor
					.getColumnIndex("memory_title")));
			m.setMemoryUserId(cursor.getInt(cursor
					.getColumnIndex("memory_user_id")));
			memoryList.add(m);
		}
		db.close();
		return memoryList;
	}

	/**
	 * return the specific landmark list by the given memId
	 * 
	 * @param memId
	 * @return
	 * 
	 * @author zouliping
	 */
	public ArrayList<Landmark> getSpecLandmarkList(int memId) {
		if (!db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		ArrayList<Landmark> landmarksList = new ArrayList<Landmark>();
		Landmark landmark = new Landmark();
		String sqlString = "select * from " + AppKeys.LANDMARK_TABLE_NAME
				+ " where landmark_memory_id = ?";
		Cursor cursor = db.rawQuery(sqlString,
				new String[] { String.valueOf(memId) });
		while (cursor.moveToNext()) {
			landmark = new Landmark();
			landmark.setLandmarkId(cursor.getInt(cursor
					.getColumnIndex("landmark_id")));
			landmark.setLandmarkName(cursor.getString(cursor
					.getColumnIndex("landmark_name")));
			landmark.setLandmarkContent(cursor.getString(cursor
					.getColumnIndex("landmark_content")));
			landmark.setLandmarkType(cursor.getInt(cursor
					.getColumnIndex("landmark_type")));
			landmark.setMediaType(cursor.getInt(cursor
					.getColumnIndex("media_type")));
			landmark.setMediaPath(cursor.getString(cursor
					.getColumnIndex("media_path")));
			landmark.setLongitude(cursor.getFloat(cursor
					.getColumnIndex("landmark_longitude")));
			landmark.setLatitude(cursor.getFloat(cursor
					.getColumnIndex("landmark_latitude")));

			landmarksList.add(landmark);
		}
		db.close();
		return landmarksList;
	}

	/**
	 * get memory id
	 * 
	 * @return Memid memoryid
	 */
	public int getMemId() {
		if (!db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		int result = 0;
		String[] columns = { "MAX(memory_id)" };
		Cursor cursor = db.query(AppKeys.MEMORY_TABLE_NAME, columns, null,
				null, null, null, null);
		if (cursor.getCount() == 0)
			return 0;
		cursor.moveToFirst();
		result = cursor.getInt(0);
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * get landmark count
	 * 
	 * @return
	 */
	public int getLandmarkCount() {
		if (!db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		String[] columns = { "COUNT(*)" };
		Cursor resultCursor = db.query(AppKeys.LANDMARK_TABLE_NAME, columns,
				null, null, null, null, null);
		resultCursor.moveToFirst();
		db.close();
		return resultCursor.getInt(0);
	}

	/**
	 * close the db
	 */
	public void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
}
