package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.client.model.ServiceInside;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;
import cn.ihealthbaby.weitaixinpro.ui.adapter.MonitorAdapter;

/**
 * @author by kang on 2015/9/11.
 */
public class MonitorFragment extends BaseFragment implements View.OnClickListener {

    private static MonitorFragment instance;
    @Bind(R.id.iv_no_monitor)
    ImageView mIvNoMonitor;
    @Bind(R.id.rl_no_monitor)
    RelativeLayout mRlNoMonitor;
    @Bind(R.id.iv_alreay_monitor)
    ImageView mIvAlreayMonitor;
    @Bind(R.id.rl_already_monitor)
    RelativeLayout mRlAlreadyMonitor;
    @Bind(R.id.pullToRefresh)
    PullToRefreshListView mPullToRefresh;

    //科室id
    private long departmentId;
    //状态 0 未检测 ,1 已检测
    private int status = 0;
    //已检测页码
    private int alreadyPage = 1;
    //未检测页码
    private int undetectedPage = 1;

    //每页记录数
    private int pageSize = 10;


    private MonitorAdapter mAdapter;
    private PageData<ServiceInside> mServiceInsidePageData = new PageData<>();

    public static MonitorFragment getInstance() {
        if (instance == null) {
            instance = new MonitorFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.monitor_fragment, null);
        ButterKnife.bind(this, view);
        departmentId = SPUtil.getHClientUser(getActivity()).getDepartmentId();
        initView();
        initListener();
        return view;
    }

    private void initListener() {
        mRlAlreadyMonitor.setOnClickListener(this);
        mRlNoMonitor.setOnClickListener(this);
    }

    private void initView() {
        mAdapter = new MonitorAdapter(getActivity());
        mIvNoMonitor.setVisibility(View.VISIBLE);
        mIvAlreayMonitor.setVisibility(View.INVISIBLE);
        mPullToRefresh.setAdapter(mAdapter);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);

        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                int page = 1;
                if (status == 0) {
                    page = undetectedPage++;
                } else {
                    page = alreadyPage++;
                }

                ApiManager.getInstance().hClientAccountApi.getServiceInsides(SPUtil.getHClientUser(getActivity()).getDepartmentId(), status, page, pageSize,
                        new HttpClientAdapter.Callback<PageData<ServiceInside>>() {
                            @Override
                            public void call(Result<PageData<ServiceInside>> t) {
                                if (t.isSuccess()) {
                                    mServiceInsidePageData = t.getData();
                                    if (mServiceInsidePageData.getCount() > 0) {
                                        mAdapter.addData(mServiceInsidePageData.getValue());
                                    } else {

                                    }
                                }
                            }
                        }, getRequestTag());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_already_monitor:
                break;
            case R.id.rl_no_monitor:
                break;
        }
    }
}
