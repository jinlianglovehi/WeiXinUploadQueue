package cn.ihealthbaby.weitaixin.activity;

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
        //this.context = contentView.getContext();
        this.context=context;
    }


    public MyPoPoWin(Activity context) {
        //设置SelectPicPopupWindow的View
        //super(v);
        //this.setContentView(mMenuView);
//

        this.context=context;
        showView();

        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        // 需要设置一下此参数，点击外边可消失
        setBackgroundDrawable(new BitmapDrawable());

        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.anim_popowin_dir);

        setTouchable(true);
        //设置点击窗口外边窗口消失
        setOutsideTouchable(true);
        // 设置此参数获得焦点，弹出窗体可点击 ,否则无法点击
        setFocusable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {//ACTION_OUTSIDE
                    MyPoPoWin.this.dismiss();
                    return true;
                }
                return false;
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = MyPoPoWin.this.context.getWindow().getAttributes();
                lp.alpha = 1.0f;
                MyPoPoWin.this.context.getWindow().setAttributes(lp);
            }
        });

        // 设置Window背景颜色变暗
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


        //取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });

        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),"拍照",Toast.LENGTH_SHORT).show();
                if (iSelectPhoto!=null) {
                    iSelectPhoto.onSelectPhoto(FLAG_TAKE_PHOTO);
                }
                dismiss();
            }
        });

        btn_pick_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),"从相册中选择",Toast.LENGTH_SHORT).show();
                if (iSelectPhoto!=null) {
                    iSelectPhoto.onSelectPhoto(FLAG_PICK_PHOTO);
                }
                dismiss();
            }
        });
    }

    public void showAtLocation(View parent){
        //设置layout在PopupWindow中显示的位置
        //ppWin.showAsDropDown(LoginActivity.this.findViewById(R.id.tv_login_action), 0, 0);
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


