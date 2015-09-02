package cn.ihealthbaby.weitaixin.ui.record;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

public class RecordPlayActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.ivActionImage) ImageView ivActionImage;
    @Bind(R.id.tvStateText) TextView tvStateText;


    private String stateFlag="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_play);
        ButterKnife.bind(this);

        title_text.setText("胎心监测");
        function.setVisibility(View.VISIBLE);
        function.setText("监护心情");

        initText();
    }

    private void initText() {
        stateFlag = getIntent().getStringExtra("status");
        if("问医生".equals(stateFlag)) {
            tvStateText.setText("问医生");
//            ivActionImage.setImageResource();
        }else if("等待回复".equals(stateFlag)){
            tvStateText.setText("等待回复");
        }else if("已回复".equals(stateFlag)){
            tvStateText.setText("已回复");
        }else if("需上传".equals(stateFlag)){
            tvStateText.setText("需上传");
        }
    }


    @OnClick(R.id.function)
    public void Function() {

    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.ivActionImage)
    public void ActionImage() {

    }


}

