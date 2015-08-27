package cn.ihealthbaby.weitaixin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;


public class HomePageFragment extends BaseFragment {
    private final static String TAG = "HomePageFragment";


    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
//


    View view;


    private static HomePageFragment instance;
    public static HomePageFragment getInstance(){
        if (instance==null) {
            instance=new HomePageFragment();
        }
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_home_page, null);
        ButterKnife.bind(this, view);
        init(view);
        back.setVisibility(View.INVISIBLE);
        LogUtil.e("HomePageFragment+Coco7","HomePageFragment+Null");
        return view;
    }

    private void init(View view) {
        title_text.setText("首页");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}




