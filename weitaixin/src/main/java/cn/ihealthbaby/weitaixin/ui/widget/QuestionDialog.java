package cn.ihealthbaby.weitaixin.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.ihealthbaby.client.model.Question;
import cn.ihealthbaby.weitaixin.R;

/**
 * @author by kang on 2015/9/1.
 */
public class QuestionDialog extends Dialog {


    private int mPosition;
    private Question mQuestion;
    private onClickTrue onClickTrue;
    private onClickFalse onClickFalse;

    public QuestionDialog(int position, Context context, Question question) {
        super(context);
        mPosition = position;
        mQuestion = question;
    }

    public QuestionDialog(Context context, int theme) {
        super(context, theme);
    }

    protected QuestionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_grade_dialog);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_subtitle = (TextView) findViewById(R.id.tv_subtitle);
        TextView tv_mid_content = (TextView) findViewById(R.id.tv_mid_content);
        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        TextView tv_number = (TextView) findViewById(R.id.tv_number);
        TextView tv_true = (TextView) findViewById(R.id.tv_true);
        TextView tv_false = (TextView) findViewById(R.id.tv_false);
        tv_number.setText(mPosition + "");
        if (0 == mPosition) {
            tv_content.setText(mQuestion.getQuestion());
            tv_number.setBackgroundResource(R.drawable.grade_begin);
        } else {
            tv_title.setVisibility(View.GONE);
            tv_subtitle.setVisibility(View.GONE);
            tv_content.setVisibility(View.GONE);
            tv_mid_content.setVisibility(View.VISIBLE);
            tv_mid_content.setText(mQuestion.getQuestion());
        }

        tv_true.setOnClickListener(onClickTrue);
        tv_false.setOnClickListener(onClickFalse);
    }


    public void setOnclick(QuestionDialog.onClickTrue clickTrue, QuestionDialog.onClickFalse clickFalse) {
        this.onClickFalse = clickFalse;
        this.onClickTrue = clickTrue;
    }

    public interface onClickTrue extends View.OnClickListener {
    }

    ;

    public interface onClickFalse extends View.OnClickListener {
    }

    ;
}
