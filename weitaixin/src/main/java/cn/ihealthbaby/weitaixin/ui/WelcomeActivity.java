package cn.ihealthbaby.weitaixin.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.ImageTool;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.service.AdviceSettingService;
import cn.ihealthbaby.weitaixin.ui.login.InfoEditActivity;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.ui.mine.GradedActivity;

/**
 * Created by chenweihua on 2015/9/21.
 */
public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.viewPagerWelcome) ViewPager viewPagerWelcome;
    @Bind(R.id.ivWelcomeStart) ImageView ivWelcomeStart;
    @Bind(R.id.llDot) LinearLayout llDot;


    private ArrayList<View> mListViews;
    private StartPagerAdapter startAdapter;

    private LayoutInflater mInflater;
    private ImageView view01;
    private ImageView view02;
    private ImageView view03;
    private ImageView view04;
    private View view05;
    private ImageView ivWelcome05;
    private TextView tvNextAction;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_welcome);
        ButterKnife.bind(this);


        ivWelcomeStart.setVisibility(View.VISIBLE);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SPUtil.isNoFirstStartApp(WelcomeActivity.this)) {
                    if (SPUtil.getUser(WelcomeActivity.this) != null) {
//                        CustomDialog customDialog = null;
//                        try {
//                            customDialog = new CustomDialog();
//                            Dialog dialog = customDialog.createDialog1(WelcomeActivity.this, "刷新用户数据...");
//                            dialog.show();
//
//                            Intent intentAdvice = new Intent(getApplicationContext(), AdviceSettingService.class);
//                            startService(intentAdvice);
//
//                            final CustomDialog finalCustomDialog = customDialog;
//                            ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(WelcomeActivity.this, new AbstractBusiness<User>() {
//                                @Override
//                                public void handleData(User data) {
//                                    SPUtil.saveUser(WelcomeActivity.this, data);
//                                    if (finalCustomDialog != null) {
//                                        finalCustomDialog.dismiss();
//                                    }
//                                }
//
//                                @Override
//                                public void handleException(Exception e) {
//                                    if (finalCustomDialog != null) {
//                                        finalCustomDialog.dismiss();
//                                    }
//                                    Intent intentHasRiskscore = new Intent(WelcomeActivity.this, LoginActivity.class);
//                                    startActivity(intentHasRiskscore);
//                                    finish();
//                                }
//                            }), getRequestTag());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            if (customDialog != null) {
//                                customDialog.dismiss();
//                            }
//                        }


                        if (SPUtil.isLogin(WelcomeActivity.this)) {
                            if (SPUtil.getUser(WelcomeActivity.this).getIsInit()) {
//                                ivWelcomeStart.setVisibility(View.GONE);
                                Intent intentIsInit = new Intent(WelcomeActivity.this, InfoEditActivity.class);
                                startActivity(intentIsInit);
                                return;
                            }

                            if (!SPUtil.getUser(WelcomeActivity.this).getHasRiskscore()) {
                                if (SPUtil.getHospitalId(WelcomeActivity.this) != -1) {
//                                    ivWelcomeStart.setVisibility(View.GONE);
                                    Intent intentHasRiskscore = new Intent(WelcomeActivity.this, GradedActivity.class);
                                    startActivity(intentHasRiskscore);
                                    return;
                                }
                            }

                            Intent intent = new Intent(WelcomeActivity.this, MeMainFragmentActivity.class);
                            startActivity(intent);
//                            ivWelcomeStart.setVisibility(View.GONE);
                            finish();
                            return;
                        }
                    } else {
//                        ivWelcomeStart.setVisibility(View.GONE);
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                } else {
                    ivWelcomeStart.setVisibility(View.GONE);
                    initView();
                }


                if (!SPUtil.isNoFirstStartApp(WelcomeActivity.this)) {
                    SPUtil.setNoFirstStartApp(WelcomeActivity.this);
                }
            }
        }, 3000);


        initDataView();
    }


    public void initDataView() {

        mListViews = new ArrayList<View>();
        mInflater = this.getLayoutInflater();

        view01 = (ImageView) mInflater.inflate(R.layout.viewpager_item, null);
        view02 = (ImageView) mInflater.inflate(R.layout.viewpager_item, null);
        view03 = (ImageView) mInflater.inflate(R.layout.viewpager_item, null);
        view04 = (ImageView) mInflater.inflate(R.layout.viewpager_item, null);
        view05 = (View) mInflater.inflate(R.layout.viewpager_item_last, null);
        ivWelcome05 = (ImageView) view05.findViewById(R.id.ivWelcome05);
        tvNextAction = (TextView) view05.findViewById(R.id.tvNextAction);


        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();


//        Bitmap bitmap01 = ImageTool.decodeSampledBitmapFromResource(getResources(), R.drawable.welcome_01, 108, 192);
//        Bitmap bitmap02 = ImageTool.decodeSampledBitmapFromResource(getResources(), R.drawable.welcome_02, 108, 192);
//        Bitmap bitmap03 = ImageTool.decodeSampledBitmapFromResource(getResources(), R.drawable.welcome_03, 108, 192);
//        Bitmap bitmap04 = ImageTool.decodeSampledBitmapFromResource(getResources(), R.drawable.welcome_04, 108, 192);
//        Bitmap bitmap05 = ImageTool.decodeSampledBitmapFromResource(getResources(), R.drawable.welcome, 108, 192);
//
//        view01.setImageBitmap(bitmap01);
//        view02.setImageBitmap(bitmap02);
//        view03.setImageBitmap(bitmap03);
//        view04.setImageBitmap(bitmap04);
//        ivWelcome05.setImageBitmap(bitmap05);
//
        view01.setImageResource(R.drawable.welcome_01);
        view02.setImageResource(R.drawable.welcome_02);
        view03.setImageResource(R.drawable.welcome_03);
        view04.setImageResource(R.drawable.welcome_04);
        ivWelcome05.setImageResource(R.drawable.welcome);

        tvNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextAction();
                finish();
            }
        });

        mListViews.add(view01);
        mListViews.add(view02);
        mListViews.add(view03);
        mListViews.add(view04);
        mListViews.add(view05);

        tvArrs = new TextView[mListViews.size()];
        for (int i = 0; i < mListViews.size(); i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.drawable.welcom_dot_unchecked);
            if (i == 0) {
                textView.setBackgroundResource(R.drawable.welcom_dot);
            }
            textView.setWidth(10);
            textView.setHeight(10);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 0, 0);
            textView.setLayoutParams(lp);
            tvArrs[i] = textView;
            llDot.addView(textView);
        }

    }

    public TextView[] tvArrs=null;

    public void checkDot(int position){
        for (int i = 0; i < tvArrs.length; i++) {
            tvArrs[i].setBackgroundResource(R.drawable.welcom_dot_unchecked);
            if (i == position) {
                tvArrs[i].setBackgroundResource(R.drawable.welcom_dot);
            }
        }
    }


    private void initView() {
        startAdapter = new StartPagerAdapter();
        viewPagerWelcome.setAdapter(startAdapter);
        viewPagerWelcome.setCurrentItem(0);

        viewPagerWelcome.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                checkDot(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }


    private class StartPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            LogUtil.d("mListViews.size()", "mListViews.size()" + mListViews.size());
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

    }


    public void nextAction() {
        if (SPUtil.isNoFirstStartApp(this)) {
            if (SPUtil.getUser(this) != null) {
                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(this, "刷新用户数据...");
                dialog.show();

                Intent intentAdvice = new Intent(getApplicationContext(), AdviceSettingService.class);
                startService(intentAdvice);

                ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(this, new AbstractBusiness<User>() {
                    @Override
                    public void handleData(User data) {
                        SPUtil.saveUser(WelcomeActivity.this, data);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        customDialog.dismiss();
                        Intent intentHasRiskscore = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intentHasRiskscore);
                        finish();
                    }
                }), getRequestTag());

                if (SPUtil.isLogin(this)) {
                    if (SPUtil.getUser(this).getIsInit()) {
                        Intent intentIsInit = new Intent(this, InfoEditActivity.class);
                        startActivity(intentIsInit);
                        return;
                    }

                    if (!SPUtil.getUser(this).getHasRiskscore()) {
                        if (SPUtil.getHospitalId(this) != -1) {
                            Intent intentHasRiskscore = new Intent(this, GradedActivity.class);
                            startActivity(intentHasRiskscore);
                            return;
                        }
                    }

                    Intent intent = new Intent(this, MeMainFragmentActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
    }


}



