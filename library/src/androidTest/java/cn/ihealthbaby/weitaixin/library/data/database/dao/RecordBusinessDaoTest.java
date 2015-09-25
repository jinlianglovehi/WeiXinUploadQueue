package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.content.Context;
import android.test.InstrumentationTestCase;

import java.util.Random;
import java.util.UUID;

/**
 * Created by liuhongjian on 15/9/16 10:47.
 */
public class RecordBusinessDaoTest extends InstrumentationTestCase {
	private static final String SERIAL_NUMBER = "HELLOWORLD";
	public RecordBusinessDao instance;
	public RecordDao recordDao;
	public String LOCAL_RECORD_ID = "9a052b62-bcdb-41a7-95cc-76e031d0751f";
	//	public String LOCAL_RECORD_ID = UUID.randomUUID().toString();
	public Record record0;
	public String localRecordId;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getInstrumentation().getContext();
		instance = RecordBusinessDao.getInstance(context);
		recordDao = instance.getRecordDao();
	}

	public void test() throws Exception {
		for (int i = 0; i < 10; i++) {
			testInsert();
			testUpdate();
		}
//		testDelete();
	}

	public void testInsert() throws Exception {
		localRecordId = UUID.randomUUID().toString();
		record0 = new Record();
		record0.setSerialNumber(SERIAL_NUMBER);
		record0.setUserName("userName");
		record0.setUserId(new Random().nextInt());
		record0.setLocalRecordId(localRecordId);
		instance.insert(record0);
		Record record = instance.queryByLocalRecordId(localRecordId);
		assertNotNull(record);
		assertEquals(record0.getLocalRecordId(), record.getLocalRecordId());
	}

	public void testInsertFail() throws Exception {
		localRecordId = UUID.randomUUID().toString();
		record0 = new Record();
		record0.setLocalRecordId(localRecordId);
		try {
			instance.insert(record0);
		} catch (Exception e) {
			assertNotNull(e instanceof IllegalArgumentException);
		}
	}

	public void testUpdate() throws Exception {
		Record record = instance.queryByLocalRecordId(LOCAL_RECORD_ID);
		record.setDuration(10000);
		recordDao.update(record);
		Record record1 = instance.queryByLocalRecordId(LOCAL_RECORD_ID);
		assertNotNull(record1);
		assertEquals(record.getDuration(), record1.getDuration());
	}

	public void testDelete() throws Exception {
		Record record = instance.queryByLocalRecordId(record0.getLocalRecordId());
		assertNotNull(record);
		recordDao.deleteByKey(record.getId());
		record = instance.queryByLocalRecordId(record0.getLocalRecordId());
		assertNull(record);
	}

	public void testQuery() throws Exception {
		Record record = instance.queryByLocalRecordId(LOCAL_RECORD_ID);
		assertNotNull(record);
		assertEquals(record.getLocalRecordId(), LOCAL_RECORD_ID);
	}

	public void testQueryFail() throws Exception {
		Record record = instance.queryByLocalRecordId(LOCAL_RECORD_ID + "1");
		assertNull(record);
	}

	public void testQueryId() throws Exception {
		Record record = instance.queryById(1l);
		assertNotNull(record);
		assertEquals(record.getId(), new Long(1));
	}

	public void testCount() throws Exception {
		long l = instance.allCount();
		assertTrue(l == 1028l);
	}

	public void testDeleteById() throws Exception {
		Record record1 = instance.queryById(1027l);
		assertNotNull(record1);
		instance.deleteById(1027l);
		Record record = instance.queryById(1027l);
		assertNull(record);
	}

	public void testDeleteAll() throws Exception {
//		long l = instance.allCount();
//		assertTrue(l > 1000);
//		instance.deleteAll();
		long l1 = instance.allCount();
		assertTrue(l1 == 0);
	}
}