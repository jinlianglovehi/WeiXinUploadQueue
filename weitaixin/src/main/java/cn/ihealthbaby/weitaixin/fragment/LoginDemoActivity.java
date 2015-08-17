package cn.ihealthbaby.weitaixin.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

public class LoginDemoActivity extends BaseActivity implements LoginFragment.LoginSuccessListener {
	private final static String TAG = "LoginDemoActivity";
	protected InputMethodManager manager;
	private FragmentManager fragmentManager;
	private LoginFragment loginFragment;

	private Fragment hospitalFragment=new Fragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);

		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		loginFragment = new LoginFragment();
		//
		fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.right_out, R.anim.left_in, R.anim.left_out);
		fragmentTransaction.replace(R.id.container, loginFragment).addToBackStack(TAG).commit();
		fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				if (fragmentManager.getBackStackEntryCount() == 0) {
					finish();
				}
			}
		});
	}

	private void showFragment(int container, Fragment fragment, int animIn, int animOut) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(animIn, animOut);
		fragmentTransaction.replace(container, fragment);
		fragmentTransaction.addToBackStack(TAG);
		fragmentTransaction.commit();
	}



	@Override
	public void onLoginSuccess() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
				fragmentTransaction.replace(R.id.container, hospitalFragment).addToBackStack(TAG).commit();
			}
		}, 1000);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
}
