package cn.ihealthbaby.weitaixin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.Information;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.view.RoundImageView;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Information> datas;

    public MyRecyclerAdapter(Context context,ArrayList<Information> datas) {
        this.context=context;
        setDatas(datas);
    }

    public void setDatas(ArrayList<Information> datas) {
        if (datas==null) {
            this.datas=new ArrayList<Information>();
        }else{
            this.datas=datas;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(viewGroup.getContext(), R.layout.item_information, null);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int index) {
        ImageLoader.getInstance().displayImage(datas.get(index).getPicPath(), viewHolder.iv_head_icon, setDisplayImageOptions());
        viewHolder.tv_title.setText(datas.get(index).getTitle());
        viewHolder.tv_message.setText(datas.get(index).getContext());
        viewHolder.tv_create_time.setText(DateTimeTool.date2Str(datas.get(index).getCreateTime()));
        viewHolder.itemView.setTag(datas.get(index));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.iv_head_icon) RoundImageView iv_head_icon;
        @Bind(R.id.tv_title) TextView tv_title;
        @Bind(R.id.tv_message) TextView tv_message;
        @Bind(R.id.tv_create_time) TextView tv_create_time;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemViewOnClick) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(itemViewOnClick,(Information)itemViewOnClick.getTag());
            }
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Information itemData);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
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
