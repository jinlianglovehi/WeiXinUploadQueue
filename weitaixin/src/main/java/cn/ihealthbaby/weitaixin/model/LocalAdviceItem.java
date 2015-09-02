package cn.ihealthbaby.weitaixin.model;

import java.util.ArrayList;
import java.util.Date;

import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.db.DataDBHelper;
import cn.ihealthbaby.weitaixin.db.DataDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;

public class LocalAdviceItem {

    /**
     * 检测信息的id
     */
    public String mid;

    /**
     * 孕周
     */
    public String gestationalWeeks;

    /**
     * 检测时间
     */
    public Date testTime;

    /**
     * 检测时长
     */
    public String testTimeLong;

    /**
     * 咨询的状态 1 提交但为咨询 2咨询未回复 3 咨询已回复 4 咨询已删除
     */
    public String status;


    public ArrayList<LocalAdviceItem> getDataLocal() {
        ArrayList<LocalAdviceItem> items=new ArrayList<LocalAdviceItem>();
        for (int i=5;0<i;i--){
            LocalAdviceItem item=new LocalAdviceItem();
            item.mid=(i+144)+"";
            item.gestationalWeeks="50周+2"+i;
//            item.testTime= DateTimeTool.date2StrAndTime2(new Date());
            item.testTime= new Date();
            item.testTimeLong=(i+3000)+"";
            item.status="3";
            items.add(item);
        }
        sortAdviceItem(items);
        return items;
    }



    private void sortAdviceItem(ArrayList<LocalAdviceItem> localAdviceItems ) {
        ArrayList<AdviceItem> nativeItem = new ArrayList<AdviceItem>();
        for (LocalAdviceItem item : localAdviceItems) {
            AdviceItem advice = new AdviceItem();
            advice.setId(Integer.parseInt(item.mid));
            advice.setGestationalWeeks(item.gestationalWeeks);
            advice.setTestTime(item.testTime);
//            advice.setTestTime(DateTimeTool.str2Date(item.testTime));
            advice.setTestTimeLong(Integer.parseInt(item.testTimeLong));
            advice.setStatus(Integer.parseInt(item.status));
            nativeItem.add(advice);
        }
        //本地保存数据库
        DataDao dataDao = new DataDao(WeiTaiXinApplication.getInstance());
        dataDao.add(DataDBHelper.tableNativeName, nativeItem);
    }

}



