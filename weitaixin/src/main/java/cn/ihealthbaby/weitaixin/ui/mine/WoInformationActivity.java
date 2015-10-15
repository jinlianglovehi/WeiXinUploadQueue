package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.Service;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.widget.MyPoPoWin;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.tools.ImageTool;
import cn.ihealthbaby.weitaixin.UploadFileEngine;
import cn.ihealthbaby.weitaixin.ui.widget.RoundImageView;


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
    @Bind(R.id.tv_doctor_advisory_number) TextView tv_doctor_advisory_number;
    @Bind(R.id.tv_doctor_surplus_advisory_number) TextView tv_doctor_surplus_advisory_number;
    @Bind(R.id.iv_wo_head_icon) RoundImageView iv_wo_head_icon;

    public Bitmap photo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_information);

        ButterKnife.bind(this);
        title_text.setText("我的信息");


    }


    private void pullData(){
        final CustomDialog customDialog = new CustomDialog();
        customDialog.createDialog1(this, "获取数据中...");
        customDialog.show();
        ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(this, new AbstractBusiness<User>() {
            @Override
            public void handleData(User data) {
                SPUtil.saveUser(WoInformationActivity.this, data);
                initInformation(data);
                LogUtil.d("User==>", "User==> " + data.toString());
                customDialog.dismiss();
            }

            @Override
            public void handleException(Exception e) {
                customDialog.dismiss();
            }

            @Override
            public void handleResult(Result<User> result) {
                super.handleResult(result);
                customDialog.dismiss();
            }
        }), getRequestTag());
    }


    public void initInformation(User user){
//        User user = SPUtil.getUser(this);
        if (SPUtil.isLogin(this) && user != null) {
            LogUtil.d("User2==>", "User2==> " + user.toString());
            ImageLoader.getInstance().displayImage(user.getHeadPic(), iv_wo_head_icon, setDisplayImageOptions());
            tv_wo_head_name.setText(user.getName() + "");
            tv_wo_head_breed_date.setText("已孕：" + DateTimeTool.getGestationalWeeks(user.getDeliveryTime()));
            tv_wo_head_deliveryTime.setText("预产：" + DateTimeTool.date2Str(user.getDeliveryTime(), "yyyy年MM月dd日"));
            tv_phone_number.setText(user.getMobile()+"");
            tv_birthday.setText(DateTimeTool.date2Str(user.getBirthday(), "MM月dd日")+"");

            ServiceInfo serviceInfo = user.getServiceInfo();
            if(serviceInfo!=null){
                tv_sn_number.setText(serviceInfo.getSerialnum()+ "");
                tv_place_name.setText(serviceInfo.getAreaInfo()+ "");
                tv_hospital_name.setText(serviceInfo.getHospitalName()+ "");
                tv_doctor_name.setText(serviceInfo.getDoctorName()+ "");

                tv_doctor_advisory_number.setText(serviceInfo.getTotalCount()+ "");
                tv_doctor_surplus_advisory_number.setText(serviceInfo.getTotalCount() - serviceInfo.getUsedCount() + "");

                if (serviceInfo.getTotalCount() == -1) {
                    tv_doctor_advisory_number.setText("无限次");
                    tv_doctor_surplus_advisory_number.setText("无限次");
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullData();

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
                Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent_camera, MyPoPoWin.FLAG_TAKE_PHOTO);
                break;

            case MyPoPoWin.FLAG_PICK_PHOTO:
//              Toast.makeText(this,"从相册中选择2",Toast.LENGTH_SHORT).show();
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, MyPoPoWin.FLAG_PICK_PHOTO);
                } else {
                    ToastUtil.show(getApplicationContext(), "请确认已经插入SD卡");
                }
                break;
        }
    }


    public UploadFileEngine engine;
    public void upLoadHeadPic(){
        CustomDialog customDialog=new CustomDialog();
        Dialog dialog=customDialog.createDialog1(this, "头像上传中...");
        engine=new UploadFileEngine(this,null ,customDialog);
        if(photo!=null) {
            dialog.show();
            engine.isUpdateHeadPic=true;
            engine.init(ImageTool.Bitmap2Bytes(photo));
        } else {
            ToastUtil.show(getApplicationContext(), "头像没有");
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (requestCode) {
            case MyPoPoWin.FLAG_TAKE_PHOTO: //拍照
                Bundle bundle  = data.getExtras();
                if (bundle != null) {
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    if(bitmap!=null){
                        bitmap=ImageTool.compressBitmap(bitmap);
                        photo=bitmap;
                        iv_wo_head_icon.setImageBitmap(bitmap);
                        upLoadHeadPic();
                    }
                }
                break;

            case MyPoPoWin.FLAG_PICK_PHOTO:
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(data.getData()));
                    if(bitmap!=null){
                        bitmap=ImageTool.compressBitmap(bitmap);
                        photo=bitmap;
                        iv_wo_head_icon.setImageBitmap(bitmap);
                        upLoadHeadPic();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }





}


