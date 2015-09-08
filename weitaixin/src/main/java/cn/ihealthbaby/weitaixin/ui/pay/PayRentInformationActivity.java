package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Doctor;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.model.LocalProductData;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.widget.RoundImageView;

public class PayRentInformationActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.rl1None) RelativeLayout rl1None;
    @Bind(R.id.rl2None) RelativeLayout rl2None;
    @Bind(R.id.gvChooseDoctorList) GridView gvChooseDoctorList;
    @Bind(R.id.tvGotoOrderAction) TextView tvGotoOrderAction;
    @Bind(R.id.tvHospitalName) TextView tvHospitalName;
    @Bind(R.id.tvCityName) TextView tvCityName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_rent_information);

        ButterKnife.bind(this);

        title_text.setText("租凭信息");

        pullData();
    }


    MyDoctorChooseAdapter adapter;
    private void pullData() {
        adapter=new MyDoctorChooseAdapter(this.getApplicationContext(), null);
        gvChooseDoctorList.setAdapter(adapter);
        gvChooseDoctorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.currentPosition = position;
                adapter.notifyDataSetChanged();
                Doctor doctor= (Doctor) adapter.getItem(position);
                LocalProductData.getLocal().put(LocalProductData.DoctorName, doctor.getName());
                LocalProductData.getLocal().put(LocalProductData.DoctorId, doctor.getId());
            }
        });

    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }



    @OnClick(R.id.rl1None)
    public void rl1None() {
        Intent intent=new Intent(this,PayHospitalChooseActivity.class);
        startActivityForResult(intent, PayConstant.requestCodeHospitalChoose);
    }


    @OnClick(R.id.rl2None)
    public void rl2None() {
        Intent intent = new Intent(this, PayHospitalChooseActivity.class);
        startActivityForResult(intent, PayConstant.requestCodeHospitalChoose);
    }



    @OnClick(R.id.tvGotoOrderAction)
    public void GotoOrderAction() {
        if(TextUtils.isEmpty(tvCityName.getText().toString().trim())){
//            ToastUtil.show(getApplicationContext(),"请选择城市");
//            return;
        }
        if(TextUtils.isEmpty(tvHospitalName.getText().toString().trim())){
            ToastUtil.show(getApplicationContext(),"请选择医院");
            return;
        }
        if(adapter.currentPosition==-1){
            ToastUtil.show(getApplicationContext(),"请选择医生");
            return;
        }
        Intent intent = new Intent(this, PayGoingOrderActivity.class);
        startActivity(intent);
    }

    private long hospitalId=-1;
    private String hospitalName="";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayConstant.requestCodeHospitalChoose) {
            if (resultCode == PayConstant.resultCodeHospitalChoose) {
                if (data!=null) {
                    hospitalId = data.getLongExtra("hospitalId", -1);
                    hospitalName = data.getStringExtra("hospitalName");
                    if (hospitalId==-1) {
                        ToastUtil.show(getApplicationContext(), "请选择医院");
                        return;
                    }
                    tvHospitalName.setText(hospitalName);


                    final CustomDialog customDialog=new CustomDialog();
                    Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
                    dialog.show();
                    ApiManager.getInstance().doctorApi.getDoctorsByHospital(hospitalId, new HttpClientAdapter.Callback<ApiList<Doctor>>() {
                        @Override
                        public void call(Result<ApiList<Doctor>> t) {
                            if (t.isSuccess()) {
                                ApiList<Doctor> data = t.getData();
                                ArrayList<Doctor> doctorList = (ArrayList<Doctor>) data.getList();
                                adapter.setDatas(doctorList);
                                adapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                            }
                            customDialog.dismiss();
                        }
                    },getRequestTag());
                }
            }
        }
    }

    public class MyDoctorChooseAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Doctor> datas;
        private LayoutInflater mInflater;
        public int currentPosition=-1;

        public MyDoctorChooseAdapter(Context context, ArrayList<Doctor> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<Doctor> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Doctor>();
            } else {
                this.datas.clear();
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<Doctor> datas) {
            if (datas != null) {
                this.datas.addAll(datas);
            }
        }


        @Override
        public int getCount() {
            return this.datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_pay_choose_doctor_list, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Doctor doctor = this.datas.get(position);
            viewHolder.tvDoctorName.setText(doctor.getName());
            viewHolder.tvDoctorLocation.setText(doctor.getTitle());

            ImageLoader.getInstance().displayImage(doctor.getHeadPic(), viewHolder.ivDoctorPicNone,setDisplayImageOptions());

            if(position == currentPosition){
                viewHolder.ivDoctorPic.setVisibility(View.VISIBLE);
            }else {
                viewHolder.ivDoctorPic.setVisibility(View.INVISIBLE);
            }

            return convertView;
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
                    .build();
            return options;
        }

    }


    static class ViewHolder {
        @Bind(R.id.ivDoctorPic)  RoundImageView ivDoctorPic;
        @Bind(R.id.ivDoctorPicNone) RoundImageView ivDoctorPicNone;
        @Bind(R.id.tvDoctorName) TextView tvDoctorName;
        @Bind(R.id.tvDoctorLocation) TextView tvDoctorLocation;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }


}


