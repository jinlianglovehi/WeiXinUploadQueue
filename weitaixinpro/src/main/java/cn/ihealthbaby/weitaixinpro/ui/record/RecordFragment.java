package cn.ihealthbaby.weitaixinpro.ui.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.ihealthbaby.client.model.HClientUser;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;
import cn.ihealthbaby.weitaixinpro.ui.monitor.LocalRecordRecyclerViewAdapter;

/**
 */
public class RecordFragment extends BaseFragment {
	private static final int MONITORING = 1;
	private static final int PAGE_SIZE = 20;
	private final static String TAG = "RecordFragment";
	/**
	 * 起始页码 从1开始
	 */
	private static final int FIRST_PAGE = 1;
	public HClientUser hClientUser;
	public int currentPage;
	public int count;
	private ArrayList<Record> list;
	private boolean loading;
	private LocalRecordRecyclerViewAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private LinearLayoutManager layoutManager;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter.notifyDataSetChanged();
		}
	};

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
		layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
		recyclerView.setLayoutManager(layoutManager);
		list = new ArrayList<Record>();
		adapter = new LocalRecordRecyclerViewAdapter(getActivity().getApplicationContext(), list);
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
				if (layoutManager.findLastVisibleItemPosition() > itemCount - 5) {
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

	private void request(final int page) {
		LogUtil.d(TAG, "page:%s", page);
		if (count != 0 && page >= (count / PAGE_SIZE) + 1) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					List<Record> records = RecordBusinessDao.getInstance(getActivity().getApplicationContext()).queryPagedRecord(page, PAGE_SIZE, Record.UPLOAD_STATE_LOCAL, Record.UPLOAD_STATE_UPLOADING);
					list.addAll(records);
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
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
