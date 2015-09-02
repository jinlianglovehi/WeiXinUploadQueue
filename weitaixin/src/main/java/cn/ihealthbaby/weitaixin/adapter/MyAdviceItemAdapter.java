package cn.ihealthbaby.weitaixin.adapter;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.ui.record.AskDoctorActivity;
import cn.ihealthbaby.weitaixin.ui.record.ReplyedActivity;
import cn.ihealthbaby.weitaixin.ui.mine.WaitReplyingActivity;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;


public class MyAdviceItemAdapter extends BaseAdapter {

    private MeMainFragmentActivity context;
    public ArrayList<AdviceItem> datas;

    private LayoutInflater mInflater;
    private String[] strFlag = new String[]{"问医生", "等待回复", "已回复", "需上传"};

    ///////
    public View selectedView;
    public View selectedViewOld;
    private int selectedItem;
    public TextView recordDelete;
    ;

    public View getSelectedView() {
        return selectedView;
    }

    public void cancel() {
        if (selectedView == null) {
            return;
        }
        ObjectAnimator.ofFloat(selectedView, "x", 0f).start();
        selectedView = null;
    }

    public void cancel(View selectedViewOld) {
        if (selectedViewOld == null) {
            return;
        }
        ObjectAnimator.ofFloat(selectedViewOld, "x", 0f).start();
        selectedViewOld = null;
    }

    //////

    public MyAdviceItemAdapter(MeMainFragmentActivity context, ArrayList<AdviceItem> datas) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        setDatas(datas);
    }

    public void setDatas(ArrayList<AdviceItem> datas) {
        if (datas == null) {
            this.datas = new ArrayList<AdviceItem>();
        } else {
            this.datas.clear();
            this.datas = datas;
            mySortByTime();
        }
    }


    public void addDatas(ArrayList<AdviceItem> datas) {
        if (datas != null) {
            this.datas.addAll(datas);
            mySortByTime();
        }
    }


    public void mySortByTime() {
        if (this.datas != null && this.datas.size() > 0)
            Collections.sort(this.datas, comparator);
    }

    public Comparator<AdviceItem> comparator = new Comparator<AdviceItem>() {
        public int compare(AdviceItem s1, AdviceItem s2) {
            Date date1 = s1.getTestTime();
            Date date2 = s2.getTestTime();
            if (date1 == null || date2 == null) {
                return -1;
            } else {
                return date2.compareTo(date1);
            }
        }
    };

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_record, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        recordDelete = viewHolder.tvRecordDelete;
        viewHolder.rlRecordItem.setOnTouchListener(new View.OnTouchListener() {
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
        final AdviceItem adviceItem = this.datas.get(position);
        String dateStr = adviceItem.getGestationalWeeks();
        String[] split = dateStr.split("\\+");
        viewHolder.tvCircleTime1.setText(split[0]);
        viewHolder.tvCircleTime2.setText(split[1]);
        if (split[1] != null && split[1].length() == 1) {
            viewHolder.tvCircleTime2.setText(0 + "" + split[1]);
        }

        viewHolder.tvTestTimeLong.setText(DateTimeTool.getTime2(adviceItem.getTestTimeLong()));//
        viewHolder.tvDateTime.setText(DateTimeTool.date2Str(adviceItem.getTestTime(), "MM月dd日 hh:mm"));
        //1提交但为咨询  2咨询未回复  3咨询已回复  4咨询已删除
        viewHolder.tvAdviceStatus.setText(strFlag[adviceItem.getStatus()]);
        if (adviceItem.getStatus() == 1) {
            viewHolder.tvAdviceStatus.setBackgroundResource(R.drawable.recode_half_circle_un);
        } else {
            viewHolder.tvAdviceStatus.setBackgroundResource(R.drawable.recode_half_circle);
        }

        viewHolder.tvAdviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemTextView(adviceItem, position);
            }
        });

        viewHolder.tvRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecordItem(position);
            }
        });
        cancel();
        return convertView;
    }

    private final int requestCode = 100;

    private void setItemTextView(AdviceItem adviceItem, int position) {
        //1提交但为咨询  2咨询未回复  3咨询已回复  4咨询已删除
        int status = adviceItem.getStatus();
        if (status == 0) {
            Intent intent = new Intent(context, AskDoctorActivity.class);
            LogUtil.d("AskDocgetId", "AskDocgetId = " + adviceItem.getId());
            intent.putExtra("adviceItemId", adviceItem.getId());
            intent.putExtra("position", position);
            context.startActivityForResult(intent, requestCode);
        } else if (status == 1) {
            Intent intent = new Intent(context, WaitReplyingActivity.class);
            intent.putExtra("relatedId", adviceItem.getId());
            context.startActivity(intent);
        } else if (status == 2) {
            Intent intent = new Intent(context, ReplyedActivity.class);
            intent.putExtra("relatedId", adviceItem.getId());
            context.startActivity(intent);
        } else if (status == 3) {

            final CustomDialog customDialog = new CustomDialog();
            Dialog dialog = customDialog.createDialog1(context, "上传中...");
            dialog.show();

            AdviceForm adviceForm = new AdviceForm();
            adviceForm.setClientId(adviceItem.getId() + "");
            adviceForm.setTestTime(adviceItem.getTestTime());
            adviceForm.setTestTimeLong(adviceItem.getTestTimeLong());
            adviceForm.setGestationalWeeks(adviceItem.getGestationalWeeks());
//            adviceForm.setData();
//            adviceForm.setAskPurpose();
//            adviceForm.setDataType();
//            adviceForm.setDeviceType();
//            adviceForm.setFeeling();
//            adviceForm.setFetalTonePath();
//            adviceForm.setLatitude();
//            adviceForm.setLongitude();

            ApiManager.getInstance().adviceApi.uploadData(adviceForm, new HttpClientAdapter.Callback<Long>() {
                @Override
                public void call(Result<Long> t) {
                    if (t.isSuccess()) {
                        Long data = t.getData();
                        ToastUtil.show(context, "上传成功");
                    } else {
                        ToastUtil.show(context, t.getMsgMap() + "");
                    }
                    customDialog.dismiss();
                }
            }, context);
        }
    }

    private void deleteRecordItem(final int position) {
        if (datas.size() > 0) {
            AdviceItem adviceItem = datas.get(position);
            if (adviceItem.getStatus() != 3) {
                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(context, "正在删除...");
                dialog.show();
                ApiManager.getInstance().adviceApi.delete(adviceItem.getId(), new HttpClientAdapter.Callback<Void>() {
                    @Override
                    public void call(Result<Void> t) {
                        if (t.isSuccess()) {
                            ToastUtil.show(context, "删除成功");
                            datas.remove(position);
                            notifyDataSetChanged();
                        } else {
                            ToastUtil.show(context, t.getMsgMap() + "");
                        }
                        customDialog.dismiss();
                    }
                }, context);
            } else {
                ToastUtil.show(context, "请先上传，才能删除~~~");
                cancel();
            }
        }
    }

    static class ViewHolder {
        @Bind(R.id.tvCircleTime1)
        TextView tvCircleTime1;
        @Bind(R.id.tvCircleTime2)
        TextView tvCircleTime2;
        @Bind(R.id.tvTestTimeLong)
        TextView tvTestTimeLong;
        @Bind(R.id.tvDateTime)
        TextView tvDateTime;
        @Bind(R.id.tvAdviceStatus)
        TextView tvAdviceStatus;
        @Bind(R.id.tvRecordDelete)
        TextView tvRecordDelete;
        @Bind(R.id.rlRecordItem)
        RelativeLayout rlRecordItem;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
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
