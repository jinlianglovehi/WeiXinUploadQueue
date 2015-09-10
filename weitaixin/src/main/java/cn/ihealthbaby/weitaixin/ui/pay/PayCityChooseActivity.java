package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Province;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class PayCityChooseActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.rlLeftCity) FrameLayout rlLeftCity;
    @Bind(R.id.rlRightCity) FrameLayout rlRightCity;

    private FragmentManager fragmentManager;
    private ArrayList<String> leftCityList=new ArrayList<String>();
    private ArrayList<String> rightCityList=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_city_choose);

        ButterKnife.bind(this);

        title_text.setText("选择城市");
        fragmentManager = getFragmentManager();

        pullData();
    }

    private void pullData() {
        CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        // 0 全部省份    1 筛选有开通线上服务医院的省份
        ApiManager.getInstance().addressApi.getProvinces(1, new HttpClientAdapter.Callback<ApiList<Province>>() {
            @Override
            public void call(Result<ApiList<Province>> t) {
                PayLeftCityFragment payLeftCityFragment=PayLeftCityFragment.getInstance(null);
                Bundle bundle=new Bundle();
                bundle.putSerializable(PayConstant.LeftCityList, leftCityList);
                payLeftCityFragment.setArguments(bundle);

                PayRightCityFragment payRightCityFragment=PayRightCityFragment.getInstance(null);
                Bundle bundleRight=new Bundle();
                bundleRight.putSerializable(PayConstant.RightCityList, rightCityList);
                payRightCityFragment.setArguments(bundleRight);

                showFragment(R.id.rlLeftCity, payLeftCityFragment, PayConstant.LeftCityList);
                showFragment(R.id.rlRightCity, payRightCityFragment, PayConstant.RightCityList);
            }
        }, getRequestTag());
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    private void showFragment(int container, Fragment fragment, String tagFragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(container, fragment, tagFragment);
        fragmentTransaction.commit();
    }



}


