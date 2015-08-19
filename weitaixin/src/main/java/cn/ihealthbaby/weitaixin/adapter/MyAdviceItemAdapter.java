package cn.ihealthbaby.weitaixin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.client.model.Information;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.view.RoundImageView;


public class MyAdviceItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AdviceItem> datas;

    private LayoutInflater mInflater;
    private String[] strFlag=new String[]{"问医生","等待回复","已回复","需要上传"};


    public MyAdviceItemAdapter(Context context, ArrayList<AdviceItem> datas) {
        mInflater = LayoutInflater.from(context);
        this.context=context;
        setDatas(datas);
    }

    public void setDatas(ArrayList<AdviceItem> datas) {
        if (datas==null) {
            this.datas=new ArrayList<AdviceItem>();
        }else{
            this.datas=datas;
        }
    }

    public void addDatas(ArrayList<AdviceItem> datas) {
        if (this.datas!=null) {
            this.datas.addAll(datas);
        }
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
        AdviceItem adviceItem = datas.get(position);

        String dateStr=adviceItem.getGestationalWeeks();
        String[] split=dateStr.split("\\+");
        viewHolder.tvCircleTime1.setText(split[0]);
        viewHolder.tvCircleTime2.setText(split[1]);

        viewHolder.tvTestTimeLong.setText(DateTimeTool.getTime2(adviceItem.getTestTimeLong()));//
        viewHolder.tvDateTime.setText(DateTimeTool.date2StrAndTime(adviceItem.getTestTime())+"");
        //1 提交但为咨询 2咨询未回复 3 咨询已回复 4 咨询已删除
        viewHolder.tvAdviceStatus.setText(strFlag[adviceItem.getAdviceStatus()]);

        return convertView;
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
