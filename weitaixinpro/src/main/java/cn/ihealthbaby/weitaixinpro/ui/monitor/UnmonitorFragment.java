package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.HClientUser;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.client.model.ServiceInside;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.base.SwipeRefreshRecyclerViewFragment;

/**
 */
public class UnmonitorFragment extends SwipeRefreshRecyclerViewFragment {
	private static final int MONITORING = 1;
	private static final int UNMONITOR = 0;
	private static final int PAGE_SIZE = 20;
	private final static String TAG = "MonitoringFragment";
	/**
	 * 起始页码 从1开始
	 */
	private static final int FIRST_PAGE = 1;
	public HClientUser hClientUser;
	public int currentPage;
	public int count;
	private List<ServiceInside> list;
	private boolean loading;
	private RecyclerViewAdapter adapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
		recyclerView.setLayoutManager(layoutManager);
		list = new ArrayList<ServiceInside>();
		adapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), list);
		recyclerView.setAdapter(adapter);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				int itemCount = adapter.getItemCount();
				LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
				linearLayoutManager.findFirstCompletelyVisibleItemPosition();
				swipeRefreshLayout.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
				if (linearLayoutManager.findLastVisibleItemPosition() > itemCount - 5) {
					request(currentPage + 1);
				}
			}
		});
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				LogUtil.d(TAG, "onrefresh isRefreshing %s", swipeRefreshLayout.isRefreshing());
				reset();
			}
		});
		reset();
	}

	public void reset() {
		swipeRefreshLayout.setEnabled(true);
		swipeRefreshLayout.setRefreshing(true);
		request(FIRST_PAGE);
	}

	private void request(int page) {
		LogUtil.d(TAG, "page:%s", page);
		if (count != 0 && page >= (count / PAGE_SIZE) + 1) {
			return;
		}
		hClientUser = SPUtil.getHClientUser(getActivity().getApplicationContext());
		if (hClientUser != null) {
			ApiManager.getInstance().hClientAccountApi.getServiceInsides(hClientUser.getDepartmentId(), UNMONITOR, page, PAGE_SIZE, new DefaultCallback<PageData<ServiceInside>>(getActivity().getApplicationContext(), new AbstractBusiness<PageData<ServiceInside>>() {
				@Override
				public void handleResult(Result<PageData<ServiceInside>> result) {
					super.handleResult(result);
					if (swipeRefreshLayout.isRefreshing()) {
						swipeRefreshLayout.setRefreshing(false);
					}
				}

				@Override
				public void handleData(PageData<ServiceInside> data) {
					count = data.getCount();
					currentPage = data.getPage();
					List<ServiceInside> dataList = data.getValue();
					list.addAll(dataList);
					adapter.notifyDataSetChanged();
				}
			}), getRequestTag());
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
