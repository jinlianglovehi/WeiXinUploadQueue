package cn.ihealthbaby.weitaixinpro.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixinpro.R;

/**
 * Created by liuhongjian on 15/9/23 23:21.
 */
public class SwipeRefreshRecyclerViewFragment extends BaseFragment {
	protected RecyclerView.LayoutManager layoutManager;
	protected RecyclerView recyclerView;
	protected SwipeRefreshLayout swipeRefreshLayout;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		swipeRefreshLayout = ((SwipeRefreshLayout) inflater.inflate(R.layout.fragment_swipe_refresh_recycler, null));
		return swipeRefreshLayout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
