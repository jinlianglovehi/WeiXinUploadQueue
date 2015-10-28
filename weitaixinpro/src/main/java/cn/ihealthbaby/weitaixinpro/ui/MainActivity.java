package cn.ihealthbaby.weitaixinpro.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.ui.login.BindActivity;
import cn.ihealthbaby.weitaixinpro.ui.monitor.tab.MonitorTabFragment;
import cn.ihealthbaby.weitaixinpro.ui.record.RecordFragment;
import cn.ihealthbaby.weitaixinpro.ui.settings.SettingsFragment;
import de.greenrobot.event.EventBus;
import im.fir.sdk.FIR;
import im.fir.sdk.callback.VersionCheckCallback;
import im.fir.sdk.version.AppVersion;

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
	private Fragment monitorTabFragment;
	private Fragment recordFragment;
	private Fragment settingsFragment;
	private FragmentManager fragmentManager;
	private long exitTime;
	private boolean rebind;

	public void onEventMainThread(RebindEvent event) {
		rebind = true;
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "MainActivity onCreate");
		init();
	}

	private void init() {
		setContentView(R.layout.activity_me_main_fragment);
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);
		fragmentManager = getSupportFragmentManager();
		monitorTabFragment = MonitorTabFragment.getInstance();
		recordFragment = RecordFragment.getInstance();
		settingsFragment = SettingsFragment.getInstance();
		llTabMonitor.performClick();
	}

	@OnClick(R.id.ll_tab_monitor)
	public void iv_tab_02() {
		showTab(iv_tab_02);
		showFragment(R.id.container, monitorTabFragment);
	}

	@Override
	protected void onResume() {
		super.onResume();
		String firToken = null;
		try {
			ApplicationInfo appInfo = null;
			appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			firToken = appInfo.metaData.getString(Constants.BUG_HD_SDK_GENERAL_KEY);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (firToken != null) {
			FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
				@Override
				public void onSuccess(AppVersion appVersion, boolean b) {
					if (b) {
						LogUtil.d(TAG, "发现新版本" + appVersion);
						ToastUtil.show(getApplicationContext(), "发现新版本" + appVersion.getVersionName() + "\r\n\r\n" + appVersion.getChangeLog());
					} else {
						LogUtil.d(TAG, "暂无新版本" + appVersion);
					}
				}

				@Override
				public void onFail(String s, int i) {
					LogUtil.d(TAG, "upload request Fail");
				}

				@Override
				public void onError(Exception e) {
					LogUtil.d(TAG, "upload request Error");
				}

				@Override
				public void onStart() {
					LogUtil.d(TAG, "开始检查版本信息");
				}

				@Override
				public void onFinish() {
					LogUtil.d(TAG, "检查版本信息结束");
				}
			});
		}
	}

	@OnClick(R.id.ll_tab_record)
	public void iv_tab_03() {
		showTab(iv_tab_03);
		showFragment(R.id.container, recordFragment);
	}

	@OnClick(R.id.ll_tab_profile)
	public void iv_tab_04() {
		showTab(iv_tab_04);
		showFragment(R.id.container, settingsFragment);
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

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - exitTime > 3000) {
			ToastUtil.show(getApplicationContext(), "再次点击返回,退出应用");
			exitTime = System.currentTimeMillis();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
		if (rebind) {
			Intent intent = new Intent(getApplicationContext(), BindActivity.class);
			startActivity(intent);
		}
	}
}
