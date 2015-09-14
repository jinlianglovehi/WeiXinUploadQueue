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



	////////////////////liuhongjian/////////////////////////



	/**
	 * 单纯添加，本地记录一定要设置uploadstate字段，不然查询不到记录
	 */
	public synchronized void add(final ArrayList<MyAdviceItem> adviceItems, final boolean isRecordNative) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (int i = 0; i < adviceItems.size(); i++) {
				MyAdviceItem adviceItem = adviceItems.get(i);
				if (isRecordNative) {
					db.beginTransaction();
					try {
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status," +
								           "feeling,purpose,userid,rdata,path,uploadstate,serialnum,jianceid) values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(),
												  adviceItem.getTestTime()==null?-1:adviceItem.getTestTime().getTime(), adviceItem.getTestTimeLong(), adviceItem.getStatus(),
										                      adviceItem.getFeeling(), adviceItem.getPurpose(),
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
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status,uploadstate) values (?,?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(), adviceItem.getTestTime().getTime(),
										                      adviceItem.getTestTimeLong(), adviceItem.getStatus(), adviceItem.getUploadstate()});
						LogUtil.d("DateTimegetTime", "DateTimegetTime==> " + adviceItem.getStatus());
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				}
			}
		}
	}


	/**
	 * 单纯添加，本地记录一定要设置uploadstate字段，不然查询不到记录
	 */
	public synchronized void add(final MyAdviceItem adviceItem, final boolean isRecordNative) {
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		adviceItems.add(adviceItem);
		add(adviceItems, isRecordNative);
	}


	/**
	 * 根据 jianceid 更新记录
	 */
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


	/**
	 * 根据 jianceid 更新记录
	 */
	public void update(final MyAdviceItem myAdviceItem) {
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		adviceItems.add(myAdviceItem);
		update(adviceItems);
	}


	/**
	 * 根据 uploadstate 只获取本地记录 的所有字段，
	 */
	public ArrayList<MyAdviceItem> getAllRecordNativeOnly() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<MyAdviceItem> adviceItemsNative = new ArrayList<MyAdviceItem>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select mid,gestationalWeeks,testTime,testTimeLong,status," +
					"feeling,purpose,userid,rdata,path,uploadstate,serialnum,jianceid from " + DataDBHelper.tableName, null);
			while (cursor.moveToNext()) {
				MyAdviceItem adviceItem = new MyAdviceItem();
				long mid = cursor.getLong(cursor.getColumnIndex("mid"));
				String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
				String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
				int testTimeLong = cursor.getInt(cursor.getColumnIndex("testTimeLong"));
				int status = cursor.getInt(cursor.getColumnIndex("status"));
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
				adviceItem.setFeeling(feeling);
				adviceItem.setPurpose(purpose);
				adviceItem.setUserid(userid);
				adviceItem.setRdata(rdata);
				adviceItem.setPath(path);
				adviceItem.setUploadstate(uploadstate);
				adviceItem.setSerialnum(serialnum);
				adviceItem.setJianceid(jianceid);
				LogUtil.d("uploadstateNATIVE", "uploadstateNATIVE==> " + uploadstate);
				if (uploadstate == MyAdviceItem.NATIVE_RECORD || uploadstate == MyAdviceItem.UPLOADING_RECORD) {
					adviceItemsNative.add(adviceItem);
				}
			}
			cursor.close();
		}
		LogUtil.d("adviceItemsNativeOnly", adviceItemsNative.size() + " -adviceItemsNativeOnly ==> " + adviceItemsNative);
		return adviceItemsNative;
	}




	////////////////公用//////////////////////



	/**
	 * 找云端记录
	 */
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
//					int uploadstate = cursor.getInt(cursor.getColumnIndex("uploadstate"));
					adviceItem.setId(Long.parseLong(mmid));
					adviceItem.setGestationalWeeks(gestationalWeeks);
					adviceItem.setTestTime(DateTimeTool.longDate2Str(Long.parseLong(testTime)));
					adviceItem.setTestTimeLong(testTimeLong);
					adviceItem.setStatus(status);
