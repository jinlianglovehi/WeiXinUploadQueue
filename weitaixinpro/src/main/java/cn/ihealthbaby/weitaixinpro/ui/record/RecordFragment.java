package cn.ihealthbaby.weitaixinpro.ui.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixinpro.R;

import cn.ihealthbaby.weitaixinpro.base.BaseFragment;

public class RecordFragment extends BaseFragment {

    static RecordFragment instance;

    public static RecordFragment getInstance() {
        if (instance == null) {
            instance = new RecordFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.record_fragment, null);
    }
}