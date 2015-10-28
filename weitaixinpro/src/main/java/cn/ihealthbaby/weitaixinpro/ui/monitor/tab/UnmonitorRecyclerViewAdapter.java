package cn.ihealthbaby.weitaixinpro.ui.monitor.tab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.ServiceInside;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.ui.monitor.MonitorActivity;

/**
 * Created by liuhongjian on 15/9/24 13:48.
 */
public class UnmonitorRecyclerViewAdapter extends RecyclerView.Adapter<UnmonitorRecyclerViewAdapter.ViewHolder> {
	private final Activity activity;
	private final List<ServiceInside> list;
	@Bind(R.id.tv_begin)
	TextView tvBegin;
	@Bind(R.id.tv_name)
	TextView tvName;
	@Bind(R.id.tv_gestational_weeks)
	TextView tvDate;
	@Bind(R.id.tv_time)
	TextView tvTime;

	public UnmonitorRecyclerViewAdapter(Activity activity, List<ServiceInside> list) {
		this.activity = activity;
		this.list = list;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.item_monitor, null);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		final ServiceInside serviceInside = list.get(position);
		holder.tvBegin.setOnClickListener(new View.OnClickListener() {
			public long lastTime;

			@Override
			public void onClick(View v) {
				final long currentTimeMillis = System.currentTimeMillis();
				if (currentTimeMillis - lastTime > 1000) {
					//开始监测
					ApiManager.getInstance().hClientAccountApi.beginServicesinside(serviceInside.getId(), new DefaultCallback<Integer>(activity, new AbstractBusiness<Integer>() {
						@Override
						public void handleData(Integer data) {
							ToastUtil.show(activity, "开始监测");
							Intent intent = new Intent(activity, MonitorActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							Bundle user = new Bundle();
							user.putLong(Constants.INTENT_USER_ID, serviceInside.getUserId());
							user.putString(Constants.INTENT_USER_NAME, serviceInside.getName());
							user.putLong(Constants.INTENT_DELIVERY_TIME, serviceInside.getDeliveryTime().getTime());
							user.putLong(Constants.INTENT_SERVICE_ID, serviceInside.getId());
							intent.putExtra(Constants.BUNDLE_USER, user);
							activity.startActivity(intent);
						}
					}), this);
					lastTime = currentTimeMillis;
				}
			}
		});
		holder.tvBegin.setText("开始\n监测");
		holder.tvGestationalWeeks.setText(serviceInside.getGestationalWeeks() + "天");
		holder.tvName.setText(serviceInside.getName());
		holder.tvTime.setText(DateTimeTool.date2StrAndTime(serviceInside.getCreatetime()));
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public TextView tvBegin;
		public TextView tvName;
		public TextView tvTime;
		public TextView tvGestationalWeeks;

		public ViewHolder(View itemView) {
			super(itemView);
			tvBegin = (TextView) itemView.findViewById(R.id.tv_begin);
			tvName = (TextView) itemView.findViewById(R.id.tv_name);
			tvTime = (TextView) itemView.findViewById(R.id.tv_time);
			tvGestationalWeeks = (TextView) itemView.findViewById(R.id.tv_gestational_weeks);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					tvBegin.performClick();
				}
			});
		}
	}
}
