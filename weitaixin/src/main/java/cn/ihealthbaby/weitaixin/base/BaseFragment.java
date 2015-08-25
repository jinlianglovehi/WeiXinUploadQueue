package cn.ihealthbaby.weitaixin.base;

import android.os.Bundle;
import android.app.Fragment;
import android.view.View;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;

/**
 * @author liuhongjian on 15/7/24 11:47.
 */
public class BaseFragment extends Fragment {
    protected RequestQueue requestQueue;
    protected Object TAG = this;

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
        requestQueue.cancelAll(TAG);
    }
}

