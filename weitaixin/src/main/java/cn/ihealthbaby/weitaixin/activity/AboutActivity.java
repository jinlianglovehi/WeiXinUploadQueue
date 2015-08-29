package cn.ihealthbaby.weitaixin.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

/**
 * @author by kang on 2015/8/29.
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout mBack;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.rl_welcome)
    RelativeLayout mRlWelcome;
    @Bind(R.id.rl_feature)
    RelativeLayout mRlFeature;
    @Bind(R.id.rl_use_process)
    RelativeLayout mRlUseProcess;
    @Bind(R.id.rl_update)
    RelativeLayout mRlUpdate;
    @Bind(R.id.rl_use_protocol)
    RelativeLayout mRlUseProtocol;
    @Bind(R.id.tv_version)
    TextView mTvVersion;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        mTitleText.setText(getString(R.string.about_title));
        initView();

    }

    private void initView() {
        mTvVersion.setText(getVersion());
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.version);
        }
    }
}
