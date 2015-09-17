package cn.ihealthbaby.weitaixin.ui.pay.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alipay.sdk.app.PayTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.pay.PayAccountActivity;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayEvent;
import cn.ihealthbaby.weitaixin.ui.pay.PayMimeOrderActivity;
import de.greenrobot.event.EventBus;

public class PayAlipayUtil  {

//	// 商户PID
//	public static final String PARTNER = "2088021104297042";
//
//	// 商户收款账号
//	public static final String SELLER = "dabao@ihealthbaby.cn";
//
//	// 商户私钥，pkcs8格式
//	public static final String RSA_PRIVATE = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBANraYl42vCDCybYR" +
//			"nJVzinbg3XtNcOBR7lcT/DaemHgTDbnghl3KE9vKkb7CNG2Cybij/S+sCiVoUilD" +
//			"3R6dOJkw+RKJl+rnse6PiBLjqav87fZ+lWV1uY5TucjN5LWYP55hMB/p7ahraJb1" +
//			"nV27aaDC47W3Gy/S7ta6+LmeM8DDAgMBAAECgYEAla2CVVkt9WIDPshwxS4OZxuH" +
//			"nsrqBiFC1r9OYbS6JnUxkAzlGMQaLNowL7z2ymcGu4c8pBwPnGHqv1owor14dNlj" +
//			"Uffsk8IU+gFKGfvbTkIe5GGKj5TZLiROQzkWTzMoqewnBqZqux2qU7OVN3/Axsrx" +
//			"OJHq/Z/xsCrRN9+WJQECQQDvy0n0Qi18hqa8p2FJ37lNPmJP+umbWoOKLKUbSENK" +
//			"zf/RNPM6LsReFkst9xbMgXwX5eTX71X1wZalBAyeWIWDAkEA6aTKp7v0UzX+q2ko" +
//			"F2NtHvBd9GvB6dVmtfhV0F7gBen9BpGlB9Tn0cD/4uBp7Xo0nbHvmr0kbX+WHuFv" +
//			"gUozwQJBANyv8EkPWsN/PRbkyMHvV5/CaUKIftSOUf8ppW5dbCj9O5GztKH4hpq8" +
//			"08Xi4KWoCagI2TruNuNtnhISjvZwK7cCQQCGKu18+L/K8Ny3jgFJvPyyoZEyhJas" +
//			"MrFymZKdv32hp8Z2+TftkWM13kCTBg+OIXkJfV58W1UJ1BRe01H2yqeBAkEAsHJn" +
//			"40pnG5x6MrQc+pos9jeVwWHshZIkackt/v/WN3Gmuw0j0PNe+7PLLy1sPlEgaIrG" +
//			"jIqjeQD6rHD6jI27Xw==";
//	// 支付宝公钥
//	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi";


	private Activity context;

	public PayAlipayUtil(Activity context){
		this.context=context;
	}

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;


	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);

					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();

					String resultStatus = payResult.getResultStatus();

					Log.d("resultStatusmsg", "resultStatusmsg = " + resultStatus);
					Log.d("payResultmsg", "payResultmsg = " + payResult);

					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						ToastUtil.show(context, "支付成功");
						//
						LocalProductData.getLocal().localProductDataMap.clear();

						EventBus.getDefault().post(new PayEvent());
						Intent intent = new Intent(context, PayAccountActivity.class);
						context.startActivity(intent);
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							ToastUtil.show(context, "支付结果确认中~~~");
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							ToastUtil.show(context, "支付失败");
						}
					}
					break;
				}

				case SDK_CHECK_FLAG: {
					ToastUtil.show(context, "检查结果为：" + msg.obj);
					break;
				}
			}
		};
	};


	/**
	 * 调用SDK支付
	 */
	public void payAction(final String payInfo) {
		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(context);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Log.d("payInforesult", "payInforesult==> " + result);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}


	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(context);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(context);
		String version = payTask.getVersion();
		ToastUtil.show(context, version+"");
	}


//
//	/**
//	 * create the order info. 创建订单信息
//	 */
//	public String getOrderInfo(String subject, String body, String price) {
//
//		// 签约合作者身份ID
//		String orderInfo = "partner=" + "\"" + PARTNER + "\"";
//
//		// 签约卖家支付宝账号
//		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
//
//		// 商户网站唯一订单号
//		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
//
//		// 商品名称
//		orderInfo += "&subject=" + "\"" + subject + "\"";
//
//		// 商品详情
//		orderInfo += "&body=" + "\"" + body + "\"";
//
//		// 商品金额
//		orderInfo += "&total_fee=" + "\"" + price + "\"";
//
//		// 服务器异步通知页面路径
//		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
//				+ "\"";
//
//		// 服务接口名称， 固定值
//		orderInfo += "&service=\"mobile.securitypay.pay\"";
//
//		// 支付类型， 固定值
//		orderInfo += "&payment_type=\"1\"";
//
//		// 参数编码， 固定值
//		orderInfo += "&_input_charset=\"utf-8\"";
//
//		// 设置未付款交易的超时时间
//		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
//		// 取值范围：1m～15d。
//		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
//		// 该参数数值不接受小数点，如1.5h，可转换为90m。
//		orderInfo += "&it_b_pay=\"30m\"";
//
//		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
//		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
//
//		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//		orderInfo += "&return_url=\"m.alipay.com\"";
//
//		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//		// orderInfo += "&paymethod=\"expressGateway\"";
//
//		return orderInfo;
//	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
//	public String sign(String content) {
//		return SignUtils.sign(content, RSA_PRIVATE);
//	}

	/**
	 * get the sign type we use. 获取签名方式
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
