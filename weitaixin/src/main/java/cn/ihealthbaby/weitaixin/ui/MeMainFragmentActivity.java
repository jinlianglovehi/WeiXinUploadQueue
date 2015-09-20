package cn.ihealthbaby.weitaixin.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.home.HomePageFragment;
import cn.ihealthbaby.weitaixin.ui.login.InfoEditActivity;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.ui.mine.WoInfoFragment;
import cn.ihealthbaby.weitaixin.ui.monitor.MonitorFragment;
import cn.ihealthbaby.weitaixin.ui.record.RecordFragment;

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
        showTabFirst();
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
                showFragment(R.id.container, homePageFragment);
            }
        }
    }



    @OnClick(R.id.ll_tab_monitor)
    public void iv_tab_02() {
        if (showTab(iv_tab_02)) {
            if (SPUtil.isLogin(this)) {
                if (SPUtil.getUser(this).getServiceInfo() != null && !WeiTaiXinApplication.getInstance().isMonitoring()) {
                    if (monitorFragment == null) {
                        monitorFragment = new MonitorFragment();
                    }
                    showFragment(R.id.container, monitorFragment);
                }
            }
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
            }
            fragmentTransaction.add(container, fragment);
        } else if (oldFragment != fragment) {
            fragmentTransaction.hide(oldFragment);
            fragmentTransaction.show(fragment);
        }
        oldFragment = fragment;
        LogUtil.d("ChildCount==", "ChildCount= %s", this.container.getChildCount());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (recordFragment != null) {
            recordFragment.onActivityResult(requestCode, resultCode, data);
        }
    }


}










