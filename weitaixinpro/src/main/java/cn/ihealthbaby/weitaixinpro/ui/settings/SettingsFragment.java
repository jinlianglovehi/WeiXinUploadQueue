package cn.ihealthbaby.weitaixinpro.ui.settings;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.model.HClientUser;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;

/**
 * @author by kang on 2015/9/9.
 */
public class SettingsFragment extends BaseFragment {


    static SettingsFragment instance;
    @Bind(R.id.back)
    RelativeLayout mBack;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.function)
    TextView mFunction;
    @Bind(R.id.rl_monitor_settings)
    RelativeLayout mRlMonitorSettings;
    @Bind(R.id.rl_host_id)
    RelativeLayout mRlHostId;
    @Bind(R.id.rl_probe_sn)
    RelativeLayout mRlProbeSn;
    @Bind(R.id.rl_hospital_name)
    RelativeLayout mRlHospitalName;
    @Bind(R.id.rl_sections)
    RelativeLayout mRlSections;
    @Bind(R.id.rl_version)
    RelativeLayout mRlVersion;
    @Bind(R.id.tv_host_id)
    TextView mTvHostId;
    @Bind(R.id.tv_index_sn)
    TextView mTvIndexSn;
    @Bind(R.id.tv_hospital)
    TextView mTvHospital;
    @Bind(R.id.tv_department)
    TextView mTvDepartment;
    @Bind(R.id.tv_version)
    TextView mTvVersion;


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
        return view;
    }


    private void initView() {
        mTitleText.setText("系统设置");
        mBack.setVisibility(View.INVISIBLE);
        mRlMonitorSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MonitorSetActivity.class);
                startActivity(intent);
            }
        });

        HClientUser hClientUser = SPUtil.getHClientUser(getActivity());
        mTvHostId.setText(hClientUser.getDeviceId() + "");
        mTvIndexSn.setText(hClientUser.getSerialnum());
        mTvHospital.setText(hClientUser.getHospitalName());
        mTvDepartment.setText(hClientUser.getDepartmentName());
        mTvVersion.setText(getVersion());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.version);
        }
    }

}
