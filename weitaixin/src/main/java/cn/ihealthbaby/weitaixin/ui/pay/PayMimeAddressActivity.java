package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Address;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.PayMimeAddressAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.CustomDialog;

public class PayMimeAddressActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.lvAddressAllOrder) ListView lvAddressAllOrder;
    @Bind(R.id.tvAddNewAddress) TextView tvAddNewAddress;


    private  PayMimeAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_mime_address);

        ButterKnife.bind(this);

        title_text.setText("我的地址");

        initView();

//        pullData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        pullData();
    }


    @OnClick(R.id.tvAddNewAddress)
    public void AddNewAddress() {
        Intent intent=new Intent(this, PayAddAddressActivity.class);
        startActivity(intent);
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
        adapter = new PayMimeAddressAdapter(getApplicationContext(), null);
        lvAddressAllOrder.setAdapter(adapter);

        lvAddressAllOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(PayMimeAddressActivity.this, "数据加载中...");
                dialog.show();

                final Address item = (Address) adapter.getItem(position );
                ApiManager.getInstance().addressApi.setDef(item.getId(), new HttpClientAdapter.Callback<Void>() {
                    @Override
                    public void call(Result<Void> t) {
                        if (t.isSuccess()) {
                            adapter.currentPosition = (position );
                            item.setIsDef(true);
                            adapter.notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.putExtra("addressItem", item);
                            setResult(PayConfirmOrderActivity.RESULTCODE_MIMEADDRESS, intent);
                            PayMimeAddressActivity.this.finish();
                        } else {
                            ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                        }
                        customDialog.dismiss();
                    }
                }, getRequestTag());
            }
        });

    }



    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


}


