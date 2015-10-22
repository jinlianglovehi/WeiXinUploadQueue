package cn.ihealthbaby.weitaixin.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.util.Util;

/**
 * Created by liuhongjian on 15/9/8 14:47.绘图的基类
 */
public class CurveBasicView extends CoordinateView {
	public RectF rectF;
	protected Paint paint;
	protected float textSizeY = 40;
	protected float textSizeX = 30;
	protected int textColorX = Color.parseColor("#9B9B9B");
	protected int textColorY;
	protected int safeLineColor = Color.parseColor("#B2FE9DBF");
	protected int limitLineColor = Color.parseColor("#B24FDEB7");
	protected int redPointColor = Color.parseColor("#01CF97");
	protected int gridColor = Color.parseColor("#B2DEDEDE");
	protected int shadowColor = Color.parseColor("#E8F6EF");
	private Bitmap scaledBitmap;
	/**
	 * 保存的是发生胎动的点的位置
	 */
	private List<Integer> hearts = new ArrayList<>();
	private Path path = new Path();
	/**
	 * 保存胎心数值
	 */
	private List<Integer> fhrs = new ArrayList<>();
	private int position;
	private int heartWidth;
	private float curveStrokeWidth = 2;

	public CurveBasicView(Context context) {
		this(context, null, 0);
	}

