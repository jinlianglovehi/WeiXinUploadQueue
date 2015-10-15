package cn.ihealthbaby.weitaixin.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.service.AdviceSettingService;
import cn.ihealthbaby.weitaixin.ui.login.InfoEditActivity;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.ui.mine.GradedActivity;

/**
 * Created by chenweihua on 2015/10/13.
 */
public class Fragment05 extends Fragment {

    private ImageView ivWelcome05;
    private ImageView tvNextAction;

    public boolean isFlag=false;

    public boolean isFlag() {
        return isFlag;
    }

    public void setIsFlag(boolean isFlag) {
        this.isFlag = isFlag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view05 = (LinearLayout) inflater.inflate(R.layout.viewpager_item_last, null);

        ivWelcome05 = (ImageView) view05.findViewById(R.id.ivWelcome05);
        tvNextAction = (ImageView) view05.findViewById(R.id.tvNextAction);
        ivWelcome05.setImageResource(R.drawable.welcome_05);
        tvNextAction.setImageResource(R.drawable.welcome_05_btn);

        tvNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlag()) {
                    getActivity().finish();
                }else{
                    nextAction();
                }
            }
        });


        return view05;
    }


        public void nextAction() {
            if (SPUtil.isNoFirstStartApp(getActivity())) {
                if (SPUtil.getUser(getActivity()) != null) {
//                    final CustomDialog customDialog = new CustomDialog();
//                    Dialog dialog = customDialog.createDialog1(getActivity(), "刷新用户数据...");
//                    dialog.show();
//
//                    Intent intentAdvice = new Intent(getActivity(), AdviceSettingService.class);
//                    getActivity().startService(intentAdvice);
//
//                    ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(getActivity(), new AbstractBusiness<User>() {
//                        @Override
//                        public void handleData(User data) {
//                            SPUtil.saveUser(getActivity(), data);
//                            customDialog.dismiss();
//                        }
//
//                        @Override
//                        public void handleException(Exception e) {
//                            customDialog.dismiss();
//                            Intent intentHasRiskscore = new Intent(getActivity(), LoginActivity.class);
//                            startActivity(intentHasRiskscore);
//                            getActivity().finish();
//                        }
//                    }), this);

                    if (SPUtil.isLogin(getActivity())) {
                        if (SPUtil.getUser(getActivity()).getIsInit()) {
                            Intent intentIsInit = new Intent(getActivity(), InfoEditActivity.class);
                            startActivity(intentIsInit);
                            return;
                        }

                        if (!SPUtil.getUser(getActivity()).getHasRiskscore()) {
                            if (SPUtil.getHospitalId(getActivity()) != -1) {
                                Intent intentHasRiskscore = new Intent(getActivity(), GradedActivity.class);
                                startActivity(intentHasRiskscore);
                                return;
                            }
                        }

                        Intent intent = new Intent(getActivity(), MeMainFragmentActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    return;
                }
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return;
            }
        }



}
