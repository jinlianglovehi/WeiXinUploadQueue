package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import cn.ihealthbaby.client.model.Address;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.PayMimeAddressAdapter;
import cn.ihealthbaby.weitaixin.adapter.PayMimeAddressWithEditAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

public class PayMimeAddressWithEditActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    @Bind(R.id.flDelAction) FrameLayout flDelAction;
    @Bind(R.id.ivDelectAction) ImageView ivDelectAction;
    @Bind(R.id.tvDelectAction) TextView tvDelectAction;
    //

    @Bind(R.id.lvAddressAllOrder) ListView lvAddressAllOrder;
    @Bind(R.id.tvTextTip) TextView tvTextTip;
    @Bind(R.id.tvAddNewAddress) TextView tvAddNewAddress;


    private PayMimeAddressWithEditAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_mime_address);

        ButterKnife.bind(this);

        title_text.setText("我的地址");

        flDelAction.setVisibility(View.VISIBLE);
        flDelAction.setTag(true);
        tvDelectAction.setVisibility(View.GONE);
        ivDelectAction.setVisibility(View.VISIBLE);

        initView();

//        pullData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        pullData();
    }


    @OnClick(R.id.flDelAction)
    public void DelAction() {
        boolean tag = (boolean) flDelAction.getTag();
        if (tag) {
            tvDelectAction.setVisibility(View.VISIBLE);
            ivDelectAction.setVisibility(View.GONE);
            adapter.isDel=true;
            tvTextTip.setVisibility(View.INVISIBLE);
            tvAddNewAddress.setText("确定");
        } else {
            tvDelectAction.setVisibility(View.GONE);
            ivDelectAction.setVisibility(View.VISIBLE);
            adapter.isDel=false;
            tvTextTip.setVisibility(View.VISIBLE);
            tvAddNewAddress.setText("添加新地址");
        }
        flDelAction.setTag(!tag);
        adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.tvAddNewAddress)
    public void AddNewAddress() {
        boolean tag = (boolean) flDelAction.getTag();
        if (tag) {
            Intent intent = new Intent(this, PayAddAddressActivity.class);

            startActivity(intent);
        } else {
            HashMap<Integer, Boolean> addset = adapter.addressMap;
            for (int i = 0; i < addset.size(); i++) {
                if (addset.get(i)) {
                    adapter.datas.remove(i);
                    addset.remove(i);
                }
            }
            for (int i = 0; i < addset.size(); i++) {
                addset.put(i, false);
            }
            adapter.notifyDataSetChanged();

            //
            tvDelectAction.setVisibility(View.GONE);
            ivDelectAction.setVisibility(View.VISIBLE);
            adapter.isDel=false;
            tvTextTip.setVisibility(View.VISIBLE);
            tvAddNewAddress.setText("添加新地址");
            flDelAction.setTag(true);
        }
    }


    private void pullData() {
        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().addressApi.getAddresss(new HttpClientAdapter.Callback<ApiList<Address>>() {
            @Override
            public void call(Result<ApiList<Address>> t) {
                if (t.isSuccess()) {
                    ApiList<Address> data = t.getData();
                    ArrayList<Address> addressList = (ArrayList<Address>) data.getList();
                    LogUtil.d("addressList", "addressList =%s ", addressList);
                    if (addressList.size() <= 0) {
                        ToastUtil.show(getApplicationContext(), "没有数据");
                    } else {
                        adapter.setDatas(addressList);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }


    private void initView() {
        adapter = new PayMimeAddressWithEditAdapter(this, null);
        lvAddressAllOrder.setAdapter(adapter);

        lvAddressAllOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                if (adapter.isDel) {
                    boolean isSeleced = adapter.addressMap.get(position);
                    adapter.addressMap.put(position, !isSeleced);
                    adapter.notifyDataSetChanged();
                }else{
                    Address item = (Address) adapter.getItem(position);
                    Intent intent=new Intent(getApplicationContext(),PayAddAddressWithEditActivity.class);
                    intent.putExtra("AddressItem",item);
                    startActivity(intent);
                }
            }
        });

    }



    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


}


