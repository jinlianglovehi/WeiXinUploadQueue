package cn.ihealthbaby.weitaixin.ui.pay;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.base.BaseFragment;


public class PayRightCityFragment extends BaseFragment {

    private final static String TAG = "PayLeftCityFragment";

    @Bind(R.id.lvPayRightCity) ListView lvPayRightCity;

    private BaseActivity context;

    private static ArrayList<String> rightCityList=new ArrayList<String>();
    private MyPayRightCityAdapter adapter;


    private static PayRightCityFragment instance;

    public static PayRightCityFragment getInstance(ArrayList<String> rightCityList) {
        if (instance == null) {
            instance = new PayRightCityFragment();
        }
        if (rightCityList == null) {
            PayRightCityFragment.rightCityList = new ArrayList<String>();
        } else {
            PayRightCityFragment.rightCityList = rightCityList;
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_right_city, null);
        ButterKnife.bind(this, view);

        context = (BaseActivity) getActivity();
        Bundle bundle = getArguments();
        rightCityList = (ArrayList<String>) bundle.getSerializable(PayConstant.RightCityList);

        initView();

        return view;
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

    public void getRestful(ArrayList<String> datas) {
        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
    }


    public class MyPayRightCityAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyPayRightCityAdapter(Context context, ArrayList<String> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<String> datas) {
            if (datas == null) {
                this.datas = new ArrayList<String>();
            } else {
                this.datas.clear();
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<String> datas) {
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

            String name = this.datas.get(position);
            viewHolder.tvName.setText(name+"");

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




