package cn.ihealthbaby.weitaixinpro.ui.monitor.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.HClientUser;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;
import de.greenrobot.event.EventBus;

/**
 *
 */
public class MonitorTabFragment extends BaseFragment {
	private static MonitorTabFragment instance;
	@Bind(R.id.iv_unmonitor)
	ImageView ivUnmonitor;
	@Bind(R.id.rl_unmonitor)
	RelativeLayout rlUnmonitor;
	@Bind(R.id.iv_monitoring)
	ImageView ivMonitoring;
	@Bind(R.id.rl_monitoring)
	RelativeLayout rlMonitoring;
	@Bind(R.id.tv_host_name)
	TextView tvHostName;
	@Bind(R.id.tv_department_name)
	TextView tvDepartmentName;
	@Bind(R.id.tv_title)
	TextView tvTitle;
	@Bind(R.id.container)
	FrameLayout container;
	private android.support.v4.app.FragmentManager fragmentManager;
	private MonitoringFragment monitoringFragment;
	private UnmonitorFragment unmonitorFragment;

	public static MonitorTabFragment getInstance() {
		if (instance == null) {
			instance = new MonitorTabFragment();
		}
		return instance;
	}

	@OnClick(R.id.rl_monitoring)
	void change2() {
		ivMonitoring.setVisibility(View.VISIBLE);
		ivUnmonitor.setVisibility(View.GONE);
		changeFragment(monitoringFragment);
	}

	@OnClick(R.id.rl_unmonitor)
	void change1() {
		ivMonitoring.setVisibility(View.GONE);
		ivUnmonitor.setVisibility(View.VISIBLE);
		changeFragment(unmonitorFragment);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.monitor_fragment, null);
		fragmentManager = getChildFragmentManager();
		monitoringFragment = new MonitoringFragment();
		unmonitorFragment = new UnmonitorFragment();
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		EventBus.getDefault().register(this);
		final HClientUser hClientUser = SPUtil.getHClientUser(getActivity().getApplicationContext());
		tvHostName.setText(hClientUser.getHospitalName());
		tvDepartmentName.setText(hClientUser.getDepartmentName());
		change1();
	}

	private void changeFragment(Fragment fragment) {
		if (fragment == null) {
			return;
		}
		fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(CountEvent event) {
		long count = event.getCount();
		switch (event.getType()) {
			case CountEvent.TYPE_UNMONITOR:
				tvTitle.setText("未监测" + count + "人");
				break;
			case CountEvent.TYPE_MONITORING:
				tvTitle.setText("监测中" + count + "人");
				break;
			default:
				break;
		}
	}
}
