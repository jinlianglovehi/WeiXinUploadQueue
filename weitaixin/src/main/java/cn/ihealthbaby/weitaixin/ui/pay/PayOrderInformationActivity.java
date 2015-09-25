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
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Product;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayEvent;
import cn.ihealthbaby.weitaixin.ui.widget.CustomListView;
import de.greenrobot.event.EventBus;

public class PayOrderInformationActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.lvCashPledgeProduct) CustomListView lvCashPledgeProduct;
    @Bind(R.id.lvRentProduct) CustomListView lvRentProduct;
    @Bind(R.id.lvCouplingProduct) CustomListView lvCouplingProduct;
    @Bind(R.id.lvConsultProduct) CustomListView lvConsultProduct;

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


    private boolean isAddPrice=true;

    private void pullData() {
        myCashPledgeProductAdapter=new MyCashPledgeProductAdapter(this,null);
        lvCashPledgeProduct.setAdapter(myCashPledgeProductAdapter);
        myRentProductAdapter=new MyRentProductAdapter(this,null);
        lvRentProduct.setAdapter(myRentProductAdapter);
        myCouplingProductAdapter=new MyCouplingProductAdapter(this,null);
        lvCouplingProduct.setAdapter(myCouplingProductAdapter);
        myConsultProductAdapter=new MyConsultProductAdapter(this,null);
        lvConsultProduct.setAdapter(myConsultProductAdapter);


        lvRentProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myRentProductAdapter.oldPosition = myRentProductAdapter.currentPosition;
                myRentProductAdapter.currentPosition = position;
                myRentProductAdapter.notifyDataSetChanged();

                Product product = (Product) myRentProductAdapter.getItem(position);
                Product oldProduct = (Product) myRentProductAdapter.getItem(myRentProductAdapter.oldPosition);

                priceCount -= oldProduct.getPrice();
                priceCount += product.getPrice();
                tvPriceGoingOrder.setText("总计￥" + (priceCount/100) + "");
            }
        });

        long HospitalId = (long) LocalProductData.getLocal().get(LocalProductData.HospitalId);

        if (HospitalId <= 0) {
            ToastUtil.show(getApplicationContext(), "医院Id不正确");
            return;
        }

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().productApi.getInitProducts(HospitalId,
                new DefaultCallback<ApiList<Product>>(this, new AbstractBusiness<ApiList<Product>>() {
                    @Override
                    public void handleData(ApiList<Product> data) {
                        ArrayList<Product> list = (ArrayList<Product>) data.getList();

                        //商品类型 0 押金, 1 耗材包 , 2 租金 ,3 咨询费
                        for (int i = 0; i < list.size(); i++) {
                            Product product = list.get(i);

                            int productType = product.getProductType();
                            if (productType == 0) {
                                cashPledgeProduct.add(product);
                                priceCount += product.getPrice();
                            } else if (productType == 1) {
                                couplingProduct.add(product);
                                priceCount += product.getPrice();
                            } else if (productType == 2) {
                                rentProduct.add(product);
                                if(isAddPrice){
                                    priceCount += product.getPrice();
                                    isAddPrice=false;
                                }
                            } else if (productType == 3) {
                                consultProduct.add(product);
                                priceCount += product.getPrice();
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


                        tvPriceGoingOrder.setText("总计"+PayUtils.showPrice(priceCount));

                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }

                }),getRequestTag());
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.tvVerifyOrder)
    public void VerifyOrder() {

        ArrayList<Product> rentProduct = myRentProductAdapter.datas;
        Product product = rentProduct.get(myRentProductAdapter.currentPosition);
//        rentProduct.clear();

        ArrayList<Product> rentProductNew=new ArrayList<Product>();
        rentProductNew.add(product);
        LocalProductData.getLocal().put(LocalProductData.Name02, rentProductNew);


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
            viewHolder.tvPrice.setText("￥"+(product.getPrice()/100)+"");

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
        public int currentPosition=0;
        public int oldPosition=0;

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
            viewHolder.tvName.setText(product.getName() + "");
            viewHolder.tvPrice.setText(PayUtils.showPrice(product.getPrice()));
            viewHolder.tvDescription.setText(product.getDescription()+"");

            if (currentPosition == position) {
                viewHolder.ivAddressImage.setImageResource(R.drawable.pay_choose);
            } else {
                viewHolder.ivAddressImage.setImageResource(R.drawable.pay_choose_un);
            }

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName) TextView tvName;
            @Bind(R.id.tvPrice) TextView tvPrice;
            @Bind(R.id.tvDescription) TextView tvDescription;
            @Bind(R.id.ivAddressImage)  ImageView ivAddressImage;

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
        public HashMap<Integer,Integer> goodsTotalPrice =new HashMap<Integer,Integer>();

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
            goodsTotalPrice.clear();
            for (int i = 0; i < this.datas.size(); i++) {
                countGoods.put(i,1);
            }
            for (int i = 0; i < this.datas.size(); i++) {
                goodsTotalPrice.put(i, this.datas.get(i).getPrice());
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
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_pay_coupling_product, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Product product = this.datas.get(position);
            final int goodsTPrice=goodsTotalPrice.get(position);
            viewHolder.tvName.setText(product.getName()+"");
            viewHolder.tvPrice.setText(PayUtils.showPrice(goodsTPrice));

            int tCount = countGoods.get(position);
            viewHolder.tvCountText.setText(tCount + "");


            if (tCount == 1) {
                viewHolder.tvReduceOne.setTextColor(getResources().getColor(R.color.black4));
                viewHolder.tvAddOne.setTextColor(getResources().getColor(R.color.black0));
            }

            if (tCount == product.getMaxAmount()) {
                viewHolder.tvReduceOne.setTextColor(getResources().getColor(R.color.black0));
                viewHolder.tvAddOne.setTextColor(getResources().getColor(R.color.black4));
            }

            if (tCount > 1 && tCount < product.getMaxAmount()) {
                viewHolder.tvReduceOne.setTextColor(getResources().getColor(R.color.black0));
                viewHolder.tvAddOne.setTextColor(getResources().getColor(R.color.black0));
            }


            viewHolder.tvReduceOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int coundNumber = countGoods.get(position);
                    LogUtil.d("countGoodsinteger","countGoodsinteger= %s ",coundNumber);
                    if (coundNumber == 1) {
                        viewHolder.tvCountText.setText("1");
//                        int singlePrice=product.getPrice()/countGoods.get(position);
                        countGoods.put(position, 1);
                        goodsTotalPrice.put(position, product.getPrice() * (countGoods.get(position)));
//                        product.setPrice(singlePrice * (countGoods.get(position)));
                        ToastUtil.show(context,"数量至少一个");
                    } else {
//                        int singlePrice=product.getPrice()/countGoods.get(position);
                        countGoods.put(position, (coundNumber - 1));
                        goodsTotalPrice.put(position, product.getPrice() * (countGoods.get(position)));
//                        product.setPrice(singlePrice * (countGoods.get(position)));
                        viewHolder.tvCountText.setText(countGoods.get(position) + "");
                        priceCount -= product.getPrice();
                    }
                    tvPriceGoingOrder.setText("总计"+PayUtils.showPrice(priceCount));
                    notifyDataSetChanged();
                }
            });

            viewHolder.tvAddOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int coundNumber = countGoods.get(position);
                    LogUtil.d("countGoodsinteger","countGoodsinteger= %s ",coundNumber);
                    if (coundNumber == product.getMaxAmount()) {
                        viewHolder.tvCountText.setText(product.getMaxAmount() + "");
//                        int singlePrice=product.getPrice()/countGoods.get(position);
                        countGoods.put(position, product.getMaxAmount());
                        goodsTotalPrice.put(position, product.getPrice() * (countGoods.get(position)));
//                        product.setPrice(singlePrice * (countGoods.get(position)));
                        ToastUtil.show(context, "数量最多"+product.getMaxAmount()+"个");
                    } else {
//                        int singlePrice=product.getPrice()/countGoods.get(position);
                        countGoods.put(position, (coundNumber + 1));
                        goodsTotalPrice.put(position, product.getPrice() * (countGoods.get(position)));
//                        product.setPrice(singlePrice * (countGoods.get(position)));
                        viewHolder.tvCountText.setText(countGoods.get(position) + "");
                        priceCount += product.getPrice();
                    }
                    tvPriceGoingOrder.setText("总计"+PayUtils.showPrice(priceCount));
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
            viewHolder.tvPrice.setText(PayUtils.showPrice(product.getPrice()));
            viewHolder.tvDescription.setText(product.getDescription()+"");

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName) TextView tvName;
            @Bind(R.id.tvPrice) TextView tvPrice;
            @Bind(R.id.tvDescription) TextView tvDescription;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }
    }


}


