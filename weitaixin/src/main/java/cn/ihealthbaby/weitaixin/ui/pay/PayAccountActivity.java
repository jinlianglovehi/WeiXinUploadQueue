package cn.ihealthbaby.weitaixin.ui.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Service;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_account);

        ButterKnife.bind(this);

        title_text.setText("我的账户");
//        tvPayAccountRentDay.setText("租用设备");

        orderId = -1;

    }


    @Override
    protected void onResume() {
        super.onResume();
        isRentEquipment = false;
        pullData();
    }


    private final int ACTIVATE_SERVICE = 2;

    private void pullData() {
        user = SPUtil.getUser(this);
        if (user != null) {
            boolean hasService = user.getHasService();
            if (hasService) {
                ApiManager.getInstance().serviceApi.getByUser(
                        new DefaultCallback<Service>(this, new AbstractBusiness<Service>() {
                    @Override
                    public void handleData(Service data) {
                        if (data != null) {
                            //0 开通未绑定设备,1绑定未激活服务,2服务已激活,3服务结束,4服务已取消
                            if (data.getServiceStatus() == ACTIVATE_SERVICE) {
                                tvPayAccountRentDay.setText("已租用设备" + data.getRentedDays() + "天");
                                orderId = data.getOrderId();
                            } else {
                                tvPayAccountRentDay.setText("租用设备");
                                orderId = data.getOrderId();
                            }
                        }
                        isRentEquipment=true;
                    }

                    @Override
                    public void handleClientError(Context context,Exception e) {
                        super.handleClientError(context,e);
                        setOrgin();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        setOrgin();
                    }
                }), getRequestTag());
            } else {
                setOrgin();
            }
        } else {
            setOrgin();
        }
    }

    public void setOrgin(){
        isRentEquipment=true;
        tvPayAccountRentDay.setText("租用设备");
        orderId = -1;
    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.llRentEquipment)
    public void RentEquipment() {
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


