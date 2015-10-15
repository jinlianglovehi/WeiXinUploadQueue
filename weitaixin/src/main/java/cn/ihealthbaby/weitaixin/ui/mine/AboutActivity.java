package cn.ihealthbaby.weitaixin.ui.mine;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.WelcomeActivity;
import cn.ihealthbaby.weitaixin.ui.mine.event.WelcomeEvent;
import de.greenrobot.event.EventBus;

/**
 * @author by kang on 2015/8/29.
 */
public class AboutActivity extends BaseActivity {


    @Bind(R.id.back)
    RelativeLayout mBack;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.function)
    TextView mFunction;
    @Bind(R.id.tv_subtitle)
    TextView mTvSubtitle;
    @Bind(R.id.rl_welcome)
    RelativeLayout mRlWelcome;
    @Bind(R.id.rl_function)
    RelativeLayout mRlFunction;
    @Bind(R.id.rl_use)
    RelativeLayout mRlUse;
    @Bind(R.id.tv_version_name)
    TextView mTvVersionName;
    @Bind(R.id.tv_message_count)
    ImageView mTvMessageCount;
    @Bind(R.id.rl_update)
    RelativeLayout mRlUpdate;
    @Bind(R.id.rl_protocol)
    RelativeLayout mRlProtocol;

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
        mTvSubtitle.setText(getString(R.string.subtitle) + " " + getVersion());
    }


    @OnClick(R.id.rl_welcome)
    public void welcomeOnclick() {
        SPUtil.setNoFirstStartApp(this, false);
        Intent intent = new Intent(this, Welcome2Activity.class);
        startActivity(intent);
//        EventBus.getDefault().post(new WelcomeEvent());
    }

    @OnClick(R.id.rl_function)
    public void rlFunctionOnclick() {
        Intent intent = new Intent(this, FeatureActiviy.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_protocol)
    public void rlProtocolOnclick() {
        Intent intent = new Intent(this, UseProtocolActiviy.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_use)
    public void rlUseOnclick() {
        Intent intent = new Intent(this, UseProcessActiviy.class);
        startActivity(intent);
    }

    @OnClick(R.id.back)
    public void backOnclick() {
        finish();
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
