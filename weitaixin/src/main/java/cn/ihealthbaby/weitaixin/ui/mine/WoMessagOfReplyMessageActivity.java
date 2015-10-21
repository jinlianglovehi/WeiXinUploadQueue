package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import cn.ihealthbaby.client.model.AdviceReply;
import cn.ihealthbaby.client.model.ReplyDetail;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.tools.RelativeDateFormat;
import cn.ihealthbaby.weitaixin.ui.widget.RoundImageView;


public class WoMessagOfReplyMessageActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //

    @Bind(R.id.iv_wo_head_icon)
    RoundImageView iv_wo_head_icon;
    @Bind(R.id.tv_wo_head_name)
    TextView tv_wo_head_name;
    @Bind(R.id.tvAskPurpose)
    TextView tvAskPurpose;
    @Bind(R.id.tvFeeling)
    TextView tvFeeling;
    @Bind(R.id.tvQuestion)
    TextView tvQuestion;
    @Bind(R.id.tvAskTime)
    TextView tvAskTime;
    //
    @Bind(R.id.ivDoctorHeadPic)
    RoundImageView ivDoctorHeadPic;
    @Bind(R.id.tvDoctorName)
    TextView tvDoctorName;
    @Bind(R.id.tvDoctorTitle)
    TextView tvDoctorTitle;
    @Bind(R.id.tvHospitalName)
    TextView tvHospitalName;
    @Bind(R.id.tvReplyContext)
    TextView tvReplyContext;
    @Bind(R.id.tvReplyTime)
    TextView tvReplyTime;
    @Bind(R.id.tv_date)
    TextView mTvDate;

    private long relatedId;
    private long informationId;

    public static String REPLYMESSAGENOTIFICATION = "replymessagenotification";

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

        apiManager = ApiManager.getInstance();


        relatedId = getIntent().getLongExtra("AdviceReply", 0);
        informationId = getIntent().getLongExtra("informationId", 0);


        final CustomDialog customDialog = new CustomDialog();
        dialog = customDialog.createDialog1(this, "加载中...");
        dialog.show();

        ApiManager.getInstance().adviceApi.getReplyDetail(relatedId,
                new DefaultCallback<ReplyDetail>(this, new AbstractBusiness<ReplyDetail>() {
                    @Override
                    public void handleData(ReplyDetail data) {
                        if (data != null) {
                            User user = SPUtil.getUser(WoMessagOfReplyMessageActivity.this);
                            ImageLoader.getInstance().displayImage(user.getHeadPic(), iv_wo_head_icon, setDisplayImageOptions());
                            tv_wo_head_name.setText(user.getName());
                            tvAskPurpose.setText("监护目的: " + data.getAskPurpose());
                            tvFeeling.setText("监护心情: " + data.getFeeling());
                            tvQuestion.setText(data.getQuestion());
                            mTvDate.setText(DateTimeTool.getGestationalWeeks(user.getDeliveryTime()));
                            tvAskTime.setText(DateTimeTool.date2Str(data.getAskTime(), "MM月dd日 HH:mm"));

                            AdviceReply adviceReply = data.getAdviceReply();
                            if (adviceReply != null) {
                                ImageLoader.getInstance().displayImage(adviceReply.getDoctorHeadPic(), ivDoctorHeadPic, setDisplayImageOptions());
                                tvDoctorName.setText(adviceReply.getDoctorName());
                                tvDoctorTitle.setText(adviceReply.getDoctorTitle());
                                tvHospitalName.setText(adviceReply.getHospitalName());
                                tvReplyContext.setText(adviceReply.getReplyContext());
                                tvReplyTime.setText(DateTimeTool.date2Str(adviceReply.getReplyTime(), "MM月dd日 HH:mm"));
//                                tvReplyTime.setText(RelativeDateFormat.format(adviceReply.getReplyTime()));
                            }
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());


        ApiManager.getInstance().informationApi.readInformation(informationId,
                new DefaultCallback<Void>(this, new AbstractBusiness<Void>() {
                    @Override
                    public void handleData(Void data) {
                        Intent intent = new Intent();
                        intent.putExtra("data", informationId);
                        intent.setAction(REPLYMESSAGENOTIFICATION);
                        sendBroadcast(intent);
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                    }
                }), getRequestTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
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



