package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;

/**
 * Created by liuhongjian on 15/9/24 14:23.
 */
public class MonitorActivity extends BaseActivity {
	public MonitorFragment monitorFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_monitor);
		FragmentManager fragmentManager = getSupportFragmentManager();
		monitorFragment = MonitorFragment.getInstance();
		final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.container, monitorFragment).commit();
	}

	@Override
	public void onBackPressed() {
		monitorFragment.back();
	}
}
