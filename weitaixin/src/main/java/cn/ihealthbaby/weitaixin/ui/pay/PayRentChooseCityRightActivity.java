package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.City;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayChooseCityCloseEvent;
import de.greenrobot.event.EventBus;

public class PayRentChooseCityRightActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.lvPayRightCity) ListView lvPayRightCity;

    private String provinceid;
    private String ProvinceNamed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_city_choose_right);

        ButterKnife.bind(this);

        title_text.setText("选择区/县");

        provinceid = getIntent().getStringExtra("provinceid");
        ProvinceNamed = getIntent().getStringExtra("ProvinceNamed");
        if (TextUtils.isEmpty(ProvinceNamed)) {
            ProvinceNamed = "";
        }
        initView();
        pullData();
    }

    private MyPayRightCityAdapter adapterRight;
    private void initView() {
        adapterRight=new MyPayRightCityAdapter(this,null);
        lvPayRightCity.setAdapter(adapterRight);
        lvPayRightCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterRight.currentPosition = position;
                City item = (City) adapterRight.getItem(position);
                String cityid = item.getCityid();
                String cityName = item.getCity();
                adapterRight.notifyDataSetChanged();

//                Intent intent = new Intent();
//                intent.putExtra("cityid", cityid);
//                intent.putExtra("cityName", ProvinceNamed+cityName);

                PayRentInformationActivity.cityNameText = ProvinceNamed + cityName;

                LocalProductData.getLocal().put(LocalProductData.CityId, cityid);
                LocalProductData.getLocal().put(LocalProductData.CityName, ProvinceNamed + cityName);
//                setResult(PayConstant.resultCodeCityChoose, intent);

                EventBus.getDefault().post(new PayChooseCityCloseEvent());

                PayRentChooseCityRightActivity.this.finish();
            }
        });
    }



    private void pullData() {
        if (TextUtils.isEmpty(provinceid)) {
            ToastUtil.show(getApplicationContext(),"请选择省份");
            return;
        }

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().addressApi.getCities(provinceid, 1,
                new DefaultCallback<ApiList<City>>(this, new AbstractBusiness<ApiList<City>>() {
                    @Override
                    public void handleData(ApiList<City> data) {
                        ArrayList<City> rightCityList = (ArrayList<City>) data.getList();

                        if (rightCityList != null && rightCityList.size() > 0) {
                            adapterRight.setDatas(rightCityList);
                            adapterRight.notifyDataSetChanged();
                        } else {
                            ToastUtil.show(getApplicationContext(), "没有数据~~~");
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
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    public class MyPayRightCityAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<City> datas;
        private LayoutInflater mInflater;
        public int currentPosition=-1;

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
            viewHolder.tvName.setText(provinceName.getCity() + "");

            if (currentPosition == position) {
//                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.white0));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            }else{
//                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.gray1));
                viewHolder.tvState.setVisibility(View.INVISIBLE);
            }

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


