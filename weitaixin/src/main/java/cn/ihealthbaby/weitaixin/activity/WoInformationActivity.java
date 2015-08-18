package cn.ihealthbaby.weitaixin.activity;

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
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.view.RoundImageView;


public class WoInformationActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.tv_wo_head_name) TextView tv_wo_head_name;
    @Bind(R.id.tv_wo_head_breed_date) TextView tv_wo_head_breed_date;
    @Bind(R.id.tv_wo_head_deliveryTime) TextView tv_wo_head_deliveryTime;
    @Bind(R.id.tv_phone_number) TextView tv_phone_number;
    @Bind(R.id.tv_birthday) TextView tv_birthday;
    @Bind(R.id.tv_sn_number) TextView tv_sn_number;
    @Bind(R.id.tv_place_name) TextView tv_place_name;
    @Bind(R.id.tv_hospital_name) TextView tv_hospital_name;
    @Bind(R.id.tv_doctor_name) TextView tv_doctor_name;
    @Bind(R.id.iv_wo_head_icon)
    RoundImageView iv_wo_head_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_information);

        ButterKnife.bind(this);
        title_text.setText("我的信息");
        if (WeiTaiXinApplication.getInstance().isLogin&& WeiTaiXinApplication.user!=null) {
            User user= WeiTaiXinApplication.user;
            ImageLoader.getInstance().displayImage(WeiTaiXinApplication.user.getHeadPic(), iv_wo_head_icon, setDisplayImageOptions());
            tv_wo_head_name.setText(user.getName()+"");
            tv_wo_head_breed_date.setText("已孕：" + DateTimeTool.getGestationalWeeks(user.getDeliveryTime()));
            tv_wo_head_deliveryTime.setText("预产：" + DateTimeTool.date2Str(user.getDeliveryTime()));
            tv_phone_number.setText(user.getMobile()+"");
            tv_birthday.setText(DateTimeTool.date2Str(user.getBirthday())+"");
            if(user.getServiceInfo()!=null){
                tv_sn_number.setText(user.getServiceInfo().getSerialnum()+ "");
                tv_place_name.setText(user.getServiceInfo().getAreaInfo()+ "");
                tv_hospital_name.setText(user.getServiceInfo().getHospitalName()+ "");
                tv_doctor_name.setText(user.getServiceInfo().getDoctorName()+ "");
            }
        }
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
