package cn.ihealthbaby.weitaixinpro.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.FetalHeart;
import cn.ihealthbaby.weitaixinpro.R;

/**
 * @author by kang on 2015/9/10.
 */
public class PopupHostIdAdapter extends BaseAdapter {

    List<FetalHeart> mFetalHeartApiList;
    Context mContext;
    int selectPositiion;

    public PopupHostIdAdapter(Context context) {
        this.mFetalHeartApiList = new ArrayList<FetalHeart>();
        mContext = context;
    }

    public void addData(List<FetalHeart> fetalHeartApiList) {
        if (mFetalHeartApiList == null) {
            mFetalHeartApiList = new ArrayList<FetalHeart>();
        }
        mFetalHeartApiList.clear();
        mFetalHeartApiList.addAll(fetalHeartApiList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFetalHeartApiList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_host_id, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTvHostId.setText(mFetalHeartApiList.get(position).getSerialnum());
        holder.mTvHostDepartment.setText(mFetalHeartApiList.get(position).getDepartmentName());
        holder.mTvHostName.setText(mFetalHeartApiList.get(position).getHospitalName());
        holder.mTvIndexNumber.setText(mFetalHeartApiList.get(position).getIndexNumber() + "");
        holder.mIvSelect.setVisibility(View.INVISIBLE);
        if (selectPositiion == position) {
            holder.mIvSelect.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public void showSelect(int position) {
        selectPositiion = position;
        notifyDataSetChanged();
    }


    static class ViewHolder {
        @Bind(R.id.tv_host_id)
        TextView mTvHostId;
        @Bind(R.id.tv_host_name)
        TextView mTvHostName;
        @Bind(R.id.tv_host_department)
        TextView mTvHostDepartment;
        @Bind(R.id.tv_index_number)
        TextView mTvIndexNumber;
        @Bind(R.id.iv_select)
        ImageView mIvSelect;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
