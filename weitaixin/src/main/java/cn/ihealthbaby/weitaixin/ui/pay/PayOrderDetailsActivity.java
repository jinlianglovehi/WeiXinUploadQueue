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
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.Address;
import cn.ihealthbaby.client.model.Order;
import cn.ihealthbaby.client.model.OrderDetail;
import cn.ihealthbaby.client.model.OrderItem;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.widget.PayDialog;

public class PayOrderDetailsActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //


//    @Bind(R.id.rlExpressageAction) RelativeLayout rlExpressageAction;


    @Bind(R.id.tvAddressName)
    TextView tvAddressName;
    @Bind(R.id.tvAddressPhoneNumber)
    TextView tvAddressPhoneNumber;
    @Bind(R.id.tvAddressText)
    TextView tvAddressText;
    @Bind(R.id.tvOrderGoodsNumber)
    TextView tvOrderGoodsNumber;
    @Bind(R.id.lvGoodsList)
    ListView lvGoodsList;
    @Bind(R.id.tvPayAffirmGoodsOrGoPay)
    TextView tvPayAffirmGoodsOrGoPay;
    @Bind(R.id.tvCancelOrder)
    TextView tvCancelOrder;
    @Bind(R.id.tvOrderDetailsPayway)
    TextView tvOrderDetailsPayway;
    @Bind(R.id.tvOrderDetailsPullway)
    TextView tvOrderDetailsPullway;
    @Bind(R.id.tvOrderDetailsPrice)
    TextView tvOrderDetailsPrice;

//    private int hospitalStatus=-1;

    private long orderId = -1;
    private int orderStatus;
    private MyGoodsListAdapter adapter;
    private ArrayList<OrderItem> orderItems;
    private OrderDetail orderDetail;


    private String[] payTypeArr = new String[]{"院内现金支付", "支付宝", "微信支付", "银联支付"};
    private String[] deliverTypeArr = new String[]{"到院自提", "邮寄"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order_details_new);

        ButterKnife.bind(this);

        title_text.setText("订单详情");

        orderId = getIntent().getLongExtra(PayConstant.ORDERID, -1);

        LogUtil.d("orderIdExtra", "orderIdExtra==>" + orderId);

        //0 未启用,1 开通院内,2 开通院外,3 开通院外线上但不支持邮寄, 4 开通院外线上且支持邮寄
//        hospitalStatus = (int) LocalProductData.getLocal().get(LocalProductData.HospitalStatus);
//        hospitalAddress = (String) LocalProductData.getLocal().get(LocalProductData.HospitalAddress);

