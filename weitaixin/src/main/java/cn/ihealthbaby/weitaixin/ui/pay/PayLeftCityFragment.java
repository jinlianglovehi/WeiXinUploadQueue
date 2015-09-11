package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.Order;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.client.model.Product;
import cn.ihealthbaby.client.model.Province;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.PayAllOrderAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;


public class PayLeftCityFragment extends BaseFragment {

    private final static String TAG = "PayLeftCityFragment";

    @Bind(R.id.lvPayLeftCity) ListView lvPayLeftCity;


    private BaseActivity context;
    private FragmentManager fragmentManager;
    private Fragment oldFragment;

    private static ArrayList<Province> leftCityList=new ArrayList<Province>();

    private MyPayLeftCityAdapter adapter;


    private static PayLeftCityFragment instance;

    public static PayLeftCityFragment getInstance(ArrayList<Province> leftCityList) {
        if (instance == null) {
            instance = new PayLeftCityFragment();
        }
//        if (leftCityList==null) {
//            leftCityList=new ArrayList<Province>();
//        }else{
//            PayLeftCityFragment.leftCityList=leftCityList;
//        }
        return instance;
    }
    PayRightCityFragment rightFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_left_city, null);
        ButterKnife.bind(this, view);

        context = (BaseActivity) getActivity();
        fragmentManager = getFragmentManager();


        Bundle bundle = getArguments();
        leftCityList = (ArrayList<Province>) bundle.getSerializable(PayConstant.LeftCityList);

        LogUtil.d("leftCityList","leftCityList=> "+leftCityList.size());
        rightFragment = (PayRightCityFragment) fragmentManager.findFragmentByTag(PayConstant.RightCityList);
        rightFragment.getRestful(leftCityList.get(0));
        rightFragment.pullData();

        initView();

        return view;
    }

    private void initView() {
        adapter=new MyPayLeftCityAdapter(getActivity(), null);
        adapter.setDatas(leftCityList);
        lvPayLeftCity.setAdapter(adapter);
        lvPayLeftCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Province item = (Province) adapter.getItem(position);
                rightFragment.getRestful(item);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void showFragment(int container, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        show(container, fragmentTransaction, fragment);
        fragmentTransaction.commit();
    }

    private void show(int container, FragmentTransaction fragmentTransaction, Fragment fragment){
        if (fragment == null) {
            return;
        }

        if (!fragment.isAdded()) {
            if(oldFragment!=null){
                fragmentTransaction.hide(oldFragment);
            }
            fragmentTransaction.add(container, fragment);
        } else if( oldFragment != fragment){
            fragmentTransaction.hide(oldFragment);
            fragmentTransaction.show(fragment);
        }
        oldFragment = fragment;
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
                convertView = mInflater.inflate(R.layout.item_pay_consult_product, null);
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


}




