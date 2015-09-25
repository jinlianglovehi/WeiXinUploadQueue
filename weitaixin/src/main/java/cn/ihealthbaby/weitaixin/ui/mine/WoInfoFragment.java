package cn.ihealthbaby.weitaixin.ui.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.ui.pay.PayAccountActivity;
import cn.ihealthbaby.weitaixin.ui.widget.RoundImageView;


public class WoInfoFragment extends BaseFragment {
    private final static String TAG = "WoInfoFragment";

    @Nullable
    @Bind(R.id.back)
    RelativeLayout back;
    @Nullable
    @Bind(R.id.title_text)
    TextView title_text;
    @Nullable
    @Bind(R.id.function)
    TextView function;
//

    @Nullable
    @Bind(R.id.ll_1)
    LinearLayout ll_1;
    @Nullable
    @Bind(R.id.ll_2)
    LinearLayout ll_2;
    @Nullable
    @Bind(R.id.ll_3)
    LinearLayout ll_3;
    @Nullable
    @Bind(R.id.ll_4)
    LinearLayout ll_4;
    @Nullable
    @Bind(R.id.rl_head_img)
    RelativeLayout rl_head_img;

    @Nullable
    @Bind(R.id.tv_wo_head_name)
    TextView tv_wo_head_name;
    @Nullable
    @Bind(R.id.tv_wo_head_breed_date)
    TextView tv_wo_head_breed_date;
    @Nullable
    @Bind(R.id.tv_wo_head_deliveryTime)
    TextView tv_wo_head_deliveryTime;
    @Nullable
    @Bind(R.id.iv_wo_head_icon)
    ImageView iv_wo_head_icon;
    @Bind(R.id.tv_message_count)
    TextView mTvMessageCount;
    private View view;


    private static WoInfoFragment instance;

    public static WoInfoFragment getInstance() {
        if (instance == null) {
            instance = new WoInfoFragment();
            LogUtil.e("WoInfoFragment+Coco7", "WoInfoFragment+getInstance");
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wo_info, null);
        ButterKnife.bind(this, view);

        back.setVisibility(View.INVISIBLE);
        title_text.setText("我的");
        init();

        return view;
    }

    private void init() {
        setTextHead();
    }


    @Override
    public void onResume() {
        super.onResume();
        setTextHead();
    }


    private void setTextHead() {

        ApiManager.getInstance().informationApi.getUnReadCount(
                new DefaultCallback<Integer>(getActivity(), new AbstractBusiness<Integer>() {
                    @Override
                    public void handleData(Integer data) {
                        if (data > 0) {
                            mTvMessageCount.setVisibility(View.VISIBLE);
                            mTvMessageCount.setText(data + "");
                        } else {
                            mTvMessageCount.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        mTvMessageCount.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        mTvMessageCount.setVisibility(View.GONE);
                    }
                }), getRequestTag());


        User user = SPUtil.getUser(getActivity().getApplicationContext());
        if (SPUtil.isLogin(getActivity().getApplicationContext()) && user != null) {
            ImageLoader.getInstance().displayImage(user.getHeadPic(), iv_wo_head_icon, setDisplayImageOptions());
            tv_wo_head_name.setText(user.getName() + "");
            tv_wo_head_breed_date.setText("已孕：" + DateTimeTool.getGestationalWeeks(user.getDeliveryTime()));
            tv_wo_head_deliveryTime.setText("预产：" + DateTimeTool.date2Str(user.getDeliveryTime(), "MM月dd日"));
        }
    }

    @OnClick(R.id.ll_1)
    public void ll_1() {
        Intent intent = new Intent(getActivity().getApplicationContext(), WoMessageActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_2)
    public void ll_2() {
        Intent intent = new Intent(getActivity().getApplicationContext(), PayAccountActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_3)
    public void ll_3() {
        Intent intent = new Intent(getActivity().getApplicationContext(), SetSystemActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_4)
    public void ll_4() {
        Intent intent = new Intent(getActivity().getApplicationContext(), GradedActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.rl_head_img)
    public void rlHeadImg() {
        Intent intent = new Intent(getActivity().getApplicationContext(), WoInformationActivity.class);
        startActivity(intent);
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
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(200))
                .build();
        return options;
    }
}




