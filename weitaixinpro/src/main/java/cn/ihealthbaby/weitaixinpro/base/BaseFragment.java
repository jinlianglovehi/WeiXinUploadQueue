package cn.ihealthbaby.weitaixinpro.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * @author liuhongjian on 15/7/24 11:47.
 */
public class BaseFragment extends Fragment {
    protected RequestQueue requestQueue;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = ConnectionManager.getInstance().getRequestQueue(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d(this.getClass().getName(), "requestQueue.cancelAll:%s", getRequestTag());
        requestQueue.cancelAll(getRequestTag());
    }

    protected Object getRequestTag() {
        return this;
    }
}

