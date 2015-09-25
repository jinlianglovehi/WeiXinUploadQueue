package cn.ihealthbaby.weitaixinpro.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by liuhongjian on 15/9/8 21:07.
 */
public class CurveMonitorDetialView extends CurveBasicView {
	public CurveMonitorDetialView(Context context) {
		this(context, null, 0);
	}

	public CurveMonitorDetialView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CurveMonitorDetialView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawShadow(canvas);
		drawGrid(canvas);
		drawLimitLine(canvas);
		drawSafeLine(canvas);
		drawCurve(canvas);
		drawScaleX(canvas);
		drawRedPoints(canvas);
		drawRedHeart(canvas, 10);
	}
}
