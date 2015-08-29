package cn.ihealthbaby.weitaixin.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.view.SlideListView;


public class AAAAActivity extends BaseActivity {

    public SlideListView sslideListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aaa);

        sslideListView = (SlideListView) this.findViewById(R.id.sslideListView);
        final MyAdapter myMyAdapter=new MyAdapter(this);
        sslideListView.setAdapter(myMyAdapter);

        sslideListView.setRemoveListener(new SlideListView.RemoveListener() {
            @Override
            public void removeItem(SlideListView.RemoveDirection direction, int position) {
                myMyAdapter.datas.remove(position);
                myMyAdapter.notifyDataSetChanged();
                ToastUtil.show(AAAAActivity.this,"position == "+position);
            }
        });
    }


    public class MyAdapter extends BaseAdapter{

        public ArrayList<String> datas=new ArrayList<String>();
        Context context;
        LayoutInflater mInflater;


        public MyAdapter(Context context){
            this.context=context;
            mInflater = LayoutInflater.from(context);
            for (int i=0;i<50;i++){
                datas.add("中国"+i);
            }
        }


        @Override
        public int getCount() {
            return datas.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_a_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String s = datas.get(position);
            viewHolder.title.setText(s);

            return convertView;
        }


         class ViewHolder {
             TextView title;
        }
    }


}


