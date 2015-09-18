package cn.ihealthbaby.weitaixin.ui.pay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

public class PayAccountActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;

    //
    @Bind(R.id.rlPayMimeOrder) RelativeLayout rlPayMimeOrder;
//    @Bind(R.id.llRentEquipment) LinearLayout llRentEquipment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_account);

        ButterKnife.bind(this);

        title_text.setText("我的账户");
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.llRentEquipment)
    public void RentEquipment() {
        Intent intent = new Intent(this, PayRentInformationActivity.class);
        startActivity(intent);

//        Intent intentOrderDetails = new Intent(this, PayOrderDetailsActivity.class);
//        startActivity(intentOrderDetails);

//        User user=null;
//        if (user.getHasService()) {
//
//        } else {
//
//        }
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


