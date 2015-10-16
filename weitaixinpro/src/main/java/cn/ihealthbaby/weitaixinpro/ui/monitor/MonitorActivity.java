package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;

/**
 * Created by liuhongjian on 15/9/24 14:23.
 */
public class MonitorActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		FragmentManager fragmentManager = getSupportFragmentManager();
		MonitorFragment monitorFragment = new MonitorFragment();
		fragmentManager.beginTransaction().replace(R.id.container, monitorFragment).commit();
	}
}
