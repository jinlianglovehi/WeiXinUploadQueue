package cn.ihealthbaby.weitaixin.ui.monitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.ihealthbaby.weitaixin.R;

public class MonitorActivity extends AppCompatActivity {
	private TextView viewById;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
//		CountDownTimer countDownTimer = new CountDownTimer(100000, 250) {
//			@Override
//			public void onTick(long millisUntilFinished) {
//				List<FHRPackage> fhrPackages = DataStorage.fhrPackages;
//				if (fhrPackages != null && fhrPackages.size() > 0) {
//					int fhr1 = fhrPackages.get(fhrPackages.size() - 1).getFHR1();
//					viewById.setText(String.valueOf(fhr1));
//				}
//			}
//
//			@Override
//			public void onFinish() {
//				start();
//			}
//		};
//		countDownTimer.start();
	}
}
