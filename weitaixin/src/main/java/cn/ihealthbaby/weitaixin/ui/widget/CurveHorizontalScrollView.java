package cn.ihealthbaby.weitaixin.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by liuhongjian on 15/7/1313:48.
 */
public class CurveHorizontalScrollView extends HorizontalScrollView {
	private boolean isTouching;

	public CurveHorizontalScrollView(Context context) {
		this(context, null, 0);
	}

	public CurveHorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CurveHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				setIsTouching(true);
				break;
			case MotionEvent.ACTION_MOVE:
				setIsTouching(true);
				break;
			case MotionEvent.ACTION_UP:
				setIsTouching(false);
				break;
		}
		return super.onTouchEvent(event);
	}

	public boolean isTouching() {
		return isTouching;
	}

	public void setIsTouching(boolean isTouching) {
		this.isTouching = isTouching;
	}
}
