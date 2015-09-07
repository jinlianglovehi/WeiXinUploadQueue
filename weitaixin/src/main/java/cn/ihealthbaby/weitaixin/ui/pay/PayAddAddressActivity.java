package cn.ihealthbaby.weitaixin.ui.pay;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.PayMimeAddressAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

public class PayAddAddressActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.addressPullToRefreshAllOrder) PullToRefreshListView addressPullToRefreshAllOrder;
    @Bind(R.id.tvAddNewAddress) TextView tvAddNewAddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_add_address);

        ButterKnife.bind(this);

        title_text.setText("添加地址");

        pullData();
    }

    private void pullData() {
        PayMimeAddressAdapter adapter = new PayMimeAddressAdapter(getApplicationContext(), null);
        addressPullToRefreshAllOrder.setAdapter(adapter);
        addressPullToRefreshAllOrder.setMode(PullToRefreshBase.Mode.BOTH);
        init();

        addressPullToRefreshAllOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //上拉加载更多

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


