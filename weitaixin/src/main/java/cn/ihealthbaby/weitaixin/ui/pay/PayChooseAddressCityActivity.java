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
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayChooseAreasEvent;
import de.greenrobot.event.EventBus;

public class PayChooseAddressCityActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //


    @Bind(R.id.lvPayChooseAddressArea)
    ListView lvPayChooseAddressArea;


    private String Provinceid;
    private String Areas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_paychoose_address_area);

        ButterKnife.bind(this);

        title_text.setText("所选城市");

        Provinceid = getIntent().getStringExtra("Provinceid");
        Areas = getIntent().getStringExtra("Areas");

        EventBus.getDefault().register(this);

        pullData();
    }


    public void onEventMainThread(PayChooseAreasEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    MyPayChooseAddressCityAdapter adapter;

    private void pullData() {
        adapter = new MyPayChooseAddressCityAdapter(this, null);
        lvPayChooseAddressArea.setAdapter(adapter);

        lvPayChooseAddressArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.currentPosition = position;
                adapter.notifyDataSetChanged();

                City item = (City) adapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), PayChooseAddressAreasActivity.class);
                intent.putExtra("Cityid", item.getCityid());
                intent.putExtra("Areas", Areas + item.getCity());
                startActivity(intent);
            }
        });


        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().addressApi.getCities(Provinceid, 0,
                new DefaultCallback<ApiList<City>>(this, new AbstractBusiness<ApiList<City>>() {
                    @Override
                    public void handleData(ApiList<City> data) {
                        ArrayList<City> list = (ArrayList<City>) data.getList();
                        if (list != null && list.size() > 0) {
                            adapter.setDatas(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.show(getApplicationContext(), "没有更多数据");
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    public class MyPayChooseAddressCityAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<City> datas;
        private LayoutInflater mInflater;
        public int currentPosition = -1;

        public MyPayChooseAddressCityAdapter(Context context, ArrayList<City> datas) {
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

            City city = this.datas.get(position);
            viewHolder.tvName.setText(city.getCity() + "");

            if (currentPosition == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.gray1));
                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.gray1));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white0));
                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.white0));
                viewHolder.tvState.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName)
            TextView tvName;
            @Bind(R.id.tvState)
            ImageView tvState;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

    }


}


