package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.form.AddressForm;
import cn.ihealthbaby.client.model.Address;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

public class PayAddAddressWithEditActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //

    @Bind(R.id.tvAddAddressName)
    EditText tvAddAddressName;
    @Bind(R.id.tvAddAddressPhone)
    EditText tvAddAddressPhone;
    @Bind(R.id.tvAddAddressDetailAddress)
    EditText tvAddAddressDetailAddress;
    @Bind(R.id.tvSubmitAddress)
    TextView tvSubmitAddress;
    @Bind(R.id.tvAddAddressArea)
    TextView tvAddAddressArea;

    public final static int INTENT_REQUEST_CODE = 500;
    public final static int INTENT_RESULT_CODE = 501;

    public Address address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_add_address);

        ButterKnife.bind(this);

        title_text.setText("修改地址");

        PayConstant.AreasString = "";

        address = (Address) getIntent().getSerializableExtra("AddressItem");


        initView();

//        pullData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(PayConstant.AreasString)) {
            tvAddAddressArea.setText(PayConstant.AreasString);
        }
    }

    private void initView() {
        if (address != null) {
            tvAddAddressName.setText(address.getLinkMan());
            tvAddAddressPhone.setText(address.getMobile());
            tvAddAddressArea.setText(address.getArea());
            tvAddAddressDetailAddress.setText(address.getArea() + address.getAddress());
        }

        String addressName = tvAddAddressName.getText().toString().trim();
        if (!TextUtils.isEmpty(addressName)) {
            tvAddAddressName.setSelection(addressName.length());
        }

    }


    private void pullData() {

    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.rl3Functioned)
    public void FunctionedAddAddressArea() {
        Intent intent = new Intent(this, PayChooseAddressProvinceActivity.class);
        startActivity(intent);
    }


//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent orderDetail) {
//        super.onActivityResult(requestCode, resultCode, orderDetail);
//        LogUtil.d("AreagEx", "AreagEx==> %s = %s = %s" ,requestCode,resultCode,orderDetail!=null);
//        if (requestCode == INTENT_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                if (orderDetail != null) {
//                    String Areas = orderDetail.getStringExtra("Areas");
//                    LogUtil.d("AreagEx", "AreagEx==> " + Areas);
//                    if (!TextUtils.isEmpty(Areas)) {
//                        tvAddAddressArea.setText(Areas);
//                    }
//                }
//            }
//        }
//    }

    @OnClick(R.id.tvSubmitAddress)
    public void SubmitAddress() {
        String addressName = tvAddAddressName.getText().toString().trim();
        String addressPhone = tvAddAddressPhone.getText().toString().trim();
        String addressDetailAddress = tvAddAddressDetailAddress.getText().toString().trim();
        String addAddressArea = tvAddAddressArea.getText().toString().trim();

        if (TextUtils.isEmpty(addressName)) {
            ToastUtil.show(getApplicationContext(), "请填写收货人");
            return;
        }
        if (addressName != null && addressName.length() < 2) {
            ToastUtil.show(getApplicationContext(), "收货人至少两个字符");
            return;
        }
        if (TextUtils.isEmpty(addressPhone)) {
            ToastUtil.show(getApplicationContext(), "请填写收货人的手机号");
            return;
        }
        if (addressPhone != null && addressPhone.length() != 11) {
            ToastUtil.show(getApplicationContext(), "手机号必须是11位数字");
            return;
        }
        if (TextUtils.isEmpty(addAddressArea) || "请选择所在地区".equals(addAddressArea)) {
            ToastUtil.show(getApplicationContext(), "请选择所在地区");
            return;
        }
        if (TextUtils.isEmpty(addressDetailAddress)) {
            ToastUtil.show(getApplicationContext(), "请填写收货人的详细地址");
            return;
        }

        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();

        AddressForm addressForm = new AddressForm();
        addressForm.setLinkMan(addressName);
        addressForm.setMobile(addressPhone);
        addressForm.setArea(addAddressArea);
        addressForm.setAddress(addressDetailAddress);

        ApiManager.getInstance().addressApi.update(address.getId(), addressForm,
                new DefaultCallback<Void>(this, new AbstractBusiness<Void>() {
                    @Override
                    public void handleData(Void data) {
                        customDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                    }
                }), getRequestTag());

    }


}


