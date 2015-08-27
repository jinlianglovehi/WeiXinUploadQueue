package cn.ihealthbaby.weitaixin.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/*
 * 监听输入内容是否超出最大长度，并设置光标位置
 * */
public class MaxLengthWatcher implements TextWatcher {

	private int maxLen = 0;
	private EditText editText = null;
	private TextView tv_sugg_text_count;

	
	public MaxLengthWatcher(int maxLen, EditText editText,TextView tv_sugg_text_count) {
		this.maxLen = maxLen;
		this.editText = editText;
		this.tv_sugg_text_count=tv_sugg_text_count;
	}

	public void afterTextChanged(Editable arg0) {

	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		Editable editable = editText.getText();
		int len = editable.length();


		tv_sugg_text_count.setText(len+"/200");

	}

}