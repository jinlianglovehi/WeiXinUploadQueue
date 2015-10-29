package cn.ihealthbaby.weitaixin.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.AdviceStatistics;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.ui.login.InfoEditActivity;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.ui.mine.WoInformationActivity;
import cn.ihealthbaby.weitaixin.ui.mine.WoMessageActivity;
import cn.ihealthbaby.weitaixin.ui.pay.PayAccountActivity;

public class HomePageFragment extends BaseFragment {
    private final static String TAG = "HomePageFragment";
    //    @Bind(R.id.rlNavHead) RelativeLayout rlNavHead;
//    @Bind(R.id.back) RelativeLayout back;
//    @Bind(R.id.title_text) TextView title_text;
//    @Bind(R.id.function) TextView function;
//
    private static HomePageFragment instance;
    public MeMainFragmentActivity meMainFragmentActivity;
    @Bind(R.id.llHomeFunctionOneAction)
    LinearLayout llHomeFunctionOneAction;
    @Bind(R.id.llHomeFunctionTwoAction)
    LinearLayout llHomeFunctionTwoAction;
    @Bind(R.id.llHomeFunctionThreeAction)
    LinearLayout llHomeFunctionThreeAction;
    @Bind(R.id.llHomeFunctionFourAction) LinearLayout llHomeFunctionFourAction;
    @Bind(R.id.ivHomeHeadImg) ImageView ivHomeHeadImg;
    @Bind(R.id.tvHomeHeadName) TextView tvHomeHeadName;
    @Bind(R.id.flShowMessageCount) FrameLayout flShowMessageCount;
    @Bind(R.id.tvMessageNumberCount) TextView tvMessageNumberCount;
    @Bind(R.id.tvGestationalWeeks) TextView tvGestationalWeeks;
    //
    @Bind(R.id.tvPregnancyDayNumber) TextView tvPregnancyDayNumber;
    @Bind(R.id.tvProduceDayNumber) TextView tvProduceDayNumber;
    @Bind(R.id.tvMonitorDayNumber) TextView tvMonitorDayNumber;
    //
    @Bind(R.id.tvPregnancyDayText) TextView tvPregnancyDayText;
    @Bind(R.id.tvProduceDayText) TextView tvProduceDayText;
    @Bind(R.id.tvMonitorDayText) TextView tvMonitorDayText;
    //
    @Bind(R.id.rlPregnancyDate) RelativeLayout rlPregnancyDate;
    @Bind(R.id.rlProduceDate) RelativeLayout rlProduceDate;
    @Bind(R.id.rltvMonitorDate) RelativeLayout rltvMonitorDate;

    private int monitorCount = 0;
    private int messageCount = 0;
    private RecordBusinessDao recordBusinessDao;

    public static HomePageFragment getInstance() {
        if (instance == null) {
            instance = new HomePageFragment();
        }
        return instance;
    }



