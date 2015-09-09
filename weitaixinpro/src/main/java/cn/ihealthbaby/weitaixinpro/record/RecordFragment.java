package cn.ihealthbaby.weitaixinpro.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ihealthbaby.weitaixinpro.R;

/**
 * @author by kang on 2015/9/9.
 */
public class RecordFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.record_fragment, null);
    }
}
