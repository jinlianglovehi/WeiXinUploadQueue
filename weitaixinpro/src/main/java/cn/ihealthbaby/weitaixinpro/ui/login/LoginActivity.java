package cn.ihealthbaby.weitaixinpro.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.ui.MainActivity;
import cn.ihealthbaby.weitaixinpro.ui.adapter.PopupHostIdAdapter;

/**
 * @author by kang on 2015/9/10.
 */
public class LoginActivity extends BaseActivity {
    @Bind(R.id.iv_weitaixin)
    ImageView mIvWeitaixin;
    @Bind(R.id.rl_logo)
    RelativeLayout mRlLogo;
    @Bind(R.id.tv_host_id)
    TextView mTvHostId;
    @Bind(R.id.tv_login_action)
    TextView mTvLoginAction;

    private PopupWindow mPopupWindow;
    private String[] name = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvHostId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindow();
            }
        });

        mTvLoginAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initPopWindow() {

        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popupwindow, null);
        contentView.setBackgroundColor(Color.WHITE);

        mPopupWindow = new PopupWindow(findViewById(R.id.rl_login), mTvHostId.getWidth(), mTvHostId.getHeight() * 3);
        mPopupWindow.setContentView(contentView);

        ListView listView = (ListView) contentView.findViewById(R.id.list);
        PopupHostIdAdapter adapter = new PopupHostIdAdapter(name, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTvHostId.setText(name[position]);
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        mPopupWindow.showAsDropDown(mTvHostId);
    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;

        }

        return super.onTouchEvent(event);

    }

}
