package cn.ihealthbaby.weitaixin.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.ui.pay.wxpay.Constants;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {
		LogUtil.d(TAG, "onPayFinish, errCode = " + resp.errCode);
//		LogUtil.d(TAG, "onPayFinish, errCode = " + resp.);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage(String.format("微信支付结果：%s", resp.errStr + ";code=" + String.valueOf(resp.errCode)));
			builder.show();
		}else {
			Toast.makeText(this, "sssss", Toast.LENGTH_SHORT).show();
		}
	}


}

