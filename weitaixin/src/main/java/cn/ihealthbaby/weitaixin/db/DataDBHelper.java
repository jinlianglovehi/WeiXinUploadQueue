package cn.ihealthbaby.weitaixin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataDBHelper extends SQLiteOpenHelper{

	public DataDBHelper(Context context) {
		super(context, "record2.db", null, 1);
	}

	public static String tableName="record";
//	public static String tableName="recordTable";
//	public static String tableNativeName="recordNativeTable";
	public static int isNativeRecord = 0; //0本地  1云端


	@Override
	public void onCreate(SQLiteDatabase db) {
		
//		db.execSQL("CREATE TABLE recordTable (" +
//				"_id integer primary key autoincrement," + //
//				"mid varchar(50)," + //
//				"gestationalWeeks varchar(50)," + //
//				"testTime varchar(100)," + //
//				"testTimeLong varchar(100)," + //
//				"status varchar(20)" + //
//				")"); //
//
//
//		db.execSQL("CREATE TABLE recordNativeTable (" +
//				"_id integer primary key autoincrement," + //
//				"mid varchar(50)," + //
//				"gestationalWeeks varchar(50)," + //
//				"testTime varchar(100)," + //
//				"testTimeLong varchar(100)," + //
//				"status varchar(20)" + //
//				")"); //


		db.execSQL("CREATE TABLE record (" +
				"_id integer primary key autoincrement," + //
				"mid varchar(50)," + //
				"gestationalWeeks varchar(50)," + //
				"testTime varchar(100)," + //text - timestamp
				"testTimeLong integer," + //
				"status integer," + //
				"isNativeRecord integer," + //0本地  1云端
				"feeling varchar(50)," + //
				"purpose varchar(50)," + //
				"userid integer," + //
				"rdata text," + //
				"path varchar(50)," + //
				"uploadstate integer," + //
				"serialnum varchar(50)," + //
				"jianceid varchar(50)" + //uuid
				")"); //

	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	
}




