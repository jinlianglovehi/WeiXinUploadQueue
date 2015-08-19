package cn.ihealthbaby.weitaixin.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.MaxLengthWatcher;


public class SetSystemGuardianActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //
    @Bind(R.id.cbAutoStart) CheckBox cbAutoStart;
    @Bind(R.id.cbPoliceSet) CheckBox cbPoliceSet;
    @Bind(R.id.lvGuardian) ListView lvGuardian;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system_guardian);

        ButterKnife.bind(this);

        title_text.setText("监护设置");
//      back.setVisibility(View.INVISIBLE);
        
        initData();
    }

    MyTimeAdapter myTimeAdapter;
    private void initData() {
        myTimeAdapter=new MyTimeAdapter(this);
        lvGuardian.setAdapter(myTimeAdapter);
        lvGuardian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myTimeAdapter.isFirst=false;
                view.setSelected(true);
                myTimeAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }


    public class MyTimeAdapter extends BaseAdapter{

        private String[] titleTimes=new String[]{"5秒","10秒","15秒","20秒","25秒","30秒"};
        private LayoutInflater inflater;
        public boolean isFirst=true;

        public MyTimeAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return titleTimes.length;
        }

        @Override
        public Object getItem(int position) {
            return titleTimes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView==null) {
                convertView= inflater.inflate(R.layout.item_set_system_guardian,null);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder  = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvTime.setText(titleTimes[position]);
            System.err.println("isFirst: "+isFirst);
            if (convertView.isSelected()) {
                viewHolder.tvTime.setTextColor(getResources().getColor(R.color.green0));
                viewHolder.tvState.setTextColor(getResources().getColor(R.color.green0));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            }else{
                viewHolder.tvTime.setTextColor(getResources().getColor(R.color.gray9));
                viewHolder.tvState.setTextColor(getResources().getColor(R.color.gray9));
                viewHolder.tvState.setVisibility(View.INVISIBLE);
            }
            if (isFirst&&position==0) {
                viewHolder.tvTime.setTextColor(getResources().getColor(R.color.green0));
                viewHolder.tvState.setTextColor(getResources().getColor(R.color.green0));
                viewHolder.tvState.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        class ViewHolder{
            @Bind(R.id.tvTime) TextView tvTime;
            @Bind(R.id.tvState) TextView tvState;
            public ViewHolder(View convertView){
                ButterKnife.bind(this,convertView);
            }
        }
    }


}