//					adviceItem.setUploadstate(uploadstate);
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


	/**
	 * 根据uuid或jianceid找本地记录
	 */
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



	/**
	 * 根据mid删除记录，而不是根据uuid删除
	 */
	public void delete(long mid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (db.isOpen()) {
				db.execSQL("delete from " + DataDBHelper.tableName + " where mid=?", new Object[]{mid + ""});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 根据mid查找记录
	 */
	public boolean findById(long mid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery("select * from " + DataDBHelper.tableName + " where mid=?", new String[]{mid + ""});
				if (cursor.moveToFirst()) {
					return true;
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return false;
	}




	////////////chenweihua////////////////////////////




	/**
	 * 根据mid带删除记录的add
	 * 只适用于记录界面，其他地方不适合使用，其他地方调用单纯添加
	 */
	public synchronized void addItemList(final ArrayList<MyAdviceItem> adviceItems, final boolean isRecordNative) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (int i = 0; i < adviceItems.size(); i++) {
				MyAdviceItem adviceItem = adviceItems.get(i);
				if (isRecordNative) {
//					delete(adviceItem.getId());
					if (findById(adviceItem.getId())) {
						break;
					}
					db.beginTransaction();
					try {
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status," +
								           "feeling,purpose,userid,rdata,path,uploadstate,serialnum,jianceid) values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(),
										                      adviceItem.getTestTime().getTime(), adviceItem.getTestTimeLong(), adviceItem.getStatus(),
										                      adviceItem.getFeeling(), adviceItem.getPurpose(),
										                      adviceItem.getUserid(), adviceItem.getRdata(), adviceItem.getPath(), adviceItem.getUploadstate(),
										                      adviceItem.getSerialnum(), adviceItem.getJianceid()
								          });
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				} else {
//					delete(adviceItem.getId());
					if (findById(adviceItem.getId())) {
						break;
					}
					db.beginTransaction();
					try {
						db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status,uploadstate) values (?,?,?,?,?,?)",
								          new Object[]{adviceItem.getId(), adviceItem.getGestationalWeeks(), adviceItem.getTestTime().getTime(),
										                      adviceItem.getTestTimeLong(), adviceItem.getStatus(), adviceItem.getUploadstate()});
						LogUtil.d("DateTimegetTime", "DateTimegetTime==> " + adviceItem.getStatus());
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				}
			}
		}
	}

	/**
	 * 根据mid带删除记录的add
	 * 只适用于记录界面，其他地方不适合使用，其他地方调用单纯添加
	 */
	public synchronized void addItem(final MyAdviceItem adviceItem, final boolean isRecordNative) {
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		adviceItems.add(adviceItem);
		addItemList(adviceItems, isRecordNative);
	}



	/**
	 * 获取本地记录和云端记录的所有字段
	 */
	public ArrayList<MyAdviceItem> getAllRecordNativeAndCloud() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		ArrayList<MyAdviceItem> adviceItemsCloud = new ArrayList<MyAdviceItem>();
		ArrayList<MyAdviceItem> adviceItemsNative = new ArrayList<MyAdviceItem>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select mid,gestationalWeeks,testTime,testTimeLong,status," +
					                            "feeling,purpose,userid,rdata,path,uploadstate,serialnum,jianceid from " + DataDBHelper.tableName, null);
			while (cursor.moveToNext()) {
				MyAdviceItem adviceItem = new MyAdviceItem();
				long mid = cursor.getLong(cursor.getColumnIndex("mid"));
				String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
				String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
				int testTimeLong = cursor.getInt(cursor.getColumnIndex("testTimeLong"));
				int status = cursor.getInt(cursor.getColumnIndex("status"));
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
				adviceItem.setFeeling(feeling);
				adviceItem.setPurpose(purpose);
				adviceItem.setUserid(userid);
				adviceItem.setRdata(rdata);
				adviceItem.setPath(path);
				adviceItem.setUploadstate(uploadstate);
				adviceItem.setSerialnum(serialnum);
				adviceItem.setJianceid(jianceid);
				if (uploadstate == MyAdviceItem.NATIVE_RECORD || uploadstate == MyAdviceItem.UPLOADING_RECORD) {
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
		}
		LogUtil.d("adviceItemsAllAll", adviceItems.size() + " -adviceItemsAllAll ==> " + adviceItems);
		return adviceItems;
	}


	/**
	 * 获取记录界面的显示数据的部分字段(包含本地和云端记录)
	 */
	public ArrayList<MyAdviceItem> getAllRecordNativeAndCloudOnView() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<MyAdviceItem> adviceItems = new ArrayList<MyAdviceItem>();
		ArrayList<MyAdviceItem> adviceItemsCloud = new ArrayList<MyAdviceItem>();
		ArrayList<MyAdviceItem> adviceItemsNative = new ArrayList<MyAdviceItem>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select mid,gestationalWeeks,testTime,testTimeLong,status,uploadstate from " + DataDBHelper.tableName, null);
			while (cursor.moveToNext()) {
				MyAdviceItem adviceItem = new MyAdviceItem();
				String mid = cursor.getString(cursor.getColumnIndex("mid"));
				String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
				String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
				int testTimeLong = cursor.getInt(cursor.getColumnIndex("testTimeLong"));
				int status = cursor.getInt(cursor.getColumnIndex("status"));
				int uploadstate = cursor.getInt(cursor.getColumnIndex("uploadstate"));
				adviceItem.setId(Long.parseLong(mid));
				adviceItem.setGestationalWeeks(gestationalWeeks);
				LogUtil.d("testTime", "testTime44 ==>" + status);
				LogUtil.d("testTime", "testTime33 ==>" + DateTimeTool.longDate2Str(Long.parseLong(testTime)));
				adviceItem.setTestTime(DateTimeTool.longDate2Str(Long.parseLong(testTime)));
				adviceItem.setTestTimeLong(testTimeLong);
				adviceItem.setStatus(status);
				LogUtil.d("uploadstate", "uploadstate ==> " + uploadstate);
				if (uploadstate == MyAdviceItem.NATIVE_RECORD || uploadstate == MyAdviceItem.UPLOADING_RECORD) {
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
		LogUtil.d("adviceItemsAllAll", adviceItems.size() + " -adviceItemsAllAll ==> " + adviceItems);
		return adviceItems;
	}



}


