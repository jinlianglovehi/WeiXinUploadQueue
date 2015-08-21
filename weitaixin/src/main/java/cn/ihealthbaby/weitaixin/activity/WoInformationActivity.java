package cn.ihealthbaby.weitaixin.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.view.RoundImageView;


public class WoInformationActivity extends BaseActivity implements MyPoPoWin.ISelectPhoto {

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
    @Bind(R.id.iv_wo_head_icon) RoundImageView iv_wo_head_icon;

    public Bitmap photo;



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


    public MyPoPoWin ppWin;
    @OnClick(R.id.iv_wo_head_icon)
    public void ivWoHeadIcon( ) {
        ppWin = new MyPoPoWin(this);
        ppWin.showAtLocation(iv_wo_head_icon);
        ppWin.setListener(this);
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


    @Override
    public void onSelectPhoto(int flag) {
        switch (flag) {
            case MyPoPoWin.FLAG_TAKE_PHOTO:
//              Toast.makeText(this, "拍照2", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "ketwangwai_temp.jpg")));
                startActivityForResult(intent, PHOTOHRAPH);
                break;

            case MyPoPoWin.FLAG_PICK_PHOTO:
//              Toast.makeText(this,"从相册中选择2",Toast.LENGTH_SHORT).show();
                Intent intent_pick = new Intent(Intent.ACTION_PICK, null);
                intent_pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent_pick, PHOTOZOOM);
                break;
        }
    }


    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            //设置文件保存路径这里放在跟目录下
            File picture = new File(Environment.getExternalStorageDirectory() + "/ketwangwai_temp.jpg");
            startPhotoZoom(Uri.fromFile(picture));
        }

        if (data == null)
            return;

        // 读取相册缩放图片
        if (requestCode == PHOTOZOOM) {
            startPhotoZoom(data.getData());
        }
        // 处理结果
        if (requestCode == PHOTORESOULT) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                photo = extras.getParcelable("data");
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);// (0 - 100)压缩文件
                iv_wo_head_icon.setImageBitmap(photo);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 70);
        intent.putExtra("outputY", 70);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTORESOULT);
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }




}


