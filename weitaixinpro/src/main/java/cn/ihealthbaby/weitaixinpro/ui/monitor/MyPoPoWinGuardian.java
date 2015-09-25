package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.AskPurposeType;
import cn.ihealthbaby.client.model.FeelingType;
import cn.ihealthbaby.weitaixinpro.R;

public class MyPoPoWinGuardian extends PopupWindow {
	public ListView lvGuardianPurpose;
	protected View mContentView;
	protected Activity context;
	private int guardianPurposeIndexPosition = 0;
	private int guardianMoodIndexPosition = 0;

	public MyPoPoWinGuardian(Activity context, View contentView, int width, int height, boolean focusable) {
		super(contentView, width, height, focusable);
		this.mContentView = contentView;
		this.context = context;
	}

	public MyPoPoWinGuardian(Activity context) {
		this.context = context;
		showView();
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		this.setAnimationStyle(R.style.anim_popowin_dir);
		setTouchable(true);
		setOutsideTouchable(true);
		setFocusable(true);
		setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					MyPoPoWinGuardian.this.dismiss();
					return true;
				}
				return false;
			}
		});
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = MyPoPoWinGuardian.this.context.getWindow().getAttributes();
				lp.alpha = 1.0f;
				MyPoPoWinGuardian.this.context.getWindow().setAttributes(lp);
			}
		});
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.alpha = .3f;
		context.getWindow().setAttributes(lp);
	}

	public void disMiss() {
		WindowManager.LayoutParams lp = MyPoPoWinGuardian.this.context.getWindow().getAttributes();
		lp.alpha = 1.0f;
		MyPoPoWinGuardian.this.context.getWindow().setAttributes(lp);
		dismiss();
	}

	public void showView() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View ppView = inflater.inflate(R.layout.popowindow_guardian, null);
		this.setContentView(ppView);
		//
		lvGuardianPurpose = (ListView) ppView.findViewById(R.id.lvGuardianPurpose);
	}

	public void showAtLocation(View parent) {
		showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	public int getGuardianMoodIndexPosition() {
		return guardianMoodIndexPosition;
	}

	public void setGuardianMoodIndexPosition(int guardianMoodIndexPosition) {
		this.guardianMoodIndexPosition = guardianMoodIndexPosition;
	}

	public int getGuardianPurposeIndexPosition() {
		return guardianPurposeIndexPosition;
	}

	public void setGuardianPurposeIndexPosition(int guardianPurposeIndexPosition) {
		this.guardianPurposeIndexPosition = guardianPurposeIndexPosition;
	}

	public void initPurposetData(List<AskPurposeType> askPurposetypes, int guardianPurposeIndexPosition) {
		this.guardianPurposeIndexPosition = guardianPurposeIndexPosition;
		final MyGuardianPurposeAdapter myGuardianPurposeAdapter = new MyGuardianPurposeAdapter(context);
		myGuardianPurposeAdapter.askPurposetypes = askPurposetypes;
		lvGuardianPurpose.setAdapter(myGuardianPurposeAdapter);
		lvGuardianPurpose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                myGuardianPurposeAdapter.isFirst = false;
				MyPoPoWinGuardian.this.guardianPurposeIndexPosition = position;
				myGuardianPurposeAdapter.notifyDataSetChanged();
				disMiss();
			}
		});
	}
//    private int indexPosition = 0;
//
//    public int getIndexPosition() {
//        return indexPosition;
//    }
//
//    public void setIndexPosition(int indexPosition) {
//        this.indexPosition = indexPosition;
//    }

	public void initFeelingTypeData(List<FeelingType> feelingTypes, int guardianMoodIndexPosition) {
		this.guardianMoodIndexPosition = guardianMoodIndexPosition;
		final MyGuardianFeelingTypeAdapter myGuardianFeelingTypeAdapter = new MyGuardianFeelingTypeAdapter(context);
		myGuardianFeelingTypeAdapter.feelingTypes = feelingTypes;
		lvGuardianPurpose.setAdapter(myGuardianFeelingTypeAdapter);
		lvGuardianPurpose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                myGuardianFeelingTypeAdapter.isFirst = false;
				MyPoPoWinGuardian.this.guardianMoodIndexPosition = position;
				myGuardianFeelingTypeAdapter.notifyDataSetChanged();
				disMiss();
			}
		});
	}

	public class MyGuardianPurposeAdapter extends BaseAdapter {
		public List<AskPurposeType> askPurposetypes = new ArrayList<AskPurposeType>();
		private LayoutInflater inflater;
//        public boolean isFirst = true;

		public MyGuardianPurposeAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return askPurposetypes.size();
		}

		@Override
		public Object getItem(int position) {
			return askPurposetypes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_popo_win, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tvTime.setText(askPurposetypes.get(position).getValue() + "");
			if (MyPoPoWinGuardian.this.guardianPurposeIndexPosition == position) {
				viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.green0));
				viewHolder.tvState.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.gray9));
				viewHolder.tvState.setVisibility(View.INVISIBLE);
			}
//            if (isFirst && position == 0) {
//                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.green0));
//                viewHolder.tvState.setVisibility(View.VISIBLE);
//            }
			return convertView;
		}

		class ViewHolder {
			@Bind(R.id.tvTime)
			TextView tvTime;
			@Bind(R.id.tvState)
			ImageView tvState;

			public ViewHolder(View convertView) {
				ButterKnife.bind(this, convertView);
			}
		}
	}

	public class MyGuardianFeelingTypeAdapter extends BaseAdapter {
		public List<FeelingType> feelingTypes = new ArrayList<FeelingType>();
		private LayoutInflater inflater;
//        public boolean isFirst = true;

		public MyGuardianFeelingTypeAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return feelingTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return feelingTypes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_popo_win, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tvTime.setText(feelingTypes.get(position).getValue() + "");
			if (MyPoPoWinGuardian.this.guardianMoodIndexPosition == position) {//convertView.isSelected()
				viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.green0));
				viewHolder.tvState.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.gray9));
				viewHolder.tvState.setVisibility(View.INVISIBLE);
			}
//            if (isFirst && position == 0) {
//                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.green0));
//                viewHolder.tvState.setVisibility(View.VISIBLE);
//            }
			return convertView;
		}

		class ViewHolder {
			@Bind(R.id.tvTime)
			TextView tvTime;
			@Bind(R.id.tvState)
			ImageView tvState;

			public ViewHolder(View convertView) {
				ButterKnife.bind(this, convertView);
			}
		}
	}
}



