package cn.ihealthbaby.weitaixin.db;

import android.content.Context;
import android.test.AndroidTestCase;

import cn.ihealthbaby.weitaixin.model.MyAdviceItem;

/**
 * Created by liuhongjian on 15/9/13 16:26.
 */
public class DataDaoTest extends AndroidTestCase {
	public final static String JIANCEID = "1234567890 - dfghjkl";
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
			testAddItem();
			testFindNative();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void testAddItem() throws Exception {

		assertTrue(true);
	}

	public void testFindNative() throws Exception {
		dao.addItem(myAdviceItem, true);
		MyAdviceItem aNative = dao.findNative(JIANCEID);
		assertNotNull(aNative);
		assertTrue(aNative.getJianceid().equals(myAdviceItem.getJianceid()));
		assertTrue(aNative.getFeeling().equals(myAdviceItem.getFeeling()));
		assertTrue(aNative.getPurpose().equals(myAdviceItem.getPurpose()));
	}

	public void testUpdateItem() throws Exception {

	}
}