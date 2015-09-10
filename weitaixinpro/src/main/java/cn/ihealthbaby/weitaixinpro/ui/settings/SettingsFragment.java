package cn.ihealthbaby.weitaixinpro.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;

/**
 * @author by kang on 2015/9/9.
 */
public class SettingsFragment extends BaseFragment implements View.OnClickListener {


    static SettingsFragment instance;
    @Bind(R.id.back)
    RelativeLayout mBack;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.function)
    TextView mFunction;
    @Bind(R.id.ll_set_system_01)
    RelativeLayout mLlSetSystem01;
    @Bind(R.id.ll_set_system_02)
    RelativeLayout mLlSetSystem02;
    @Bind(R.id.ll_set_system_03)
    RelativeLayout mLlSetSystem03;
    @Bind(R.id.ll_set_system_04)
    RelativeLayout mLlSetSystem04;
    @Bind(R.id.slide_switch_upload)
    ImageView mSlideSwitchUpload;
    @Bind(R.id.ll_set_system_05)
    RelativeLayout mLlSetSystem05;


    public static SettingsFragment getInstance() {
        if (instance == null) {
            instance = new SettingsFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, null);
        ButterKnife.bind(this, view);
        initView();
        initListener();
        return view;
    }

    private void initListener() {
        mLlSetSystem03.setOnClickListener(this);
        mLlSetSystem04.setOnClickListener(this);
    }

    private void initView() {
        mTitleText.setText("系统设置");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_set_system_03:
                Intent intent = new Intent(getActivity(), MonitorSetActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_set_system_04:
                break;
            case R.id.ll_set_system_05:
                break;
        }
    }
}
