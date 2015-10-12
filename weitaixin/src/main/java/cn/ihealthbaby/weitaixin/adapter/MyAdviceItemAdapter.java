package cn.ihealthbaby.weitaixin.adapter;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.ui.mine.WaitReplyingActivity;
import cn.ihealthbaby.weitaixin.ui.record.AskDoctorActivity;
import cn.ihealthbaby.weitaixin.ui.record.RecordFragment;
import cn.ihealthbaby.weitaixin.ui.record.ReplyedActivity;

public class MyAdviceItemAdapter extends BaseAdapter {
	private final int requestCode = 100;
	public ArrayList<AdviceItem> datas;


	public ArrayList<Record> records = new ArrayList<Record>(); //本地记录

	public Record getOneRecord(String clientId) {
		for (int i = 0; i < records.size(); i++) {
			Record record = records.get(i);
			if (clientId.equals(record.getLocalRecordId())) {
				return record;
			}
		}
		return null;
	}

	///////
	public View selectedView;
	public View selectedViewOld;
	public TextView recordDelete;
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
	ViewHolder viewHolder = null;
	private MeMainFragmentActivity context;
	;
	private LayoutInflater mInflater;
	//    public View getAdviceStatused() {
//        return tvAdviceStatused;
//    }
	private String[] strFlag = new String[]{"问医生", "等待回复", "已回复", "咨询已删除", "需上传"};
	//    public View tvAdviceStatused;
//    public View tvAdviceStatusedOld;
	private int selectedItem;
	//////
	private TextView tvUsedCount;
	RecordBusinessDao recordBusinessDao;

	public MyAdviceItemAdapter(MeMainFragmentActivity context, ArrayList<AdviceItem> datas, TextView tvUsedCount) {
		recordBusinessDao = RecordBusinessDao.getInstance(context);
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.tvUsedCount = tvUsedCount;
		setDatas(datas);
	}

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

	public void setDatas(ArrayList<AdviceItem> datas) {
		if (datas == null) {
			this.datas = new ArrayList<AdviceItem>();
		} else {
			this.datas = datas;
			mySortByTime();
		}
	}

	public void setRecordsDatas(ArrayList<Record> records) {
		if (records == null) {
			this.records = new ArrayList<Record>();
		} else {
			this.records = records;
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
//                        tvAdviceStatused = (TextView) v.findViewById(R.id.tvAdviceStatus);
//                        tvAdviceStatused.setVisibility(View.INVISIBLE);
//                        if (tvAdviceStatusedOld!=null) {
//                            tvAdviceStatusedOld.setVisibility(View.VISIBLE);
//                        }
						break;
				}
				return false;
			}
		});
		final AdviceItem adviceItem = this.datas.get(position);



//		String dateStr =DateTimeTool.getGestationalWeeks(SPUtil.getDeliveryTime(context), adviceItem.getTestTime());
		String dateStr = adviceItem.getGestationalWeeks();

//		LogUtil.d("adapter", "dateStr: %s", dateStr);
	    String[] split = dateStr.split("\\+");
		if (split.length ==1 ) {
			viewHolder.tvCircleTime2.setText(split[0]+"");
		}
		if (split.length ==2 ) {
			viewHolder.tvCircleTime1.setText(split[0]+"");
			viewHolder.tvCircleTime2.setText(split[1]+"");
			if (split[1].length()==1) {
				viewHolder.tvCircleTime2.setText("0"+split[1]);
			}
		}

		//毫秒
		viewHolder.tvTestTimeLong.setText(DateTimeTool.getTime2(adviceItem.getTestTimeLong() * 1000));//
		viewHolder.tvDateTime.setText(DateTimeTool.date2Str(adviceItem.getTestTime(), "MM月dd日 HH:mm"));
