package cn.ihealthbaby.weitaixinpro.ui;

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
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.WeiTaiXinProApplication;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.ui.home.HomePageFragment;
import cn.ihealthbaby.weitaixinpro.ui.monitor.MonitorFragment;
import cn.ihealthbaby.weitaixinpro.ui.record.RecordFragment;
import cn.ihealthbaby.weitaixinpro.ui.settings.SettingsFragment;

/**
 * Created by Think on 2015/8/13.
 */
public class MeMainFragmentActivity extends BaseActivity {

    @Bind(R.id.iv_tab_02)
    ImageView iv_tab_02;
    @Bind(R.id.iv_tab_03)
    ImageView iv_tab_03;
    @Bind(R.id.iv_tab_04)
    ImageView iv_tab_04;
    @Bind(R.id.container)
    FrameLayout container;
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
        iv_tab_02.setSelected(true);
        homePageFragment = HomePageFragment.getInstance();
        fragmentManager = getFragmentManager();
        showFragment(R.id.container, homePageFragment);
    }


    public HomePageFragment homePageFragment;
    public MonitorFragment monitorFragment;
    public RecordFragment recordFragment;
    public SettingsFragment mSettingsFragment;
    public Fragment oldFragment;


    @OnClick(R.id.ll_tab_monitor)
    public void iv_tab_02() {
        showTab(iv_tab_02);
        if (WeiTaiXinProApplication.getInstance().isLogin) {
            if (monitorFragment == null) {
                monitorFragment = MonitorFragment.getInstance();
            }
            showFragment(R.id.container, monitorFragment);
        }
    }

    @OnClick(R.id.ll_tab_record)
    public void iv_tab_03() {
        showTab(iv_tab_03);
        if (WeiTaiXinProApplication.getInstance().isLogin) {
            recordFragment = RecordFragment.getInstance();
            showFragment(R.id.container, recordFragment);
        }
    }


    @OnClick(R.id.iv_tab_04)
    public void iv_tab_04() {
        showTab(iv_tab_04);
        if (WeiTaiXinProApplication.getInstance().isLogin) {
            mSettingsFragment = SettingsFragment.getInstance();
            showFragment(R.id.container, mSettingsFragment);
        }
    }


    public void showTab(ImageView imageView) {
        iv_tab_02.setSelected(false);
        iv_tab_03.setSelected(false);
        iv_tab_04.setSelected(false);
        imageView.setSelected(true);
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










