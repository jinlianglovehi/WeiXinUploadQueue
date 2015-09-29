package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.Information;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.MyRefreshAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.pay.PayConstant;
import cn.ihealthbaby.weitaixin.ui.pay.PayOrderDetailsActivity;


public class WoMessageActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;

    //
    @Bind(R.id.pullToRefresh)
    PullToRefreshListView pullToRefresh;
    private MyRefreshAdapter adapter;
    private ArrayList<Information> dataList;
    private Dialog dialog;

    int pageIndex = 1, pageSize = 5;
    private ReceiveBroadCast receiveBroadCast;
    private boolean isMove = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_message);

        ButterKnife.bind(this);
        title_text.setText("我的消息");

        adapter = new MyRefreshAdapter(this, null);
        pullToRefresh.setAdapter(adapter);
        pullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefresh.setScrollingWhileRefreshingEnabled(false);
        init();
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新
                ApiManager.getInstance().informationApi.getInformations(1, 10,
                        new DefaultCallback<PageData<Information>>(WoMessageActivity.this, new AbstractBusiness<PageData<Information>>() {
                            @Override
                            public void handleData(PageData<Information> data) {
                                dataList = null;
                                dataList = (ArrayList<Information>) data.getValue();
                                adapter.setDatas(dataList);
                                adapter.notifyDataSetChanged();
                                pageIndex = 1;
                                if (pullToRefresh != null) {
                                    pullToRefresh.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleClientError(Context context, Exception e) {
                                super.handleClientError(context, e);
                                pageIndex = 1;
                                if (pullToRefresh != null) {
                                    pullToRefresh.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleException(Exception e) {
                                super.handleException(e);
                                pageIndex = 1;
                                if (pullToRefresh != null) {
                                    pullToRefresh.onRefreshComplete();
                                }
                            }
                        }), getRequestTag());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //上拉加载更多
                ApiManager.getInstance().informationApi.getInformations((++pageIndex), pageSize,
                        new DefaultCallback<PageData<Information>>(WoMessageActivity.this, new AbstractBusiness<PageData<Information>>() {
                            @Override
                            public void handleData(PageData<Information> data) {
                                dataList = null;
                                dataList = (ArrayList<Information>) data.getValue();
                                adapter.addDatas(dataList);
                                adapter.notifyDataSetChanged();
                                if (pullToRefresh != null) {
                                    pullToRefresh.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleClientError(Context context, Exception e) {
                                super.handleClientError(context, e);
                                pageIndex--;
                                if (pullToRefresh != null) {
                                    pullToRefresh.onRefreshComplete();
                                }
                            }

                            @Override
                            public void handleException(Exception e) {
                                super.handleException(e);
                                pageIndex--;
                                if (pullToRefresh != null) {
                                    pullToRefresh.onRefreshComplete();
                                }
                            }
                        }), getRequestTag());
            }
        });

        pullToRefresh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!isMove) {
                    final Information item = (Information) adapter.getItem(position - 1);

                    LogUtil.d("Information", "Information==> " + item);

                    ApiManager.getInstance().informationApi.readInformation(item.getId(), new DefaultCallback<Void>(WoMessageActivity.this, new AbstractBusiness<Void>() {
                        @Override
                        public void handleData(Void data) {
                            item.setReadNums(item.getReadNums() + 1);
                            adapter.notifyDataSetChanged();

                            // 0 系统消息, 1 医生回复消息  2支付消息
                            int type = item.getType();
                            if (type == 0) {
                                Intent intent = new Intent(getApplicationContext(), WoMessagOfSystemMessageActivity.class);
                                intent.putExtra("SysMsg", item.getRelatedId());
                                startActivity(intent);
                            } else if (type == 1) {
                                Intent intent = new Intent(getApplicationContext(), WoMessagOfReplyMessageActivity.class);
                                intent.putExtra("AdviceReply", item.getRelatedId());
                                intent.putExtra("informationId", item.getId());
                                startActivity(intent);
                            } else if (type == 2) {
                                Intent intent = new Intent(getApplicationContext(), PayOrderDetailsActivity.class);
                                intent.putExtra(PayConstant.ORDERID, item.getRelatedId());
                                startActivity(intent);
                            }

                        }
                    }), getRequestTag());

                }
            }
        });




        pullToRefresh.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            private View selectedView;
            private View tvAdviceStatused;
            private float oldXDis;
            private float oldX;
            private float oldY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (adapter.getSelectedView() == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.selectedViewOld != null && adapter.selectedViewOld != adapter.selectedView) {
                            adapter.cancel(adapter.selectedViewOld);
                        }
                        selectedView = adapter.getSelectedView();
