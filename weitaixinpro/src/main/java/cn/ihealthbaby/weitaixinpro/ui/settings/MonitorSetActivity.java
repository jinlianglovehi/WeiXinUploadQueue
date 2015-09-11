package cn.ihealthbaby.weitaixinpro.ui.settings;

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
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;


public class MonitorSetActivity extends BaseActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_set);

        ButterKnife.bind(this);

        title_text.setText("监护设置");
//      back.setVisibility(View.INVISIBLE);
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
                boolean AutoStart = (boolean) SPUtil.getData(MonitorSetActivity.this, "AutoStart", true);
                if (!AutoStart) {
                    mSlideSwitchViewBegin.setImageResource(R.drawable.switch_on);
                } else {
                    mSlideSwitchViewBegin.setImageResource(R.drawable.switch_off);
                }
                SPUtil.setData(MonitorSetActivity.this, "AutoStart", !AutoStart);
            }
        });

        mSlideSwitchViewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean PoliceSet = (boolean) SPUtil.getData(MonitorSetActivity.this, "PoliceSet", true);
                if (!PoliceSet) {
                    mSlideSwitchViewAlarm.setImageResource(R.drawable.switch_on);
                    meLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mSlideSwitchViewAlarm.setImageResource(R.drawable.switch_off);
                    meLinearLayout.setVisibility(View.GONE);
                }
                SPUtil.setData(MonitorSetActivity.this, "PoliceSet", !PoliceSet);
            }
        });
    }


    private void initView() {
//        String AutoStart = WeiTaiXinApplication.getInstance().getValue("AutoStart", "0");

        boolean PoliceSet = (boolean) SPUtil.getData(MonitorSetActivity.this, "PoliceSet", true);
        boolean AutoStart = (boolean) SPUtil.getData(MonitorSetActivity.this, "AutoStart", true);

        if (AutoStart) {
            mSlideSwitchViewBegin.setImageResource(R.drawable.switch_on);
        } else {
            mSlideSwitchViewBegin.setImageResource(R.drawable.switch_off);
        }

//        String PoliceSet = WeiTaiXinApplication.getInstance().getValue("PoliceSet", "0");
        if (PoliceSet) {
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
                myTimeAdapter.isFirst = false;
//                view.setSelected(true);
                SPUtil.setData(MonitorSetActivity.this, "select_num", position);
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

            int select_num = (int) SPUtil.getData(MonitorSetActivity.this, "select_num", position);
            if (select_num == position) {
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
