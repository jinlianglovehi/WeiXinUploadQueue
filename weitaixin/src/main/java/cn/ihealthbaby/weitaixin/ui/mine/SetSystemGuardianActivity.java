package cn.ihealthbaby.weitaixin.ui.mine;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;


public class SetSystemGuardianActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;

    @Bind(R.id.lvGuardian)
    ListView lvGuardian;
    @Bind(R.id.meLinearLayout)
    LinearLayout meLinearLayout;
    @Bind(R.id.slide_switch_begin)
    ImageView mSlideSwitchViewBegin;
    @Bind(R.id.slide_switch_alarm)
    ImageView mSlideSwitchViewAlarm;
    private int selectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system_guardian);
        ButterKnife.bind(this);
        title_text.setText("监护设置");
        initData();
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initListener() {
        mSlideSwitchViewBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalSetting localSetting = SPUtil.getLocalSetting(SetSystemGuardianActivity.this);
                if (!localSetting.isAutostart()) {
                    mSlideSwitchViewBegin.setImageResource(R.drawable.switch_on);
                } else {
                    mSlideSwitchViewBegin.setImageResource(R.drawable.switch_off);
                }
                localSetting.setAutostart(!localSetting.isAutostart());
                SPUtil.setLocalSetting(SetSystemGuardianActivity.this, localSetting);
            }
        });

        mSlideSwitchViewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalSetting localSetting = SPUtil.getLocalSetting(SetSystemGuardianActivity.this);
                if (!localSetting.isAlertInterval()) {
                    mSlideSwitchViewAlarm.setImageResource(R.drawable.switch_on);
                    meLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mSlideSwitchViewAlarm.setImageResource(R.drawable.switch_off);
                    meLinearLayout.setVisibility(View.GONE);
                }
                localSetting.setAlertInterval(!localSetting.isAlertInterval());
                SPUtil.setLocalSetting(SetSystemGuardianActivity.this, localSetting);
            }
        });
    }


    private void initView() {

        LocalSetting localSetting = SPUtil.getLocalSetting(SetSystemGuardianActivity.this);

        if (localSetting.isAutostart()) {
            mSlideSwitchViewBegin.setImageResource(R.drawable.switch_on);
        } else {
            mSlideSwitchViewBegin.setImageResource(R.drawable.switch_off);
        }

        if (localSetting.isAlertInterval()) {
            mSlideSwitchViewAlarm.setImageResource(R.drawable.switch_on);
            meLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mSlideSwitchViewAlarm.setImageResource(R.drawable.switch_off);
            meLinearLayout.setVisibility(View.GONE);
        }

    }

    MyTimeAdapter myTimeAdapter;

    private void initData() {
        myTimeAdapter = new MyTimeAdapter(this);
        lvGuardian.setAdapter(myTimeAdapter);
        lvGuardian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                LocalSetting localSetting = SPUtil.getLocalSetting(SetSystemGuardianActivity.this);
                localSetting.setSelectPosition(position);
                SPUtil.setLocalSetting(SetSystemGuardianActivity.this, localSetting);
                myTimeAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    public class MyTimeAdapter extends BaseAdapter {

        private String[] titleTimes = new String[]{"5秒", "10秒", "15秒", "20秒", "25秒", "30秒"};
        private LayoutInflater inflater;
        public boolean isFirst = true;

        public MyTimeAdapter(Context context) {
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


            if (selectPosition == position) {
                viewHolder.tvTime.setTextColor(getResources().getColor(R.color.green0));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvTime.setTextColor(getResources().getColor(R.color.gray9));
                viewHolder.tvState.setVisibility(View.INVISIBLE);
            }

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
