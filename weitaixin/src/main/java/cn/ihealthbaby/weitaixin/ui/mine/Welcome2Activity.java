package cn.ihealthbaby.weitaixin.ui.mine;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.Fragment01;
import cn.ihealthbaby.weitaixin.ui.Fragment02;
import cn.ihealthbaby.weitaixin.ui.Fragment03;
import cn.ihealthbaby.weitaixin.ui.Fragment04;
import cn.ihealthbaby.weitaixin.ui.Fragment05;

/**
 * Created by chenweihua on 2015/9/21.
 */
public class Welcome2Activity extends FragmentActivity {

    private ViewPager viewPagerWelcome;
    private TextView ivWelcomeStart;
    private LinearLayout llDot;


    private ArrayList<Fragment> mListViews=new ArrayList<Fragment>();
    private MyFragmentPagerAdapter startAdapter;

    private LayoutInflater mInflater;
    private TextView view01;
    private TextView view02;
    private TextView view03;
    private TextView view04;
    private LinearLayout view05;
    private TextView ivWelcome05;
    private TextView tvNextAction;

    private Handler handler;
    private int screenWidth;
    private int screenHeight;
    private WindowManager wm ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_welcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏


        viewPagerWelcome = (ViewPager) this.findViewById(R.id.viewPagerWelcome);
        ivWelcomeStart = (TextView) this.findViewById(R.id.ivWelcomeStart);
        llDot = (LinearLayout) this.findViewById(R.id.llDot);


        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();



        ivWelcomeStart.setVisibility(View.VISIBLE);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivWelcomeStart.setVisibility(View.GONE);
                initView();
                initDataView();
            }
        }, 1500);


    }



    public void initDataView() {

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
        Fragment01 fragment01 = new Fragment01();
        Fragment02 fragment02 = new Fragment02();
        Fragment03 fragment03 = new Fragment03();
        Fragment04 fragment04 = new Fragment04();
        Fragment05 fragment05 = new Fragment05();
        fragment05.setIsFlag(true);
        mListViews.add(fragment01);
        mListViews.add(fragment02);
        mListViews.add(fragment03);
        mListViews.add(fragment04);
        mListViews.add(fragment05);


        startAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mListViews);
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



    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;
        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            LogUtil.d("destroyItem", "destroyItem============>"+position);
//            super.destroyItem(container, position, object);
//            container.removeView(list.get(position).getView());
//        }
//
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            LogUtil.d("instantiateItem", "instantiateItem============>"+position);
//            container.addView(list.get(position).getView());
//            return list.get(position).getView();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view==object;
//        }
    }


}





