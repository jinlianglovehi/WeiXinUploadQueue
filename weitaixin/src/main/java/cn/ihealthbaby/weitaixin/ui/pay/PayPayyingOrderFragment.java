package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Order;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.PayAllOrderAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.CustomDialog;


public class PayPayyingOrderFragment extends BaseFragment {

    private final static String TAG = "PayPayyingOrderFragment";

    @Bind(R.id.payPullToRefreshAllOrder)
    PullToRefreshListView payPullToRefreshAllOrder;

    private BaseActivity context;

    private static PayPayyingOrderFragment instance;

    public static PayPayyingOrderFragment getInstance() {
        if (instance == null) {
            instance = new PayPayyingOrderFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_all_order, null);
        ButterKnife.bind(this, view);

        context = (BaseActivity) getActivity();
        initView();
//        pullDatas();

        return view;
    }

    private int pageIndex = 1, pageSize = 10;

    private void pullDatas() {
        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(getActivity(), "数据加载中...");
        dialog.show();
        ApiManager.getInstance().orderApi.getOrders(PayConstant.notPay, pageIndex, pageSize,
                new DefaultCallback<PageData<Order>>(getActivity(), new AbstractBusiness<PageData<Order>>() {
                    @Override
                    public void handleData(PageData<Order> data) {
                        ArrayList<Order> orders = (ArrayList<Order>) data.getValue();
                        if (orders != null && orders.size() <= 0) {
                            ToastUtil.show(getActivity().getApplicationContext(), "暂时没有数据");
                        }
                        adapter.setDatas(orders);
                        adapter.notifyDataSetChanged();
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }

    private PayAllOrderAdapter adapter;

    private void initView() {
        adapter = new PayAllOrderAdapter(context, null);
        payPullToRefreshAllOrder.setAdapter(adapter);
        payPullToRefreshAllOrder.setMode(PullToRefreshBase.Mode.BOTH);
        init();

        payPullToRefreshAllOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新
                pageIndex = 1;
                ApiManager.getInstance().orderApi.getOrders(PayConstant.notPay, pageIndex, pageSize,
                        new DefaultCallback<PageData<Order>>(getActivity(), new AbstractBusiness<PageData<Order>>() {
                            @Override
                            public void handleData(PageData<Order> data) {
                                ArrayList<Order> orders = (ArrayList<Order>) data.getValue();

                                if (orders!=null) {
                                    if (orders.size()<=0) {
                                        ToastUtil.show(getActivity(),"暂时没有最新数据");
                                    }else{
                                        adapter.setDatas(orders);
                                        adapter.notifyDataSetChanged();
                                    }
                                }else{
                                    ToastUtil.show(getActivity(),"暂时没有最新数据");
                                }


                                pageIndex = 1;
                                if (payPullToRefreshAllOrder != null) {
                                    payPullToRefreshAllOrder.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleClientError(Context context, Exception e) {
                                super.handleClientError(context, e);
                                pageIndex = 1;
                                if (payPullToRefreshAllOrder != null) {
                                    payPullToRefreshAllOrder.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleException(Exception e) {
                                super.handleException(e);
                                pageIndex = 1;
                                if (payPullToRefreshAllOrder != null) {
                                    payPullToRefreshAllOrder.onRefreshComplete();
                                }
                            }
                        }), getRequestTag());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //上拉加载更多
                ApiManager.getInstance().orderApi.getOrders(PayConstant.notPay, (++pageIndex), pageSize,
                        new DefaultCallback<PageData<Order>>(getActivity(), new AbstractBusiness<PageData<Order>>() {
                            @Override
                            public void handleData(PageData<Order> data) {
                                ArrayList<Order> orders = (ArrayList<Order>) data.getValue();
                                if (orders.size() <= 0) {
                                    --pageIndex;
                                    ToastUtil.show(getActivity().getApplicationContext(), "暂时没有更多数据");
                                }
                                adapter.addDatas(orders);
                                adapter.notifyDataSetChanged();
                                if (payPullToRefreshAllOrder != null) {
                                    payPullToRefreshAllOrder.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleClientError(Context context, Exception e) {
                                super.handleClientError(context, e);
                                --pageIndex;
                                if (payPullToRefreshAllOrder != null) {
                                    payPullToRefreshAllOrder.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleException(Exception e) {
                                super.handleException(e);
                                --pageIndex;
                                if (payPullToRefreshAllOrder != null) {
                                    payPullToRefreshAllOrder.onRefreshComplete();
                                }
                            }
                        }), getRequestTag());
            }
        });

        payPullToRefreshAllOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Order order = (Order) adapter.getItem(position - 1);
                Intent intent = new Intent(getActivity().getApplicationContext(), PayOrderDetailsActivity.class);
                intent.putExtra(PayConstant.ORDERID, order.getId());
                startActivity(intent);
            }
        });

    }


    private void init() {
        ILoadingLayout startLabels = payPullToRefreshAllOrder.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = payPullToRefreshAllOrder.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        // 设置下拉刷新文本
        payPullToRefreshAllOrder.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        payPullToRefreshAllOrder.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        payPullToRefreshAllOrder.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        payPullToRefreshAllOrder.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        payPullToRefreshAllOrder.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        payPullToRefreshAllOrder.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        return options;
    }

    @Override
    public void onResume() {
        super.onResume();
        pullDatas();
    }
}




