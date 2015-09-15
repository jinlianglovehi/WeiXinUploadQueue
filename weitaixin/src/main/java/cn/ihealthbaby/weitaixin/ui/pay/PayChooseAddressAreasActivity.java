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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.collecton.ApiList;
import cn.ihealthbaby.client.model.Area;
import cn.ihealthbaby.client.model.City;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import de.greenrobot.event.EventBus;

public class PayChooseAddressAreasActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //


    @Bind(R.id.lvPayChooseAddressArea) ListView lvPayChooseAddressArea;


    private String Cityid;
    private String Areas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_paychoose_address_area);

        ButterKnife.bind(this);

        title_text.setText("所选地区");

        Cityid=getIntent().getStringExtra("Cityid");
        Areas=getIntent().getStringExtra("Areas");

        pullData();
    }


    MyPayChooseAddressCityAdapter adapter;
    private void pullData() {
        adapter = new MyPayChooseAddressCityAdapter(this, null);
        lvPayChooseAddressArea.setAdapter(adapter);

        lvPayChooseAddressArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.currentPosition=position;
                adapter.notifyDataSetChanged();

                Area item = (Area) adapter.getItem(position);
                Areas+=item.getArea();

                LogUtil.d("AreagEx11", "AreagEx11==> " + Areas);
                PayAddAddressActivity.AreasString=Areas;
//                Intent intent=new Intent();
//                intent.putExtra("Areas",Areas);
//                setResult(RESULT_OK, intent);
                finish();

                EventBus.getDefault().post(new PayChooseAreasEvent());
            }
        });


        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "数据加载中...");
        dialog.show();
        ApiManager.getInstance().addressApi.getAreas(Cityid, 0, new HttpClientAdapter.Callback<ApiList<Area>>() {
            @Override
            public void call(Result<ApiList<Area>> t) {
                if (t.isSuccess()) {
                    ApiList<Area> data = t.getData();
                    ArrayList<Area> list = (ArrayList<Area>) data.getList();
                    if (list!=null&&list.size()>0) {
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
                    }else {
                        ToastUtil.show(getApplicationContext(),"没有更多数据~~~");
                    }
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



    public class MyPayChooseAddressCityAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Area> datas;
        private LayoutInflater mInflater;
        public int currentPosition=-1;

        public MyPayChooseAddressCityAdapter(Context context, ArrayList<Area> datas) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            setDatas(datas);
        }

        public void setDatas(ArrayList<Area> datas) {
            if (datas == null) {
                this.datas = new ArrayList<Area>();
            } else {
                this.datas = datas;
            }
        }


        public void addDatas(ArrayList<Area> datas) {
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
                convertView = mInflater.inflate(R.layout.item_pay_city_choose, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Area area = this.datas.get(position);
            viewHolder.tvName.setText(area.getArea()+"");

            if (currentPosition == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.gray1));
                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.gray1));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            }else{
                convertView.setBackgroundColor(getResources().getColor(R.color.white0));
                viewHolder.tvName.setBackgroundColor(getResources().getColor(R.color.white0));
                viewHolder.tvState.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tvName)  TextView tvName;
            @Bind(R.id.tvState)  ImageView tvState;

            public ViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }

    }


}


