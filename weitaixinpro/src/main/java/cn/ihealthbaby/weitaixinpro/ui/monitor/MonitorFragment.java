package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;

/**
 * @author by kang on 2015/9/11.
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
    @Bind(R.id.tv_host_name)
    TextView mTvHostName;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.act_container)
    FrameLayout mActContainer;

    private android.app.FragmentManager mFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean clickNoMonitor = true;

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
        mFragmentManager = getFragmentManager();
        initView();
        initListener();
        return view;
    }

    private void initListener() {
        mRlNoMonitor.setOnClickListener(this);
        mRlAlreadyMonitor.setOnClickListener(this);
    }

    private void initView() {
        fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.act_container, new NoMonitorFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
                if (clickNoMonitor) {
                    clickNoMonitor = !clickNoMonitor;
                    fragmentTransaction.replace(R.id.act_container, new AlreadyMonitorFragment());
                    fragmentTransaction.commit();
                }
                break;
            case R.id.rl_no_monitor:
                if (!clickNoMonitor) {
                    clickNoMonitor = !clickNoMonitor;
                    fragmentTransaction.replace(R.id.act_container, new NoMonitorFragment());
                    fragmentTransaction.commit();
                }
                break;
        }
    }
}
