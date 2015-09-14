package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.SysMsg;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;


public class WoMessagOfSystemMessageActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //

    @Bind(R.id.tv_title_system_message)
    TextView tv_title_system_message;
    @Bind(R.id.tv_createtime_system_message)
    TextView tv_createtime_system_message;
    @Bind(R.id.tv_context_system_message)
    TextView tv_context_system_message;
    @Bind(R.id.tv_author_system_message)
    TextView tv_author_system_message;

    private Dialog dialog;
    private ApiManager apiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_message_of_system_message);

        ButterKnife.bind(this);

        title_text.setText("系统消息");

        apiManager = ApiManager.getInstance();


        long relatedId = getIntent().getLongExtra("SysMsg", 0);


        dialog = new CustomDialog().createDialog1(this, "加载中...");
        dialog.show();

        ApiManager.getInstance().informationApi.getSysMsg(relatedId, new HttpClientAdapter.Callback<SysMsg>() {
            @Override
            public void call(Result<SysMsg> t) {
                if (t.isSuccess()) {
                    SysMsg data = t.getData();
                    if (data != null) {
                        tv_title_system_message.setText(data.getTitle());
                        tv_createtime_system_message.setText(DateTimeTool.date2Str(data.getCreateTime(),"MM月dd日"));
                        tv_context_system_message.setText(data.getContext());
                        tv_author_system_message.setText(data.getAuthor());
                    } else {
                        ToastUtil.show(getApplicationContext(), t.getMsg());
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsg());
                }
                dialog.dismiss();
            }
        }, getRequestTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


}



