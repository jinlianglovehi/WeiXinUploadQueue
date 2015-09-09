package cn.ihealthbaby.weitaixin.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by liuhongjian on 15/9/8 21:08.
 */
public class CurveMonitorSimpleView extends CurveBasicView {
	public CurveMonitorSimpleView(Context context) {
		this(context, null, 0);
	}

	public CurveMonitorSimpleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CurveMonitorSimpleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		drawShadow(canvas);
//		drawGrid(canvas);
//		drawLimitLine(canvas);
		drawSafeLine(canvas);
		drawCurve(canvas);
//		drawScaleX(canvas);
//		drawRedPoints(canvas);
		drawRedHeart(canvas);
	}
}
