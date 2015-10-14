package cn.ihealthbaby.weitaixin.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ihealthbaby.weitaixin.R;

/**
 * Created by chenweihua on 2015/10/13.
 */
public class Fragment03 extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view01 = (ImageView) inflater.inflate(R.layout.viewpager_item, null);
        view01.setImageResource(R.drawable.welcome_03);
        return view01;
    }



}
