package cn.ihealthbaby.weitaixinpro.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.FetalHeart;
import cn.ihealthbaby.client.model.HClientUser;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.WeiTaiXinProApplication;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.tools.DeviceUuidFactory;
import cn.ihealthbaby.weitaixinpro.ui.MainActivity;
import cn.ihealthbaby.weitaixinpro.ui.adapter.PopupHostIdAdapter;

/**
 * @author by kang on 2015/9/10.
 */
public class LoginActivity extends BaseActivity {

    PopupHostIdAdapter adapter;
    @Bind(R.id.iv_weitaixin)
    ImageView mIvWeitaixin;
    @Bind(R.id.rl_logo)
    RelativeLayout mRlLogo;
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.tv_login_action)
    TextView mTvLoginAction;
    @Bind(R.id.tv_device_id)
    TextView mTvDeviceId;
    @Bind(R.id.rl_login)
    RelativeLayout mRlLogin;

    private List<FetalHeart> mFetalHeartApiList;
    private FetalHeart mFetalHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        adapter = new PopupHostIdAdapter(this);
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFetalHeart = mFetalHeartApiList.get(position);
                adapter.showSelect(position);
            }
        });

        mTvLoginAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFetalHeart != null) {

                    ApiManager.getInstance().hClientAccountApi.login("863425026498381", new HttpClientAdapter.Callback<HClientUser>() {
                        @Override
                        public void call(Result<HClientUser> t) {
                            if (t.isSuccess() && t.getData().getLoginToken() != null) {
                                HClientUser user = t.getData();
                                WeiTaiXinProApplication.getInstance().mAdapter.setAccountToken(user.getLoginToken());
                                Toast.makeText(getApplicationContext(), user.getLoginToken(), Toast.LENGTH_LONG).show();
                                System.out.println("token:" + user.getLoginToken());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                SPUtil.saveHClientUser(getApplication(), user);
                                startActivity(intent);
                                finish();
                            } else {
                                //TODO
                            }
                        }
                    }, getRequestTag());
                } else {
                    Toast.makeText(getApplication(), "null", Toast.LENGTH_LONG).show();
                }
            }
        });

        mTvDeviceId.setText(new DeviceUuidFactory(this).getDeviceUuid());
    }


    private void initData() {
        ApiManager.getInstance().hClientAccountApi.getFetalHearts(new HttpClientAdapter.Callback<ApiList<FetalHeart>>() {
            @Override
            public void call(Result<ApiList<FetalHeart>> t) {
                if (t.isSuccess()) {
                    mFetalHeartApiList = t.getData().getList();
                    mFetalHeart = mFetalHeartApiList.get(0);
                    adapter.addData(mFetalHeartApiList);
                }
            }
        }, getRequestTag());
    }

}
