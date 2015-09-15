package cn.ihealthbaby.weitaixinpro.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.ServiceInside;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.tools.DateTimeTool;

/**
 * @author by kang on 2015/9/14.
 */
public class MonitorAdapter extends BaseAdapter {

    private List<ServiceInside> mServiceInsides;
    private Context mContext;

    public MonitorAdapter(Context context) {
        mContext = context;
        mServiceInsides = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mServiceInsides.size();
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
            convertView = View.inflate(mContext, R.layout.item_monitor, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTvDate.setText(DateTimeTool.getGestationalWeeks(mServiceInsides.get(position).getDeliveryTime()));
        holder.mTvName.setText(mServiceInsides.get(position).getName());
        holder.mTvTime.setText(DateTimeTool.date2St2(mServiceInsides.get(position).getBirthday(), "MM月dd日 hh:mm"));
        holder.mTvBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                ToastUtil.show(mContext, "开始检测:" + position);
            }
        });
        return convertView;
    }

    public void clearAddSetData(List<ServiceInside> serviceInsides) {
        mServiceInsides.clear();
        mServiceInsides.addAll(serviceInsides);
        notifyDataSetChanged();
    }

    public void clearData() {
        mServiceInsides.clear();
        notifyDataSetChanged();
    }


    public void addData(List<ServiceInside> serviceInsides) {
        mServiceInsides.addAll(serviceInsides);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.tv_begin)
        TextView mTvBegin;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_date)
        TextView mTvDate;
        @Bind(R.id.tv_time)
        TextView mTvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
