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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.OrderDetail;
import cn.ihealthbaby.client.model.OrderItem;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.widget.PayDialog;

public class PayOrderDetailsActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //


//    @Bind(R.id.rlExpressageAction) RelativeLayout rlExpressageAction;


    @Bind(R.id.tvAddressName) TextView tvAddressName;
    @Bind(R.id.tvAddressPhoneNumber) TextView tvAddressPhoneNumber;
    @Bind(R.id.tvAddressText) TextView tvAddressText;
    @Bind(R.id.tvOrderGoodsNumber) TextView tvOrderGoodsNumber;
    @Bind(R.id.lvGoodsList) ListView lvGoodsList;
    @Bind(R.id.tvPayAffirmGoodsOrGoPay) TextView tvPayAffirmGoodsOrGoPay;
    @Bind(R.id.tvCancelOrder) TextView tvCancelOrder;

//    private int hospitalStatus=-1;

    private String orderId;
    private int orderStatus;
    private MyGoodsListAdapter adapter;
    private ArrayList<OrderItem> orderItems;
    OrderDetail data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order_details_new);

        ButterKnife.bind(this);

        title_text.setText("订单详情");

        orderId = getIntent().getStringExtra("orderId");


        //0 未启用,1 开通院内,2 开通院外,3 开通院外线上但不支持邮寄, 4 开通院外线上且支持邮寄
//        hospitalStatus = (int) LocalProductData.getLocal().get(LocalProductData.HospitalStatus);
//        hospitalAddress = (String) LocalProductData.getLocal().get(LocalProductData.HospitalAddress);