//        if (hospitalStatus == 3) {
//            rlExpressageAction.setVisibility(View.GONE);
//        } else if (hospitalStatus == 4) {
//            rlExpressageAction.setVisibility(View.VISIBLE);
//        }



    }


    @Override
    protected void onResume() {
        super.onResume();
        pullData();
    }

    private void pullData() {
        adapter = new MyGoodsListAdapter(this, null);
        lvGoodsList.setAdapter(adapter);


        if (orderId == -1) {
            ToastUtil.show(getApplicationContext(), "订单Id生成错误");
            return;
        }

        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().orderApi.getOrder(orderId,
                new DefaultCallback<OrderDetail>(this, new AbstractBusiness<OrderDetail>() {
                    @Override
                    public void handleData(OrderDetail data) {
                        orderDetail = data;
                        if (orderDetail == null) {
                            ToastUtil.show(getApplicationContext(), "没有数据");
                            return;
                        }

                        LogUtil.d("orderDetailCCC", orderDetail.getOrderStatus() + " orderD有数etail==>" + orderDetail);

                        tvOrderGoodsNumber.setText(orderDetail.getId() + "");

                        Address address = orderDetail.getAddress();
                        if (address != null) {
                            tvAddressName.setText(address.getLinkMan());
                            tvAddressPhoneNumber.setText(address.getMobile());
                            tvAddressText.setText(address.getArea() + address.getAddress() + "");
                        }



                        int payType = orderDetail.getPayType();
                        if (payType >= 0 && payType <= 3) {
                            tvOrderDetailsPayway.setText(payTypeArr[payType] + "");
                        }
                        int deliverType = orderDetail.getDeliverType();
                        if (deliverType >= 0 && deliverType <= 1) {
                            tvOrderDetailsPullway.setText(deliverTypeArr[deliverType] + "");
                        }

                        tvOrderDetailsPrice.setText("总计" + PayUtils.showPrice(orderDetail.getTotalFee()));

                        orderStatus = orderDetail.getOrderStatus();


                        //0 待付款-未支付,  1 待发货,  2待收货,  3订单结束,   4 订单取消
                        if (orderDetail.getOrderStatus() == PayConstant.notPay) {
                            tvCancelOrder.setVisibility(View.VISIBLE);
                            tvCancelOrder.setText("取消订单");
                            tvPayAffirmGoodsOrGoPay.setVisibility(View.VISIBLE);
                            tvPayAffirmGoodsOrGoPay.setText("去支付");
                            tvPayAffirmGoodsOrGoPay.setBackgroundColor(getResources().getColor(R.color.red0));
                        }

                        if (orderDetail.getOrderStatus() == PayConstant.gettingGoods) {
                            tvCancelOrder.setVisibility(View.GONE);
                            tvPayAffirmGoodsOrGoPay.setVisibility(View.VISIBLE);
                            tvPayAffirmGoodsOrGoPay.setText("确认收货");
                            tvPayAffirmGoodsOrGoPay.setBackgroundColor(getResources().getColor(R.color.green0));
                        }

                        if (orderDetail.getOrderStatus() == PayConstant.sendingGoods) {
                            tvCancelOrder.setVisibility(View.GONE);
                            tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
                        }


                        if (orderDetail.getOrderStatus() == PayConstant.orderFinish) {
                            tvCancelOrder.setVisibility(View.VISIBLE);
                            tvCancelOrder.setText("删除");
                            tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
                        }


                        if (orderDetail.getOrderStatus() == PayConstant.orderCancel) {
                            tvCancelOrder.setVisibility(View.VISIBLE);
                            tvCancelOrder.setText("删除");
                            tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
                        }


                        orderItems = (ArrayList<OrderItem>) orderDetail.getOrderItems();
                        adapter.setDatas(orderItems);
                        adapter.notifyDataSetChanged();

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


    @OnClick(R.id.tvCancelOrder)
    public void tvCancelOrder() {
        if (orderDetail.getOrderStatus() == PayConstant.notPay) {
            cancelOrder();
        } else if (orderDetail.getOrderStatus() == PayConstant.orderFinish || orderDetail.getOrderStatus() == PayConstant.orderCancel) {
            deleteOrder();
        }
    }


    public void cancelOrder() {
        PayDialog payDialog = new PayDialog(this, new String[]{"确定取消订单", "不取消", "确定取消"});
        payDialog.show();
        payDialog.operationAction = new PayDialog.OperationAction() {
            @Override
            public void payYes(Object... obj) {
                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(PayOrderDetailsActivity.this, "取消中...");
                dialog.show();
                ApiManager.getInstance().orderApi.cancel(orderDetail.getId(),
                        new DefaultCallback<Void>(PayOrderDetailsActivity.this, new AbstractBusiness<Void>() {
                            @Override
                            public void handleData(Void data) {
                                ToastUtil.show(PayOrderDetailsActivity.this.getApplicationContext(), "取消成功");
//                              orderDetail.setOrderStatus(PayConstant.orderCancel);
                                PayOrderDetailsActivity.this.finish();
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

            @Override
            public void payNo(Object... obj) {

            }
        };
    }


    public void deleteOrder() {
        PayDialog payDialog = new PayDialog(this, new String[]{"确定删除订单", "不删除", "确定删除"});
        payDialog.show();
        payDialog.operationAction = new PayDialog.OperationAction() {
            @Override
            public void payYes(Object... obj) {
                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(PayOrderDetailsActivity.this, "删除中...");
                dialog.show();
                ApiManager.getInstance().orderApi.delete(orderDetail.getId(),
                        new DefaultCallback<Void>(PayOrderDetailsActivity.this, new AbstractBusiness<Void>() {
                            @Override
                            public void handleData(Void data) {
                                ToastUtil.show(PayOrderDetailsActivity.this.getApplicationContext(), "删除成功");
                                PayOrderDetailsActivity.this.finish();
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
                        }), getRequestTag());
            }

            @Override
            public void payNo(Object... obj) {

            }
        };
    }


    @OnClick(R.id.tvPayAffirmGoodsOrGoPay)
    public void PayAffirmGoodsOrGoPay() {
        if (orderDetail == null) {
            ToastUtil.show(getApplicationContext(), "没有数据");
            return;
        }

        if (orderDetail.getOrderStatus() == PayConstant.notPay) {
            Intent intent = new Intent(getApplicationContext(), PayAffirmPaymentActivity.class);
            LocalProductData.getLocal().put(LocalProductData.PriceCount, orderDetail.getTotalFee());
            intent.putExtra(PayConstant.ORDERID, orderDetail.getId());
            startActivity(intent);
        } else if (orderDetail.getOrderStatus() == PayConstant.gettingGoods) {
            PayDialog payDialog = new PayDialog(PayOrderDetailsActivity.this, new String[]{"确定收货", "不收货", "确定收货"});
            payDialog.show();
            payDialog.operationAction = new PayDialog.OperationAction() {
                @Override
                public void payYes(Object... obj) {
                    final CustomDialog customDialog = new CustomDialog();
                    Dialog dialog = customDialog.createDialog1(PayOrderDetailsActivity.this, "收货中...");
                    dialog.show();
                    ApiManager.getInstance().orderApi.confirmReceive(orderDetail.getId(),
                            new DefaultCallback<Void>(PayOrderDetailsActivity.this, new AbstractBusiness<Void>() {
                                @Override
                                public void handleData(Void data) {
                                    ToastUtil.show(PayOrderDetailsActivity.this.getApplicationContext(), "收货成功");
                                    PayOrderDetailsActivity.this.finish();
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
                            }), getRequestTag());
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
//    protected void onActivityResult(int requestCode, int resultCode, Intent orderDetail) {
//        super.onActivityResult(requestCode, resultCode, orderDetail);
//        if (requestCode==888) {
//            if (resultCode==999) {
//                if (orderDetail!=null) {
//                    Address addressItem= (Address) orderDetail.getSerializableExtra("addressItem");
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
            viewHolder.tvPrice.setText(PayUtils.showPrice(orderItem.getPrice()) + "");

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName)
            TextView tvName;
            @Bind(R.id.tvPrice)
            TextView tvPrice;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

    }


}


