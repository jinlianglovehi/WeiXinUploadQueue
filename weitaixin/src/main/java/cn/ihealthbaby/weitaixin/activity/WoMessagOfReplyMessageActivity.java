package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.AdviceAsk;
import cn.ihealthbaby.client.model.AdviceReply;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.view.RoundImageView;


public class WoMessagOfReplyMessageActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.iv_wo_head_icon) RoundImageView iv_wo_head_icon;
    @Bind(R.id.tv_wo_head_name) TextView tv_wo_head_name;
    @Bind(R.id.tvAskPurpose) TextView tvAskPurpose;
    @Bind(R.id.tvFeeling) TextView tvFeeling;
    @Bind(R.id.tvQuestion) TextView tvQuestion;
    @Bind(R.id.tvAskTime) TextView tvAskTime;
    //
    @Bind(R.id.ivDoctorHeadPic) RoundImageView ivDoctorHeadPic;
    @Bind(R.id.tvDoctorName) TextView tvDoctorName;
    @Bind(R.id.tvDoctorTitle) TextView tvDoctorTitle;
    @Bind(R.id.tvHospitalName) TextView tvHospitalName;
    @Bind(R.id.tvReplyContext) TextView tvReplyContext;
    @Bind(R.id.tvReplyTime) TextView tvReplyTime;


    //
    private Dialog dialog;
    private ApiManager apiManager;
    private String[] askPurpose = new String[]{"日常监护", "感觉有胎动了", "胎动频繁"};//0 日常监护 1 感觉有胎动了 2 胎动频繁
    private String[] feeling = new String[]{"一般", "高兴", "郁闷"};// 0 一般 1 高兴 2 郁闷

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_message_of_reply_message);

        ButterKnife.bind(this);

        title_text.setText("回复详情");

        apiManager=ApiManager.getInstance();


        long relatedId=getIntent().getLongExtra("AdviceReply", 0);


        dialog=new CustomDialog().createDialog1(this,"加载中...");
        dialog.show();


        ApiManager.getInstance().adviceApi.getReply(relatedId, new HttpClientAdapter.Callback<AdviceReply>() {
            @Override
            public void call(Result<AdviceReply> t) {
                if (t.isSuccess()) {
                    AdviceReply data = t.getData();
                    if (data != null) {
                        AdviceAsk adviceAsk = data.getAdviceAsks().get(0);
                        if (adviceAsk!=null) {
                            ImageLoader.getInstance().displayImage(WeiTaiXinApplication.user.getHeadPic(), iv_wo_head_icon, setDisplayImageOptions());
                            tv_wo_head_name.setText(WeiTaiXinApplication.user.getName());
                            tvAskPurpose.setText("监护目的: "+askPurpose[adviceAsk.getAskPurpose()]);
                            tvFeeling.setText("监护心情: "+feeling[adviceAsk.getFeeling()]);
                            tvQuestion.setText(adviceAsk.getQuestion());
                            tvAskTime.setText(DateTimeTool.date2Str(adviceAsk.getAskTime()));
                        }
                        ImageLoader.getInstance().displayImage(data.getDoctorHeadPic(), ivDoctorHeadPic, setDisplayImageOptions());
                        tvDoctorName.setText(data.getDoctorName());
                        tvDoctorTitle.setText(data.getDoctorTitle());
                        tvHospitalName.setText(data.getHospitalName());
                        tvReplyContext.setText(data.getReplyContext());
                        tvReplyTime.setText(DateTimeTool.date2Str(data.getReplyTime()));
                    } else {
                        ToastUtil.show(getApplicationContext(), t.getMsg());
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsg());
                }
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options=null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
//				.displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }

}



