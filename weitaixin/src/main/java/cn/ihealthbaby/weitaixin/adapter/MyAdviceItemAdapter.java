package cn.ihealthbaby.weitaixin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.activity.AskDoctorActivity;
import cn.ihealthbaby.weitaixin.activity.ReplyedActivity;
import cn.ihealthbaby.weitaixin.activity.WaitReplyingActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;


public class MyAdviceItemAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<AdviceItem> datas;

    private LayoutInflater mInflater;
    private String[] strFlag=new String[]{"问医生","等待回复","已回复","需要上传"};

    public MyAdviceItemAdapter(Context context, ArrayList<AdviceItem> datas) {
        mInflater = LayoutInflater.from(context);
        this.context=context;
        setDatas(datas);
    }

    public void setDatas(ArrayList<AdviceItem> datas) {
        if (datas == null) {
            this.datas = new ArrayList<AdviceItem>();
        } else {
            this.datas.clear();
            this.datas = datas;
            mySortByTime(null);
        }
    }


    public void addDatas(ArrayList<AdviceItem> datas) {
        if (datas != null) {
            this.datas.addAll(datas);
            mySortByTime(null);
        }
    }


    public void mySortByTime(ArrayList<AdviceItem> datas){
        Comparator<AdviceItem> comparator = new Comparator<AdviceItem>(){
            public int compare(AdviceItem s1, AdviceItem s2) {
//                LogUtil.d("coWmpare s1","s1=%s",s1);
//                LogUtil.d("coWmpare s2","s2=%s",s2);
//                LogUtil.d("coWmpare cha","(s2-s1)=%s",(int)(s2.getTestTime().getTime()-s1.getTestTime().getTime()));

                return s2.getTestTime().compareTo(s1.getTestTime());
//                return (int)(s2.getTestTime().getTime()-s1.getTestTime().getTestTimeTime());
            }
        };

        Collections.sort(this.datas, comparator);
    }


    @Override
    public int getCount() {
        return this.datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_record, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//
        final AdviceItem adviceItem = this.datas.get(position);

        String dateStr=adviceItem.getGestationalWeeks();
        String[] split=dateStr.split("\\+");
        viewHolder.tvCircleTime1.setText(split[0]);
        viewHolder.tvCircleTime2.setText(split[1]);

        viewHolder.tvTestTimeLong.setText(DateTimeTool.getTime2(adviceItem.getTestTimeLong()));//
        viewHolder.tvDateTime.setText(DateTimeTool.date2StrAndTime(adviceItem.getTestTime())+"");
        //1提交但为咨询  2咨询未回复  3咨询已回复  4咨询已删除
        viewHolder.tvAdviceStatus.setText(strFlag[adviceItem.getStatus()]);
        if (adviceItem.getStatus()==1) {
            viewHolder.tvAdviceStatus.setBackgroundResource(R.drawable.recode_half_circle_un);
        }else{
            viewHolder.tvAdviceStatus.setBackgroundResource(R.drawable.recode_half_circle);
        }

        viewHolder.tvAdviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemTextView(adviceItem);
            }
        });
        return convertView;
    }

    private void setItemTextView(AdviceItem adviceItem){
        //1提交但为咨询  2咨询未回复  3咨询已回复  4咨询已删除
        int status= adviceItem.getStatus();
        if (status == 0) {
            Intent intent=new Intent(context, AskDoctorActivity.class);
            intent.putExtra("status",status);
            context.startActivity(intent);
        } else if (status == 1) {
            Intent intent=new Intent(context, WaitReplyingActivity.class);
            intent.putExtra("relatedId", adviceItem.getId());
            context.startActivity(intent);
        } else if (status == 2) {
            Intent intent=new Intent(context, ReplyedActivity.class);
            intent.putExtra("relatedId", adviceItem.getId());
            context.startActivity(intent);
        } else if (status == 3) {

        }
    }

    static class ViewHolder {
        @Bind(R.id.tvCircleTime1) TextView tvCircleTime1;
        @Bind(R.id.tvCircleTime2) TextView tvCircleTime2;
        @Bind(R.id.tvTestTimeLong) TextView tvTestTimeLong;
        @Bind(R.id.tvDateTime) TextView tvDateTime;
        @Bind(R.id.tvAdviceStatus) TextView tvAdviceStatus;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options=null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
//				.displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }


}
