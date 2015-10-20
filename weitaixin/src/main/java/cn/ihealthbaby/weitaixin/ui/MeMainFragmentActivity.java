package cn.ihealthbaby.weitaixin.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Version;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.DownloadAPK;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.home.HomePageFragment;
import cn.ihealthbaby.weitaixin.ui.login.InfoEditActivity;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.ui.mine.WoInfoFragment;
import cn.ihealthbaby.weitaixin.ui.mine.event.LogoutEvent;
import cn.ihealthbaby.weitaixin.ui.monitor.MonitorFragment;
import cn.ihealthbaby.weitaixin.ui.record.RecordFragment;
import de.greenrobot.event.EventBus;

/**
 * Created by Think on 2015/8/13
 */
public class MeMainFragmentActivity extends BaseActivity {
    public HomePageFragment homePageFragment;
    public MonitorFragment monitorFragment;
    public RecordFragment recordFragment;
    public WoInfoFragment woInfoFragment;
    public Fragment oldFragment;
    @Bind(R.id.iv_tab_01)
    ImageView iv_tab_01;
    @Bind(R.id.iv_tab_02)
    ImageView iv_tab_02;
    @Bind(R.id.iv_tab_03)
    ImageView iv_tab_03;
    @Bind(R.id.iv_tab_04)
    ImageView iv_tab_04;
    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.ll_tab_home)
    LinearLayout mLlTabHome;
    @Bind(R.id.ll_tab_monitor)
    LinearLayout mLlTabMonitor;
    @Bind(R.id.ll_tab_record)
    LinearLayout mLlTabRecord;
    @Bind(R.id.ll_tab_profile)
    LinearLayout mLlTabProfile;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_main_fragment);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        showTabFirst();

        pullV();

    }


    public void pullV(){
        //软件类型 0 微胎心会员端安卓版, 1 微胎心院内安卓版, 2 微胎心会员端iOS版
        ApiManager.getInstance().versionApi.checkVersion(getversionName(), 0,
                new DefaultCallback<Version>(this, new AbstractBusiness<Version>() {
            @Override
            public void handleData(Version data) {
                int flag = data.getFlag();
                LogUtil.d("flag", "flag==>"+flag);
                switch(flag){
                    case 0:  //0 已是最新, 1 有新版本更新,2 需强制升级

                        break;

                    case 1:
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(data.getUpdateUrl());
                        intent.setData(content_url);
                        startActivity(intent);

//                        DownloadAPK downloadAPK = new DownloadAPK(MeMainFragmentActivity.this);
//                        downloadAPK.apkUrl=data.getUpdateUrl();
//                        downloadAPK.checkUpdateInfo();
                        break;

                    case 2:
                        Intent intent2 = new Intent();
                        intent2.setAction("android.intent.action.VIEW");
                        Uri content_url2 = Uri.parse(data.getUpdateUrl());
                        intent2.setData(content_url2);
                        startActivity(intent2);
                        break;
                }
            }
        }), getRequestTag());

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

    /**
     * 获得版本号
     */
    public int getVerCode(Context context){
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo("cn.ihealthbaby.weitaixin", 0).versionCode;
            LogUtil.d("verCode", "verCode==>" + verCode);
        } catch (Exception e) {
            Log.e("版本号获取异常", e.getMessage());
        }
        return verCode;
    }



    public void onEventMainThread(LogoutEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showTabFirst() {
        iv_tab_01.setSelected(true);
        homePageFragment = HomePageFragment.getInstance();
        fragmentManager = getFragmentManager();
        showFragment(R.id.container, homePageFragment);
    }



    @OnClick(R.id.ll_tab_home)
    public void iv_tab_01() {
        if (showTab(iv_tab_01)) {
            if (SPUtil.isLogin(this)) {
                homePageFragment = HomePageFragment.getInstance();
                homePageFragment.getNumber();
                homePageFragment.startAnim();
//                pullV();
                showFragment(R.id.container, homePageFragment);
            }
        }
    }



    @OnClick(R.id.ll_tab_monitor)
    public void iv_tab_02() {
        if (showTab(iv_tab_02)) {
            if (monitorFragment == null) {
                monitorFragment = new MonitorFragment();
            }
            showFragment(R.id.container, monitorFragment);
        }
    }



    @OnClick(R.id.ll_tab_record)
    public void iv_tab_03() {
        if (showTab(iv_tab_03)) {
            if (SPUtil.isLogin(this)) {
                recordFragment = RecordFragment.getInstance();
                showFragment(R.id.container, recordFragment);
            }
        }
    }


    @OnClick(R.id.ll_tab_profile)
    public void iv_tab_04() {
        if (showTab(iv_tab_04)) {
            if (SPUtil.isLogin(this)) {
                woInfoFragment = WoInfoFragment.getInstance();
                showFragment(R.id.container, woInfoFragment);
            }
        }
    }


    public boolean showTab(ImageView imageView) {
        if (!SPUtil.isLogin(this)) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return false;
        }

        if (SPUtil.isIsInit(this)) {
            Intent intentAct = new Intent(getApplicationContext(), InfoEditActivity.class);
            startActivity(intentAct);
            return false;
        }

        iv_tab_01.setSelected(false);
        iv_tab_02.setSelected(false);
        iv_tab_03.setSelected(false);
        iv_tab_04.setSelected(false);
        imageView.setSelected(true);
        return true;
    }


    private void showFragment(int container, Fragment fragment/*, int animIn, int animOut*/) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        show(container, fragmentTransaction, fragment);
        fragmentTransaction.commit();
    }

    private void show(int container, FragmentTransaction fragmentTransaction, Fragment fragment) {
        if (fragment == null) {
            return;
        }
        if (!fragment.isAdded()) {
            if (oldFragment != null) {
                fragmentTransaction.hide(oldFragment);
	            if (oldFragment instanceof MonitorFragment) {
		            final MonitorFragment monitorFragment = (MonitorFragment) this.oldFragment;
		            monitorFragment.stopMonitor();
	            }
            }
            fragmentTransaction.add(container, fragment);
        } else if (oldFragment != fragment) {
            fragmentTransaction.hide(oldFragment);
            fragmentTransaction.show(fragment);
        }
        oldFragment = fragment;
//        LogUtil.d("ChildCount==", "ChildCount= %s", this.container.getChildCount());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (recordFragment != null) {
			recordFragment.onActivityResult(requestCode, resultCode, data);
		}
	}
}










