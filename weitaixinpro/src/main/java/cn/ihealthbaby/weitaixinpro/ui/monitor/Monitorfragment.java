package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;

/**
 * Created by liuhongjian on 15/8/12 17:52.
 */
public class MonitorFragment extends BaseFragment implements View.OnClickListener {

    private static MonitorFragment instance;
    @Bind(R.id.iv_no_monitor)
    ImageView mIvNoMonitor;
    @Bind(R.id.rl_no_monitor)
    RelativeLayout mRlNoMonitor;
    @Bind(R.id.iv_alreay_monitor)
    ImageView mIvAlreayMonitor;
    @Bind(R.id.rl_already_monitor)
    RelativeLayout mRlAlreadyMonitor;
    @Bind(R.id.lv_monitor)
    ListView mLvMonitor;


    public static MonitorFragment getInstance() {
        if (instance == null) {
            instance = new MonitorFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.monitor_fragment, null);
        ButterKnife.bind(this, view);
        initView();
        initListener();
        return view;
    }

    private void initListener() {
        mRlAlreadyMonitor.setOnClickListener(this);
        mRlNoMonitor.setOnClickListener(this);
    }

    private void initView() {
        mIvNoMonitor.setVisibility(View.VISIBLE);
        mIvAlreayMonitor.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_already_monitor:

                break;

            case R.id.rl_no_monitor:
                break;
        }
    }
}



