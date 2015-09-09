package cn.ihealthbaby.weitaixinpro.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ihealthbaby.weitaixinpro.BaseTabFragment;

/**
 * @author by kang on 2015/9/9.
 */
public class SettingsTabFragment extends BaseTabFragment {
    private boolean IsViewInited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(cn.ihealthbaby.weitaixinpro.R.layout.main_tab_container, null);
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
        replaceFragment(new SettingsFragment(), false);
    }
}
