package cn.ihealthbaby.weitaixin.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.ihealthbaby.client.model.RiskScore;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.ui.mine.GradedActivity;

/**
 * @author by kang on 2015/9/1.
 */
public class ResultDialog extends Dialog {
    RiskScore mRiskScore;

    public ResultDialog(Context context, RiskScore riskScore) {
        super(context);
        mRiskScore = riskScore;
    }

    public ResultDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ResultDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_grade_result_dialog);
        TextView tv_score = (TextView) findViewById(R.id.tv_score);
        TextView tv_result_true = (TextView) findViewById(R.id.tv_result_true);
        tv_score.setText(mRiskScore.getScore() + "分");
        tv_result_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onFinishQuit != null) {
                    onFinishQuit.quit();
                }
            }
        });
    }


    public OnFinishQuit onFinishQuit;

    public interface OnFinishQuit {
        void quit();
    }

    public void setOnFinishQuit(OnFinishQuit onFinishQuit) {
        this.onFinishQuit = onFinishQuit;
    }


}



