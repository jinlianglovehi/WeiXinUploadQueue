package cn.ihealthbaby.weitaixin.ui.mine;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.form.AnswerForm;
import cn.ihealthbaby.client.form.AnswerForms;
import cn.ihealthbaby.client.model.Question;
import cn.ihealthbaby.client.model.RiskScore;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.widget.QuestionDialog;
import cn.ihealthbaby.weitaixin.ui.widget.ResultDialog;

public class GradedActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;

    private ApiList<Question> questionList;
    private int questionIndex;
    private AnswerForms mAnswerForms = new AnswerForms();
    private RiskScore mRiskScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graded);
        ButterKnife.bind(this);

        title_text.setText("高危评分");
        back.setVisibility(View.INVISIBLE);


        long hospitalId = 0;

        User user= SPUtil.getUser(this);
        if (user != null && user.getServiceInfo() != null) {
            hospitalId = user.getServiceInfo().getHospitalId();
            LogUtil.e("hospitalId", "hospitalId: " + hospitalId);
        } else {
            ToastUtil.show(getApplicationContext(), "暂时没有问题哦");
            return;
        }

        ApiManager.getInstance().riskScoreApi.getQuestions(hospitalId, new HttpClientAdapter.Callback<ApiList<Question>>() {
            @Override
            public void call(Result<ApiList<Question>> t) {
                if (t.getStatus()==Result.SUCCESS) {
                    questionList = t.getData();
                    questionIndex = 0;
                    start();
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap()+"");
                }
            }
        }, getRequestTag());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void start() {
        final AnswerForm answerForm = new AnswerForm();
        if (questionIndex < questionList.getList().size()) {

            Question question = questionList.getList().get(questionIndex);
            answerForm.setQuestionId(question.getId());
            final QuestionDialog questionDialog = new QuestionDialog(questionIndex, this, question);
            questionDialog.setCanceledOnTouchOutside(false);
            questionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            questionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            questionDialog.setOnclick(new QuestionDialog.onClickTrue() {
                @Override
                public void onClick(View v) {
                    answerForm.setAnswer(0);
                    mAnswerForms.addAnswerForms(answerForm);
                    questionDialog.dismiss();
                    start();
                }
            }, new QuestionDialog.onClickFalse() {
                @Override
                public void onClick(View v) {
                    answerForm.setAnswer(1);
                    mAnswerForms.addAnswerForms(answerForm);
                    questionDialog.dismiss();
                    start();
                }
            });

            questionDialog.show();
            questionIndex++;

        } else if (questionIndex != 0 && questionIndex == questionList.getList().size()) {
            ApiManager.getInstance().riskScoreApi.submitQuestionnaire(mAnswerForms, new HttpClientAdapter.Callback<RiskScore>() {
                @Override
                public void call(Result<RiskScore> t) {
                    if (t.getStatus()==Result.SUCCESS) {
                        mRiskScore = t.getData();
                        result();
                    } else {
                        ToastUtil.show(getApplicationContext(), t.getMsgMap()+"");
                    }
                }
            }, getRequestTag());
        }
    }

    private void result() {
        ResultDialog resultDialog = new ResultDialog(this, mRiskScore);
        resultDialog.setCanceledOnTouchOutside(false);
        resultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        resultDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resultDialog.show();
        resultDialog.setOnFinishQuit(new ResultDialog.OnFinishQuit() {
            @Override
            public void quit() {
                GradedActivity.this.finish();
            }
        });
    }


    @OnClick(R.id.back)
    public void back() {
        finish();
    }


}








