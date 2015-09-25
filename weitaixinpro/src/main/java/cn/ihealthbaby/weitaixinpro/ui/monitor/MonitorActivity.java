package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;

/**
 * Created by liuhongjian on 15/9/24 14:23.
 */
public class MonitorActivity extends BaseActivity {
	@Bind(R.id.container)
	FrameLayout container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		ButterKnife.bind(this);
		FragmentManager fragmentManager = getSupportFragmentManager();
		MonitorFragment monitorFragment = new MonitorFragment();
//		Bundle user = getIntent().getBundleExtra(Constants.BUNDLE_USER);
//
//		monitorFragment.setArguments(user);
		fragmentManager.beginTransaction().replace(R.id.container, monitorFragment).commit();
	}
}
