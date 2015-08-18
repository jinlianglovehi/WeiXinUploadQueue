package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.AdviceReply;
import cn.ihealthbaby.client.model.Information;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.client.model.SysMsg;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.MyRefreshAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;


public class WoMessageActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.pullToRefresh) PullToRefreshListView pullToRefresh;
    private MyRefreshAdapter adapter;
    private ArrayList<Information> dataList=new ArrayList<Information>();
    private Dialog dialog;

    int pageIndex=1, pageSize=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_message);

        ButterKnife.bind(this);
        title_text.setText("我的消息");
        back.setVisibility(View.INVISIBLE);
//

        adapter=new MyRefreshAdapter(this,null);
        pullToRefresh.setAdapter(adapter);
        pullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        init();


        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新
                ApiManager.getInstance().informationApi.getInformations(1, 10, new HttpClientAdapter.Callback<PageData<Information>>() {
                    @Override
                    public void call(Result<PageData<Information>> t) {
                        if (t.isSuccess()) {
                            PageData<Information> data = t.getData();
                            ArrayList<Information> dataList = (ArrayList<Information>) data.getValue();
                            adapter.setDatas(dataList);
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.show(getApplicationContext(), t.getMsg());
                        }
                        pageIndex = 1;
                        pullToRefresh.onRefreshComplete();
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //上拉加载更多
                ApiManager.getInstance().informationApi.getInformations((++pageIndex), pageSize, new HttpClientAdapter.Callback<PageData<Information>>() {
                    @Override
                    public void call(Result<PageData<Information>> t) {
                        if (t.isSuccess()) {
                            PageData<Information> data = t.getData();
                            ArrayList<Information> dataList = (ArrayList<Information>) data.getValue();
                            adapter.addDatas(dataList);
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.show(getApplicationContext(), t.getMsg());
                            pageIndex--;
                        }
                        pullToRefresh.onRefreshComplete();
                    }
                });
            }
        });

        pullToRefresh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Information item = (Information) adapter.getItem(position-1);
                // 0 系统消息, 1 医生回复消息  2支付消息
                int type=item.getType();
                if (type == 0) {
                    Intent intent=new Intent(getApplicationContext(),WoMessagOfSystemMessageActivity.class);
                    intent.putExtra("SysMsg", item.getRelatedId());
                    startActivity(intent);
                } else if (type == 1) {
                    Intent intent=new Intent(getApplicationContext(),WoMessagOfReplyMessageActivity.class);
                    intent.putExtra("AdviceReply",item.getRelatedId());
                    startActivity(intent);
                } else if (type == 2) {

                }
                ToastUtil.show(getApplicationContext(),(position-1)+":"+item.getTitle()+" : "+item.getId());
            }
        });


        pullDatas();
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

        dialog=new CustomDialog().createDialog1(this,"数据加载中...");
        dialog.show();

        ApiManager.getInstance().informationApi.getInformations(1, 10, new HttpClientAdapter.Callback<PageData<Information>>() {
            @Override
            public void call(Result<PageData<Information>> t) {
                if (t.isSuccess()) {
                    PageData<Information> data = t.getData();
                    ArrayList<Information> dataList = (ArrayList<Information>) data.getValue();
                    adapter.setDatas(dataList);
                    adapter.notifyDataSetChanged();
                }else {
                    ToastUtil.show(getApplicationContext(),t.getMsg());
                }
                pullToRefresh.onRefreshComplete();
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }


}
