package cn.ihealthbaby.weitaixin.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.form.UserInfoForm;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;
import cn.ihealthbaby.weitaixin.tools.UploadFileEngine;
import cn.ihealthbaby.weitaixin.view.RoundImageView;

public class InfoEditActivity extends BaseActivity implements MyPoPoWin.ISelectPhoto {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.iv_head_icon_info)
    RoundImageView iv_head_icon_info;
    @Bind(R.id.et_birthdate_info) TextView et_birthdate_info;
    @Bind(R.id.et_date_info) TextView et_date_info;
    @Bind(R.id.tv_info_edit_action) TextView tv_info_edit_action;
    @Bind(R.id.et_name_info) EditText et_name_info;

    public boolean isDone1=false;
    public boolean isDone2=false;
    public boolean isDone3=false;
    public boolean isDone4=false;

    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);
        ButterKnife.bind(this);

        title_text.setText("完善个人信息");

        form=new UserInfoForm();
    }


    @OnClick(R.id.back)
    public void onBack(RelativeLayout view) {
        this.finish();
    }


    public MyPoPoWin ppWin;
    @OnClick(R.id.iv_head_icon_info)
    public void iv_head_icon_info(ImageView iv_head_icon_info) {
        ppWin = new MyPoPoWin(this);
        ppWin.showAtLocation(iv_head_icon_info);
        ppWin.setListener(this);
    }

    private int year, monthOfYear, dayOfMonth, hourOfDay, minute;
    @OnClick(R.id.et_birthdate_info)
    public void et_birthdate_info() {
//        ToastUtil.warn(getApplicationContext(),"birthdate");
//        Toast.makeText(this, "birthdate", Toast.LENGTH_SHORT).show();
        setDate(false,et_birthdate_info);
    }

    @OnClick(R.id.et_date_info)
    public void et_date_info() {
//        ToastUtil.warn(getApplicationContext(),"date");
//        Toast.makeText(this, "date", Toast.LENGTH_SHORT).show();
        setDate(true, et_date_info);
    }

    public void setDate(final boolean bool, final TextView textView ){
        Calendar calendar = Calendar.getInstance();
//      calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        //
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                String dateStr="生日  " + year + "年" + (monthOfYear + 1) + "月" + dayOfMonth+"日";
                SpannableString ss = new SpannableString(dateStr);
                ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FFB8B8B8")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                form.setBirthday(new Date(year,(monthOfYear+1),dayOfMonth));
                if(bool){
                    dateStr="预产日期  " + year + "年"+ (monthOfYear + 1) + "月" + dayOfMonth+"日";
                    ss = new SpannableString(dateStr);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FFB8B8B8")), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(ss);
                    isDone3=true;
                    form.setDeliveryTime(new Date(year,(monthOfYear+1),dayOfMonth));
                }else {
                    textView.setText(ss);
                    isDone4=true;
                }
            }
        }, year, monthOfYear, dayOfMonth);
        datePickerDialog.show();
    }

    UserInfoForm form;
    UploadFileEngine engine;
    Dialog dialog;
    @OnClick(R.id.tv_info_edit_action)
    public void tv_info_edit_action() {
        if (!TextUtils.isEmpty(et_name_info.getText().toString().trim())) {
            isDone2=true;
        }
        if(isDone1&&isDone2&&isDone3&&isDone4){
            form.setName(et_name_info.getText().toString().trim());
            CustomDialog customDialog=new CustomDialog();
            dialog=customDialog.createDialog1(this, "发送中...");
            engine=new UploadFileEngine(this,form,dialog);
            if(photo!=null) {
                dialog.show();
                engine.init(Bitmap2Bytes(photo));
            } else {
                ToastUtil.show(getApplicationContext(), "头像没有");
            }
        }else {
            ToastUtil.show(getApplicationContext(), "请完善个人信息");
        }
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
                iv_head_icon_info.setImageBitmap(photo);
                isDone1=true;
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
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTORESOULT);
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            if (!TextUtils.isEmpty(et_name_info.getText().toString().trim())) {
                isDone2=true;
            }
            if(isDone1&&isDone2&&isDone3&&isDone4){
                return false;
            }else {
                ToastUtil.show(getApplicationContext(), "请完善个人信息");
            }
            return true;
        }

        return false;
    }



}
