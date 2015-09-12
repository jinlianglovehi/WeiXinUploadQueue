package cn.ihealthbaby.weitaixin.ui.pay;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.adapter.PayMimeAddressAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;

public class PayAddAddressActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.tvAddAddressName)   EditText tvAddAddressName;
    @Bind(R.id.tvAddAddressPhone)   EditText tvAddAddressPhone;
    @Bind(R.id.tvAddAddressDetailAddress)   EditText tvAddAddressDetailAddress;
    @Bind(R.id.tvSubmitAddress)   TextView tvSubmitAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_add_address);

        ButterKnife.bind(this);

        title_text.setText("添加地址");

        initView();
        pullData();
    }

    private void initView() {
//        User user=WeiTaiXinApplication.getInstance().user;
        User user = SPUtil.getUser(this);
        if (user!=null){
            tvAddAddressName.setText(user.getName());
            tvAddAddressPhone.setText(user.getMobile());
        }
    }

    private void pullData() {

    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.tvSubmitAddress)
    public void SubmitAddress() {

    }


}


