package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Product;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayEvent;
import de.greenrobot.event.EventBus;

public class PayOrderInformationActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.lvCashPledgeProduct) ListView lvCashPledgeProduct;
    @Bind(R.id.lvRentProduct) ListView lvRentProduct;
    @Bind(R.id.lvCouplingProduct) ListView lvCouplingProduct;
    @Bind(R.id.lvConsultProduct) ListView lvConsultProduct;

    @Bind(R.id.tvPriceGoingOrder) TextView tvPriceGoingOrder;
    @Bind(R.id.tvVerifyOrder) TextView tvVerifyOrder;
    private int priceCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_going_order);

        ButterKnife.bind(this);

        title_text.setText("订单信息");

        EventBus.getDefault().register(this);

        pullData();
    }


    public void onEventMainThread(PayEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private ArrayList<Product> cashPledgeProduct = new ArrayList<Product>();
    private ArrayList<Product> rentProduct = new ArrayList<Product>();
    private ArrayList<Product> couplingProduct = new ArrayList<Product>();
    private ArrayList<Product> consultProduct = new ArrayList<Product>();

    private MyCashPledgeProductAdapter myCashPledgeProductAdapter;
    private MyRentProductAdapter myRentProductAdapter;
    private MyCouplingProductAdapter myCouplingProductAdapter;
    private MyConsultProductAdapter myConsultProductAdapter;

    private void pullData() {
        myCashPledgeProductAdapter=new MyCashPledgeProductAdapter(this,null);
        lvCashPledgeProduct.setAdapter(myCashPledgeProductAdapter);
        myRentProductAdapter=new MyRentProductAdapter(this,null);
        lvRentProduct.setAdapter(myRentProductAdapter);
        myCouplingProductAdapter=new MyCouplingProductAdapter(this,null);
        lvCouplingProduct.setAdapter(myCouplingProductAdapter);
        myConsultProductAdapter=new MyConsultProductAdapter(this,null);
        lvConsultProduct.setAdapter(myConsultProductAdapter);

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().productApi.getInitProducts(1, new HttpClientAdapter.Callback<ApiList<Product>>() {
            @Override
            public void call(Result<ApiList<Product>> t) {
                if (t.isSuccess()) {
                    ApiList<Product> data = t.getData();
                    ArrayList<Product> list = (ArrayList<Product>) data.getList();

                    //商品类型 0 押金, 1 耗材包 , 2 租金 ,3 咨询费
                    for (int i = 0; i < list.size(); i++) {
                        Product product = list.get(i);
                        priceCount += product.getPrice();
                        int productType = product.getProductType();
                        if (productType == 0) {
                            cashPledgeProduct.add(product);
                        } else if (productType == 1) {
                            couplingProduct.add(product);
                        } else if (productType == 2) {
                            rentProduct.add(product);
                        } else if (productType == 3) {
                            consultProduct.add(product);
                        }
                    }

                    LogUtil.d("cashPledgeProductaa", "cashPledgeProduct==%s=> %s", cashPledgeProduct.size(), cashPledgeProduct);
                    myCashPledgeProductAdapter.setDatas(cashPledgeProduct);
                    myCashPledgeProductAdapter.notifyDataSetChanged();
                    LocalProductData.getLocal().put(LocalProductData.Name01, cashPledgeProduct);

                    myRentProductAdapter.setDatas(rentProduct);
                    myRentProductAdapter.notifyDataSetChanged();
                    LocalProductData.getLocal().put(LocalProductData.Name02, rentProduct);

                    myCouplingProductAdapter.setDatas(couplingProduct);
                    myCouplingProductAdapter.notifyDataSetChanged();
                    LocalProductData.getLocal().put(LocalProductData.Name03, couplingProduct);

                    myConsultProductAdapter.setDatas(consultProduct);
                    myConsultProductAdapter.notifyDataSetChanged();
                    LocalProductData.getLocal().put(LocalProductData.Name04, consultProduct);


                    tvPriceGoingOrder.setText("总计￥"+priceCount);
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

    @OnClick(R.id.tvVerifyOrder)
    public void VerifyOrder() {
        Intent intent=new Intent(this, PayConfirmOrderActivity.class);
        startActivity(intent);
    }



    public class MyCashPledgeProductAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Product> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyCashPledgeProductAdapter(Context context, ArrayList<Product> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<Product> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Product>();
            } else {
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<Product> datas) {
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
                convertView = mInflater.inflate(R.layout.item_pay_cashpledge_product, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Product product = this.datas.get(position);
            viewHolder.tvName.setText(product.getName()+"");
            viewHolder.tvPrice.setText("￥"+product.getPrice()+"");

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


    public class MyRentProductAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Product> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyRentProductAdapter(Context context, ArrayList<Product> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<Product> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Product>();
            } else {
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<Product> datas) {
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
                convertView = mInflater.inflate(R.layout.item_pay_rent_product, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Product product = this.datas.get(position);
            viewHolder.tvName.setText(product.getName()+"");
            viewHolder.tvPrice.setText("￥"+product.getPrice()+"");

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


    public class MyCouplingProductAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Product> datas;
        private LayoutInflater mInflater;
        public int currentPosition;
        public HashMap<Integer,Integer> countGoods=new HashMap<Integer,Integer>();

        public MyCouplingProductAdapter(Context context, ArrayList<Product> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
            LocalProductData.getLocal().put(LocalProductData.CountGoods, countGoods);
        }

        public void setDatas(ArrayList<Product> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Product>();
            } else {
                this.datas = datas;
            }
            initSet();
        }

        public void initSet(){
            countGoods.clear();
            for (int i = 0; i < this.datas.size(); i++) {
                countGoods.put(i,1);
            }
        }

        public void addDatas(ArrayList<Product> datas) {
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

        ViewHolder viewHolder = null;
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_pay_coupling_product, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Product product = this.datas.get(position);
            viewHolder.tvName.setText(product.getName()+"");
            viewHolder.tvPrice.setText("￥" + product.getPrice() + "");

            int tCount = countGoods.get(position);
            viewHolder.tvCountText.setText(tCount+"");

            viewHolder.tvReduceOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int integer = countGoods.get(position);
                    LogUtil.d("countGoodsinteger","countGoodsinteger= %s ",integer);
                    if (integer == 1) {
                        viewHolder.tvCountText.setText("1");
                        countGoods.put(position, 1);
                    } else {
                        countGoods.put(position, (integer - 1));
                        viewHolder.tvCountText.setText(countGoods.get(position) + "");
                        priceCount -= product.getPrice();
                    }
                    tvPriceGoingOrder.setText("总计￥"+priceCount);
                    notifyDataSetChanged();
                }
            });

            viewHolder.tvAddOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int integer = countGoods.get(position);
                    LogUtil.d("countGoodsinteger","countGoodsinteger= %s ",integer);
                    if (integer == product.getMaxAmount()) {
                        viewHolder.tvCountText.setText(product.getMaxAmount() + "");
                        countGoods.put(position, product.getMaxAmount());
                    } else {
                        countGoods.put(position, (integer + 1));
                        viewHolder.tvCountText.setText(countGoods.get(position) + "");
                        priceCount += product.getPrice();
                    }
                    tvPriceGoingOrder.setText("总计￥"+priceCount);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName) TextView tvName;
            @Bind(R.id.tvPrice) TextView tvPrice;
            @Bind(R.id.tvReduceOne) TextView tvReduceOne;
            @Bind(R.id.tvCountText) TextView tvCountText;
            @Bind(R.id.tvAddOne) TextView tvAddOne;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }
    }


    public class MyConsultProductAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Product> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyConsultProductAdapter(Context context, ArrayList<Product> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<Product> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Product>();
            } else {
                this.datas.clear();
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<Product> datas) {
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

            Product product = this.datas.get(position);
            viewHolder.tvName.setText(product.getName()+"");
            viewHolder.tvPrice.setText("￥"+product.getPrice()+"");

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


