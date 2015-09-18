package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.client.model.ServiceInside;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;
import cn.ihealthbaby.weitaixinpro.ui.adapter.MonitorAdapter;

/**
 * @author by kang on 2015/9/17.
 */
public class NoMonitorFragment extends BaseFragment implements MonitorAdapter.BeginMoniter {
    @Bind(R.id.pullToRefresh)
    PullToRefreshListView mPullToRefresh;

    private MonitorAdapter mAdapter;
    private PageData<ServiceInside> mServiceInsidePageData = new PageData<>();
    //科室id
    private long departmentId;
    //页码
    private int page = 1;
    //每页记录数
    private int pageSize = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_monitor_fragment, null);
        ButterKnife.bind(this, view);
        departmentId = SPUtil.getHClientUser(getActivity()).getDepartmentId();
        initView();
        initListener();
        return view;
    }

    private void initListener() {

    }

    private void initView() {
        mAdapter.setOnLoadListener(this);
        initData(1);
        mPullToRefresh.setAdapter(mAdapter);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);

        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                int page = 1;
                ApiManager.getInstance().hClientAccountApi.getServiceInsides(SPUtil.getHClientUser(getActivity()).getDepartmentId(), 0, page, pageSize,
                        new DefaultCallback<PageData<ServiceInside>>(getActivity(), new Business<PageData<ServiceInside>>() {
                            @Override
                            public void handleData(PageData<ServiceInside> data) throws Exception {
                                mServiceInsidePageData = data;
                                if (data.getCount() > 0) {
                                    mAdapter.clearAddSetData(data.getValue());
                                    mPullToRefresh.onRefreshComplete();
                                } else {
                                    mPullToRefresh.onRefreshComplete();
                                }
                            }
                        }), getRequestTag());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData(++page);
            }
        });
    }

    private void initData(final int page) {
        ApiManager.getInstance().hClientAccountApi.getServiceInsides(SPUtil.getHClientUser(getActivity()).getDepartmentId(), 0, page, pageSize,
                new DefaultCallback<PageData<ServiceInside>>(getActivity(), new Business<PageData<ServiceInside>>() {
                    @Override
                    public void handleData(PageData<ServiceInside> data) throws Exception {
                        mServiceInsidePageData = data;
                        if (data.getCount() > 0) {
                            mAdapter.addData(data.getValue());
                            mPullToRefresh.onRefreshComplete();
                        } else {
                            mPullToRefresh.onRefreshComplete();
                        }
//                        setTvTitle(data);

                    }
                }), getRequestTag());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void begin(ServiceInside serviceInside) {

    }
}
