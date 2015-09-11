package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class PayCityChooseActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.lvPayLeftCity) ListView lvPayLeftCity;
    @Bind(R.id.lvPayRightCity) ListView lvPayRightCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_city_choose);

        ButterKnife.bind(this);

        title_text.setText("选择城市");
        initView();
        pullData();
    }

    MyPayLeftCityAdapter adapterLeft;
    MyPayRightCityAdapter adapterRight;
    private void initView() {
        adapterLeft=new MyPayLeftCityAdapter(this,null);
        lvPayLeftCity.setAdapter(adapterLeft);
        lvPayLeftCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Province item = (Province) adapterLeft.getItem(position);
                String provinceid = item.getProvinceid();


                final CustomDialog customDialog=new CustomDialog();
                Dialog dialog = customDialog.createDialog1(PayCityChooseActivity.this, "数据加载中...");
                dialog.show();
                ApiManager.getInstance().addressApi.getCities(provinceid, 1, new HttpClientAdapter.Callback<ApiList<City>>() {
                    @Override
                    public void call(Result<ApiList<City>> t) {
                        if (t.isSuccess()) {
                            ApiList<City> data = t.getData();

                            ArrayList<City> rightCityList = (ArrayList<City>) data.getList();

                            if (rightCityList != null && rightCityList.size() > 0) {
                                adapterRight.setDatas(rightCityList);
                                adapterRight.notifyDataSetChanged();
                            } else {
                                ToastUtil.show(getApplicationContext(), "没有数据~~~");
                            }
                        } else {
                            ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                        }
                        customDialog.dismiss();
                    }
                },getRequestTag());
            }
        });

        adapterRight=new MyPayRightCityAdapter(this,null);
        lvPayRightCity.setAdapter(adapterRight);
        lvPayRightCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                if (t.isSuccess()) {
                    ApiList<Province> data = t.getData();

                    ArrayList<Province> leftCityList = (ArrayList<Province>) data.getList();

                    if (leftCityList != null && leftCityList.size() > 0) {
                        adapterLeft.setDatas(leftCityList);
                        adapterLeft.notifyDataSetChanged();
                    } else {
                        ToastUtil.show(getApplicationContext(), "没有数据~~~");
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
        public int currentPosition;

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

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName)  TextView tvName;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

    }




    public class MyPayRightCityAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<City> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyPayRightCityAdapter(Context context, ArrayList<City> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<City> datas) {
            if (datas == null) {
                this.datas = new ArrayList<City>();
            } else {
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<City> datas) {
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

            City provinceName = this.datas.get(position);
            viewHolder.tvName.setText(provinceName.getCity()+"");

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName)  TextView tvName;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

    }



}


