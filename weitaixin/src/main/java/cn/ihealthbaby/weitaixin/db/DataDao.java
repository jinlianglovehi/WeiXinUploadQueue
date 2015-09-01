package cn.ihealthbaby.weitaixin.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;

public class DataDao {
	
	private Context context;
	private DataDBHelper dbHelper;

	public DataDao(Context context) {
		this.context = context;
		dbHelper = new DataDBHelper(context);
	}

	
	public synchronized  void add(String tableName, final ArrayList<AdviceItem>  adviceItems){
		new Thread(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				if(db.isOpen()) {
					for (int i = 0; i < adviceItems.size(); i++) {
						AdviceItem adviceItem = adviceItems.get(i);
						delete(String.valueOf(adviceItem.getId()));
						db.beginTransaction();
						try {
							db.execSQL("insert into " + DataDBHelper.tableName + " (mid,gestationalWeeks,testTime,testTimeLong,status) values (?,?,?,?,?)",
									new Object[]{String.valueOf(adviceItem.getId()), adviceItem.getGestationalWeeks(), String.valueOf(DateTimeTool.date2Str(adviceItem.getTestTime(),null)), String.valueOf(adviceItem.getTestTimeLong()), String.valueOf(adviceItem.getStatus())});
							db.setTransactionSuccessful();
						} finally {
							db.endTransaction();
						}
					}
				}
			}
		}).start();
	}


	public void delete(String mid){
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
	
	
	public ArrayList<AdviceItem> getAllRecord(int pageSize){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<AdviceItem> adviceItems = new ArrayList<AdviceItem>();
		if (db.isOpen()) {
		  Cursor cursor = db.rawQuery("select mid,gestationalWeeks,testTime,testTimeLong," +
		  		"status from " + DataDBHelper.tableName, null);
			while (cursor.moveToNext()) {
				if (pageSize > 0) {
					AdviceItem adviceItem = new AdviceItem();
					String mid = cursor.getString(cursor.getColumnIndex("mid"));
					String gestationalWeeks = cursor.getString(cursor.getColumnIndex("gestationalWeeks"));
					String testTime = cursor.getString(cursor.getColumnIndex("testTime"));
					String testTimeLong = cursor.getString(cursor.getColumnIndex("testTimeLong"));
					String status = cursor.getString(cursor.getColumnIndex("status"));
					adviceItem.setId(Long.parseLong(mid));
					adviceItem.setGestationalWeeks(gestationalWeeks);
					adviceItem.setTestTime(DateTimeTool.str2Date(testTime));
					adviceItem.setTestTimeLong(Integer.parseInt(testTimeLong));
					adviceItem.setStatus(Integer.parseInt(status));
					adviceItems.add(adviceItem);
				}
				pageSize--;
			}
			cursor.close();
		}
		return adviceItems;
	}
	
	
	
}






