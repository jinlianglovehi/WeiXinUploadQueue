package cn.ihealthbaby.weitaixin.adapter;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.client.model.Order;
import cn.ihealthbaby.client.model.OrderItem;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.model.LocalProductData;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.ui.mine.WaitReplyingActivity;
import cn.ihealthbaby.weitaixin.ui.pay.PayAffirmPaymentActivity;
import cn.ihealthbaby.weitaixin.ui.pay.PayConstant;
import cn.ihealthbaby.weitaixin.ui.pay.PayMimeOrderActivity;
import cn.ihealthbaby.weitaixin.ui.record.AskDoctorActivity;
import cn.ihealthbaby.weitaixin.ui.record.ReplyedActivity;
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
        viewHolder.tvGoodsTime.setText(DateTimeTool.date2St2(order.getCreateTime(), "yyyy-MM-dd hh: mm"));
        viewHolder.tvPayPriceText.setText("￥" + order.getTotalFee() + "");

        if (order.getOrderStatus()==PayConstant.notPay) {
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.VISIBLE);
            viewHolder.tvPayAffirmGoodsOrGoPay.setText("去支付");
            viewHolder.tvPayAffirmGoodsOrGoPay.setBackgroundColor(context.getResources().getColor(R.color.red0));
            viewHolder.tvPayAffirmGoodsOrGoPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.show(context, position + "");
                    Intent intent=new Intent(context.getApplicationContext(), PayAffirmPaymentActivity.class);
                    LocalProductData.getLocal().put(LocalProductData.PriceCount, order.getTotalFee());
                    context.startActivity(intent);
                }
            });
        }else if (order.getOrderStatus()==PayConstant.gettingGoods) {
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
                            Dialog dialog = customDialog.createDialog1(context, "确认收货中...");
                            dialog.show();
                            ApiManager.getInstance().orderApi.confirmReceive(order.getId(), new HttpClientAdapter.Callback<Void>() {
                                @Override
                                public void call(Result<Void> t) {
                                    if (t.isSuccess()) {
                                        ToastUtil.show(context.getApplicationContext(), "确认收货成功");
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


        if (order.getOrderStatus()==PayConstant.orderFinish) {
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
        }


        if (order.getOrderStatus()==PayConstant.orderCancel) {
            viewHolder.tvPayAffirmGoodsOrGoPay.setVisibility(View.GONE);
        }


        viewHolder.tvPayDelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayDialog payDialog=new PayDialog(context,new String[]{"确定删除订单","不删除","确定删除"});
                payDialog.show();
                payDialog.operationAction=new PayDialog.OperationAction() {
                    @Override
                    public void payYes(Object... obj) {
                        final CustomDialog customDialog=new CustomDialog();
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
