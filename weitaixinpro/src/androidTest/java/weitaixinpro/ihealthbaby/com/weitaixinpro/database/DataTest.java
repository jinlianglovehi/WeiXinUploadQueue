package weitaixinpro.ihealthbaby.com.weitaixinpro.database;

import android.test.InstrumentationTestCase;

import java.util.UUID;

import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;

/**
 * @author by kang on 2015/9/17.
 */
public class DataTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testAddRecord() {
        try {
            for (int i = 0; i < 10; i++) {
                Record record = new Record();
                record.setDuration(Long.parseLong(i + ""));
                record.setFeeling(1);
                record.setId(System.currentTimeMillis());
                String string = UUID.randomUUID().toString();
                record.setLocalRecordId(string);
                record.setPurpose(100);
                RecordBusinessDao.getInstance(getInstrumentation().getContext()).insert(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
