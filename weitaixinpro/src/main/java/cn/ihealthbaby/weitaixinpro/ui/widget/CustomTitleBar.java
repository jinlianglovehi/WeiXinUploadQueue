package cn.ihealthbaby.weitaixinpro.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ihealthbaby.weitaixinpro.R;

/**
 * @author by kang on 2015/9/9.
 */
public class CustomTitleBar extends RelativeLayout {

    private final Context mContext;
    private String mRightStr;
    private int mLeftIconDrawable;
    private String mTitleStr;
    private boolean mBackShow;

    public CustomTitleBar(Context context) {
        this(context, null);
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setAttrs(attrs);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_title_bar, this);

        ImageView backView = (ImageView) view.findViewById(R.id.iv_back);
        backView.setImageResource(mLeftIconDrawable);
        if (!mBackShow) {
            backView.setVisibility(View.GONE);
        }

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(mTitleStr);


        TextView tvRight = (TextView) view.findViewById(R.id.tv_right);
        if (TextUtils.isEmpty(mRightStr)) {
            tvRight.setVisibility(GONE);
        } else {
            tvRight.setVisibility(VISIBLE);
            tvRight.setText(mRightStr);
        }
    }

    public void setAttrs(AttributeSet attrs) {
        TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.weitaixinTitleBar);
        mRightStr = attributes.getString(R.styleable.weitaixinTitleBar_right_title_text);
        mLeftIconDrawable = attributes.getInt(R.styleable.weitaixinTitleBar_left_title_icon, R.drawable.head_back);
        mTitleStr = attributes.getString(R.styleable.weitaixinTitleBar_title_weitaixin_actionbar);
        mBackShow = attributes.getBoolean(R.styleable.weitaixinTitleBar_left_title_icon_show, false);
        attributes.recycle();
    }


}
