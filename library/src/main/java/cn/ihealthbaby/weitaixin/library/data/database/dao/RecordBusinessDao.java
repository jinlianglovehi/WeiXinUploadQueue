package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by liuhongjian on 15/9/16 10:25.
 */
public class RecordBusinessDao {
	public static RecordBusinessDao instance;
	public RecordDao recordDao;

	private RecordBusinessDao(Context context) {
		DaoMaster.DevOpenHelper dbHelper = new DaoMaster.DevOpenHelper(context, "localrecord.db", null);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(database);
		DaoSession daoSession = daoMaster.newSession();
		recordDao = daoSession.getRecordDao();
	}

	public static RecordBusinessDao getInstance(Context context) {
		if (instance == null) {
			synchronized (RecordBusinessDao.class) {
				if (instance == null) {
					instance = new RecordBusinessDao(context);
				}
			}
		}
		return instance;
	}

	/**
	 * 查询所有数据
	 *
	 * @return
	 */
	public List<Record> queryAll() {
		List<Record> records = recordDao.queryBuilder().where(RecordDao.Properties.UploadState.eq(Record.UPLOAD_STATE_LOCAL)).orderDesc(RecordDao.Properties.RecordStartTime).build().list();
		return records;
	}

	public RecordDao getRecordDao() {
		return recordDao;
	}

	public long insert(Record record) {
		return recordDao.insert(record);
	}

	public void update(Record record) {
		recordDao.update(record);
	}

	public Record query(String localRecordId) {
		return recordDao.queryBuilder().where(RecordDao.Properties.LocalRecordId.eq(localRecordId)).uniqueOrThrow();
	}

	public Record query(Long id) {
		return recordDao.queryBuilder().where(RecordDao.Properties.Id.eq(id)).uniqueOrThrow();
	}

	public Record query(Record record) {
		return query(record.getId());
	}

	public void delete(Record record) {
		delete(record.getId());
	}

	public void delete(Long id) {
		recordDao.deleteByKey(id);
	}

	public void delete(String localRecordId) {
		Record record = query(localRecordId);
		delete(record);
	}

	public List<Record> loadAll() {
		return recordDao.loadAll();
	}

	public void deleteAll() {
		recordDao.deleteAll();
	}
}

