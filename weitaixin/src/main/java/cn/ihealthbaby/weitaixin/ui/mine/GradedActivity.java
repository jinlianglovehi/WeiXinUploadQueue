package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

import static cn.ihealthbaby.weitaixin.R.id.tv_true;

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

        long hospitalId = 0;
        if (WeiTaiXinApplication.user != null && WeiTaiXinApplication.user.getServiceInfo() != null) {
            hospitalId = WeiTaiXinApplication.user.getServiceInfo().getHospitalId();
            LogUtil.e("hospitalId", "hospitalId: " + hospitalId);
        } else {
            ToastUtil.show(getApplicationContext(), "暂时没有问题~~~");
            return;
        }

        ApiManager.getInstance().riskScoreApi.getQuestions(hospitalId, new HttpClientAdapter.Callback<ApiList<Question>>() {
            @Override
            public void call(Result<ApiList<Question>> t) {
                if (t.isSuccess()) {
                    questionList = t.getData();
                    questionIndex = 0;
                    start();
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsg());
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
            final Dialog dialog = new Dialog(GradedActivity.this);
            View view = LayoutInflater.from(GradedActivity.this).inflate(R.layout.view_grade_dialog, null);
            TextView mTvContent = (TextView) view.findViewById(R.id.tv_content);

            mTvContent.setText(question.getQuestion());
            TextView mTvNumber = (TextView) view.findViewById(R.id.tv_number);
            if (0 == questionIndex) {
                mTvNumber.setBackgroundResource(R.drawable.grade_end);
            } else {
                mTvNumber.setBackgroundResource(R.drawable.grade_progress);
            }
            mTvNumber.setText((questionIndex + 1) + "");
            LinearLayout mRlSelect = (LinearLayout) view.findViewById(R.id.rl_select);
            TextView mTvTrue = (TextView) view.findViewById(tv_true);
            TextView mTvFalse = (TextView) view.findViewById(R.id.tv_false);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            TextView tv_subtitle = (TextView) view.findViewById(R.id.tv_subtitle);

            if (questionIndex > 0) {
                tv_title.setVisibility(View.INVISIBLE);
                tv_subtitle.setVisibility(View.GONE);
            }
            mTvFalse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerForm.setAnswer(0);
                    mAnswerForms.addAnswerForms(answerForm);
                    dialog.dismiss();
                    start();
                }
            });
            mTvTrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerForm.setAnswer(1);
                    mAnswerForms.addAnswerForms(answerForm);
                    dialog.dismiss();
                    start();
                }
            });

            dialog.setCancelable(false);
            dialog.setContentView(view);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            dialog.getWindow().setLayout((6 * width) / 7, (1 * height) / 2);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            questionIndex++;

        } else if (questionIndex != 0 && questionIndex == questionList.getList().size()) {
            ApiManager.getInstance().riskScoreApi.submitQuestionnaire(mAnswerForms, new HttpClientAdapter.Callback<RiskScore>() {
                @Override
                public void call(Result<RiskScore> t) {
                    if (t.isSuccess()) {
                        mRiskScore = t.getData();
                        result();
                    } else {
                        ToastUtil.show(getApplicationContext(), t.getMsg());
                    }
                }
            }, getRequestTag());
        }
    }

    private void result() {
        final Dialog dialog = new Dialog(GradedActivity.this);
        View view = LayoutInflater.from(GradedActivity.this).inflate(R.layout.view_grade_dialog, null);
        LinearLayout ll_begin = (LinearLayout) view.findViewById(R.id.ll_begin);
        ll_begin.setVisibility(View.GONE);
        LinearLayout ll_result = (LinearLayout) view.findViewById(R.id.ll_result);
        ll_result.setVisibility(View.VISIBLE);
        TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
        tv_number.setBackgroundResource(R.drawable.grade_end);
        TextView tv_score = (TextView) view.findViewById(R.id.tv_score);
        tv_score.setText(mRiskScore.getScore() + "分");
        TextView tv_result_true = (TextView) view.findViewById(R.id.tv_result_true);
        tv_result_true.setVisibility(View.VISIBLE);
//        dialog.setCancelable(false);
        dialog.setContentView(view);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, (4 * height) / 5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tv_result_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }
}








