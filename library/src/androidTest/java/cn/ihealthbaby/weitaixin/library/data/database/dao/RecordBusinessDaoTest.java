package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.content.Context;
import android.test.InstrumentationTestCase;

import java.util.List;

import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by liuhongjian on 15/9/16 10:47.
 */
public class RecordBusinessDaoTest extends InstrumentationTestCase {
	private static final String SERIAL_NUMBER = "HELLOWORLD";
	public RecordBusinessDao instance;
	public RecordDao recordDao;
	public String LOCAL_RECORD_ID = "1234567890";
	public Record record0;



	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getInstrumentation().getContext();
		instance = RecordBusinessDao.getInstance(context);
		recordDao = instance.getRecordDao();
		record0 = new Record();
//		LOCAL_RECORD_ID = UUID.randomUUID().toString();
		record0.setLocalRecordId(LOCAL_RECORD_ID);
		record0.setSerialNumber(SERIAL_NUMBER);
	}

	public void test() {
		testInsert();
		testUpdate();
		testDelete();
	}

	public void testInsert() {
		recordDao.insert(record0);
		Record record = query(LOCAL_RECORD_ID);
		assertNotNull(record);
		assertEquals(record0.getLocalRecordId(), record.getLocalRecordId());
	}

	public void testUpdate() {
		Record record = recordDao.queryBuilder().where(RecordDao.Properties.LocalRecordId.eq(LOCAL_RECORD_ID)).uniqueOrThrow();
		record.setDuration(10000l);
		recordDao.update(record);
		Record record1 = recordDao.queryBuilder().where(RecordDao.Properties.LocalRecordId.eq(LOCAL_RECORD_ID)).uniqueOrThrow();
		assertNotNull(record);
		assertEquals(record.getDuration(), record1.getDuration());
	}

	public void testDelete() {
		Record record = query(record0.getLocalRecordId());
		assertNotNull(record);
		recordDao.deleteByKey(record.getId());
		record = query(record0.getLocalRecordId());
		assertNull(record);
	}

	public void testQuery() {
		Record record = recordDao.queryBuilder().where(RecordDao.Properties.LocalRecordId.eq(LOCAL_RECORD_ID)).unique();
		assertNotNull(record);
		assertEquals(record.getLocalRecordId(), LOCAL_RECORD_ID);
	}

	public Record query(final String localRecordId) {
		Record record = recordDao.queryBuilder().where(new WhereCondition() {
			@Override
			public void appendTo(StringBuilder builder, String tableAlias) {
				builder.append("LOCAL_RECORD_ID = " + localRecordId);
			}

			@Override
			public void appendValuesTo(List<Object> values) {
			}
		}).unique();
		return record;
	}
}