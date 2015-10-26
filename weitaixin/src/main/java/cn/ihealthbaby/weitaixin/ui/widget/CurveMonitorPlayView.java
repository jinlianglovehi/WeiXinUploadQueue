package cn.ihealthbaby.weitaixin.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by liuhongjian on 15/9/10 13:56.先设置list,然后自动取点来绘制曲线
 */
public class CurveMonitorPlayView extends CurveBasicView {
	public Path backgroundPath;
	private int position;
	private int pureLineBackgroundColor = Color.GRAY;

	public CurveMonitorPlayView(Context context) {
		this(context, null, 0);
	}

	public CurveMonitorPlayView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CurveMonitorPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		backgroundPath = new Path();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawShadow(canvas);
		drawGrid(canvas);
		drawLimitLine(canvas);
		drawSafeLine(canvas);
		drawWholeCurve(canvas);
		drawCurve(canvas);
		drawScaleX(canvas);
		drawRedPoints(canvas);
		drawRedHeart(canvas, 10);
	}

	public void draw2Position(int position) {
		this.position = position;
		generatePath(path, position);
	}

	public void add2Position(int position) {
		this.position = position;
		add2Path(path, position);
	}

	private Path add2Path(Path path, int position) {
		final int size = fhrs.size();
		if (position >= size) {
			return null;
		}
		int fhr = fhrs.get(position);
		if (fhr < limitMin || fhr > limitMax) {
			fhr = 0;
		}
		if (fhr == 0 || position == 0) {
			path.moveTo(convertX(positionToX(position)), convertY(fhr));
		} else if (Math.abs(fhr - fhrs.get(position - 1)) > 20) {
			path.moveTo(convertX(positionToX(position)), convertY(fhr));
		} else {
			path.lineTo(convertX(positionToX(position)), convertY(fhr));
		}
		return path;
	}

	protected void drawWholeCurve(Canvas canvas) {
		resetPaint();
		paint.setStrokeWidth(curveStrokeWidth);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setColor(pureLineBackgroundColor);
		canvas.save();
		canvas.clipRect(convertX(0), convertY(limitMax), convertX(xMax), convertY(limitMin));
		canvas.drawPath(backgroundPath, paint);
		canvas.restore();
	}

	private Path generatePath(Path path, int position) {
		final int size = fhrs.size();
		if (position >= size) {
			return null;
		}
		for (int i = 0; i <= position; i++) {
			int fhr = fhrs.get(i);
			if (fhr < limitMin || fhr > limitMax) {
				fhr = 0;
			}
			if (fhr == 0 || i == 0) {
				path.moveTo(convertX(positionToX(i)), convertY(fhr));
			} else if (Math.abs(fhr - fhrs.get(i - 1)) > 20) {
				path.moveTo(convertX(positionToX(i)), convertY(fhr));
			} else {
				path.lineTo(convertX(positionToX(i)), convertY(fhr));
			}
		}
		return path;
	}

	public float getCurrentPositionX() {
		return convertX(positionToX(position));
	}

	public float convertPositionX(int position) {
		return convertX(positionToX(position));
	}

	@Override
	public void setFhrs(List<Integer> fhrs) {
		super.setFhrs(fhrs);
		generatePath(backgroundPath, fhrs.size() - 1);
	}

	@Override
	public void reset() {
		path.reset();
		position = 0;
	}
}
