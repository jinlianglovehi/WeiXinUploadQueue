package cn.ihealthbaby.weitaixin.ui.mine;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Version;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.DownloadAPKUtils;
import cn.ihealthbaby.weitaixin.Global;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
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


    @OnClick(R.id.rl_update)
    public void rl_update() {
        pullV();
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





    public void pullV(){
        long downloadAPK = SPUtil.getDownloadAPK(this);

        Calendar cd = Calendar.getInstance();
        long postTime = cd.getTimeInMillis();

        if (postTime - downloadAPK >= 1 * 3600 * 1000) {
            //软件类型 0 微胎心会员端安卓版, 1 微胎心院内安卓版, 2 微胎心会员端iOS版
            ApiManager.getInstance().versionApi.checkVersion(getversionName(), 0,
                    new DefaultCallback<Version>(this, new AbstractBusiness<Version>() {
                        @Override
                        public void handleData(Version data) {
                            int flag = data.getFlag();
                            LogUtil.d("flag", "flag==>" + flag);
                            switch (flag) {
                                case 0:  //0 已是最新, 1 有新版本更新,2 需强制升级
                                    ToastUtil.show(AboutActivity.this, "已是最新版本");
                                    break;

                                case 1:
                                    Global.downloadURL = data.getUpdateUrl();
                                    DownloadAPKUtils downloadAPKUtils = new DownloadAPKUtils(AboutActivity.this,false);
                                    downloadAPKUtils.showDownDialog(true);

    //                        Intent intent = new Intent();
    //                        intent.setAction("android.intent.action.VIEW");
    //                        Uri content_url = Uri.parse(data.getUpdateUrl());
    //                        intent.setData(content_url);
    //                        startActivity(intent);

    //                        DownloadAPK downloadAPK = new DownloadAPK(MeMainFragmentActivity.this);
    //                        downloadAPK.apkUrl=data.getUpdateUrl();
    //                        downloadAPK.checkUpdateInfo();
                                    break;

                                case 2:
                                    Global.downloadURL = data.getUpdateUrl();
                                    DownloadAPKUtils downloadAPKUtils2 = new DownloadAPKUtils(AboutActivity.this, true);
                                    downloadAPKUtils2.showDownDialog(true);
                                    break;
                            }
                        }
                    }), getRequestTag());
            SPUtil.saveDownloadAPK(this, Calendar.getInstance().getTimeInMillis());
        }
    }


    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getversionName() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