    public void startAnim(){
        rlPregnancyDate.clearAnimation();
        rlProduceDate.clearAnimation();
        rltvMonitorDate.clearAnimation();

        tvPregnancyDayText.setText("怀孕天数");
        tvProduceDayText.setText("距预产期");
        tvMonitorDayText.setText("已监测");

        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.4f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        animationSet.setFillEnabled(true);
        animationSet.setFillAfter(true);
        animationSet.setDuration(300);
        animationSet.setStartOffset(300);
        animationSet.addAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvPregnancyDayText.setText("");
                    }
                }, 300);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet2 = new AnimationSet(true);
                TranslateAnimation translateAnimation2 = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -0.4f,
                        Animation.RELATIVE_TO_SELF, -0.2f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f);
                animationSet2.setFillEnabled(true);
                animationSet2.setFillAfter(true);
                animationSet2.setDuration(300);
                animationSet2.addAnimation(translateAnimation2);
                translateAnimation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AnimationSet animationSet3 = new AnimationSet(true);
                        TranslateAnimation translateAnimation3 = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, -0.2f,
                                Animation.RELATIVE_TO_SELF, -0.3f,
                                Animation.RELATIVE_TO_SELF, 0f,
                                Animation.RELATIVE_TO_SELF, 0f);
                        animationSet3.setFillEnabled(true);
                        animationSet3.setFillAfter(true);
                        animationSet3.setDuration(300);
                        animationSet3.addAnimation(translateAnimation3);
                        animationSet3.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                AnimUntils.startAnim(rlProduceDate, tvProduceDayText, "距预产期", rltvMonitorDate, tvMonitorDayText, "已监测");
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        rlPregnancyDate.startAnimation(animationSet3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rlPregnancyDate.startAnimation(animationSet2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rlPregnancyDate.startAnimation(animationSet);

    }

    public void stopAnim() {
        if (rlPregnancyDate != null && rlProduceDate != null && rltvMonitorDate != null) {
            rlPregnancyDate.clearAnimation();
            rlProduceDate.clearAnimation();
            rltvMonitorDate.clearAnimation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnim();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, null);
        ButterKnife.bind(this, view);

        startAnim();


//        title_text.setText("首页");
//        back.setVisibility(View.INVISIBLE);
//        rlNavHead.setBackgroundResource(R.color.green6);

        if (messageCount == 0) {
            flShowMessageCount.setVisibility(View.INVISIBLE);
        }

//        pullHeadDatas();

        meMainFragmentActivity = (MeMainFragmentActivity) getActivity();
        recordBusinessDao = RecordBusinessDao.getInstance(getActivity().getApplicationContext());

        return view;
    }

    //获取本地记录
    public int getLocalDB() {

        ArrayList<Record> records = new ArrayList<Record>();
        try {
            records = (ArrayList<Record>) recordBusinessDao.queryUserRecord(SPUtil.getUserID(getActivity().getApplicationContext()), Record.UPLOAD_STATE_LOCAL, Record.UPLOAD_STATE_UPLOADING);
            LogUtil.d(TAG, records.size() + " =recordBusinessDao= " + records);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "recordBusinessDao= " + e.toString());
        }
        if (records != null) {
            return records.size();
        }
        return 0;
    }

    private void pullHeadDatas() {
        User user = SPUtil.getUser(getActivity().getApplicationContext());
        if (user != null) {
            Date deliveryTime = user.getDeliveryTime();
            if (deliveryTime != null) {
                tvPregnancyDayNumber.setText(DateTimeTool.getGestationalDays(deliveryTime) + "");
                tvProduceDayNumber.setText(DateTimeTool.fromDeliveryTime(deliveryTime) + "");
                String gestationalWeeks = DateTimeTool.getGestationalWeeks(deliveryTime);
                String[] split = gestationalWeeks.split("\\+");
                String gWeeks = "";
                if (split.length == 1) {
                    gWeeks = split[0];
                }
                if (split.length == 2) {
                    gWeeks = split[0];
                }
                tvGestationalWeeks.setText(gWeeks + "");
            }
        }


        if (SPUtil.isLogin(getActivity().getApplicationContext()) && user != null) {
//            LogUtil.d(TAG, "getHeadPic==>" + user.getHeadPic());
            ImageLoader.getInstance().displayImage(user.getHeadPic(), ivHomeHeadImg, setDisplayImageOptions());
            tvHomeHeadName.setText(user.getName()+"");
        }


        getNumber();

    }


    public void getNumber() {
        ApiManager.getInstance().adviceApi.getStatistics(
                new DefaultCallback<AdviceStatistics>(getActivity(), new AbstractBusiness<AdviceStatistics>() {
                    @Override
                    public void handleData(AdviceStatistics data) {
                        messageCount = 0;
                        monitorCount = 0;
                        if (data != null) {
                            messageCount += data.getAdviceUnReadReplyCount();
                            if (messageCount != 0) {
                                flShowMessageCount.setVisibility(View.VISIBLE);
                                tvMessageNumberCount.setText(messageCount + "");
                            }else{
                                flShowMessageCount.setVisibility(View.INVISIBLE);
                            }

                            monitorCount += data.getAdviceUploadCount();
                            monitorCount += getLocalDB();
                            tvMonitorDayNumber.setText(monitorCount + "");
                        } else {
                            ToastUtil.show(getActivity(), "没有获取到数据");
                        }
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        if (messageCount == 0) {
                            flShowMessageCount.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        if (messageCount == 0) {
                            flShowMessageCount.setVisibility(View.INVISIBLE);
                        }
                    }
                }), getRequestTag());

    }


    @OnClick(R.id.ivHomeHeadImg)
    public void ivHomeHeadImg() {
        if (SPUtil.isLogin(getActivity())) {
            Intent intent = new Intent(getActivity().getApplicationContext(), WoInformationActivity.class);
//            Intent intent = new Intent(getActivity().getApplicationContext(), InfoEditActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.llHomeFunctionOneAction)
    public void llHomeFunctionOneAction() {
        meMainFragmentActivity.iv_tab_02();
    }

    @OnClick(R.id.llHomeFunctionTwoAction)
    public void llHomeFunctionTwoAction() {
        if (SPUtil.isLogin(getActivity())) {
            Intent intent = new Intent(getActivity().getApplicationContext(), WoMessageActivity.class);
            intent.putExtra("MessageType", 1);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.llHomeFunctionThreeAction)
    public void llHomeFunctionThreeAction() {
        meMainFragmentActivity.iv_tab_03();
    }

    @OnClick(R.id.llHomeFunctionFourAction)
    public void llHomeFunctionFourAction() {
        if (SPUtil.isLogin(getActivity())) {
            Intent intent = new Intent(getActivity().getApplicationContext(), PayAccountActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        pullHeadDatas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.home_head_icon)
                .showImageForEmptyUri(R.drawable.home_head_icon)
                .showImageOnFail(R.drawable.home_head_icon)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
//                .displayer(new SimpleBitmapDisplayer())
                .displayer(new RoundedBitmapDisplayer(350))
                .build();
        return options;
    }
}



