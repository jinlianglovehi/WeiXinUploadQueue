package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by liuhongjian on 15/9/16 21:39.
 */
public class DBOpenHelper extends DaoMaster.OpenHelper {
	public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
		super(context, name, factory);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion > newVersion) {
			Log.d("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion);
		}
	}
}
