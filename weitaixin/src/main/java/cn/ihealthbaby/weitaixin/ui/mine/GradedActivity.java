package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.content.Intent;
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
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
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
    private long hospitalId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graded);
        ButterKnife.bind(this);

        title_text.setText("高危评分");
        back.setVisibility(View.INVISIBLE);


        hospitalId = SPUtil.getHospitalId(this);
        if (hospitalId == -1) {
            ToastUtil.show(getApplicationContext(), "暂未开通服务");
            Intent intent = new Intent(this, MeMainFragmentActivity.class);
            startActivity(intent);
            finish();
            return;
        }else{
            ApiManager.getInstance().riskScoreApi.getQuestions(hospitalId,
                    new DefaultCallback<ApiList<Question>>(this, new AbstractBusiness<ApiList<Question>>() {
                        @Override
                        public void handleData(ApiList<Question> data) {
                            questionList = data;
                            questionIndex = 0;
                            start();
                        }

                        @Override
                        public void handleClientError(Exception e) {
                            super.handleClientError(e);
                        }

                        @Override
                        public void handleException(Exception e) {
                            super.handleException(e);
                        }
                    }), getRequestTag());
        }
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
                    answerForm.setAnswer(1);
                    mAnswerForms.addAnswerForms(answerForm);
                    questionDialog.dismiss();
                    start();
                }
            }, new QuestionDialog.onClickFalse() {
                @Override
                public void onClick(View v) {
                    answerForm.setAnswer(0);
                    mAnswerForms.addAnswerForms(answerForm);
                    questionDialog.dismiss();
                    start();
                }
            });

            questionDialog.show();
            questionIndex++;

        } else if (questionIndex != 0 && questionIndex == questionList.getList().size()) {
            ApiManager.getInstance().riskScoreApi.submitQuestionnaire(mAnswerForms,
                    new DefaultCallback<RiskScore>(this, new AbstractBusiness<RiskScore>() {
                        @Override
                        public void handleData(RiskScore data) {
                            mRiskScore = data;
                            result();
                        }

                        @Override
                        public void handleException(Exception e) {
                            super.handleException(e);
                        }

                        @Override
                        public void handleClientError(Exception e) {
                            super.handleClientError(e);
                        }
                    }), getRequestTag());
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

                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(GradedActivity.this, "刷新用户数据...");
                dialog.show();

                ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(GradedActivity.this, new AbstractBusiness<User>() {
                    @Override
                    public void handleData(User data)   {
                        if (data != null) {
                            SPUtil.saveUser(GradedActivity.this, data);
                        }

                        customDialog.dismiss();
                        Intent intent = new Intent(GradedActivity.this, MeMainFragmentActivity.class);
                        startActivity(intent);
                        GradedActivity.this.finish();
                    }

                    @Override
                    public void handleException(Exception e) {
                        customDialog.dismiss();
                        Intent intentHasRiskscore = new Intent(GradedActivity.this, LoginActivity.class);
                        startActivity(intentHasRiskscore);
                        GradedActivity.this.finish();
                    }
                }), getRequestTag());

            }
        });
    }


    @OnClick(R.id.back)
    public void back() {
        finish();
    }


}








