package cn.ihealthbaby.weitaixinpro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.ui.monitor.tab.MonitorTabFragment;
import cn.ihealthbaby.weitaixinpro.ui.record.RecordFragment;
import cn.ihealthbaby.weitaixinpro.ui.settings.SettingsFragment;

/**
 * Created by Think on 2015/8/13.
 */
public class MainActivity extends BaseActivity {
	public Fragment oldFragment;
	@Bind(R.id.iv_tab_02)
	ImageView iv_tab_02;
	@Bind(R.id.iv_tab_03)
	ImageView iv_tab_03;
	@Bind(R.id.iv_tab_04)
	ImageView iv_tab_04;
	@Bind(R.id.container)
	FrameLayout container;
	@Bind(R.id.ll_tab_monitor)
	LinearLayout llTabMonitor;
	@Bind(R.id.ll_tab_record)
	LinearLayout llTabRecord;
	@Bind(R.id.ll_tab_profile)
	LinearLayout mLlTabProfile;
	private MonitorTabFragment monitorTabFragment;
	private RecordFragment recordFragment;
	private SettingsFragment mSettingsFragment;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "MainActivity onCreate");
		setContentView(R.layout.activity_me_main_fragment);
		ButterKnife.bind(this);
		fragmentManager = getSupportFragmentManager();
		llTabMonitor.performClick();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setContentView(R.layout.activity_me_main_fragment);
		ButterKnife.bind(this);
		fragmentManager = getSupportFragmentManager();
		llTabMonitor.performClick();
	}

	@OnClick(R.id.ll_tab_monitor)
	public void iv_tab_02() {
		showTab(iv_tab_02);
		if (monitorTabFragment == null) {
			monitorTabFragment = MonitorTabFragment.getInstance();
		}
		showFragment(R.id.container, monitorTabFragment);
	}

	@OnClick(R.id.ll_tab_record)
	public void iv_tab_03() {
		showTab(iv_tab_03);
		if (recordFragment == null) {
			recordFragment = new RecordFragment();
		}
		showFragment(R.id.container, recordFragment);
	}

	@OnClick(R.id.ll_tab_profile)
	public void iv_tab_04() {
		showTab(iv_tab_04);
		if (mSettingsFragment == null) {
			mSettingsFragment = SettingsFragment.getInstance();
		}
		showFragment(R.id.container, mSettingsFragment);
	}

	public void showTab(ImageView imageView) {
		iv_tab_02.setSelected(false);
		iv_tab_03.setSelected(false);
		iv_tab_04.setSelected(false);
		imageView.setSelected(true);
	}

	private void showFragment(int container, Fragment fragment/*, int animIn, int animOut*/) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		show(container, fragmentTransaction, fragment);
		fragmentTransaction.commit();
	}

	private void show(int container, FragmentTransaction fragmentTransaction, Fragment fragment) {
		if (fragment == null) {
			return;
		}
		if (!fragment.isAdded()) {
			if (oldFragment != null) {
				fragmentTransaction.hide(oldFragment);
			}
			fragmentTransaction.add(container, fragment);
		} else if (oldFragment != fragment) {
			fragmentTransaction.hide(oldFragment);
			fragmentTransaction.show(fragment);
		}
		oldFragment = fragment;
	}
}










