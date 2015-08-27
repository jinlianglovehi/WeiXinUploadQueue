package cn.ihealthbaby.weitaixin.activity;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.view.SlideSwitchView;


public class SetSystemUploadActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    @Bind(R.id.slide_switch_upload)
    SlideSwitchView mSlideSwitchViewUpload;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system_upload);

        ButterKnife.bind(this);

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
        mSlideSwitchViewUpload.setSlideListener(new SlideSwitchView.SlideListener() {
            @Override
            public void open() {
                WeiTaiXinApplication.getInstance().putValue("SetSystemUpload", "1");
            }

            @Override
            public void close() {
                WeiTaiXinApplication.getInstance().putValue("SetSystemUpload", "0");
            }
        });
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    private void initView() {
        String SetSystemUpload = WeiTaiXinApplication.getInstance().getValue("SetSystemUpload", "0");
        if ("1".equals(SetSystemUpload)) {
            mSlideSwitchViewUpload.setState(true);
        } else {
            mSlideSwitchViewUpload.setState(false);
        }
    }

}
