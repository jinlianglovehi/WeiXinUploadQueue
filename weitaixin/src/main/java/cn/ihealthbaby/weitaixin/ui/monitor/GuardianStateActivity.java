package cn.ihealthbaby.weitaixin.ui.monitor;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.AskPurposeType;
import cn.ihealthbaby.client.model.CommonConfig;
import cn.ihealthbaby.client.model.FeelingType;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class GuardianStateActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
//
    @Bind(R.id.flGuardianPurpose) FrameLayout flGuardianPurpose;
    @Bind(R.id.flGuardianMood) FrameLayout flGuardianMood;
    @Bind(R.id.ivFooter) TextView ivFooter;
    @Bind(R.id.tvGuardianPurposeText) TextView tvGuardianPurposeText;
    @Bind(R.id.tvGuardianMoodText) TextView tvGuardianMoodText;


    public List<AskPurposeType> askPurposetypes;
    public List<FeelingType> feelingTypes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_state);
        ButterKnife.bind(this);

        title_text.setText("监护状态");

        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "加载中...");
        dialog.show();
        ApiManager.getInstance().commonApi.getCommonConfig(new HttpClientAdapter.Callback<CommonConfig>() {
            @Override
            public void call(Result<CommonConfig> t) {
                if (t.isSuccess()) {
                    CommonConfig data = t.getData();
                    askPurposetypes = data.getAskPurposetypes();
                    feelingTypes = data.getFeelingTypes();
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.flGuardianPurpose)
    public void GuardianPurpose(FrameLayout flGuardianPurpose) {
        if (askPurposetypes == null) {
            ToastUtil.show(getApplicationContext(), "没数据~~~");
            return;
        }
        final MyPoPoWinGuardian myPoPoWinGuardian = new MyPoPoWinGuardian(this);
        myPoPoWinGuardian.initPurposetData(askPurposetypes);
        myPoPoWinGuardian.showAtLocation(flGuardianPurpose);
        myPoPoWinGuardian.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tvGuardianPurposeText.setText(askPurposetypes.get(myPoPoWinGuardian.indexPosition).getViewValue()+"");
            }
        });
    }


    @OnClick(R.id.flGuardianMood)
    public void GuardianMood(FrameLayout flGuardianMood) {
        if (feelingTypes == null) {
            ToastUtil.show(getApplicationContext(), "没数据~~~");
            return;
        }
        final MyPoPoWinGuardian myPoPoWinGuardian = new MyPoPoWinGuardian(this);
        myPoPoWinGuardian.initFeelingTypeData(feelingTypes);
        myPoPoWinGuardian.showAtLocation(flGuardianMood);
        myPoPoWinGuardian.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tvGuardianMoodText.setText(feelingTypes.get(myPoPoWinGuardian.indexPosition).getViewValue()+"");
            }
        });
    }


    @OnClick(R.id.ivFooter)
    public void Footer(){
        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "加载中...");
        dialog.show();
//        ApiManager.getInstance().adviceApi.
    }


}








