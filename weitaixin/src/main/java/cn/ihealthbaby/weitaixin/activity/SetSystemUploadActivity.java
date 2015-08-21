package cn.ihealthbaby.weitaixin.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;


public class SetSystemUploadActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //


    @Bind(R.id.cbUpload) CheckBox cbUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system_upload);

        ButterKnife.bind(this);

        title_text.setText("上传设置");
//      back.setVisibility(View.INVISIBLE);
        
        initView();
    }


    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }



    private void initView() {
        String SetSystemUpload=WeiTaiXinApplication.getInstance().getValue("SetSystemUpload","0");
        if ("1".equals(SetSystemUpload)) {
            cbUpload.setChecked(true);
        } else {
            cbUpload.setChecked(false);
        }
    }


    @OnCheckedChanged(R.id.cbUpload)
    public void cbUpload(){
        if (cbUpload.isChecked()) {
            WeiTaiXinApplication.getInstance().putValue("SetSystemUpload","1");
        }else{
            WeiTaiXinApplication.getInstance().putValue("SetSystemUpload","0");
        }
    }


}
