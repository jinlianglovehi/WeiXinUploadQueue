package cn.ihealthbaby.weitaixinpro.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.form.HClientForm;
import cn.ihealthbaby.client.model.FetalHeart;
import cn.ihealthbaby.client.model.HClientUser;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.WeiTaiXinProApplication;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.service.ConfigService;
import cn.ihealthbaby.weitaixinpro.ui.MainActivity;
import cn.ihealthbaby.weitaixinpro.ui.adapter.HostIdAdapter;

/**
 * @author by kang on 2015/9/10.
 */
public class BindActivity extends BaseActivity {
	private final static String TAG = "BindActivity";
	public String deviceId;
	HostIdAdapter adapter;
	@Bind(R.id.iv_weitaixin)
	ImageView mIvWeitaixin;
	@Bind(R.id.rl_logo)
	RelativeLayout mRlLogo;
	@Bind(R.id.list)
	ListView listview;
	@Bind(R.id.tv_login_action)
	TextView mTvLoginAction;
	@Bind(R.id.tv_device_id)
	TextView mTvDeviceId;
	@Bind(R.id.rl_login)
	RelativeLayout mRlLogin;
	private List<FetalHeart> list = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		if (isLogin()) {
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
			finish();
		}
		deviceId = "000000000000015";
		try {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = tm.getDeviceId() == null ? deviceId : tm.getDeviceId();
			LogUtil.d(TAG, "deviceId:" + deviceId);
		} catch (Exception e) {
		}
		// TODO: 15/10/13  打包发布时务必去掉
		deviceId = "353490069872709";
		mTvDeviceId.setText(deviceId);
		login(deviceId);
		initView();
	}

	private boolean isLogin() {
		return SPUtil.isLogin(getApplicationContext());
	}

	private void login(@NonNull String deviceId) {
		ApiManager.getInstance().hClientAccountApi.login(deviceId, new DefaultCallback<HClientUser>(getApplicationContext(), new AbstractBusiness<HClientUser>() {
			@Override
			public void handleData(HClientUser data) {
				((WeiTaiXinProApplication) getApplication()).getAdapter().setAccountToken(data.getLoginToken());
				SPUtil.saveHClientUser(getApplicationContext(), data);
				requestAdviceSetting(data);
				ToastUtil.show(getApplicationContext(), "登录成功");
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}

			@Override
			public void handleValidator(Context context) {
				super.handleValidator(context);
				ToastUtil.show(getApplicationContext(), "请绑定设备");
				initData();
			}
		}), getRequestTag());
	}

	private void requestAdviceSetting(HClientUser user) {
		Intent service = new Intent(getApplicationContext(), ConfigService.class);
		service.putExtra(Constants.INTENT_SERVICE_TYPE, ConfigService.TYPE_ADVICE_SETTING);
		service.putExtra(Constants.INTENT_HID, user.getHospitalId());
		startService(service);
	}

	private void initView() {
		adapter = new HostIdAdapter(getApplicationContext(), list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setSelection(position);
			}
		});
		mTvLoginAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (adapter.getSelection() != HostIdAdapter.SELECT_NONE) {
					String serialnum = ((FetalHeart) adapter.getItem(adapter.getSelection())).getSerialnum();
					HClientForm hClientForm = new HClientForm();
					hClientForm.setSerialnum(serialnum);
					hClientForm.setDeviceId(deviceId);
					if (deviceId != null) {
						ApiManager.getInstance().hClientAccountApi.bind(hClientForm, new DefaultCallback<HClientUser>(getApplicationContext(), new AbstractBusiness<HClientUser>() {
							@Override
							public void handleData(HClientUser data) {
								SPUtil.saveHClientUser(getApplicationContext(), data);
								requestAdviceSetting(data);
								ToastUtil.show(getApplicationContext(), "绑定成功");
								startActivity(new Intent(getApplicationContext(), MainActivity.class));
								finish();
							}
						}), getRequestTag());
					} else {
						ToastUtil.show(getApplicationContext(), "请选择设备");
					}
				}
			}
		});
	}

	private void initData() {
		ApiManager.getInstance().hClientAccountApi.getFetalHearts(new DefaultCallback<ApiList<FetalHeart>>(getApplicationContext(), new AbstractBusiness<ApiList<FetalHeart>>() {
			@Override
			public void handleData(ApiList<FetalHeart> data) {
				if (data != null) {
					List<FetalHeart> dataList = data.getList();
					if (dataList != null && dataList.size() > 0) {
						list.addAll(dataList);
						adapter.notifyDataSetChanged();
					}
				}
			}
		}), getRequestTag());
	}
}
