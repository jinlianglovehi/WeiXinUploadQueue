package cn.ihealthbaby.weitaixin.model;

import java.util.ArrayList;
import java.util.Date;

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
    public String testTime;

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
        for (int i=0;i<10;i++){
            LocalAdviceItem item=new LocalAdviceItem();
            item.mid=(i+111)+"";
            item.gestationalWeeks="50周+2"+i;
            item.testTime= DateTimeTool.date2StrAndTime2(new Date());
//            item.testTime= new Date().toString();
            item.testTimeLong=(i+3000)+"";
            item.status="3";
            items.add(item);
        }
        return items;
    }


}



