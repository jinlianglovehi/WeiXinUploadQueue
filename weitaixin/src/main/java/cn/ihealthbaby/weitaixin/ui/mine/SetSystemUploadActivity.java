package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;


public class SetSystemUploadActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    @Bind(R.id.slide_switch_upload)
    ImageView mSlideSwitchViewUpload;

    private SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system_upload);

        ButterKnife.bind(this);
        mySharedPreferences = getSharedPreferences("config",
                Activity.MODE_PRIVATE);
        title_text.setText("上传设置");
//      back.setVisibility(View.INVISIBLE);

        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initListener() {


        mSlideSwitchViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean upload = mySharedPreferences.getBoolean("upload", true);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                if (!upload) {
                    mSlideSwitchViewUpload.setImageResource(R.drawable.switch_on);
                } else {
                    mSlideSwitchViewUpload.setImageResource(R.drawable.switch_off);
                }
                editor.putBoolean("upload", !upload);
                editor.commit();
            }
        });
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    private void initView() {
//        String SetSystemUpload = WeiTaiXinApplication.getInstance().getValue("SetSystemUpload", "0");
        boolean upload = mySharedPreferences.getBoolean("upload", true);
        if (upload) {
            mSlideSwitchViewUpload.setImageResource(R.drawable.switch_on);
        } else {
            mSlideSwitchViewUpload.setImageResource(R.drawable.switch_off);
        }
    }

}
