package cn.ihealthbaby.weitaixinpro;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.monitor.MonitorTabFragment;
import cn.ihealthbaby.weitaixinpro.record.RecordTabFragment;
import cn.ihealthbaby.weitaixinpro.settings.SettingsTabFragment;

public class MainActivity extends AppCompatActivity {

    @butterknife.Bind(android.R.id.tabhost)
    FragmentTabHost mTabhost;

    private static final String TAB_MONITOR_TAG = "monitor";
    private static final String TAB_RECORD_TAG = "record";
    private static final String TAB_SETTINGS_TAG = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTabhost.setup(this, getSupportFragmentManager(), R.id.fl_content);
        mTabhost.addTab(setIndicator(MainActivity.this, mTabhost.newTabSpec(TAB_MONITOR_TAG),
                R.color.white, "监测", R.drawable.tab_monitor), MonitorTabFragment.class, null);
        mTabhost.addTab(setIndicator(MainActivity.this, mTabhost.newTabSpec(TAB_RECORD_TAG),
                R.color.white, "记录", R.drawable.tab_record), RecordTabFragment.class, null);
        mTabhost.addTab(setIndicator(MainActivity.this, mTabhost.newTabSpec(TAB_SETTINGS_TAG),
                R.color.white, "设置", R.drawable.tab_profile), SettingsTabFragment.class, null);
        mTabhost.getTabWidget().setDividerDrawable(null);
    }

    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        String currentTabTag = mTabhost.getCurrentTabTag();

        if (currentTabTag.equals(TAB_MONITOR_TAG)) {
            isPopFragment = ((BaseTabFragment) getSupportFragmentManager().findFragmentByTag(TAB_MONITOR_TAG)).popFragment();
        } else if (currentTabTag.equals(TAB_RECORD_TAG)) {
            isPopFragment = ((BaseTabFragment) getSupportFragmentManager().findFragmentByTag(TAB_RECORD_TAG)).popFragment();
        } else if (currentTabTag.equals(TAB_SETTINGS_TAG)) {
            isPopFragment = ((BaseTabFragment) getSupportFragmentManager().findFragmentByTag(TAB_SETTINGS_TAG)).popFragment();
        }

        if (!isPopFragment) {
            finish();
        }
    }

    private TabHost.TabSpec setIndicator(Context context, TabHost.TabSpec spec,
                                         int resid, String string, int genresIcon) {
        View v = LayoutInflater.from(context).inflate(R.layout.main_tab_item, null);
        v.setBackgroundResource(resid);
        TextView tv = (TextView) v.findViewById(R.id.tv_tab);
        ImageView img = (ImageView) v.findViewById(R.id.iv_tab);

        tv.setText(string);
        img.setBackgroundResource(genresIcon);
        return spec.setIndicator(v);
    }

}
