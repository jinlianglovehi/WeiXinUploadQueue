package cn.ihealthbaby.weitaixin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.activity.InfoEditActivity;
import cn.ihealthbaby.weitaixin.activity.SetSystemActivity;
import cn.ihealthbaby.weitaixin.activity.WoGoldenActivity;
import cn.ihealthbaby.weitaixin.activity.WoInformationActivity;
import cn.ihealthbaby.weitaixin.activity.WoMessageActivity;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;


public class WoInfoFragment extends BaseFragment {
    private final static String TAG = "WoInfoFragment";
    private LoginSuccessListener loginSuccessListener;

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
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
    @Bind(R.id.rl_head_img)
    RelativeLayout rl_head_img;

    @Bind(R.id.tv_wo_head_name)
    TextView tv_wo_head_name;
    @Bind(R.id.tv_wo_head_breed_date)
    TextView tv_wo_head_breed_date;
    @Bind(R.id.tv_wo_head_deliveryTime)
    TextView tv_wo_head_deliveryTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wo_info, null);
        ButterKnife.bind(this, view);
        init(view);
        back.setVisibility(View.INVISIBLE);
        return view;
    }

    private void init(View view) {
        title_text.setText("设置");
        setTextHead();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTextHead();
    }

    private void setTextHead(){
        if (WeiTaiXinApplication.getInstance().isLogin&& WeiTaiXinApplication.user!=null) {
            tv_wo_head_name.setText(WeiTaiXinApplication.user.getName()+"");
            tv_wo_head_breed_date.setText("已孕：" + DateTimeTool.date2Str(WeiTaiXinApplication.user.getDeliveryTime()));
            tv_wo_head_deliveryTime.setText("预产：" + DateTimeTool.date2Str(WeiTaiXinApplication.user.getDeliveryTime()));
        }
    }

    @OnClick(R.id.ll_1)
    public void ll_1() {
        Intent intent = new Intent(getActivity().getApplicationContext(), WoMessageActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_2)
    public void ll_2() {
        Intent intent = new Intent(getActivity().getApplicationContext(), WoGoldenActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_3)
    public void ll_3() {
        Intent intent = new Intent(getActivity().getApplicationContext(), SetSystemActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_4)
    public void ll_4() {
        Intent intent = new Intent(getActivity().getApplicationContext(), InfoEditActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.rl_head_img)
    public void rl_head_img() {
        Intent intent = new Intent(getActivity().getApplicationContext(), WoInformationActivity.class);
        startActivity(intent);
    }

    public LoginSuccessListener getLoginSuccessListener() {
        return loginSuccessListener;
    }

    public void setLoginSuccessListener(LoginSuccessListener listener) {
        this.loginSuccessListener = listener;
    }

    public interface LoginSuccessListener {
        void onLoginSuccess();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}




