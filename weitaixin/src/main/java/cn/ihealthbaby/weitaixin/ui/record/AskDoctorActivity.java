package cn.ihealthbaby.weitaixin.ui.record;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AskForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.client.model.Service;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;
import cn.ihealthbaby.weitaixin.tools.MaxLengthWatcher;


public class AskDoctorActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //


    @Bind(R.id.etAskDoctorText) EditText etAskDoctorText;
    @Bind(R.id.tvSendDoctorAction) TextView tvSendDoctorAction;
    @Bind(R.id.tvOtherInfo) TextView tvOtherInfo;
    @Bind(R.id.tvAskDoctorTextCount) TextView tvAskDoctorTextCount;


    private int status;
    private AdviceItem adviceItem;
    private final int resultCoded = 200;
    private final int statused = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_doctor);

        ButterKnife.bind(this);

        title_text.setText("问医生");
//      back.setVisibility(View.INVISIBLE);

        status = getIntent().getIntExtra("status", 0);
        adviceItem = (AdviceItem) getIntent().getSerializableExtra("adviceItem");


        etAskDoctorText.setMovementMethod(ScrollingMovementMethod.getInstance());
        etAskDoctorText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

      etAskDoctorText.addTextChangedListener(new MaxLengthWatcher(200, etAskDoctorText, tvAskDoctorTextCount));

        pullData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }


    private int totalCount,usedCount;
    private void pullData(){
        ApiManager.getInstance().serviceApi.getByUser(new HttpClientAdapter.Callback<Service>() {
            @Override
            public void call(Result<Service> t) {
                if (t.isSuccess()) {
                    Service data = t.getData();
                     totalCount=data.getTotalCount();
                     usedCount=data.getUsedCount();
                    tvOtherInfo.setText("共"+totalCount+"次，已咨询"+usedCount+"次，剩余"+(totalCount-usedCount)+"次");
                } else {
                    ToastUtil.show(AskDoctorActivity.this.getApplicationContext(), t.getMsg());
                }
            }
        },getRequestTag());
    }



    @OnClick(R.id.tvSendDoctorAction)
    public void tvSendDoctorAction( ) {
        String askDoctorText = etAskDoctorText.getText().toString();
        if (TextUtils.isEmpty(askDoctorText)) {
            ToastUtil.show(getApplicationContext(),"详情描述，便于医生诊断~~~");
            return;
        }
        if ((totalCount-usedCount)<=0) {
            ToastUtil.show(getApplicationContext(),"没有咨询次数了~~~");
            return;
        }

        final CustomDialog customDialog = new CustomDialog();
        final Dialog dialog=customDialog.createDialog1(this,"发送中...");
        dialog.show();

        AskForm askForm=new AskForm();
        askForm.setAdviceId(status);
        askForm.setQuestion(askDoctorText);
        ApiManager.getInstance().adviceApi.askDoctor(askForm, new HttpClientAdapter.Callback<Integer>() {
            @Override
            public void call(Result<Integer> t) {
                if (customDialog.isNoCancel) {
                    if (t.isSuccess()) {
                        adviceItem.setStatus(statused);
                        setResult(resultCoded);
                        AskDoctorActivity.this.finish();
                    } else {
                        ToastUtil.show(AskDoctorActivity.this.getApplicationContext(), t.getMsg());
                    }
                }
                dialog.dismiss();
            }
        },getRequestTag());

    }


}


