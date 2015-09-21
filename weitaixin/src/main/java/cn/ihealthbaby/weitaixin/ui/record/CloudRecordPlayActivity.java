package cn.ihealthbaby.weitaixin.ui.record;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Advice;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.data.net.AbstractBusiness;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.ui.monitor.RecordPlayActivity;

public class CloudRecordPlayActivity extends RecordPlayActivity {
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView title_text;
	@Bind(R.id.function)
	TextView function;
	//
	@Bind(R.id.ivActionImage)
	ImageView ivActionImage;
	@Bind(R.id.tvStateText)
	TextView tvStateText;
	private String stateFlag = "";
	private Advice advice;

	@Override
	protected void function() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_play);
		ButterKnife.bind(this);
		title_text.setText("胎心监测");
		initText();
	}

	@Override
	protected void getData() {
		String clientId = getIntent().getStringExtra(Constants.INTENT_LOCAL_RECORD_ID);
		String url = getIntent().getStringExtra(Constants.INTENT_URL);
		long id = getIntent().getLongExtra(Constants.INTENT_ID, 0);
		ApiManager.getInstance().adviceApi.getAdviceDetail(id, new DefaultCallback<Advice>(getApplicationContext(), new AbstractBusiness<Advice>() {
			@Override
			public void handleData(Advice advice) throws Exception {
				CloudRecordPlayActivity.this.advice = advice;
			}
		}), getRequestTag());

	}

	private void initText() {
		stateFlag = getIntent().getStringExtra("strStateFlag");
		if ("问医生".equals(stateFlag)) {
			tvStateText.setText("问医生");
//            ivActionImage.setImageResource();
		} else if ("等待回复".equals(stateFlag)) {
			tvStateText.setText("等待回复");
		} else if ("已回复".equals(stateFlag)) {
			tvStateText.setText("已回复");
		} else if ("需上传".equals(stateFlag)) {
			tvStateText.setText("需上传");
		}
	}

	@OnClick(R.id.function)
	public void Function() {
	}

	@OnClick(R.id.back)
	public void onBack() {
		this.finish();
	}

	@OnClick(R.id.ivActionImage)
	public void ActionImage() {
	}
}