	public CurveBasicView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CurveBasicView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		heartWidth = Util.dip2px(context, 6);
		scaledBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.red_heart_small)).getBitmap(), heartWidth, heartWidth, true);
		getRectF();
	}

	public int getSafeLineColor() {
		return safeLineColor;
	}

	public void setSafeLineColor(int safeLineColor) {
		this.safeLineColor = safeLineColor;
	}

	public int getLimitLineColor() {
		return limitLineColor;
	}

	public void setLimitLineColor(int limitLineColor) {
		this.limitLineColor = limitLineColor;
	}

	public float getCurveStrokeWidth() {
		return curveStrokeWidth;
	}

	public void setCurveStrokeWidth(float curveStrokeWidth) {
		this.curveStrokeWidth = curveStrokeWidth;
	}

	public List<Integer> getHearts() {
		return hearts;
	}

	public void setHearts(List<Integer> hearts) {
		this.hearts = hearts;
	}

	public List<Integer> getFhrs() {
		return fhrs;
	}

	public void setFhrs(List<Integer> fhrs) {
		this.fhrs = fhrs;
	}

	public int getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(int shadowColor) {
		this.shadowColor = shadowColor;
	}

	public void resetPaint() {
		paint.reset();
		paint.setAntiAlias(true);
	}

	protected void drawShadow(Canvas canvas) {
		resetPaint();
		paint.setColor(shadowColor);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(rectF, paint);
	}

	private void getRectF() {
		rectF = new RectF(convertX(xMin), convertY(safeMax), convertX(xMax), convertY(safeMin));
	}

	@Override
	public void setxMax(int xMax) {
		super.setxMax(xMax);
		getRectF();
	}

	@Override
	public void setxMin(int xMin) {
		super.setxMin(xMin);
		getRectF();
	}

	@Override
	public void setSafeMax(int safeMax) {
		super.setSafeMax(safeMax);
		getRectF();
	}

	@Override
	public void setSafeMin(int safeMin) {
		super.setSafeMin(safeMin);
		getRectF();
	}

	public void addPoint(int fhr) {
		fhrs.add(fhr);
		int position = fhrs.size();
		if (fhr < limitMin || fhr > limitMax) {
			fhr = 0;
		}
		if (fhr == 0 || position == 0) {
			path.moveTo(convertX(positionToX(position)), convertY(fhr));
		} else {
			path.lineTo(convertX(positionToX(position)), convertY(fhr));
		}
	}




	public void reset() {
		fhrs.clear();
		hearts.clear();
		path.reset();
		position = 0;
	}

	public void resetPoints() {
		int position = fhrs.size() - 1;
		path.reset();
		for (int i = 0; i < position; i++) {
			int fhr = fhrs.get(i);
			if (fhr == 0 || i == 0) {
				path.moveTo(convertX(positionToX(i)), convertY(fhr));
			} else  {
				path.lineTo(convertX(positionToX(i)), convertY(fhr));
			}
		}
	}

	protected void drawCurve(Canvas canvas) {
		resetPaint();
		paint.setStrokeWidth(curveStrokeWidth);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);
		canvas.save();
		paint.setColor(safeLineColor);
		canvas.clipRect(convertX(0), convertY(safeMin), convertX(xMax), convertY(limitMin));
		canvas.drawPath(path, paint);
		canvas.restore();
		//
		canvas.save();
		paint.setColor(safeLineColor);
		canvas.clipRect(convertX(0), convertY(limitMax), convertX(xMax), convertY(safeMax));
		canvas.drawPath(path, paint);
		canvas.restore();
		//
		canvas.save();
		paint.setColor(limitLineColor);
		canvas.clipRect(convertX(0), convertY(safeMax), convertX(xMax), convertY(safeMin));
		canvas.drawPath(path, paint);
		canvas.restore();
		//
	}

	protected void drawRedHeart(Canvas canvas, int y) {
		resetPaint();
		int size = hearts.size();
		if (size <= 0) {
			return;
		}
		for (int i = 0; i < size; i++) {
			canvas.drawBitmap(scaledBitmap, convertX(positionToX(hearts.get(i))) - heartWidth / 2, convertY(y) - heartWidth / 2, paint);
		}
	}

	public void addRedHeart(int position) {
		hearts.add(position);
	}

	protected void drawScaleX(Canvas canvas) {
		resetPaint();
		paint.setColor(textColorX);
		paint.setTextSize(textSizeX);
		for (int i = 0; i <= xMax / 60; i++) {
			canvas.drawText(i + "min", convertX(i * 60 - 3), convertY(-15), paint);
		}
		for (int j = 0; j < xMax / 60; j++) {
			paint.setColor(limitLineColor);
			canvas.drawText(limitMaxString, convertX(j * 60 - 2), convertY(limitMax - 5), paint);
			canvas.drawText(limitMinString, convertX(j * 60 - 2), convertY(limitMin - 5), paint);
			paint.setColor(safeLineColor);
			canvas.drawText(safeMaxString, convertX(j * 60 - 2), convertY(safeMax - 5), paint);
			canvas.drawText(safeMinString, convertX(j * 60 - 2), convertY(safeMin - 5), paint);
		}
	}

	protected void drawRedPoints(Canvas canvas) {
		resetPaint();
		paint.setColor(redPointColor);
		paint.setStyle(Paint.Style.FILL);
		int iMax = xMax / 60 + (xMax % 60 == 0 ? 0 : 1);
		for (int i = 0; i <= iMax; i++) {
			canvas.drawCircle(convertX(i * 60), convertY(0), 6, paint);
		}
		paint.setStyle(Paint.Style.STROKE);
	}

	protected void drawSafeLine(Canvas canvas) {
		resetPaint();
		paint.setColor(safeLineColor);
		paint.setTextSize(textSizeY);
		canvas.drawLine(convertX(0), convertY(safeMin), convertX(xMax), convertY(safeMin), paint);
		canvas.drawLine(convertX(0), convertY(safeMax), convertX(xMax), convertY(safeMax), paint);
	}

	protected void drawLimitLine(Canvas canvas) {
		resetPaint();
		paint.setColor(limitLineColor);
		paint.setTextSize(textSizeY);
		canvas.drawLine(convertX(0), convertY(limitMin), convertX(xMax), convertY(limitMin), paint);
		canvas.drawLine(convertX(0), convertY(limitMax), convertX(xMax), convertY(limitMax), paint);
	}

	protected void drawGrid(Canvas canvas) {
		resetPaint();
		paint.setColor(gridColor);
		for (int i = 0; i <= (xMax - xMin) / gridX; i++) {
			canvas.drawLine(convertX(i * gridX), convertY(0), convertX(i * gridX), convertY(yMax), paint);
		}
		for (int j = 0; j <= (yMax - yMin) / gridY; j++) {
			if (j * gridY == limitMax || j * gridY == limitMin || j * gridY == safeMax || j * gridY == safeMin) {
				continue;
			}
			canvas.drawLine(convertX(0), convertY(j * gridY), convertX(xMax), convertY(j * gridY), paint);
		}
	}

	protected float positionToX(int position) {
		return ((float) position) * 60 / pointsPerMin;
	}

	public float getCurrentPositionX() {
		return convertX(positionToX(fhrs.size() - 1));
	}
}
