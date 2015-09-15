package cn.ihealthbaby.weitaixinpro.ui.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.model.HAdviceForm;

/**
 * @author by kang on 2015/9/12.
 */
public class RecordAdapter extends BaseAdapter {

    private Context mContext;
    public List<HAdviceForm> mMyAdviceItems;
    public View selectedView;
    public View selectedViewOld;
    public TextView recordDelete;

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

    public RecordAdapter(Context context) {
        mContext = context;
        mMyAdviceItems = new ArrayList<>();
    }

    public void addData(List<HAdviceForm> myAdviceItems) {
        mMyAdviceItems.addAll(myAdviceItems);
        notifyDataSetChanged();
    }

    public void clearData() {
        mMyAdviceItems.clear();
        notifyDataSetChanged();
    }

    public void clearAndAddData(List<HAdviceForm> myAdviceItems) {
        mMyAdviceItems.clear();
        mMyAdviceItems.addAll(myAdviceItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMyAdviceItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_record, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mRlItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectedView = v;
                        break;
                }
                return false;
            }
        });

        recordDelete = holder.mTvDeleteItem;
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_delete_item)
        TextView mTvDeleteItem;
        @Bind(R.id.tvCircleTime1)
        TextView mTvCircleTime1;
        @Bind(R.id.tvCircleTime2)
        TextView mTvCircleTime2;
        @Bind(R.id.ll_Layout)
        LinearLayout mLlLayout;
        @Bind(R.id.ivClock)
        ImageView mIvClock;
        @Bind(R.id.tvTestTimeLong)
        TextView mTvTestTimeLong;
        @Bind(R.id.tvDateTime)
        TextView mTvDateTime;
        @Bind(R.id.iv_circle)
        ImageView mIvCircle;
        @Bind(R.id.tvAdviceStatus)
        TextView mTvAdviceStatus;
        @Bind(R.id.rl_item)
        RelativeLayout mRlItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
