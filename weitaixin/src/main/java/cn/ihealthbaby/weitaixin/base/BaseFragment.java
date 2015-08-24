package cn.ihealthbaby.weitaixin.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.android.volley.RequestQueue;
import com.orhanobut.logger.Logger;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;

/**
 * Created by liuhongjian on 15/7/24 11:47.
 */
public class BaseFragment extends Fragment {
    protected RequestQueue requestQueue;
    protected String TAG = getClass().getName();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = ConnectionManager.getInstance().getRequestQueue(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("hello");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requestQueue.cancelAll(TAG);
    }
}

