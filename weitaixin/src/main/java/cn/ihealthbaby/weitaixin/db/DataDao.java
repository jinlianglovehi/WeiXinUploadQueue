package cn.ihealthbaby.weitaixin.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.model.MyAdviceItem;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;

public class DataDao {
	private static DataDBHelper dbHelper;
	private static DataDao dataDao;

	private DataDao() {
	}

	public static DataDao getInstance(Context context) {
		if (dataDao == null) {
			dataDao = new DataDao();
		}
		if (dbHelper == null) {
			dbHelper = new DataDBHelper(context);
		}
		return dataDao;
	}

	public synchronized void add(final ArrayList<MyAdviceItem> adviceItems, final boolean isRecordNative) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (int i = 0; i < adviceItems.size(); i++) {
				MyAdviceItem adviceItem = adviceItems.get(i);
				if (isRecordNative) {
					db.beginTransaction();
					try {
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status," +
								           "isNativeRecord,feeling,purpose,userid,rdata,path,uploadstate,serialnum,jianceid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(),
										                      adviceItem.getTestTime().getTime(), adviceItem.getTestTimeLong(), adviceItem.getStatus(),
										                      adviceItem.getIsNativeRecord(), adviceItem.getFeeling(), adviceItem.getPurpose(),
										                      adviceItem.getUserid(), adviceItem.getRdata(), adviceItem.getPath(), adviceItem.getUploadstate(),
										                      adviceItem.getSerialnum(), adviceItem.getJianceid()
								          });
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				} else {
					db.beginTransaction();
					try {
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status) values (?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(), adviceItem.getTestTime().getTime(),
										                      adviceItem.getTestTimeLong(), adviceItem.getStatus()});
						LogUtil.d("DateTimegetTime", "DateTimegetTime==> " + adviceItem.getStatus());
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				}
			}
		}
	}

	public synchronized void add(final MyAdviceItem adviceItem, final boolean isRecordNative) {
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		adviceItems.add(adviceItem);
		addItemList(adviceItems, isRecordNative);
	}

	public synchronized MyAdviceItem find(long mid) {
		MyAdviceItem adviceItem = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery("select * from " + DataDBHelper.tableName + " where mid=?", new String[]{mid + ""});
				if (cursor.moveToNext()) {
					adviceItem = new MyAdviceItem();
					String mmid = cursor.getString(cursor.getColumnIndex("mid"));
					String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
					String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
					int testTimeLong = cursor.getInt(cursor.getColumnIndex("testTimeLong"));
					int status = cursor.getInt(cursor.getColumnIndex("status"));
					int isNativeRecord = cursor.getInt(cursor.getColumnIndex("isNativeRecord"));
					adviceItem.setId(Long.parseLong(mmid));
					adviceItem.setGestationalWeeks(gestationalWeeks);
					adviceItem.setTestTime(DateTimeTool.longDate2Str(Long.parseLong(testTime)));
					adviceItem.setTestTimeLong(testTimeLong);
					adviceItem.setStatus(status);
					adviceItem.setIsNativeRecord(isNativeRecord);
					return adviceItem;
				} else {
					return adviceItem;
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			return adviceItem;
		}
	}

	public synchronized MyAdviceItem findNative(String uuid) {
		MyAdviceItem adviceItem = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery("select * from " + DataDBHelper.tableName + " where jianceid=?", new String[]{uuid + ""});
				if (cursor.moveToNext()) {
					adviceItem = new MyAdviceItem();
					long mid = cursor.getLong(cursor.getColumnIndex("mid"));
					String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
					String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
					int testTimeLong = cursor.getInt(cursor.getColumnIndex("testTimeLong"));
					int status = cursor.getInt(cursor.getColumnIndex("status"));
					int isNativeRecord = cursor.getInt(cursor.getColumnIndex("isNativeRecord"));
					String feeling = cursor.getString(cursor.getColumnIndex("feeling"));
					String purpose = cursor.getString(cursor.getColumnIndex("purpose"));
					int userid = cursor.getInt(cursor.getColumnIndex("userid"));
					String rdata = cursor.getString(cursor.getColumnIndex("rdata"));
					String path = cursor.getString(cursor.getColumnIndex("path"));
					int uploadstate = cursor.getInt(cursor.getColumnIndex("uploadstate"));
					String serialnum = cursor.getString(cursor.getColumnIndex("serialnum"));
					String jianceid = cursor.getString(cursor.getColumnIndex("jianceid"));
					adviceItem.setId(mid);
					adviceItem.setGestationalWeeks(gestationalWeeks);
					adviceItem.setTestTime(DateTimeTool.longDate2Str(Long.parseLong(testTime)));
					adviceItem.setTestTimeLong(testTimeLong);
					adviceItem.setStatus(status);
					adviceItem.setIsNativeRecord(isNativeRecord);
					adviceItem.setFeeling(feeling);
					adviceItem.setPurpose(purpose);
					adviceItem.setUserid(userid);
					adviceItem.setRdata(rdata);
					adviceItem.setPath(path);
					adviceItem.setUploadstate(uploadstate);
					adviceItem.setSerialnum(serialnum);
					adviceItem.setJianceid(jianceid);
					return adviceItem;
				} else {
					return adviceItem;
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			return adviceItem;
		}
	}

	public synchronized void addItemList(final ArrayList<MyAdviceItem> adviceItems, final boolean isRecordNative) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (int i = 0; i < adviceItems.size(); i++) {
				MyAdviceItem adviceItem = adviceItems.get(i);
				if (isRecordNative) {
					delete(adviceItem.getId());
					db.beginTransaction();
					try {
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status," +
								           "isNativeRecord,feeling,purpose,userid,rdata,path,uploadstate,serialnum,jianceid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(),
										                      adviceItem.getTestTime().getTime(), adviceItem.getTestTimeLong(), adviceItem.getStatus(),
										                      adviceItem.getIsNativeRecord(), adviceItem.getFeeling(), adviceItem.getPurpose(),
										                      adviceItem.getUserid(), adviceItem.getRdata(), adviceItem.getPath(), adviceItem.getUploadstate(),
										                      adviceItem.getSerialnum(), adviceItem.getJianceid()
								          });
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				} else {
					delete(adviceItem.getId());
					db.beginTransaction();
					try {
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status) values (?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(), adviceItem.getTestTime().getTime(),
										                      adviceItem.getTestTimeLong(), adviceItem.getStatus()});
						LogUtil.d("DateTimegetTime", "DateTimegetTime==> " + adviceItem.getStatus());
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				}
			}
		}
	}

	public synchronized void addItem(final MyAdviceItem adviceItem, final boolean isRecordNative) {
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		adviceItems.add(adviceItem);
		addItemList(adviceItems, isRecordNative);
	}

	public void delete(long mid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (db.isOpen()) {
				db.execSQL("delete from " + DataDBHelper.tableName + " where mid=?", new Object[]{mid});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void update(final ArrayList<MyAdviceItem> adviceItems) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (db.isOpen()) {
				for (int i = 0; i < adviceItems.size(); i++) {
					MyAdviceItem myAdviceItem = adviceItems.get(i);
					ContentValues values = new ContentValues();
					if (myAdviceItem.getId() != -1) {
						values.put("mid", myAdviceItem.getId());
					}
					if (myAdviceItem.getGestationalWeeks() != null) {
						values.put("gestationalWeeks", myAdviceItem.getGestationalWeeks());
					}
					if (myAdviceItem.getTestTime() != null) {
						values.put("testTime", myAdviceItem.getTestTime().getTime());
					}
					if (myAdviceItem.getTestTimeLong() != -1) {
						values.put("testTimeLong", myAdviceItem.getTestTimeLong());
					}
					if (myAdviceItem.getStatus() != -1) {
						values.put("status", myAdviceItem.getStatus());
					}
					if (myAdviceItem.getIsNativeRecord() != -1) {
						values.put("isNativeRecord", myAdviceItem.getIsNativeRecord());
					}
					if (myAdviceItem.getFeeling() != null) {
						values.put("feeling", myAdviceItem.getFeeling());
					}
					if (myAdviceItem.getPurpose() != null) {
						values.put("purpose", myAdviceItem.getPurpose());
					}
					if (myAdviceItem.getUserid() != -1) {
						values.put("userid", myAdviceItem.getUserid());
					}
					if (myAdviceItem.getRdata() != null) {
						values.put("rdata", myAdviceItem.getRdata());
					}
					if (myAdviceItem.getPath() != null) {
						values.put("path", myAdviceItem.getPath());
					}
					if (myAdviceItem.getUploadstate() != -1) {
						values.put("uploadstate", myAdviceItem.getUploadstate());
					}
					if (myAdviceItem.getSerialnum() != null) {
						values.put("serialnum", myAdviceItem.getSerialnum());
					}
					if (myAdviceItem.getJianceid() != null) {
						values.put("jianceid", myAdviceItem.getJianceid());
					}
					db.update(DataDBHelper.tableName, values, "jianceid=?", new String[]{myAdviceItem.getJianceid() + ""});
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void updateItem(final MyAdviceItem myAdviceItem) {
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		adviceItems.add(myAdviceItem);
		update(adviceItems);
	}

	public ArrayList<MyAdviceItem> getAllRecord(boolean isRecordNativeed) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		ArrayList<MyAdviceItem> adviceItemsCloud = new ArrayList<MyAdviceItem>();
		ArrayList<MyAdviceItem> adviceItemsNative = new ArrayList<MyAdviceItem>();
		if (db.isOpen()) {
			if (isRecordNativeed) {
				Cursor cursor = db.rawQuery("mid,gestationalWeeks,testTime,testTimeLong,status," +
						                            "isNativeRecord,feeling,purpose,userid,rdata,path,uploadstate,serialnum,jianceid from " + DataDBHelper.tableName, null);
				while (cursor.moveToNext()) {
					MyAdviceItem adviceItem = new MyAdviceItem();
					long mid = cursor.getLong(cursor.getColumnIndex("mid"));
					String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
					String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
					int testTimeLong = cursor.getInt(cursor.getColumnIndex("testTimeLong"));
					int status = cursor.getInt(cursor.getColumnIndex("status"));
					int isNativeRecord = cursor.getInt(cursor.getColumnIndex("isNativeRecord"));
					String feeling = cursor.getString(cursor.getColumnIndex("feeling"));
					String purpose = cursor.getString(cursor.getColumnIndex("purpose"));
					int userid = cursor.getInt(cursor.getColumnIndex("userid"));
					String rdata = cursor.getString(cursor.getColumnIndex("rdata"));
					String path = cursor.getString(cursor.getColumnIndex("path"));
					int uploadstate = cursor.getInt(cursor.getColumnIndex("uploadstate"));
					String serialnum = cursor.getString(cursor.getColumnIndex("serialnum"));
					String jianceid = cursor.getString(cursor.getColumnIndex("jianceid"));
					adviceItem.setId(mid);
					adviceItem.setGestationalWeeks(gestationalWeeks);
					adviceItem.setTestTime(DateTimeTool.longDate2Str(Long.parseLong(testTime)));
					adviceItem.setTestTimeLong(testTimeLong);
					adviceItem.setStatus(status);
					adviceItem.setIsNativeRecord(isNativeRecord);
					adviceItem.setFeeling(feeling);
					adviceItem.setPurpose(purpose);
					adviceItem.setUserid(userid);
					adviceItem.setRdata(rdata);
					adviceItem.setPath(path);
					adviceItem.setUploadstate(uploadstate);
					adviceItem.setSerialnum(serialnum);
					adviceItem.setJianceid(jianceid);
					if (isNativeRecord == 100) { //100本地  1云端
						adviceItemsNative.add(adviceItem);
					} else {
						adviceItemsCloud.add(adviceItem);
					}
				}
				ArrayList<MyAdviceItem> adviceItemsCloudTenCount = new ArrayList<MyAdviceItem>();
				if (adviceItemsCloud.size() > 0) {
					if (adviceItemsCloud.size() >= 10) {
						for (int i = 0; i < 10; i++) {
							adviceItemsCloudTenCount.add(adviceItemsCloud.get(i));
						}
					} else {
						for (int i = 0; i < adviceItemsCloud.size(); i++) {
							adviceItemsCloudTenCount.add(adviceItemsCloud.get(i));
						}
					}
				}
				adviceItems.addAll(adviceItemsCloudTenCount);
				adviceItems.addAll(adviceItemsNative);
				cursor.close();
			} else {
				Cursor cursor = db.rawQuery("select mid,gestationalWeeks,testTime,testTimeLong,status,isNativeRecord from " + DataDBHelper.tableName, null);
				while (cursor.moveToNext()) {
					MyAdviceItem adviceItem = new MyAdviceItem();
					String mid = cursor.getString(cursor.getColumnIndex("mid"));
					String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
					String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
					int testTimeLong = cursor.getInt(cursor.getColumnIndex("testTimeLong"));
					int status = cursor.getInt(cursor.getColumnIndex("status"));
					int isNativeRecord = cursor.getInt(cursor.getColumnIndex("isNativeRecord"));
					adviceItem.setId(Long.parseLong(mid));
					adviceItem.setGestationalWeeks(gestationalWeeks);
					LogUtil.d("testTime", "testTime44 ==>" + status);
					LogUtil.d("testTime", "testTime33 ==>" + DateTimeTool.longDate2Str(Long.parseLong(testTime)));
					adviceItem.setTestTime(DateTimeTool.longDate2Str(Long.parseLong(testTime)));
					adviceItem.setTestTimeLong(testTimeLong);
					adviceItem.setStatus(status);
					adviceItem.setIsNativeRecord(isNativeRecord);
					LogUtil.d("isNativeRecord", "isNativeRecord ==> " + isNativeRecord);
					if (isNativeRecord == 100) { //100本地  1云端
						adviceItemsNative.add(adviceItem);
					} else {
						adviceItemsCloud.add(adviceItem);
					}
				}
				ArrayList<MyAdviceItem> adviceItemsCloudTenCount = new ArrayList<MyAdviceItem>();
				if (adviceItemsCloud.size() > 0) {
					if (adviceItemsCloud.size() >= 10) {
						for (int i = 0; i < 10; i++) {
							adviceItemsCloudTenCount.add(adviceItemsCloud.get(i));
						}
					} else {
						for (int i = 0; i < adviceItemsCloud.size(); i++) {
							adviceItemsCloudTenCount.add(adviceItemsCloud.get(i));
						}
					}
				}
				LogUtil.d("adviceItemsNative", adviceItemsCloudTenCount.size() + " <==adviceItemsNative==> " + adviceItemsNative.size());
				adviceItems.addAll(adviceItemsCloudTenCount);
				adviceItems.addAll(adviceItemsNative);
				cursor.close();
			}
		}
		LogUtil.d("adviceItemsAllAll", adviceItems.size() + " -adviceItemsAllAll ==> " + adviceItems);
		return adviceItems;
	}
}


