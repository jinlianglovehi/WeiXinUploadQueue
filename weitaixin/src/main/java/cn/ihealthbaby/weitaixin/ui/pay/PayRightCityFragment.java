package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.City;
import cn.ihealthbaby.client.model.Province;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;


public class PayRightCityFragment extends BaseFragment {

    private final static String TAG = "PayLeftCityFragment";

    @Bind(R.id.lvPayRightCity) ListView lvPayRightCity;

    private BaseActivity context;

    private static ArrayList<Province> rightCityList=new ArrayList<Province>();
    private MyPayRightCityAdapter adapter;
    private Province province;

    private static PayRightCityFragment instance;

    public static PayRightCityFragment getInstance() {
        if (instance == null) {
            instance = new PayRightCityFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_right_city, null);
        ButterKnife.bind(this, view);

        context = (BaseActivity) getActivity();

        initView();

        pullData();

        return view;
    }

    private void pullData() {
        CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(getActivity(), "数据加载中...");
        dialog.show();
        ApiManager.getInstance().addressApi.getCities(province.getProvinceid(), 1, new HttpClientAdapter.Callback<ApiList<City>>() {
            @Override
            public void call(Result<ApiList<City>> t) {
                if (t.isSuccess()) {
                    ApiList<City> data = t.getData();
                    ArrayList<City> list = (ArrayList<City>) data.getList();
                    if (list != null && list.size() > 0) {
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.show(getActivity().getApplicationContext(), "没有更多数据~~~");
                    }
                }else {
                    ToastUtil.show(context,t.getMsgMap()+"");
                }
            }
        },getRequestTag());
    }

    private void initView() {
        adapter=new MyPayRightCityAdapter(getActivity(), rightCityList);
        lvPayRightCity.setAdapter(adapter);
        lvPayRightCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void getRestful(Province province) {
        this.province=province;
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
                this.datas.clear();
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
                convertView = mInflater.inflate(R.layout.item_pay_consult_product, null);
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