//		viewHolder.tvDateTime.setText(DateTimeTool.date2Str(new Date(), "MM月dd日 HH:mm"));




		if (adviceItem.getStatus() < 0 || adviceItem.getStatus() > 4) {
			viewHolder.tvAdviceStatus.setText("");
		} else {
			viewHolder.tvAdviceStatus.setText(strFlag[adviceItem.getStatus()]);
		}

		if (adviceItem.getStatus() == 1) {
			viewHolder.tvAdviceStatus.setBackgroundResource(R.drawable.recode_half_circle_un);
//			viewHolder.iv_circle.setVisibility(View.GONE);
		} else {
			viewHolder.tvAdviceStatus.setBackgroundResource(R.drawable.recode_half_circle);
//			viewHolder.tvAdviceStatus.setBackgroundColor(context.getResources().getColor(R.color.green0));
//			viewHolder.iv_circle.setVisibility(View.VISIBLE);
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
				LogUtil.d("tvRecordDelete", "tvRecordDelete==> " + selectedView.getX() + " : " + (viewHolder.tvRecordDelete.getWidth() - 40));
				if (selectedView.getX() < -(viewHolder.tvRecordDelete.getWidth() - 40)) {
					deleteRecordItem(position);
				}
			}
		});
		cancel();
		if (tvUsedCount != null) {
			tvUsedCount.setText(this.datas.size() + "");
		}
		return convertView;
	}

	private void setItemTextView(final AdviceItem adviceItem, int position) {
		//1提交但为咨询  2咨询未回复  3咨询已回复  4咨询已删除
		int status = adviceItem.getStatus();
		if (status == 0) {
			LogUtil.d("AskDocgetId", "AskDocgetId = " + adviceItem.getId());
			int askMinTime=RecordFragment.askMinTime;
			if (askMinTime != -1) {
				if (adviceItem.getTestTimeLong() >= askMinTime*60*1000) {
					Intent intent = new Intent(context, AskDoctorActivity.class);
					intent.putExtra(Constants.INTENT_ID, adviceItem.getId());
					intent.putExtra(Constants.INTENT_PURPOSE, adviceItem.getAskPurpose());
					intent.putExtra(Constants.INTENT_FEELING, adviceItem.getFeeling());
					intent.putExtra(Constants.INTENT_POSITION, position);
					context.startActivityForResult(intent, requestCode);
				} else {
					ToastUtil.show(context, "不满" + askMinTime + "分钟，不能问医生");
				}
			}
		} else if (status == 1) {
			Intent intent = new Intent(context, WaitReplyingActivity.class);
			intent.putExtra(Constants.INTENT_ID, adviceItem.getId());
			context.startActivity(intent);
		} else if (status == 2) {
			Intent intent = new Intent(context, ReplyedActivity.class);
			intent.putExtra(Constants.INTENT_ID, adviceItem.getId());
			context.startActivity(intent);
		} else if (status == Record.UPLOAD_STATE_CLOUD) {
			final CustomDialog customDialog = new CustomDialog();
			Dialog dialog = customDialog.createDialog1(context, "上传中...");
			dialog.show();

			final AdviceForm adviceForm = new AdviceForm();
			adviceForm.setClientId(adviceItem.getClientId());
			adviceForm.setTestTime(adviceItem.getTestTime());
			adviceForm.setTestTimeLong(adviceItem.getTestTimeLong());
			adviceForm.setAskPurpose(adviceItem.getAskPurpose());
			adviceForm.setFeeling(adviceItem.getFeeling());
			adviceForm.setFetalTonePath(adviceItem.getFetalTonePath());

			final Record oneRecord = getOneRecord(adviceItem.getClientId());
			adviceForm.setData(oneRecord.getRecordData());
			adviceForm.setDataType(1);
			adviceForm.setDeviceType(1);
//          adviceForm.setLatitude(adviceItem.get);
//          adviceForm.setLongitude();
			ApiManager.getInstance().adviceApi.uploadData(adviceForm,
					new DefaultCallback<AdviceItem>(context, new AbstractBusiness<AdviceItem>() {
						@Override
						public void handleData(AdviceItem data) {
							//更新状态
							oneRecord.setUploadState(Record.UPLOAD_STATE_CLOUD);
							//更新云端路径
							oneRecord.setSoundUrl(data.getFetalTonePath());
							try {
								recordBusinessDao.update(oneRecord);
							} catch (Exception e) {
								e.printStackTrace();
							}
							//更新状态
							adviceItem.setStatus(0);
							//更新云端路径
							adviceItem.setFetalTonePath(data.getFetalTonePath());
							notifyDataSetChanged();
							ToastUtil.show(context, "上传成功");
							customDialog.dismiss();
						}

						@Override
						public void handleClientError(Context context, Exception e) {
							super.handleClientError(context, e);
							customDialog.dismiss();
						}

						@Override
						public void handleException(Exception e) {
							super.handleException(e);
							customDialog.dismiss();
						}

						@Override
						public void handleResult(Result<AdviceItem> result) {
							super.handleResult(result);
							customDialog.dismiss();
						}
					}), context);
		}
	}

	private void deleteRecordItem(final int position) {
		if (datas != null && datas.size() > 0) {
			final AdviceItem adviceItem = datas.get(position);
			final int status = adviceItem.getStatus();
			LogUtil.d("status","status==> "+status);
			if (status != 1 && status != 2) { //  等待回复1   已回复2   的记录不能删除
				final CustomDialog customDialog = new CustomDialog();
				Dialog dialog = customDialog.createDialog1(context, "正在删除...");
				dialog.show();
				if(status == 4) {
					try {
						recordBusinessDao.deleteByLocalRecordId(adviceItem.getClientId());
						datas.remove(position);
						notifyDataSetChanged();
						ToastUtil.show(context, "删除成功");
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtil.show(context, "删除失败");
					}
					customDialog.dismiss();
				} else {
					ApiManager.getInstance().adviceApi.delete(adviceItem.getId(),
							new DefaultCallback<Void>(context, new AbstractBusiness<Void>() {
								@Override
								public void handleData(Void data) {
									try {
	//									if (status == 4) {
	//										recordBusinessDao.deleteByLocalRecordId(adviceItem.getClientId());
	//									}
										datas.remove(position);
										notifyDataSetChanged();
										ToastUtil.show(context, "删除成功");
									} catch (Exception e) {
										e.printStackTrace();
										ToastUtil.show(context, "删除失败");
									}
									customDialog.dismiss();
								}

								@Override
								public void handleException(Exception e) {
									super.handleException(e);
									ToastUtil.show(context, "删除失败");
									customDialog.dismiss();
								}

								@Override
								public void handleClientError(Context context, Exception e) {
									super.handleClientError(context, e);
									ToastUtil.show(context, "删除失败");
									customDialog.dismiss();
								}

								@Override
								public void handleResult(Result<Void> result) {
									super.handleResult(result);
									customDialog.dismiss();
								}
							}), context);
				}
			} else {
				ToastUtil.show(context, "问医生的记录不能删除");
				cancel();
			}
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
		@Bind(R.id.iv_circle)
		ImageView iv_circle;

		public ViewHolder(View itemView) {
			ButterKnife.bind(this, itemView);
		}
	}
}
