package cn.ihealthbaby.weitaixin.adapter;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.Information;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.RelativeDateFormat;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.mine.WoMessageActivity;
import cn.ihealthbaby.weitaixin.ui.widget.RoundImageView;


public class MyRefreshAdapter extends BaseAdapter {

    private WoMessageActivity context;
    private ArrayList<Information> datas;

    private LayoutInflater mInflater;

    public View selectedView;
    public View selectedViewOld;
    public TextView recordDelete;
    private int selectedItem;


    public View getSelectedView() {
        return selectedView;
    }

    public void cancel() {
//        if (tvAdviceStatused != null) {
//            tvAdviceStatused.setVisibility(View.VISIBLE);
//        }
        if (selectedView == null) {
            return;
        }
        ObjectAnimator.ofFloat(selectedView, "x", 0f).start();
        selectedView = null;
//        tvAdviceStatused=null;
    }

    public void cancel(View selectedViewOld) {
        if (selectedViewOld == null) {
            return;
        }
        ObjectAnimator.ofFloat(selectedViewOld, "x", 0f).start();
        selectedViewOld = null;
    }



    public MyRefreshAdapter(WoMessageActivity context, ArrayList<Information> datas) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        setDatas(datas);
    }

    public void setDatas(ArrayList<Information> datas) {
        if (datas == null) {
            this.datas = new ArrayList<Information>();
        } else {
            this.datas = datas;
        }
    }

    public void addDatas(ArrayList<Information> datas) {
        if (this.datas != null) {
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

    ViewHolder viewHolder = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_information, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        recordDelete = viewHolder.tvRecordDelete;
        viewHolder.rlMessageItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectedView = v;
                        selectedItem = position;
                        break;
                }
                return false;
            }
        });


        Information data = datas.get(position);
        ImageLoader.getInstance().displayImage(data.getPicPath(), viewHolder.mIvHeadIcon, setDisplayImageOptions());
        viewHolder.mTvTitle.setText(data.getTitle());
        viewHolder.mTvMessage.setText(data.getContext());
        viewHolder.tv_create_time.setText(RelativeDateFormat.format(data.getCreateTime()));
        if (data.getReadNums() == 0) {
            viewHolder.tv_notification.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_notification.setVisibility(View.GONE);
        }


        viewHolder.tvRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedView.getX() < -(viewHolder.tvRecordDelete.getWidth() - 40)) {
                    deleteRecordItem(position);
                }
            }
        });

        cancel();


        return convertView;
    }


    private void deleteRecordItem(final int position) {
        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog1 = customDialog.createDialog1(context, "正在删除...");
        dialog1.show();
        Information information = this.datas.get(position);
        long inforId = information.getId();
        LogUtil.d("inforId","inforId==> "+inforId);
        ApiManager.getInstance().informationApi.deleteInformation(inforId,
                new DefaultCallback<Void>(context, new AbstractBusiness<Void>() {
                    @Override
                    public void handleData(Void data) {
                        ToastUtil.show(context,"删除成功");
                        datas.remove(position);
                        notifyDataSetChanged();
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        ToastUtil.show(context, "删除失败");
                        cancel();
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        ToastUtil.show(context, "删除失败");
                        cancel();
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleResult(Result<Void> result) {
                        super.handleResult(result);
                        customDialog.dismiss();
                    }
                }),context);
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
//                .displayer(new SimpleBitmapDisplayer())
                .displayer(new RoundedBitmapDisplayer(80))
                .build();
        return options;
    }


    static class ViewHolder {
        @Bind(R.id.iv_head_icon)
        ImageView mIvHeadIcon;
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_message)
        TextView mTvMessage;
        @Bind(R.id.tv_create_time)
        TextView tv_create_time;
        @Bind(R.id.tv_notification)
        TextView tv_notification;
        @Bind(R.id.tvRecordDelete)
        TextView tvRecordDelete;
        @Bind(R.id.rlMessageItem)
        RelativeLayout rlMessageItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
