package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
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
	 * 查询所有数据
	 *
	 * @return
	 */
	public List<Record> queryRecord(final int... uploadStates) throws Exception {
		QueryBuilder<Record> recordQueryBuilder = recordDao.queryBuilder();
		List<Record> records = recordQueryBuilder
				                       .where(new WhereCondition() {
					                       @Override
					                       public void appendTo(StringBuilder builder, String tableAlias) {
						                       for (int i = 0; i < uploadStates.length; i++) {
							                       builder.append("UPLOAD_STATE = " + uploadStates[i]);
							                       if (i != uploadStates.length - 1) {
								                       builder.append("AND");
							                       }
						                       }
					                       }

					                       @Override
					                       public void appendValuesTo(List<Object> values) {
					                       }
				                       })
				                       .orderDesc(RecordDao.Properties.RecordStartTime)
				                       .build()
				                       .list();
		return records;
	}

	/**
	 * 查询某一用户所有状态的数据,例如: queryUserRecord(1000l, Record.UPLOAD_STATE_LOCAL,
	 * Record.UPLOAD_STATE_UPLOADING);
	 *
	 * @return
	 */
	public List<Record> queryUserRecord(long userId, final int... uploadStates) throws Exception {
		QueryBuilder<Record> recordQueryBuilder = recordDao.queryBuilder();
		List<Record> records = recordQueryBuilder
				                       .where(new WhereCondition() {
					                       @Override
					                       public void appendTo(StringBuilder builder, String tableAlias) {
						                       for (int i = 0; i < uploadStates.length; i++) {
							                       builder.append("UPLOAD_STATE = " + uploadStates[i]);
							                       if (i != uploadStates.length - 1) {
								                       builder.append(" OR ");
							                       }
						                       }
					                       }

					                       @Override
					                       public void appendValuesTo(List<Object> values) {
					                       }
				                       })
				                       .where(RecordDao.Properties.UserId.eq(userId))
				                       .orderDesc(RecordDao.Properties.RecordStartTime)
				                       .build()
				                       .list();
		return records;
	}

	/**
	 * 查询本地所有用户数据,带分页功能,依据开始时间降序排序
	 *
	 * @param page     页码,从1开始
	 * @param pageSize 每页数据数量
	 * @return
	 */
	public List<Record> queryPagedRecord(int page, int pageSize, final int... uploadStates) throws Exception {
		List<Record> list = recordDao.queryBuilder()
				                    .limit(pageSize)
				                    .offset((page - 1) * pageSize)
				                    .where(new WhereCondition() {
					                    @Override
					                    public void appendTo(StringBuilder builder, String tableAlias) {
						                    for (int i = 0; i < uploadStates.length; i++) {
							                    builder.append("UPLOAD_STATE = " + uploadStates[i]);
							                    if (i != uploadStates.length - 1) {
								                    builder.append("AND");
							                    }
						                    }
					                    }

					                    @Override
					                    public void appendValuesTo(List<Object> values) {
					                    }
				                    })
				                    .orderDesc(RecordDao.Properties.RecordStartTime)
				                    .build()
				                    .list();
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
	 * 根据上传状态查询本地数据
	 *
	 * @return
	 */
	public long count(final int... uploadStates) throws Exception {
		long count = recordDao.queryBuilder()
				             .where(new WhereCondition() {
					                    @Override
					                    public void appendTo(StringBuilder builder, String tableAlias) {
						                    for (int i = 0; i < uploadStates.length; i++) {
							                    builder.append("UPLOAD_STATE = " + uploadStates[i]);
						                    }
					                    }

					                    @Override
					                    public void appendValuesTo(List<Object> values) {
					                    }
				                    }
				             )
				             .count();
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
		if (record.getUserId() == 0) {
			throw new IllegalArgumentException("UserId cannot be 0");
		}
		if (record.getUploadState() == 0) {
			record.setUploadState(Record.UPLOAD_STATE_LOCAL);
		}
		return recordDao.insert(record);
	}

	/**
	 * 更新,只更新非默认值
	 *
	 * @param record
	 * @throws Exception
	 */
	public void update(Record record) throws Exception {
		Record query = query(record);
		Integer uploadState = record.getUploadState();
		Date recordStartTime = record.getRecordStartTime();
		Integer duration = record.getDuration();
		String recordData = record.getRecordData();
		String soundPath = record.getSoundPath();
		Integer feelingId = record.getFeelingId();
		String feelingString = record.getFeelingString();
		Integer purposeId = record.getPurposeId();
		String purposeString = record.getPurposeString();
		if (uploadState != 0) {
			record.setUploadState(uploadState);
		}
		if (recordStartTime != null) {
			record.setRecordStartTime(recordStartTime);
		}
		if (duration != null) {
			record.setDuration(duration);
		}
		if (recordData != null) {
			record.setRecordData(recordData);
		}
		if (soundPath != null) {
			record.setSoundPath(soundPath);
		}
		if (feelingId != null) {
			record.setFeelingId(feelingId);
		}
		if (purposeId != null) {
			record.setPurposeId(purposeId);
		}
		if (feelingString != null) {
			record.setFeelingId(feelingId);
		}
		if (purposeString != null) {
			record.setPurposeId(purposeId);
		}
		recordDao.update(query);
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
}

