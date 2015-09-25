package cn.ihealthbaby.weitaixinpro.ui.record;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixinpro.R;

/**
 * Created by liuhongjian on 15/9/24 13:48.
 */
public class LocalRecordRecyclerViewAdapter extends RecyclerView.Adapter<LocalRecordRecyclerViewAdapter.ViewHolder> {
	private final Context context;
	private final List<Record> list;
	@Bind(R.id.tv_begin)
	TextView tvBegin;
	@Bind(R.id.tv_name)
	TextView tvName;
	@Bind(R.id.tv_gestational_weeks)
	TextView tvDate;
	@Bind(R.id.tv_time)
	TextView tvTime;

	public LocalRecordRecyclerViewAdapter(Context context, ArrayList<Record> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.item_record, null);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Record record = list.get(position);
		holder.tvDate.setText(DateTimeTool.date2StrAndTime(record.getRecordStartTime()));
		holder.tvName.setText(record.getUserName());
		holder.tvDuration.setText(DateTimeTool.getTime2(record.getDuration()) + "");
		int uploadState = record.getUploadState();
		switch (uploadState) {
			case Record.UPLOAD_STATE_LOCAL:
			case Record.UPLOAD_STATE_UPLOADING:
				holder.tvUploadStatus.setText("需上传");
				break;
			case Record.UPLOAD_STATE_CLOUD:
				holder.tvUploadStatus.setText("已上传");
				break;
			default:
				break;
		}
		holder.tvUploadStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//开始监测
//				ApiManager.getInstance().hClientAccountApi.beginServicesinside(record.getId(), new DefaultCallback<Integer>(context, new AbstractBusiness<Integer>() {
//					@Override
//					public void handleData(Integer data) {
//						ToastUtil.show(context, "开始监测");
//						Intent intent = new Intent(context, MonitorActivity.class);
//						context.startActivity(intent);
//					}
//				}), this);
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public TextView tvUploadStatus;
		public TextView tvName;
		public TextView tvDuration;
		public TextView tvDate;

		public ViewHolder(View itemView) {
			super(itemView);
			tvUploadStatus = (TextView) itemView.findViewById(R.id.tv_upload_status);
			tvName = (TextView) itemView.findViewById(R.id.tv_name);
			tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
			tvDate = (TextView) itemView.findViewById(R.id.tv_date);
		}
	}
}
