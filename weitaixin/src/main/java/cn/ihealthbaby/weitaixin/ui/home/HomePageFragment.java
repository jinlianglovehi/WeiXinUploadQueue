package cn.ihealthbaby.weitaixin.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    @Bind(R.id.llHomeFunctionFourAction)
    LinearLayout llHomeFunctionFourAction;
    @Bind(R.id.ivHomeHeadImg)
    ImageView ivHomeHeadImg;
    @Bind(R.id.tvHomeHeadName)
    TextView tvHomeHeadName;
    @Bind(R.id.flShowMessageCount)
    FrameLayout flShowMessageCount;
    @Bind(R.id.tvMessageNumberCount)
    TextView tvMessageNumberCount;
    @Bind(R.id.tvGestationalWeeks)
    TextView tvGestationalWeeks;
    @Bind(R.id.tvPregnancyDayNumber)
    TextView tvPregnancyDayNumber;
    @Bind(R.id.tvProduceDayNumber)
    TextView tvProduceDayNumber;
    @Bind(R.id.tvMonitorDayNumber)
    TextView tvMonitorDayNumber;

    private int monitorCount = 0;
    private int messageCount = 0;
    private RecordBusinessDao recordBusinessDao;

    public static HomePageFragment getInstance() {
        if (instance == null) {
            instance = new HomePageFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, null);
        ButterKnife.bind(this, view);

//        title_text.setText("首页");
//        back.setVisibility(View.INVISIBLE);
//        rlNavHead.setBackgroundResource(R.color.green6);

        if (messageCount == 0) {
            flShowMessageCount.setVisibility(View.INVISIBLE);
        }

        pullHeadDatas();

        meMainFragmentActivity = (MeMainFragmentActivity) getActivity();
        recordBusinessDao = RecordBusinessDao.getInstance(getActivity().getApplicationContext());
        ApiManager.getInstance().adviceApi.getStatistics(new DefaultCallback<AdviceStatistics>(getActivity(), new AbstractBusiness<AdviceStatistics>() {
            @Override
            public void handleData(AdviceStatistics data) {
                if (data != null) {
                    messageCount += data.getAdviceUnReadReplyCount();
                    if (messageCount != 0) {
                        flShowMessageCount.setVisibility(View.VISIBLE);
                        tvMessageNumberCount.setText(messageCount + "");
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
            public void handleClientError(Context context,Exception e) {
                super.handleClientError(context,e);
                if (messageCount == 0) {
                    flShowMessageCount.setVisibility(View.INVISIBLE);
                }
            }
        }), getRequestTag());

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
            LogUtil.d(TAG, "getHeadPic==>" + user.getHeadPic());
            ImageLoader.getInstance().displayImage(user.getHeadPic(), ivHomeHeadImg, setDisplayImageOptions());
            tvHomeHeadName.setText(user.getName());
        }
    }

    @OnClick(R.id.ivHomeHeadImg)
    public void ivHomeHeadImg() {
        if (SPUtil.isLogin(getActivity())) {
            Intent intent = new Intent(getActivity().getApplicationContext(), WoInformationActivity.class);
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
            startActivity(intent);
        }else{
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
        }else{
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
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
//                .displayer(new SimpleBitmapDisplayer())
                .displayer(new RoundedBitmapDisplayer(350))
                .build();
        return options;
    }
}



