package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Hospital;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class PayHospitalChooseActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //

    @Bind(R.id.lvHospitalChoose) ListView lvHospitalChoose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_hospital_choose);

        ButterKnife.bind(this);

        title_text.setText("选择医院");

        pullDataFirst();
    }

    private MyHospitalAdapter adapter;
    public int resultCode=122;
    private void pullDataFirst() {
        adapter=new MyHospitalAdapter(this, null);
        lvHospitalChoose.setAdapter(adapter);
        lvHospitalChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.currentPosition = position;
                long hospitalId=adapter.datas.get(position).getId();
                adapter.notifyDataSetChanged();
                Intent intent=new Intent();
                intent.putExtra("hospitalId", hospitalId);
                intent.putExtra("hospitalName", adapter.datas.get(position).getName());
                setResult(PayConstant.resultCodeHospitalChoose, intent);
                finish();
            }
        });


        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().hospitalApi.getHospitalsByCity("110100", new HttpClientAdapter.Callback<ApiList<Hospital>>() {
            @Override
            public void call(Result<ApiList<Hospital>> t) {
                if (t.isSuccess()) {
                    ApiList<Hospital> data = t.getData();
                    ArrayList<Hospital> hospitalList = (ArrayList<Hospital>) data.getList();
                    if (hospitalList.size()<=0) {
                        ToastUtil.show(getApplicationContext(), "没有数据");
                    }
                    adapter.setDatas(hospitalList);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        },getRequestTag());
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    public class MyHospitalAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Hospital> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyHospitalAdapter(Context context, ArrayList<Hospital> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<Hospital> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Hospital>();
            } else {
                this.datas.clear();
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<Hospital> datas) {
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
                convertView = mInflater.inflate(R.layout.item_pay_hospital_choose, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Hospital hospital = this.datas.get(position);
            viewHolder.tvHospitalName.setText(hospital.getName());

            if(position == currentPosition){
                viewHolder.ivHospitalImage.setVisibility(View.VISIBLE);
            }else {
                viewHolder.ivHospitalImage.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

    }
    static class ViewHolder {
        @Bind(R.id.ivHospitalImage)  ImageView ivHospitalImage;
        @Bind(R.id.tvHospitalName) TextView tvHospitalName;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}


