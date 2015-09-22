package cn.ihealthbaby.weitaixin.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.ui.pay.PayAffirmPaymentActivity;
import cn.ihealthbaby.weitaixin.ui.pay.PayConstant;
import cn.ihealthbaby.weitaixin.ui.pay.PayUtils;
import cn.ihealthbaby.weitaixin.ui.widget.PayDialog;


public class PayAllOrderAdapter extends BaseAdapter {

    private BaseActivity context;
    private ArrayList<Order> datas;
    private LayoutInflater mInflater;

    public PayAllOrderAdapter(BaseActivity context, ArrayList<Order> datas) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        setDatas(datas);
    }

    public void setDatas(ArrayList<Order> datas) {
        if (datas == null) {
            this.datas = new ArrayList<Order>();
        } else {
            this.datas.clear();
            this.datas = datas;
        }
    }


    public void addDatas(ArrayList<Order> datas) {
        if (datas != null && datas.size() > 0) {
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
            convertView = mInflater.inflate(R.layout.item_pay_all_order, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Order order = this.datas.get(position);
        viewHolder.tvPayType.setText("【"+PayConstant.orderType[order.getOrderStatus()]+"】");
        viewHolder.tvGoodsName.setText(order.getDescription()+"");
        viewHolder.tvGoodsTime.setText(DateTimeTool.date2St2(order.getCreateTime(), "yyyy-MM-dd HH: mm"));
        viewHolder.tvPayPriceText.setText(PayUtils.showPrice(order.getTotalFee()));

        //0 待付款-未支付,  1 待发货,  2待收货,  3订单结束,   4 订单取消
        if (order.getOrderStatus()==PayConstant.notPay) {
            viewHolder.tvPayDelect.setVisibility(View.VISIBLE);
            viewHolder.tvPayDelect.setText("取消订单");
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.VISIBLE);
            viewHolder.tvPayAffirmGoodsOrGoPay.setText("去支付");
            viewHolder.tvPayAffirmGoodsOrGoPay.setBackgroundColor(context.getResources().getColor(R.color.red0));

            viewHolder.tvPayDelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayDialog payDialog=new PayDialog(context,new String[]{"取消订单","不取消","确定取消"});
                    payDialog.show();
                    payDialog.operationAction=new PayDialog.OperationAction() {
                        @Override
                        public void payYes(Object... obj) {
                            final CustomDialog customDialog=new CustomDialog();
                            Dialog dialog = customDialog.createDialog1(context, "取消中...");
                            dialog.show();
                            ApiManager.getInstance().orderApi.cancel(order.getId(), new HttpClientAdapter.Callback<Void>() {
                                @Override
                                public void call(Result<Void> t) {
                                    if (t.isSuccess()) {
                                        ToastUtil.show(context.getApplicationContext(), "取消成功");
                                        order.setOrderStatus(PayConstant.orderCancel);
                                        notifyDataSetChanged();
                                    } else {
                                        ToastUtil.show(context.getApplicationContext(), t.getMsgMap() + "");
                                    }
                                    customDialog.dismiss();
                                }
                            }, context);
                        }

                        @Override
                        public void payNo(Object... obj) {

                        }
                    };
                }
            });

            viewHolder.tvPayAffirmGoodsOrGoPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ToastUtil.show(context, position + "");
                    Intent intent=new Intent(context.getApplicationContext(), PayAffirmPaymentActivity.class);
                    LocalProductData.getLocal().put(LocalProductData.PriceCount, order.getTotalFee());
                    intent.putExtra(PayConstant.ORDERID,order.getId());
                    context.startActivity(intent);
                }
            });
        } else if (order.getOrderStatus()==PayConstant.gettingGoods) {
            viewHolder.tvPayDelect.setVisibility(View.GONE);
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.VISIBLE);
            viewHolder.tvPayAffirmGoodsOrGoPay.setText("确认收货");
            viewHolder.tvPayAffirmGoodsOrGoPay.setBackgroundColor(context.getResources().getColor(R.color.green0));
            viewHolder.tvPayAffirmGoodsOrGoPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayDialog payDialog=new PayDialog(context,new String[]{"确定收货","不收货","确定收货"});
                    payDialog.show();
                    payDialog.operationAction=new PayDialog.OperationAction() {
                        @Override
                        public void payYes(Object... obj) {
                            final CustomDialog customDialog=new CustomDialog();
                            Dialog dialog = customDialog.createDialog1(context, "收货中...");
                            dialog.show();
                            ApiManager.getInstance().orderApi.confirmReceive(order.getId(), new HttpClientAdapter.Callback<Void>() {
                                @Override
                                public void call(Result<Void> t) {
                                    if (t.isSuccess()) {
                                        ToastUtil.show(context.getApplicationContext(), "收货成功");
                                        order.setOrderStatus(PayConstant.orderFinish);
                                        notifyDataSetChanged();
                                    } else {
                                        ToastUtil.show(context.getApplicationContext(), t.getMsgMap() + "");
                                    }
                                    customDialog.dismiss();
                                }
                            }, context);
                        }

                        @Override
                        public void payNo(Object... obj) {

                        }
                    };
                }
            });
        }


        if(order.getOrderStatus()==PayConstant.sendingGoods) {
            viewHolder.tvPayDelect.setVisibility(View.GONE);
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
        }


        if (order.getOrderStatus()==PayConstant.orderFinish) {
            viewHolder.tvPayDelect.setVisibility(View.VISIBLE);
            viewHolder.tvPayDelect.setText("删除");
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
            setListener(viewHolder.tvPayDelect, order, position);
        }


        if (order.getOrderStatus()==PayConstant.orderCancel) {
            viewHolder.tvPayDelect.setVisibility(View.VISIBLE);
            viewHolder.tvPayDelect.setText("删除");
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
            setListener(viewHolder.tvPayDelect,order, position);
        }


        LogUtil.d("OrdeorderrStatus", "OrdeorderrStatus==> "+order.getOrderStatus());

        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.tvPayType) TextView tvPayType;
        @Bind(R.id.tvGoodsName) TextView tvGoodsName;
        @Bind(R.id.tvGoodsTime) TextView tvGoodsTime;
        @Bind(R.id.tvPayPriceText) TextView tvPayPriceText;
        @Bind(R.id.tvPayDelect) TextView tvPayDelect;
        @Bind(R.id.tvPayAffirmGoodsOrGoPay) TextView tvPayAffirmGoodsOrGoPay;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    public void setListener(View view, final Order order, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayDialog payDialog = new PayDialog(context, new String[]{"确定删除订单", "不删除", "确定删除"});
                payDialog.show();
                payDialog.operationAction = new PayDialog.OperationAction() {
                    @Override
                    public void payYes(Object... obj) {
                        final CustomDialog customDialog = new CustomDialog();
                        Dialog dialog = customDialog.createDialog1(context, "删除中...");
                        dialog.show();
                        ApiManager.getInstance().orderApi.delete(order.getId(), new HttpClientAdapter.Callback<Void>() {
                            @Override
                            public void call(Result<Void> t) {
                                if (t.isSuccess()) {
                                    ToastUtil.show(context.getApplicationContext(), "删除成功");
                                    datas.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    ToastUtil.show(context.getApplicationContext(), t.getMsgMap() + "");
                                }
                                customDialog.dismiss();
                            }
                        }, context);
                    }

                    @Override
                    public void payNo(Object... obj) {

                    }
                };
            }
        });
    }

    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
//				.displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }


}
