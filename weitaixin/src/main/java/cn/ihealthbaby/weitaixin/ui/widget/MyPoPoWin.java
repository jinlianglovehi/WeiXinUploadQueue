package cn.ihealthbaby.weitaixin.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import cn.ihealthbaby.weitaixin.R;

public class MyPoPoWin extends PopupWindow {

    protected View mContentView;
    protected Activity context;

    public MyPoPoWin(Activity context,View contentView, int width, int height,  boolean focusable) {
        super(contentView, width, height, focusable);
        this.mContentView = contentView;
        this.context=context;
    }


    public MyPoPoWin(Activity context) {
        this.context=context;
        showView();

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new BitmapDrawable());

        this.setAnimationStyle(R.style.anim_popowin_dir);

        setTouchable(true);
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    MyPoPoWin.this.dismiss();
                    return true;
                }
                return false;
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = MyPoPoWin.this.context.getWindow().getAttributes();
                lp.alpha = 1.0f;
                MyPoPoWin.this.context.getWindow().setAttributes(lp);
            }
        });

        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = .3f;
        context.getWindow().setAttributes(lp);
    }

    public void showView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ppView = inflater.inflate(R.layout.ppwin_view, null);
        this.setContentView(ppView);
        //
        Button btn_cancel = (Button) ppView.findViewById(R.id.btn_cancel);
        Button btn_take_photo = (Button) ppView.findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = (Button) ppView.findViewById(R.id.btn_pick_photo);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (iSelectPhoto!=null) {
                    iSelectPhoto.onSelectPhoto(FLAG_TAKE_PHOTO);
                }
                dismiss();
            }
        });

        btn_pick_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (iSelectPhoto!=null) {
                    iSelectPhoto.onSelectPhoto(FLAG_PICK_PHOTO);
                }
                dismiss();
            }
        });
    }

    public void showAtLocation(View parent){
        showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public final static int FLAG_TAKE_PHOTO=111; //拍照
    public final static int FLAG_PICK_PHOTO=222; //相册
    public interface ISelectPhoto{
        void onSelectPhoto(int flag);
    }

    public ISelectPhoto iSelectPhoto;
    public void setListener(ISelectPhoto iSelectPhoto){
        this.iSelectPhoto=iSelectPhoto;
    }
}


