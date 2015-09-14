package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.OrderItemForm;
import cn.ihealthbaby.client.form.ServiceOrderForm;
import cn.ihealthbaby.client.model.Address;
import cn.ihealthbaby.client.model.Order;
import cn.ihealthbaby.client.model.Product;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;

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
    @Bind(R.id.tvSericeSubmitOrder) TextView tvSericeSubmitOrder;
    @Bind(R.id.tvAddressName) TextView tvAddressName;
    @Bind(R.id.tvAddressPhoneNumber) TextView tvAddressPhoneNumber;
    @Bind(R.id.tvAddressText) TextView tvAddressText;
    @Bind(R.id.tvHospitalAddress) TextView tvHospitalAddress;

    private ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
    private MyGoodsListAdapter myGoodsListAdapter;
    private int priceCount=0;
    private ServiceOrderForm serviceOrderForm=new ServiceOrderForm();
    public ArrayList<OrderItemForm> orderItemForms=new ArrayList<OrderItemForm>();
    private boolean isHospitalFlag=false;
    private long addressId=-1;
    private int deliverType=-1;
    private int hospitalStatus=-1;
    private String hospitalAddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm_order);

        ButterKnife.bind(this);

        title_text.setText("确认订单");

        //0 未启用,1 开通院内,2 开通院外,3 开通院外线上但不支持邮寄, 4 开通院外线上且支持邮寄
        hospitalStatus = (int) LocalProductData.getLocal().get(LocalProductData.HospitalStatus);
        hospitalAddress = (String) LocalProductData.getLocal().get(LocalProductData.HospitalAddress);

        if (hospitalStatus == 3) {
            rlExpressageAction.setVisibility(View.GONE);
        } else if (hospitalStatus == 4) {
            rlExpressageAction.setVisibility(View.VISIBLE);
        }
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

        HashMap<String, String> expressDataMap=new HashMap<String, String>();
        expressDataMap.put("快递费用", "0");
        datas.add(expressDataMap);


        myGoodsListAdapter.setDatas(datas);
        myGoodsListAdapter.notifyDataSetChanged();

        tvPrice.setText("总计￥" + priceCount + "");
        LocalProductData.getLocal().put(LocalProductData.PriceCount, priceCount);
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

            OrderItemForm itemForm=new OrderItemForm();
            itemForm.setAmount(1);
            itemForm.setProductId(product.getId());
            orderItemForms.add(itemForm);
            serviceOrderForm.setItemForms(orderItemForms);
        }
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.rlNoneGet)
    public void NoneGet() {
        Intent intent=new Intent(getApplicationContext(),PayAddAddressActivity.class);
        startActivityForResult(intent, 666);
    }

    @OnClick(R.id.rlExpressageGet)
    public void rlExpressageGet() {
        Intent intent=new Intent(getApplicationContext(),PayMimeAddressActivity.class);
        startActivityForResult(intent, 888);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==888) {
            if (resultCode==999) {
                if (data!=null) {
                    Address addressItem= (Address) data.getSerializableExtra("addressItem");
                    tvAddressName.setText(addressItem.getLinkMan());
                    tvAddressPhoneNumber.setText(addressItem.getMobile());
                    tvAddressText.setText(addressItem.getAddress());
                    addressId=addressItem.getId();
                }
            }
        }
    }

    @OnClick(R.id.tvSericeSubmitOrder)
    public void SericeSubmitOrder() {
        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();


        serviceOrderForm.setDoctorId(Long.parseLong(LocalProductData.getLocal().get(LocalProductData.DoctorId) + ""));
        serviceOrderForm.setHospitalId(Long.parseLong(LocalProductData.getLocal().get(LocalProductData.HospitalId) + ""));

        serviceOrderForm.setItemForms(orderItemForms);
        serviceOrderForm.setDeliverType(deliverType);
        serviceOrderForm.setAddressId(addressId);
        ApiManager.getInstance().orderApi.submitServiceOrder(serviceOrderForm, new HttpClientAdapter.Callback<Order>() {
            @Override
            public void call(Result<Order> result) {
                if (result.isSuccess()) {
                    Order data = result.getData();
                    LogUtil.d("dataInteger", "dataInteger  =  " + data);
                    if (data !=null) {
                        Intent intent = new Intent(getApplicationContext(), PayAffirmPaymentActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtil.show(getApplicationContext(), "尚有未结束的服务");
                    }
                }else {
                    ToastUtil.show(getApplicationContext(), result.getMsgMap()+"");
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }


    @OnClick(R.id.rlExpressageAction)
    public void ExpressageAction() {
        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ivExpressageAction.setImageResource(R.drawable.pay_choose_un);
        ivHospitalAction.setImageResource(R.drawable.pay_choose_un);
        ivExpressageAction.setImageResource(R.drawable.pay_choose);
        rlExpressageGet.setVisibility(View.GONE);
        rlHospitalGet.setVisibility(View.GONE);
        isHospitalFlag=false;
        deliverType=1;
        ApiManager.getInstance().addressApi.getDefAddresss(new HttpClientAdapter.Callback<Address>() {
            @Override
            public void call(Result<Address> t) {
                if (t.isSuccess()) {
                    Address data = t.getData();
                    if (data==null) {
                        rlNoneGet.setVisibility(View.VISIBLE);
                        rlExpressageGet.setVisibility(View.GONE);
                    }else {
                        rlNoneGet.setVisibility(View.GONE);
                        rlExpressageGet.setVisibility(View.VISIBLE);
                        tvAddressName.setText(data.getLinkMan());
                        tvAddressPhoneNumber.setText(data.getMobile());
                        tvAddressText.setText(data.getAddress());
                        addressId=data.getId();
                    }
                }else {
                    ToastUtil.show(getApplicationContext(),t.getMsgMap()+"");
                }
                customDialog.dismiss();
            }
        },getRequestTag());
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
        isHospitalFlag=true;
        deliverType=0;
        addressId=0;
        tvHospitalAddress.setText(hospitalAddress+"");
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


