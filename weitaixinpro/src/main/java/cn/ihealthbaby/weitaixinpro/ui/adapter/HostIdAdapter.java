package cn.ihealthbaby.weitaixinpro.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.FetalHeart;
import cn.ihealthbaby.weitaixinpro.R;

/**
 * @author by kang on 2015/9/10.
 */
public class HostIdAdapter extends BaseAdapter {
	public static final int SELECT_NONE = -1;
	private Context context;
	private List<FetalHeart> list;
	private int selection = SELECT_NONE;

	public HostIdAdapter(Context context, List<FetalHeart> list) {
		this.context = context;
		this.list = list;
	}

	public void setList(List<FetalHeart> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_host_id, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FetalHeart fetalHeart = list.get(position);
		holder.mTvHostId.setText(fetalHeart.getSerialnum());
		holder.mTvHostDepartment.setText(fetalHeart.getDepartmentName());
		holder.mTvHostName.setText(fetalHeart.getHospitalName());
		final long indexNumber = fetalHeart.getIndexNumber();
		holder.mTvIndexNumber.setText("院内编号:" + String.format("%04d", indexNumber));
		if (selection == position) {
			holder.mIvSelect.setVisibility(View.VISIBLE);
		} else {
			holder.mIvSelect.setVisibility(View.GONE);
		}
		return convertView;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
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
