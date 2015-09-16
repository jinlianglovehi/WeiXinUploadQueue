package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by liuhongjian on 15/9/16 10:25.
 */
public class RecordBusinessDao {
	public static RecordBusinessDao instance;
	public RecordDao recordDao;

	private RecordBusinessDao(Context context) {
		DBOpenHelper dbHelper = new DBOpenHelper(context, "record.db", null);
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
	 * 查询所有未上传的数据
	 *
	 * @return
	 */
	public List<Record> queryUnuploadedRecord() throws Exception {
		List<Record> records = recordDao.queryBuilder().where(RecordDao.Properties.UploadState.eq(Record.UPLOAD_STATE_LOCAL)).orderDesc(RecordDao.Properties.RecordStartTime).build().list();
		return records;
	}

	/**
	 * 查询本地未上传的数据,带分页功能
	 *
	 * @param page     页码
	 * @param pageSize 每页数据数量
	 * @return
	 */
	public List<Record> queryPagedUnuploadedRecord(int page, int pageSize) throws Exception {
		List<Record> list = recordDao.queryBuilder().limit(pageSize).where(new WhereCondition() {
			@Override
			public void appendTo(StringBuilder builder, String tableAlias) {
			}

			@Override
			public void appendValuesTo(List<Object> values) {
			}
		}).orderDesc(RecordDao.Properties.RecordStartTime).build().list();
		return list;
	}

	/**
	 * 查询所有数据的条数
	 *
	 * @return
	 */
	public long allCount() throws Exception {
		return recordDao.queryBuilder().buildCount().count();
	}

	/**
	 * 查询本地未上传的数据
	 *
	 * @return
	 */
	public long unuploadedCount() throws Exception {
		long count = recordDao.queryBuilder().where(new WhereCondition() {
			@Override
			public void appendTo(StringBuilder builder, String tableAlias) {
			}

			@Override
			public void appendValuesTo(List<Object> values) {
			}
		}).count();
		return count;
	}

	public RecordDao getRecordDao() {
		return recordDao;
	}

	/**
	 * 插入数据,非空字段必须进行设置,否则抛出异常
	 *
	 * @param record
	 * @return
	 * @throws Exception 插入失败,非空字段留空,或者唯一字段重复插入
	 */
	public long insert(Record record) throws Exception {
		return recordDao.insert(record);
	}

	/**
	 * 更新,会使用默认值
	 *
	 * @param record
	 * @throws Exception
	 */
	public void update(Record record) throws Exception {
		recordDao.update(record);
	}

	/**
	 * 用本地数据记录id进行查询
	 *
	 * @param localRecordId
	 * @return
	 * @throws Exception 查询出错会抛出异常
	 */
	public Record queryByLocalRecordId(String localRecordId) throws Exception {
		return recordDao.queryBuilder().where(RecordDao.Properties.LocalRecordId.eq(localRecordId)).uniqueOrThrow();
	}

	/**
	 * 根据id查询
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Record queryById(Long id) throws Exception {
		return recordDao.queryBuilder().where(RecordDao.Properties.Id.eq(id)).uniqueOrThrow();
	}

	/**
	 * 查询,数据需要有id
	 *
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public Record query(Record record) throws Exception {
		return queryById(record.getId());
	}

	/**
	 * 删除,需要有id
	 *
	 * @param record
	 * @throws Exception
	 */
	public void delete(Record record) throws Exception {
		deleteById(record.getId());
	}

	/**
	 * 根据id删除
	 *
	 * @param id
	 * @throws Exception
	 */
	public void deleteById(Long id) throws Exception {
		recordDao.deleteByKey(id);
	}

	/**
	 * 根据本地记录id删除
	 *
	 * @param localRecordId
	 * @throws Exception
	 */
	public void deleteByLocalRecordId(String localRecordId) throws Exception {
		Record record = queryByLocalRecordId(localRecordId);
		delete(record);
	}

	/**
	 * 列出所有数据,慎用,数据量过大时响应较慢
	 *
	 * @return
	 * @throws Exception
	 */
	public List<Record> loadAll() throws Exception {
		return recordDao.loadAll();
	}

	/**
	 * 删除所有
	 *
	 * @throws Exception
	 */
	public void deleteAll() throws Exception {
		recordDao.deleteAll();
	}

	/**
	 * 根据用户id查询
	 *
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Record queryByUserId(Long userId) throws Exception {
		Record record = recordDao.queryBuilder().where(RecordDao.Properties.UserId.eq(userId)).where(new WhereCondition() {
			@Override
			public void appendTo(StringBuilder builder, String tableAlias) {
				builder.append("UPLOAD_STATE=");
			}

			@Override
			public void appendValuesTo(List<Object> values) {
			}
		}).uniqueOrThrow();
		return record;
	}
}

