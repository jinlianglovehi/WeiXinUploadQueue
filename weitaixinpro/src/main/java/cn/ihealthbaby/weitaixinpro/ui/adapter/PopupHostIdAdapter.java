package cn.ihealthbaby.weitaixinpro.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.R;

/**
 * @author by kang on 2015/9/10.
 */
public class PopupHostIdAdapter extends BaseAdapter {

    String hostIds[];
    Context mContext;

    public PopupHostIdAdapter(String[] hostIds, Context context) {
        this.hostIds = hostIds;
        mContext = context;
    }

    @Override
    public int getCount() {
        return hostIds.length;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_host_id, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTvHostId.setText(hostIds[position]);
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.tv_host_id)
        TextView mTvHostId;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
