package cn.ihealthbaby.weitaixinpro.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * @author liuhongjian on 15/7/24 11:47.
 */
public class BaseFragment extends Fragment {
	protected RequestQueue requestQueue;
	protected String TAG = getClass().getSimpleName();

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		LogUtil.d(TAG, "onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		requestQueue = ConnectionManager.getInstance().getRequestQueue(getActivity());
	}

	@Override
	public void onResume() {
		LogUtil.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		LogUtil.d(TAG, "onDestroyView");
		super.onDestroyView();
		LogUtil.d(this.getClass().getName(), "requestQueue.cancelAll:%s", getRequestTag());
		requestQueue.cancelAll(getRequestTag());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "onCreate");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtil.d(TAG, "onActivityCreated");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogUtil.d(TAG, "onAttach");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		LogUtil.d(TAG, "onDetach");
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.d(TAG, "onPause");
	}

	@Override
	public void onStart() {
		super.onStart();
		LogUtil.d(TAG, "onStart");
	}

	@Override
	public void onStop() {
		super.onStop();
		LogUtil.d(TAG, "onStop");
	}

	protected Object getRequestTag() {
		return this;
	}
}

