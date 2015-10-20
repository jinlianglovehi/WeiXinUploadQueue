package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.Service;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

public class PayAccountActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;

    //
    @Bind(R.id.rlPayMimeOrder)
    RelativeLayout rlPayMimeOrder;
    @Bind(R.id.tvPayAccountRentDay)
    TextView tvPayAccountRentDay;


    private User user;
    private long orderId = -1;
    private boolean isRentEquipment=false;
    private boolean isRentErr=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_account);

        ButterKnife.bind(this);

        title_text.setText("我的账户");

        tvPayAccountRentDay.setText("租用设备");

        orderId = -1;

    }


    @Override
    protected void onResume() {
        super.onResume();
        isRentEquipment = false;
        isRentErr = false;
        pullData();
    }


    //0 开通未绑定设备,1绑定未激活服务,2服务已激活,3服务结束,4服务已取消
    private final int ACTIVATE_SERVICE = 2;

    private void pullData() {
        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog1 = customDialog.createDialog1(this, "数据加载中...");
        dialog1.show();

        ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(this, new AbstractBusiness<User>() {
            @Override
            public void handleData(User data) {
                SPUtil.saveUser(PayAccountActivity.this, data);
            }
        }),getRequestTag());

        user = SPUtil.getUser(this);
        if (user != null) {
            boolean hasService = user.getHasService();
            if (hasService) {
                ApiManager.getInstance().serviceApi.getByUser(
                        new DefaultCallback<Service>(this, new AbstractBusiness<Service>() {
                            @Override
                            public void handleData(Service data) {
                                //0开通未绑定设备, 1绑定未激活服务, 2服务已激活, 3服务结束, 4服务已取消
                                LogUtil.d("Service", "Service+==>" + data);
                                if (data.getServiceStatus() == ACTIVATE_SERVICE) {
                                    tvPayAccountRentDay.setText("已租用设备" + data.getRentedDays() + "天");
                                    orderId = data.getOrderId();
                                } else {
                                    tvPayAccountRentDay.setText("租用设备");
                                    orderId = data.getOrderId();
                                }
                                LogUtil.d("Service_orderId", "Service_orderId+==>" + orderId);

                                isRentErr = false;
                                isRentEquipment = true;
                            }

                            @Override
                            public void handleResult(Result<Service> result) {
                                super.handleResult(result);
                                customDialog.dismiss();
                            }

                            @Override
                            public void handleClientError(Context context,Exception e) {
                                super.handleClientError(context,e);
                                isRentErr = true;
                                setOrgin(customDialog);
                            }

                            @Override
                            public void handleAllFailure(Context context) {
                                super.handleAllFailure(context);
                                isRentErr = true;
                                setOrgin(customDialog);
                            }

                            @Override
                            public void handleException(Exception e) {
                                super.handleException(e);
                                isRentErr = true;
                                setOrgin(customDialog);
                            }
                }), getRequestTag());
            } else {
                setOrgin(customDialog);
            }
        } else {
//            ToastUtil.show(this, "用户没有服务");
            setOrgin(customDialog);
        }
    }

    public void setOrgin(CustomDialog customDialog) {
        isRentEquipment = true;
        tvPayAccountRentDay.setText("租用设备");
        orderId = -1;
        customDialog.dismiss();
    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.llRentEquipment)
    public void RentEquipment() {

        if(isRentErr){
            ToastUtil.show(this, "服务器错误");
            return;
        }


        if (isRentEquipment) {
            if (user != null) {
                boolean hasService = user.getHasService();
                if (hasService) {
                    if (orderId != -1) {
                        Intent intentOrderDetails = new Intent(this, PayOrderDetailsActivity.class);
                        intentOrderDetails.putExtra(PayConstant.ORDERID, orderId);
                        startActivity(intentOrderDetails);
                    } else {
                        Intent intent = new Intent(this, PayRentInformationActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(this, PayRentInformationActivity.class);
                    startActivity(intent);
                }
            }
        }else{
            ToastUtil.show(this, "用户中断了获取租凭信息的请求，请重新获取数据");
        }
    }


    @OnClick(R.id.rlPayMimeOrder)
    public void PayMimeOrder() {
        Intent intent = new Intent(this, PayMimeOrderActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.rlPayAddress)
    public void PayAddress() {
        Intent intent = new Intent(this, PayMimeAddressWithEditActivity.class);
        startActivity(intent);
    }


}


