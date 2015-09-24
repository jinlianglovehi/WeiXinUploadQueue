package cn.ihealthbaby.weitaixin.ui.mine;

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
import cn.ihealthbaby.client.form.FeedBackForm;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.library.tools.MaxLengthWatcher;


public class SetSystemSuggestionActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //
    @Bind(R.id.et_suggestion_text) EditText et_suggestion_text;
    @Bind(R.id.tv_send_suggestion_action) TextView tv_send_suggestion_action;
    @Bind(R.id.tv_sugg_text_count) TextView tv_sugg_text_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system_suggestion);

        ButterKnife.bind(this);

        title_text.setText("意见和反馈");
//        back.setVisibility(View.INVISIBLE);

        et_suggestion_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        et_suggestion_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        et_suggestion_text.addTextChangedListener(new MaxLengthWatcher(2, et_suggestion_text,tv_sugg_text_count));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }

    @OnClick(R.id.tv_send_suggestion_action)
    public void tv_send_suggestion_action( ) {
        String suggestion = et_suggestion_text.getText().toString();
        if (TextUtils.isEmpty(suggestion)) {
            ToastUtil.show(getApplicationContext(),"请输入这一刻你的想法...");
            return;
        }

        final CustomDialog customDialog = new CustomDialog();
        final Dialog dialog=customDialog.createDialog1(this,"发送中...");
        dialog.show();


        FeedBackForm form=new FeedBackForm();
        form.setContext(suggestion);
        ApiManager.getInstance().feedBackApi.create(form, new HttpClientAdapter.Callback<Void>() {
            @Override
            public void call(Result<Void> t) {
                    if (t.isSuccess()) {
                        ToastUtil.show(SetSystemSuggestionActivity.this.getApplicationContext(), "提交成功");
                        SetSystemSuggestionActivity.this.finish();
                    } else {
                        ToastUtil.show(SetSystemSuggestionActivity.this.getApplicationContext(), t.getMsg());
                    }
                dialog.dismiss();
            }
        }, getRequestTag());

    }


}
