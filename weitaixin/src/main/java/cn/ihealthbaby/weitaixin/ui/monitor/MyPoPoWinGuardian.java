package cn.ihealthbaby.weitaixin.ui.monitor;

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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixin.R;

public class MyPoPoWinGuardian extends PopupWindow {

    protected View mContentView;
    protected Activity context;
    public ListView lvGuardianPurpose;
    public MyGuardianPurposeAdapter myGuardianPurposeAdapter;

    public MyPoPoWinGuardian(Activity context, View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        this.mContentView = contentView;
        this.context=context;
    }


    public MyPoPoWinGuardian(Activity context) {

        this.context=context;
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


    public void showView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ppView = inflater.inflate(R.layout.popowindow_guardian, null);
        this.setContentView(ppView);
        //
        lvGuardianPurpose = (ListView) ppView.findViewById(R.id.lvGuardianPurpose);
        initData();
    }

    public void showAtLocation(View parent){
        showAtLocation(parent, Gravity.CENTER, 0, 0);
    }



    private void initData() {
        myGuardianPurposeAdapter = new MyGuardianPurposeAdapter(context);
        lvGuardianPurpose.setAdapter(myGuardianPurposeAdapter);
        lvGuardianPurpose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myGuardianPurposeAdapter.isFirst = false;
                view.setSelected(true);
                myGuardianPurposeAdapter.notifyDataSetChanged();
            }
        });
    }


    public class MyGuardianPurposeAdapter extends BaseAdapter {

        private String[] titleTimes = new String[]{"5秒", "10秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "15秒", "20秒", "25秒", "30秒"};
        private LayoutInflater inflater;
        public boolean isFirst = true;

        public MyGuardianPurposeAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return titleTimes.length;
        }

        @Override
        public Object getItem(int position) {
            return titleTimes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_set_system_guardian, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvTime.setText(titleTimes[position]);
            if (convertView.isSelected()) {
                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.green0));
                viewHolder.tvState.setTextColor(context.getResources().getColor(R.color.green0));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.gray9));
                viewHolder.tvState.setTextColor(context.getResources().getColor(R.color.gray9));
                viewHolder.tvState.setVisibility(View.INVISIBLE);
            }
            if (isFirst && position == 0) {
                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.green0));
                viewHolder.tvState.setTextColor(context.getResources().getColor(R.color.green0));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvTime)
            TextView tvTime;
            @Bind(R.id.tvState)
            TextView tvState;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }



}



