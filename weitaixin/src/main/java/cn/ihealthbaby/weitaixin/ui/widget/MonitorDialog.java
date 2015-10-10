package cn.ihealthbaby.weitaixin.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.ihealthbaby.weitaixin.R;

public class MonitorDialog extends Dialog implements View.OnClickListener{


    public TextView tvPayTitle;
    public TextView tvPayNO;
    public TextView tvPayYES;
    public OperationAction operationAction;

    public StringBuffer sb=new StringBuffer();
    public String[] tipStrArr=new String[3];


    public MonitorDialog(Context context, int theme, String[] tipStrArr) {
        super(context, theme);
        this.tipStrArr=tipStrArr;
        setTheme();
    }

    public MonitorDialog(Context context, String[] tipStrArr) {
        super(context);
        this.tipStrArr=tipStrArr;
        setTheme();
    }

    protected MonitorDialog(Context context, boolean cancelable, OnCancelListener cancelListener, String[] tipStrArr) {
        super(context, cancelable, cancelListener);
        this.tipStrArr=tipStrArr;
        setTheme();
    }


    public void setTheme(){
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.monitor_dialog);

        tvPayTitle = (TextView) findViewById(R.id.tvPayTitle);
        tvPayNO = (TextView) findViewById(R.id.tvPayNO);
        tvPayYES = (TextView) findViewById(R.id.tvPayYES);
        tvPayNO.setOnClickListener(this);
        tvPayYES.setOnClickListener(this);
        tvPayTitle.setText(tipStrArr[0] + "");
        tvPayNO.setText(tipStrArr[1] + "");
        tvPayYES.setText(tipStrArr[2] + "");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvPayYES:
                if (operationAction!=null) {
                    dismiss();
                    operationAction.payYes();
                }
                break;

            case R.id.tvPayNO:
                if (operationAction!=null) {
                    dismiss();
                    operationAction.payNo();
                }
                break;
        }
    }


    public interface OperationAction{
        void payYes(Object... obj);
        void payNo(Object... obj);
    }

    ;
}


