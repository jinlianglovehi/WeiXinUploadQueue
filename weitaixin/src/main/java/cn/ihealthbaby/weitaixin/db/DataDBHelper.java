package cn.ihealthbaby.weitaixin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataDBHelper extends SQLiteOpenHelper{

	public DataDBHelper(Context context) {
		super(context, "record.db", null, 1);
	}

	public static String tableName="recordTable";
	public static String tableNativeName="recordNativeTable";

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE recordTable (" +
				"_id integer primary key autoincrement," + //
				"mid varchar(50)," + //
				"gestationalWeeks varchar(50)," + //
				"testTime varchar(100)," + //
				"testTimeLong varchar(100)," + //
				"status varchar(20)" + //
				")"); //


		db.execSQL("CREATE TABLE recordNativeTable (" +
				"_id integer primary key autoincrement," + //
				"mid varchar(50)," + //
				"gestationalWeeks varchar(50)," + //
				"testTime varchar(100)," + //
				"testTimeLong varchar(100)," + //
				"status varchar(20)" + //
				")"); //
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	
}




