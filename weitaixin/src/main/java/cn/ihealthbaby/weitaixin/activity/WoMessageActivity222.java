package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.Information;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.MyRecyclerAdapter;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;


public class WoMessageActivity222 extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //
    @Bind(R.id.mRecyclerView) RecyclerView mRecyclerView;


    MyRecyclerAdapter adapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_message222);

        ButterKnife.bind(this);

        title_text.setText("我的消息");
        back.setVisibility(View.INVISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MyRecyclerAdapter(this,null);
        mRecyclerView.setAdapter(adapter);

//        mRecyclerView.addItemDecoration(new MyDecoration(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            Paint paint = new Paint();
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

        adapter.setOnItemClickListener(new MyRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Information itemData) {
                    ToastUtil.show(getApplicationContext(), itemData.getId()+" : "+itemData.getContext());
            }
        });

        pullDatas();
    }

    private void pullDatas() {

        dialog=new CustomDialog().createDialog1(this,"登录中...");
        dialog.show();

        ApiManager.getInstance().informationApi.getInformations(1, 5, new HttpClientAdapter.Callback<PageData<Information>>() {
            @Override
            public void call(Result<PageData<Information>> t) {
                if (t.isSuccess()) {
                    PageData<Information> data = t.getData();
                    ArrayList<Information> dataList = (ArrayList<Information>) data.getValue();
                    adapter.setDatas(dataList);
                    adapter.notifyDataSetChanged();
                }else {
                    ToastUtil.show(getApplicationContext(),t.getMsg());
                }
                dialog.dismiss();
            }
        }, getRequestTag());
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }



    //
    public class MyDecoration extends RecyclerView.ItemDecoration {

        private  final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        private Drawable mDivider;

        public MyDecoration(Context ctx){
            final TypedArray a = ctx.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int top = parent.getPaddingTop();
            int bottom = parent.getHeight() - parent.getPaddingBottom();
            int childCount = parent.getChildCount();
            for(int i=0;i < childCount;i++){
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)child.getLayoutParams();
                int left = child.getRight() + layoutParams.rightMargin;
                int right = left + mDivider.getIntrinsicWidth();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }

    }

}
