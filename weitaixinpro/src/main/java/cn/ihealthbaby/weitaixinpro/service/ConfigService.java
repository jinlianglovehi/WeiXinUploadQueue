package cn.ihealthbaby.weitaixinpro.service;

import android.app.IntentService;
import android.content.Intent;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;

public class ConfigService extends IntentService {
	public static final int TYPE_ADVICE_SETTING = 1;
	private final static String TAG = "ConfigService";

	public ConfigService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int type = intent.getIntExtra(Constants.INTENT_SERVICE_TYPE, -1);
		switch (type) {
			case ConfigService.TYPE_ADVICE_SETTING:
				requestAdviceSetting(intent);
				break;
		}
	}

	private void requestAdviceSetting(Intent intent) {
		long hid = intent.getLongExtra(Constants.INTENT_HID, -1);
		ApiManager.getInstance().hClientAccountApi.getAdviceSetting(hid, new DefaultCallback<AdviceSetting>(getApplicationContext(), new AbstractBusiness<AdviceSetting>() {
			@Override
			public void handleData(AdviceSetting data) {
				SPUtil.saveAdviceSetting(getApplicationContext(), data);
				LogUtil.d(TAG, data.toString());
			}
		}), this);
	}
}

