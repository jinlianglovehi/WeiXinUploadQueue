package cn.ihealthbaby.weitaixinpro.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.ihealthbaby.weitaixinpro.R;

public class MonitorDialog extends Dialog implements View.OnClickListener {
	private TextView title;
	private TextView left;
	private TextView right;
	private OperationAction operationAction;
	private StringBuffer sb = new StringBuffer();
	private String[] tipStrArr = new String[3];

	public MonitorDialog(Context context, int theme, String[] tipStrArr) {
		super(context, theme);
		this.tipStrArr = tipStrArr;
		setTheme();
	}

	public MonitorDialog(Context context, String[] tipStrArr) {
		super(context);
		this.tipStrArr = tipStrArr;
		setTheme();
	}

	protected MonitorDialog(Context context, boolean cancelable, OnCancelListener cancelListener, String[] tipStrArr) {
		super(context, cancelable, cancelListener);
		this.tipStrArr = tipStrArr;
		setTheme();
	}

	public void setTheme() {
		setCanceledOnTouchOutside(true);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.monitor_dialog);
		title = (TextView) findViewById(R.id.title);
		left = (TextView) findViewById(R.id.left);
		right = (TextView) findViewById(R.id.right);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		title.setText(tipStrArr[0] + "");
		left.setText(tipStrArr[1] + "");
		right.setText(tipStrArr[2] + "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.right:
				if (operationAction != null) {
					operationAction.right();
				}
				break;
			case R.id.left:
				if (operationAction != null) {
					operationAction.left();
				}
				break;
		}
	}

	public void setOperationAction(OperationAction operationAction) {
		this.operationAction = operationAction;
	}

	public interface OperationAction {
		void left(Object... obj);

		void right(Object... obj);
	}

	;
}


