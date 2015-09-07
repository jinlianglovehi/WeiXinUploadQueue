package cn.ihealthbaby.weitaixin.ui.pay;

import android.content.Context;
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

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.Product;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.adapter.PayMimeAddressAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.model.GoodsList;
import cn.ihealthbaby.weitaixin.model.LocalProductData;

public class PayConfirmOrderActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.lvGoodsList) ListView lvGoodsList;
    @Bind(R.id.ivExpressageAction) ImageView ivExpressageAction;
    @Bind(R.id.ivHospitalAction) ImageView ivHospitalAction;

    @Bind(R.id.rlExpressageAction) RelativeLayout rlExpressageAction;
    @Bind(R.id.rlHospitalAction) RelativeLayout rlHospitalAction;

    @Bind(R.id.rlHospitalGet) RelativeLayout rlHospitalGet;
    @Bind(R.id.rlNoneGet) RelativeLayout rlNoneGet;
    @Bind(R.id.rlExpressageGet) RelativeLayout rlExpressageGet;

    @Bind(R.id.tvHospitalName) TextView tvHospitalName;
    @Bind(R.id.tvDoctorName) TextView tvDoctorName;
    @Bind(R.id.tvPrice) TextView tvPrice;

    private ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
    private MyGoodsListAdapter myGoodsListAdapter;
    private int priceCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm_order);

        ButterKnife.bind(this);

        title_text.setText("确认订单");

        HospitalAction();
        pullData();
    }



    private void pullData() {
        tvPrice.setText("总计￥"+priceCount+"");
        myGoodsListAdapter=new MyGoodsListAdapter(this,null);
        lvGoodsList.setAdapter(myGoodsListAdapter);

        //

        ArrayList<Product> products01= (ArrayList<Product>) LocalProductData.getLocal().get(LocalProductData.Name01);
        ArrayList<Product> products02= (ArrayList<Product>) LocalProductData.getLocal().get(LocalProductData.Name02);
        ArrayList<Product> products03= (ArrayList<Product>) LocalProductData.getLocal().get(LocalProductData.Name03);
        ArrayList<Product> products04= (ArrayList<Product>) LocalProductData.getLocal().get(LocalProductData.Name04);

        productFor(products01);
        productFor(products02);
        productFor(products03);
        productFor(products04);

        myGoodsListAdapter.setDatas(datas);
        myGoodsListAdapter.notifyDataSetChanged();

        tvPrice.setText("总计￥"+priceCount+"");
    }

    public void productFor(ArrayList<Product> productDatas){
        if (productDatas==null) {
            return;
        }
        for(int i=0;i<productDatas.size();i++){
            Product product = productDatas.get(i);
            priceCount+=product.getPrice();
            HashMap<String, String> dataMap=new HashMap<String, String>();
            dataMap.put(product.getName(), product.getPrice()+"");
            datas.add(dataMap);
        }
    }



    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.rlExpressageAction)
    public void ExpressageAction() {
        ivExpressageAction.setImageResource(R.drawable.pay_choose_un);
        ivHospitalAction.setImageResource(R.drawable.pay_choose_un);
        ivExpressageAction.setImageResource(R.drawable.pay_choose);
        rlExpressageGet.setVisibility(View.GONE);
        rlHospitalGet.setVisibility(View.GONE);
        rlNoneGet.setVisibility(View.GONE);
        rlExpressageGet.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.rlHospitalAction)
    public void HospitalAction() {
        ivExpressageAction.setImageResource(R.drawable.pay_choose_un);
        ivHospitalAction.setImageResource(R.drawable.pay_choose_un);
        ivHospitalAction.setImageResource(R.drawable.pay_choose);
        rlExpressageGet.setVisibility(View.GONE);
        rlHospitalGet.setVisibility(View.GONE);
        rlNoneGet.setVisibility(View.GONE);
        rlHospitalGet.setVisibility(View.VISIBLE);
        tvHospitalName.setText(LocalProductData.getLocal().get(LocalProductData.HospitalName) + "");
        tvDoctorName.setText(LocalProductData.getLocal().get(LocalProductData.DoctorName)+"");
    }


    public class MyGoodsListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String,String>> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyGoodsListAdapter(Context context, ArrayList<HashMap<String,String>> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<HashMap<String,String>> datas) {
            if (datas == null) {
                this.datas = new ArrayList<HashMap<String,String>>();
            } else {
                this.datas.clear();
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<HashMap<String,String>> datas) {
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
                convertView = mInflater.inflate(R.layout.item_pay_goods_list, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            HashMap<String, String> goodsList = this.datas.get(position);

            Iterator iter = goodsList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                viewHolder.tvName.setText(entry.getKey()+"");
                viewHolder.tvPrice.setText("￥" + entry.getValue() + "");
            }
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName) TextView tvName;
            @Bind(R.id.tvPrice) TextView tvPrice;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }
    }



}


