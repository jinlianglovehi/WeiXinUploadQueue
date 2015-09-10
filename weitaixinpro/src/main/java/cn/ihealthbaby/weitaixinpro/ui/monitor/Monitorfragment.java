package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;

/**
 * Created by liuhongjian on 15/8/12 17:52.
 */
public class MonitorFragment extends BaseFragment {

    private static MonitorFragment instance;

    public static MonitorFragment getInstance() {
        if (instance == null) {
            instance = new MonitorFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.monitor_fragment, null);
    }
}



