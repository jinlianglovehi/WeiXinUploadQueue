package cn.ihealthbaby.weitaixin.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liuhongjian on 15/8/10 15:54.
 */
public class RoundMaskView extends View {
	private Paint paint;
	private float angel;
	private float radius;
	private RectF oval;

	public RoundMaskView(Context context) {
		this(context, null, 0);
	}

	public RoundMaskView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(0xEFEFEF);
		paint.setAlpha(0xFF);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (angel <= 360) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				canvas.drawArc(0, 0, getWidth(), getWidth(), angel - 90, 360 - angel, true, paint);
			} else {
				canvas.drawArc(new RectF(0, 0, getWidth(), getWidth()), angel - 90, 360 - angel, true, paint);
			}
		}
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
