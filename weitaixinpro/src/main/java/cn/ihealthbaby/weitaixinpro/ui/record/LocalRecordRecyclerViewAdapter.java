package cn.ihealthbaby.weitaixinpro.ui.record;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.ui.widget.ChooseUploadContentPopupWindow;
import cn.ihealthbaby.weitaixinpro.ui.widget.UploadedEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/9/24 13:48.
 */
public class LocalRecordRecyclerViewAdapter extends RecyclerView.Adapter<LocalRecordRecyclerViewAdapter.ViewHolder> {
	private static final int UPLOAD_ALL = 1;
	private static final int UPLOAD_DATA = 2;
	private final Activity activity;
	private final List<Record> list;
	@Bind(R.id.tv_begin)
	TextView tvBegin;
	@Bind(R.id.tv_name)
	TextView tvName;
	@Bind(R.id.tv_gestational_weeks)
	TextView tvDate;
	@Bind(R.id.tv_time)
	TextView tvTime;

	public LocalRecordRecyclerViewAdapter(Activity activity, ArrayList<Record> list) {
		this.activity = activity;
		this.list = list;
		EventBus.getDefault().register(this);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.item_record, null);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final Record record = list.get(position);
		holder.tvDate.setText(DateTimeTool.date2StrAndTime(record.getRecordStartTime()));
		holder.tvName.setText(record.getUserName());
		holder.tvDuration.setText(DateTimeTool.getTime2(record.getDuration() * 1000) + "");
		final int uploadState = record.getUploadState();
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
				//显示对话框,用户选择上传曲线还是全部上传
				ChooseUploadContentPopupWindow chooseUploadContentPopupWindow = new ChooseUploadContentPopupWindow(activity, record, position);
				chooseUploadContentPopupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public void onEventMainThread(UploadedEvent event) {
		int position = event.getPosition();
		notifyItemChanged(position);
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
