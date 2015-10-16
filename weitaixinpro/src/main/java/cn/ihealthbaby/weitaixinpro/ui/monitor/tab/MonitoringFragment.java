package cn.ihealthbaby.weitaixinpro.ui.monitor.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;
import de.greenrobot.event.EventBus;

/**
 */
public class MonitoringFragment extends BaseFragment {
	private static final int MONITORING = 1;
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
	private MonitoringRecyclerViewAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private LinearLayoutManager layoutManager;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		swipeRefreshLayout = ((SwipeRefreshLayout) inflater.inflate(R.layout.fragment_swipe_refresh_recycler, null));
		recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
		return swipeRefreshLayout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				                                          android.R.color.holo_green_light,
				                                          android.R.color.holo_orange_light,
				                                          android.R.color.holo_red_light);
		layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
		recyclerView.setLayoutManager(layoutManager);
		list = new ArrayList<ServiceInside>();
		adapter = new MonitoringRecyclerViewAdapter(getActivity().getApplicationContext(), list);
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
				layoutManager.findFirstCompletelyVisibleItemPosition();
				swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
				if (itemCount > PAGE_SIZE && layoutManager.findLastVisibleItemPosition() > itemCount - 5) {
					request(currentPage + 1);
				}
			}
		});
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				reset();
			}
		});
		reset();
	}

	public void reset() {
		list.clear();
		currentPage = 0;
		count = 0;
		request(FIRST_PAGE);
	}

	private void request(int page) {
		LogUtil.d(TAG, "page:%s", page);
		if (count != 0 && page >= (count / PAGE_SIZE) + 1) {
			return;
		}
		hClientUser = SPUtil.getHClientUser(getActivity().getApplicationContext());
		if (hClientUser != null) {
			ApiManager.getInstance().hClientAccountApi.getServiceInsides(hClientUser.getDepartmentId(), MONITORING, page, PAGE_SIZE, new DefaultCallback<PageData<ServiceInside>>(getActivity().getApplicationContext(), new AbstractBusiness<PageData<ServiceInside>>() {
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
					EventBus.getDefault().post(new CountEvent(CountEvent.TYPE_MONITORING, count));
					currentPage = data.getPage();
					List<ServiceInside> dataList = data.getValue();
					list.addAll(dataList);
					adapter.notifyDataSetChanged();
				}
			}), getRequestTag());
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (swipeRefreshLayout.isRefreshing()) {
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
