package cn.ihealthbaby.weitaixin.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import butterknife.Bind;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.pay.PayAccountActivity;
import cn.ihealthbaby.weitaixin.ui.pay.PayConstant;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayEvent;
import de.greenrobot.event.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

//	@Bind(R.id.tvWxErr) TextView tvWxErr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, PayConstant.WXPAY_APPID);

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
		LogUtil.d(TAG, "onPayFinish, errCode = " + resp.toString());

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(String.format("微信支付结果：%s", resp.errStr + ";code=" + String.valueOf(resp.errCode)));
//			builder.show();
			LogUtil.d("微信支付结果==> ", String.format("微信支付结果：%s", resp.errStr + ";code=" + String.valueOf(resp.errCode)));

			if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
				ToastUtil.show(this, "支付成功");
//				tvWxErr.setText(resp.errStr+"支付成功");

				LocalProductData.getLocal().localProductDataMap.clear();

				EventBus.getDefault().post(new PayEvent());
				Intent intent = new Intent(this, PayAccountActivity.class);
				this.startActivity(intent);
//				finish();
			}else{
				ToastUtil.show(this, "支付失败");
//				tvWxErr.setText(resp.errStr + "支付失败");
//				finish();
			}
		}else {
			Toast.makeText(this, "支付错误", Toast.LENGTH_SHORT).show();
		}


		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

		finish();

	}



}

