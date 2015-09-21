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
import cn.ihealthbaby.client.model.City;
import cn.ihealthbaby.client.model.Province;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayChooseCityCloseEvent;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayEvent;
import de.greenrobot.event.EventBus;

public class PayRentChooseProvincesLeftActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.lvPayLeftCity) ListView lvPayLeftCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_city_choose_left);

        ButterKnife.bind(this);

        title_text.setText("选择城市");
        EventBus.getDefault().register(this);

//        LocalProductData.getLocal().put(LocalProductData.CityId, "");
//        LocalProductData.getLocal().put(LocalProductData.CityName, "");
//        LocalProductData.getLocal().put(LocalProductData.HospitalId, "");
//        LocalProductData.getLocal().put(LocalProductData.HospitalName,"");

        initView();
        pullData();
    }


    public void onEventMainThread(PayChooseCityCloseEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    MyPayLeftCityAdapter adapterLeft;
    private void initView() {
        adapterLeft=new MyPayLeftCityAdapter(this,null);
        lvPayLeftCity.setAdapter(adapterLeft);
        lvPayLeftCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Province item = (Province) adapterLeft.getItem(position);
                String provinceid = item.getProvinceid();
                adapterLeft.currentPosition=position;
                adapterLeft.notifyDataSetChanged();

                Intent intent=new Intent(getApplicationContext(),PayRentChooseCityRightActivity.class);
                intent.putExtra("provinceid", provinceid);
                intent.putExtra("ProvinceNamed", item.getProvince());
                startActivity(intent);
            }
        });

    }

    private void pullData() {
        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        // 0 全部省份    1 筛选有开通线上服务医院的省份
        ApiManager.getInstance().addressApi.getProvinces(1, new HttpClientAdapter.Callback<ApiList<Province>>() {
            @Override
            public void call(Result<ApiList<Province>> t) {
                if (t.getStatus()==Result.SUCCESS) {
                    ApiList<Province> data = t.getData();

                    ArrayList<Province> leftCityList = (ArrayList<Province>) data.getList();

                    if (leftCityList != null && leftCityList.size() > 0) {
                        adapterLeft.setDatas(leftCityList);
                        adapterLeft.notifyDataSetChanged();
                    } else {
                        ToastUtil.show(getApplicationContext(), "没有数据");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }




    public class MyPayLeftCityAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Province> datas;
        private LayoutInflater mInflater;
        public int currentPosition=-1;

        public MyPayLeftCityAdapter(Context context, ArrayList<Province> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<Province> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Province>();
            } else {
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<Province> datas) {
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
                convertView = mInflater.inflate(R.layout.item_pay_city_choose, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Province provinceName = this.datas.get(position);
            viewHolder.tvName.setText(provinceName.getProvince()+"");

            if (currentPosition == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.white0));
                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.white0));
            }else{
                convertView.setBackgroundColor(getResources().getColor(R.color.gray1));
                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.gray1));
            }
            viewHolder.tvState.setVisibility(View.INVISIBLE);

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName)  TextView tvName;
            @Bind(R.id.tvState) ImageView tvState;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

    }



}