//        if (hospitalStatus == 3) {
//            rlExpressageAction.setVisibility(View.GONE);
//        } else if (hospitalStatus == 4) {
//            rlExpressageAction.setVisibility(View.VISIBLE);
//        }


        pullData();
    }



    private void pullData() {
        adapter = new MyGoodsListAdapter(this, null);
        lvGoodsList.setAdapter(adapter);

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().orderApi.getOrder(Integer.parseInt(orderId), new HttpClientAdapter.Callback<OrderDetail>() {
            @Override
            public void call(Result<OrderDetail> t) {
                if (t.isSuccess()) {
                     data = t.getData();
                    tvOrderGoodsNumber.setText(data.getId() + "");
                    tvAddressName.setText(data.getAddress().getLinkMan());
                    tvAddressPhoneNumber.setText(data.getAddress().getMobile());
                    tvAddressText.setText(data.getAddress().getAddress());

                    orderStatus = data.getOrderStatus();

                    if (data.getOrderStatus() == PayConstant.gettingGoods) {
                        tvPayAffirmGoodsOrGoPay.setVisibility(View.VISIBLE);
                        tvPayAffirmGoodsOrGoPay.setText("确认收货");
                        tvPayAffirmGoodsOrGoPay.setBackgroundColor(getResources().getColor(R.color.green0));
                    }
                    if (data.getOrderStatus() == PayConstant.notPay) {
                        tvPayAffirmGoodsOrGoPay.setVisibility(View.VISIBLE);
                        tvPayAffirmGoodsOrGoPay.setText("去支付");
                        tvPayAffirmGoodsOrGoPay.setBackgroundColor(getResources().getColor(R.color.red0));
                    }

                    if (data.getOrderStatus() == PayConstant.orderFinish) {
                        tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
                    }


                    if (data.getOrderStatus() == PayConstant.orderCancel) {
                        tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
                    }


                    orderItems = (ArrayList<OrderItem>) data.getOrderItems();
                    adapter.setDatas(orderItems);
                    adapter.notifyDataSetChanged();


                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.tvCancelOrder)
    public void CancelOrder() {
        PayDialog payDialog=new PayDialog(this,new String[]{"确定删除订单","不删除","确定删除"});
        payDialog.show();
        payDialog.operationAction=new PayDialog.OperationAction() {
            @Override
            public void payYes(Object... obj) {
                final CustomDialog customDialog=new CustomDialog();
                Dialog dialog = customDialog.createDialog1(PayOrderDetailsActivity.this, "删除中...");
                dialog.show();
                ApiManager.getInstance().orderApi.delete(data.getId(), new HttpClientAdapter.Callback<Void>() {
                    @Override
                    public void call(Result<Void> t) {
                        if (t.isSuccess()) {
                            ToastUtil.show(PayOrderDetailsActivity.this.getApplicationContext(), "删除成功");
                            PayOrderDetailsActivity.this.finish();
                        } else {
                            ToastUtil.show(PayOrderDetailsActivity.this.getApplicationContext(), t.getMsgMap() + "");
                        }
                        customDialog.dismiss();
                    }
                }, getRequestTag());
            }

            @Override
            public void payNo(Object... obj) {

            }
        };
    }


    @OnClick(R.id.tvPayAffirmGoodsOrGoPay)
    public void PayAffirmGoodsOrGoPay() {
        if (data.getOrderStatus()==PayConstant.notPay) {
            Intent intent=new Intent(getApplicationContext(), PayAffirmPaymentActivity.class);
            LocalProductData.getLocal().put(LocalProductData.PriceCount, data.getTotalFee());
            startActivity(intent);
        }else if(data.getOrderStatus()==PayConstant.gettingGoods){
            PayDialog payDialog=new PayDialog(PayOrderDetailsActivity.this,new String[]{"确定收货","不收货","确定收货"});
            payDialog.show();
            payDialog.operationAction=new PayDialog.OperationAction() {
                @Override
                public void payYes(Object... obj) {
                    final CustomDialog customDialog=new CustomDialog();
                    Dialog dialog = customDialog.createDialog1(PayOrderDetailsActivity.this, "确认收货中...");
                    dialog.show();
                    ApiManager.getInstance().orderApi.confirmReceive(data.getId(), new HttpClientAdapter.Callback<Void>() {
                        @Override
                        public void call(Result<Void> t) {
                            if (t.isSuccess()) {
                                ToastUtil.show(PayOrderDetailsActivity.this.getApplicationContext(), "确认收货成功");
                                PayOrderDetailsActivity.this.finish();
                            } else {
                                ToastUtil.show(PayOrderDetailsActivity.this.getApplicationContext(), t.getMsgMap() + "");
                            }
                            customDialog.dismiss();
                        }
                    }, getRequestTag());
                }

                @Override
                public void payNo(Object... obj) {

                }
            };
        }
    }



//    @OnClick(R.id.rlNoneGet)
//    public void NoneGet() {
//        Intent intent=new Intent(getApplicationContext(),PayAddAddressActivity.class);
//        startActivityForResult(intent, 666);
//    }
//
//    @OnClick(R.id.rlExpressageGet)
//    public void rlExpressageGet() {
//        Intent intent=new Intent(getApplicationContext(),PayMimeAddressActivity.class);
//        startActivityForResult(intent, 888);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==888) {
//            if (resultCode==999) {
//                if (data!=null) {
//                    Address addressItem= (Address) data.getSerializableExtra("addressItem");
//                    tvAddressName.setText(addressItem.getLinkMan());
//                    tvAddressPhoneNumber.setText(addressItem.getMobile());
//                    tvAddressText.setText(addressItem.getAddress());
////                    addressId=addressItem.getId();
//                }
//            }
//        }
//    }



    public class MyGoodsListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<OrderItem> datas;
        private LayoutInflater mInflater;
        public int currentPosition;

        public MyGoodsListAdapter(Context context, ArrayList<OrderItem> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<OrderItem> datas) {
            if (datas == null) {
                this.datas = new ArrayList<OrderItem>();
            } else {
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<OrderItem> datas) {
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

            OrderItem orderItem = this.datas.get(position);
            viewHolder.tvName.setText(orderItem.getProductName());
            viewHolder.tvPrice.setText(orderItem.getPrice()+"");

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


