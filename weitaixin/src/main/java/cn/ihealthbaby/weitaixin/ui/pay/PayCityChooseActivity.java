package cn.ihealthbaby.weitaixin.ui.pay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

public class PayCityChooseActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //
    @Bind(R.id.lvCityChoose)
    ListView lvCityChoose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_city_choose);

        ButterKnife.bind(this);

        title_text.setText("选择城市");


    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }



}


