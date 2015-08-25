package cn.ihealthbaby.weitaixin.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseFragment;


public class LoginFragment extends BaseFragment {
	private final static String TAG = "LoginFragment";
	private LoginSuccessListener loginSuccessListener;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.fragment_login, null);
		View view=null;
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof LoginSuccessListener) {
			setLoginSuccessListener((LoginSuccessListener) activity);
		} else {
			throw new RuntimeException("activity should implement LoginSuccessListener");
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final EditText username = (EditText) view.findViewById(R.id.etPassword);
		final EditText password = (EditText) view.findViewById(R.id.etPassword);
		username.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String usernameString = username.getText().toString().trim();
				String passwordString = password.getText().toString().trim();
			}
		});
	}


	public LoginSuccessListener getLoginSuccessListener() {
		return loginSuccessListener;
	}

	public void setLoginSuccessListener(LoginSuccessListener listener) {
		this.loginSuccessListener = listener;
	}

	public interface LoginSuccessListener {
		void onLoginSuccess();
	}

}
