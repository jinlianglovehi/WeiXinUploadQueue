package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;

/**
 * Created by liuhongjian on 15/9/24 14:23.
 */
public class MonitorActivity extends BaseActivity {
	@Bind(R.id.container)
	FrameLayout container;
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView titleText;
	@Bind(R.id.function)
	TextView function;

	@OnClick(R.id.back)
	void back() {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		ButterKnife.bind(this);
		FragmentManager fragmentManager = getSupportFragmentManager();
		MonitorFragment monitorFragment = new MonitorFragment();
		fragmentManager.beginTransaction().replace(R.id.container, monitorFragment).commit();
	}
}