//                        tvAdviceStatused = adapter.getAdviceStatused();
                        oldXDis = event.getX();
                        oldX = event.getX();
                        oldY = event.getY();
                        isMove = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        isMove = true;
                        float distanceX = event.getX() - oldX;
                        if (distanceX < 0) {
                            float distanceY = event.getY() - oldY;
                            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                                if (Math.abs(event.getX() - oldXDis) >= adapter.recordDelete.getWidth() && selectedView != null) {
                                    selectedView.setX(-adapter.recordDelete.getWidth());
                                } else {
                                    if (selectedView != null && selectedView.getX() >= -adapter.recordDelete.getWidth()) {
                                        selectedView.setX(selectedView.getX() + distanceX);
                                    }
                                }
                            }
                        } else {
//                            float distanceY = event.getY() - oldY;
//                            if (Math.abs(distanceX) > Math.abs(distanceY)&&selectedView.getX()<=0) {
//                                if (Math.abs(event.getX() - oldXDis) >= adapter.recordDelete.getWidth() && selectedView != null) {
//                                    selectedView.setX(0);
//                                } else {
//                                    if (selectedView != null) {
//                                        selectedView.setX(selectedView.getX() + distanceX);
//                                    }
//                                }
//                            }

                            adapter.cancel();
                        }
                        oldX = event.getX();
                        oldY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        float distanceX2 = event.getX() - oldXDis;
                        if (distanceX2 < 0) {
                            if (Math.abs(distanceX2) >= adapter.recordDelete.getWidth() / 2 && selectedView != null) {
                                selectedView.setX(-adapter.recordDelete.getWidth());
                            } else {
                                if (selectedView != null) {
                                    selectedView.setX(0);
                                }
                            }
                        } else {
                            adapter.cancel();
                        }
                        adapter.selectedViewOld = selectedView;
//                        adapter.tvAdviceStatusedOld = tvAdviceStatused;
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullDatas();

        if (receiveBroadCast == null) {
            receiveBroadCast = new ReceiveBroadCast();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WoMessagOfReplyMessageActivity.REPLYMESSAGENOTIFICATION);
            registerReceiver(receiveBroadCast, filter);
        }

    }


    private void init() {
        ILoadingLayout startLabels = pullToRefresh.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = pullToRefresh.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        // 设置下拉刷新文本
        pullToRefresh.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        pullToRefresh.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        pullToRefresh.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        pullToRefresh.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        pullToRefresh.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        pullToRefresh.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
    }

    private void pullDatas() {

        final CustomDialog customDialog = new CustomDialog();
        dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();

        ApiManager.getInstance().informationApi.getInformations(1, 10,
                new DefaultCallback<PageData<Information>>(this, new AbstractBusiness<PageData<Information>>() {
                    @Override
                    public void handleData(PageData<Information> data) {
                        dataList = null;
                        dataList = (ArrayList<Information>) data.getValue();
                        adapter.setDatas(dataList);
                        adapter.notifyDataSetChanged();
                        if (pullToRefresh!=null) {
                            pullToRefresh.onRefreshComplete();
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        if (pullToRefresh!=null) {
                            pullToRefresh.onRefreshComplete();
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        if (pullToRefresh!=null) {
                            pullToRefresh.onRefreshComplete();
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleResult(Result<PageData<Information>> result) {
                        super.handleResult(result);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Long message = intent.getLongExtra("data", 0);
            for (Information information : dataList) {
                if (message == information.getId()) {
                    information.setReadNums(information.getReadNums() + 1);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiveBroadCast != null) {
            unregisterReceiver(receiveBroadCast);
        }
    }

    @OnClick(R.id.back)
    public void backOnclick() {
        finish();
    }
}
