package cn.ihealthbaby.weitaixin.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Address;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.ui.pay.PayAddAddressWithEditActivity;

public class PayMimeAddressWithEditAdapter extends BaseAdapter {

    private Activity context;
    public ArrayList<Address> datas;
    private LayoutInflater mInflater;
    public int currentPosition = -1;
    public Address addressOld;
    public boolean isDel = false;

    public HashMap<Integer, Boolean> addressMap = new HashMap<Integer, Boolean>();

    public PayMimeAddressWithEditAdapter(Activity context, ArrayList<Address> datas) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        setDatas(datas);
    }

    public void setDatas(ArrayList<Address> datas) {
        if (datas == null) {
            this.datas = new ArrayList<Address>();
        } else {
            this.datas.clear();
            this.datas = datas;
        }
        setClearMap();
    }


    public void setClearMap(){
        addressMap.clear();
        for (int i = 0; i < this.datas.size(); i++) {
            addressMap.put(i, false);
        }
    }

    public void addDatas(ArrayList<Address> datas) {
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
            convertView = mInflater.inflate(R.layout.item_pay_mime_address, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Address address = this.datas.get(position);
        viewHolder.tvAddressName.setText(address.getLinkMan());
        viewHolder.tvAddressPhoneNumber.setText(address.getMobile());
        viewHolder.tvAddressText.setText(address.getArea() + address.getAddress() + "");

        LogUtil.d("getIsDef", address.getIsDef() + "  sssssss " + position);

        if (currentPosition == position) {
            viewHolder.ivAddressImaged.setImageResource(R.drawable.pay_choose);
        } else {
            viewHolder.ivAddressImaged.setImageResource(R.drawable.pay_choose_un);
        }


        viewHolder.ivArrowAddress.setVisibility(View.VISIBLE);


        if (isDel) {
            viewHolder.ivArrowAddress.setVisibility(View.INVISIBLE);
            Boolean aBoolean = addressMap.get(position);
            if (aBoolean) {
                viewHolder.ivAddressImaged.setImageResource(R.drawable.pay_choose);
            } else {
                viewHolder.ivAddressImaged.setImageResource(R.drawable.pay_choose_un);
            }
            viewHolder.ivAddressImaged.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDel) {
                        boolean isSeleced = addressMap.get(position);
                        addressMap.put(position, !isSeleced);
                        notifyDataSetChanged();
                    }
                }
            });
        } else {
            if (address.getIsDef()) {
                viewHolder.ivAddressImaged.setImageResource(R.drawable.pay_choose);
            } else {
                viewHolder.ivAddressImaged.setImageResource(R.drawable.pay_choose_un);
            }
            viewHolder.ivAddressImaged.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CustomDialog customDialog = new CustomDialog();
                    Dialog dialog = customDialog.createDialog1(context, "数据加载中...");
                    dialog.show();
                    ApiManager.getInstance().addressApi.setDef(address.getId(),
                            new DefaultCallback<Void>(context, new AbstractBusiness<Void>() {
                                @Override
                                public void handleData(Void data) {
                                    currentPosition = (position);
                                    for (int i = 0; i < datas.size(); i++) {
                                        Address addRess = datas.get(i);
                                        addRess.setIsDef(false);
                                        if (i == position) {
                                            address.setIsDef(true);
                                        }
                                    }
                                    datas.add(0, datas.remove(position));

                                    notifyDataSetChanged();

//                            Intent intent = new Intent();
//                            intent.putExtra("addressItem", address);
//                            context.setResult(999, intent);
//                            context.finish();

                                    customDialog.dismiss();
                                }

                                @Override
                                public void handleException(Exception e) {
                                    super.handleException(e);
                                    customDialog.dismiss();
                                }

                                @Override
                                public void handleClientError(Context context, Exception e) {
                                    super.handleClientError(context, e);
                                    customDialog.dismiss();
                                }

                            }), context);
                }
            });
        }


        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.ivAddressImaged)
        ImageView ivAddressImaged;
        @Bind(R.id.tvAddressName)
        TextView tvAddressName;
        @Bind(R.id.tvAddressPhoneNumber)
        TextView tvAddressPhoneNumber;
        @Bind(R.id.tvAddressText)
        TextView tvAddressText;
        @Bind(R.id.ivArrowAddress)
        ImageView ivArrowAddress;

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
                .build();
        return options;
    }


}
