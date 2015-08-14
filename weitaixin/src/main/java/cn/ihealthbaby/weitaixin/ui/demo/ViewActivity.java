package cn.ihealthbaby.weitaixin.ui.demo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.ui.monitor.MonitorFragment;
import cn.ihealthbaby.weitaixin.ui.wedget.RoundMaskView;

public class ViewActivity extends AppCompatActivity {
	private RoundMaskView roundMaskView;
	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main3);
		FragmentManager fragmentManager = getFragmentManager();
		MonitorFragment monitorFragment = new MonitorFragment();
		fragmentManager.beginTransaction().add(R.id.container, monitorFragment).commit();
	}
}
