package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Address;
import cn.ihealthbaby.client.model.Order;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.PayAllOrderAdapter;
import cn.ihealthbaby.weitaixin.adapter.PayMimeAddressAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class PayMimeAddressActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.addressPullToRefreshAllOrder) PullToRefreshListView addressPullToRefreshAllOrder;
    @Bind(R.id.tvAddNewAddress) TextView tvAddNewAddress;


    private  PayMimeAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_mime_address);

        ButterKnife.bind(this);

        title_text.setText("我的地址");

        initView();
        pullData();
    }


    @OnClick(R.id.tvAddNewAddress)
    public void AddNewAddress() {
        Intent intent=new Intent(this,PayAddAddressActivity.class);
        startActivity(intent);
    }


    private void pullData() {
        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().addressApi.getAddresss(new HttpClientAdapter.Callback<ApiList<Address>>() {
            @Override
            public void call(Result<ApiList<Address>> t) {
                if (t.isSuccess()) {
                    ApiList<Address> data = t.getData();
                    ArrayList<Address> addressList = (ArrayList<Address>) data.getList();
                    if (addressList.size()<=0) {
                        ToastUtil.show(getApplicationContext(), "没有数据");
                    }
                    adapter.setDatas(addressList);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }


    private void initView() {
        adapter = new PayMimeAddressAdapter(getApplicationContext(), null);
        addressPullToRefreshAllOrder.setAdapter(adapter);
        addressPullToRefreshAllOrder.setMode(PullToRefreshBase.Mode.DISABLED);
        init();

        addressPullToRefreshAllOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新

                if (addressPullToRefreshAllOrder != null) {
                    addressPullToRefreshAllOrder.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //上拉加载更多

                if (addressPullToRefreshAllOrder != null) {
                    addressPullToRefreshAllOrder.onRefreshComplete();
                }
            }
        });

        addressPullToRefreshAllOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            }
        });

    }


    private void init() {
        ILoadingLayout startLabels = addressPullToRefreshAllOrder.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = addressPullToRefreshAllOrder.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        // 设置下拉刷新文本
        addressPullToRefreshAllOrder.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        addressPullToRefreshAllOrder.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        addressPullToRefreshAllOrder.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        addressPullToRefreshAllOrder.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        addressPullToRefreshAllOrder.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        addressPullToRefreshAllOrder.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
    }



    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


}


