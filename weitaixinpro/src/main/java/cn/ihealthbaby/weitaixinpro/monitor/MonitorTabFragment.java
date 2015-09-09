package cn.ihealthbaby.weitaixinpro.monitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ihealthbaby.weitaixinpro.BaseTabFragment;
import cn.ihealthbaby.weitaixinpro.R;

/**
 * @author by kang on 2015/9/9.
 */
public class MonitorTabFragment extends BaseTabFragment {

    private boolean IsViewInited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_tab_container, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!IsViewInited) {
            IsViewInited = true;
            initView();
        }
    }

    private void initView() {
        replaceFragment(new Monitorfragment(), false);
    }
}
