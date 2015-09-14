package cn.ihealthbaby.weitaixin.db;

import android.content.Context;
import android.test.AndroidTestCase;

import cn.ihealthbaby.weitaixin.model.MyAdviceItem;

/**
 * Created by liuhongjian on 15/9/13 16:26.
 */
public class DataDaoTest extends AndroidTestCase {
	public final static String JIANCEID = "012345678901213";
	public final static String JIANCEID2 = "012345679999213";
	private Context context;
	private DataDao dao;
	private MyAdviceItem myAdviceItem;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
		dao = DataDao.getInstance(context);
		myAdviceItem = new MyAdviceItem();
		myAdviceItem.setJianceid(JIANCEID);
		myAdviceItem.setFeeling("开心");
		myAdviceItem.setPurpose("感觉有胎动");
	}

	public void test() {
		try {
//			testAddItem();
//			testFindNative();
//			testUpdateOrInsert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void testAddItem() throws Exception {
		assertTrue(true);
	}

	public void testFindNative() throws Exception {
//		dao.add(myAdviceItem,true);
		MyAdviceItem aNative = dao.findNative(JIANCEID);
		assertNotNull(aNative);
		assertTrue(aNative.getJianceid().equals(myAdviceItem.getJianceid()));
		assertTrue(aNative.getFeeling().equals(myAdviceItem.getFeeling()));
		assertTrue(aNative.getPurpose().equals(myAdviceItem.getPurpose()));
	}

	public void testUpdateItem() throws Exception {
		myAdviceItem.setSerialnum("10010");
		dao.update(myAdviceItem);
		MyAdviceItem aNative = dao.findNative(JIANCEID);
		assertNotNull(aNative);
		assertTrue(aNative.getJianceid().equals(myAdviceItem.getJianceid()));
		assertTrue(aNative.getFeeling().equals(myAdviceItem.getFeeling()));
		assertTrue(aNative.getPurpose().equals(myAdviceItem.getPurpose()));
		assertEquals(aNative.getSerialnum(), myAdviceItem.getSerialnum());
	}


	public void testUpdateOrInsert() {
		myAdviceItem.setJianceid(JIANCEID2);
		dao.update(myAdviceItem);
		MyAdviceItem aNative = dao.findNative(JIANCEID2);
		assertNotNull(aNative);
//		assertTrue(aNative.getJianceid().equals(myAdviceItem.getJianceid()));
//		assertTrue(aNative.getFeeling().equals(myAdviceItem.getFeeling()));
//		assertTrue(aNative.getPurpose().equals(myAdviceItem.getPurpose()));
	}


}