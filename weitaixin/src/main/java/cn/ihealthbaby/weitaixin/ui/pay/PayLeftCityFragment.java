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
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.PayAllOrderAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;


public class PayLeftCityFragment extends BaseFragment {

    private final static String TAG = "PayLeftCityFragment";

    @Bind(R.id.lvPayLeftCity) ListView lvPayLeftCity;


    private BaseActivity context;
    private FragmentManager fragmentManager;
    private Fragment oldFragment;

    private static ArrayList<String> leftCityList=new ArrayList<String>();
    private ArrayList<String> rightCityList=new ArrayList<String>();

    private MyPayLeftCityAdapter adapter;


    private static PayLeftCityFragment instance;

    public static PayLeftCityFragment getInstance(ArrayList<String> leftCityList) {
        if (instance == null) {
            instance = new PayLeftCityFragment();
        }
        if (leftCityList==null) {
            leftCityList=new ArrayList<String>();
        }else{
            PayLeftCityFragment.leftCityList=leftCityList;
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_left_city, null);
        ButterKnife.bind(this, view);

        context = (BaseActivity) getActivity();
        fragmentManager = getFragmentManager();

        Bundle bundle = getArguments();
        leftCityList = (ArrayList<String>) bundle.getSerializable(PayConstant.LeftCityList);



        initView();

        return view;
    }

    private void initView() {
        adapter=new MyPayLeftCityAdapter(getActivity(), leftCityList);
        lvPayLeftCity.setAdapter(adapter);
        lvPayLeftCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayRightCityFragment rightFragment = (PayRightCityFragment) fragmentManager.findFragmentByTag(PayConstant.RightCityList);
                rightFragment.getRestful(rightCityList);
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
        private ArrayList<String> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyPayLeftCityAdapter(Context context, ArrayList<String> datas) {
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




