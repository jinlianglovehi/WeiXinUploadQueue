package cn.ihealthbaby.weitaixin.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liuhongjian on 15/8/10 15:54.
 */
public class RoundMaskView extends View {
	private Paint paint;
	private float angel;
	private float radius = 100;

	public RoundMaskView(Context context) {
		this(context, null, 0);
	}

	public RoundMaskView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GREEN);
		paint.setAlpha(0x0F);
//		CountDownTimer countDownTimer = new CountDownTimer(10000, 10) {
//			public float angel;
//
//			@Override
//			public void onTick(long millisUntilFinished) {
//				setAngel(angel++);
//				invalidate();
//			}
//
//			@Override
//			public void onFinish() {
//			}
//		};
//		countDownTimer.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		RectF oval = new RectF(0, 0, radius, radius);
		canvas.drawArc(oval, 0, angel, true, paint);
	}

	public float getAngel() {
		return angel;
	}

	public void setAngel(float angel) {
		this.angel = angel;
	}

	public float getRadius() {
		return radius;
	}
}
