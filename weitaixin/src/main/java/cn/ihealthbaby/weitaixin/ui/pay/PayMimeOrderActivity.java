package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.home.HomePageFragment;
import cn.ihealthbaby.weitaixin.ui.mine.WoInfoFragment;
import cn.ihealthbaby.weitaixin.ui.monitor.MonitorFragment;
import cn.ihealthbaby.weitaixin.ui.record.RecordFragment;

public class PayMimeOrderActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //
    @Bind(R.id.llAllOrder) LinearLayout llAllOrder;
    @Bind(R.id.tvAllOrderText) TextView tvAllOrderText;
    @Bind(R.id.viewAllOrderLine) View viewAllOrderLine;

    @Bind(R.id.llPayingOrder) LinearLayout llPayingOrder;
    @Bind(R.id.tvPayOrderText) TextView tvPayOrderText;
    @Bind(R.id.viewPayOrderLine) View viewPayOrderLine;

    @Bind(R.id.llGetingGoods) LinearLayout llGetingGoods;
    @Bind(R.id.tvGetGoodsText) TextView tvGetGoodsText;
    @Bind(R.id.viewGetGoodsLine) View viewGetGoodsLine;

    @Bind(R.id.flContainerPay)  FrameLayout flContainerPay;

    private FragmentManager fragmentManager;
    public PayAllOrderFragment payAllOrderFragment;
    public PayPayyingOrderFragment payPayyingOrderFragment;
    public PayGettingGoodsFragment payGettingGoodsFragment;
    public Fragment oldFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_mime_order);

        ButterKnife.bind(this);

        title_text.setText("我的订单");

        fragmentManager = getFragmentManager();
        AllOrder();
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.llAllOrder)
    public void AllOrder() {
        if (SPUtil.isLogin(this)) {
            chooesTab(tvAllOrderText, viewAllOrderLine);
            payAllOrderFragment = PayAllOrderFragment.getInstance();
            showFragment(R.id.flContainerPay, payAllOrderFragment);
        }
    }


    @OnClick(R.id.llPayingOrder)
    public void PayOrder() {
        if (SPUtil.isLogin(this)) {
            chooesTab(tvPayOrderText, viewPayOrderLine);
            payPayyingOrderFragment = PayPayyingOrderFragment.getInstance();
            showFragment(R.id.flContainerPay, payPayyingOrderFragment);
        }
    }


    @OnClick(R.id.llGetingGoods)
    public void GetGoods() {
        if (SPUtil.isLogin(this)) {
            chooesTab(tvGetGoodsText, viewGetGoodsLine);
            payGettingGoodsFragment = PayGettingGoodsFragment.getInstance();
            showFragment(R.id.flContainerPay, payGettingGoodsFragment);
        }
    }


    private void chooesTab(TextView textView, View view){
        tvAllOrderText.setTextColor(getResources().getColor(R.color.font_color));
        viewAllOrderLine.setVisibility(View.INVISIBLE);
        tvPayOrderText.setTextColor(getResources().getColor(R.color.font_color));
        viewPayOrderLine.setVisibility(View.INVISIBLE);
        tvGetGoodsText.setTextColor(getResources().getColor(R.color.font_color));
        viewGetGoodsLine.setVisibility(View.INVISIBLE);
        textView.setTextColor(getResources().getColor(R.color.green0));
        view.setVisibility(View.VISIBLE);
    }


    private void showFragment(int container, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        show(container,fragmentTransaction,fragment);
        fragmentTransaction.commit();
    }

    private void show(int container, FragmentTransaction fragmentTransaction, Fragment fragment){
        fragmentTransaction.replace(container,fragment);
        return;
//
//        if (fragment == null) {
//            return;
//        }
//
//        if (!fragment.isAdded()) {
//            if(oldFragment!=null){
//                fragmentTransaction.hide(oldFragment);
//            }
//            fragmentTransaction.addItemList(container, fragment);
//        } else if( oldFragment != fragment){
//            fragmentTransaction.hide(oldFragment);
//            fragmentTransaction.show(fragment);
//        }
//        oldFragment = fragment;
//        LogUtil.d("ChildCountPay==", "ChildCountPay= %s", this.flContainerPay.getChildCount());
    }


}


