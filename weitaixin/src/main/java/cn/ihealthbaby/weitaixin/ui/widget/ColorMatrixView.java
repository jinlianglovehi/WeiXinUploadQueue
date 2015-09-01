package cn.ihealthbaby.weitaixin.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liuhongjian on 15/8/10 17:08.
 */
public class ColorMatrixView extends View {
	private Context context;
	private float[] colorArray = {
			                             1, 0, 0, 0, 0,
			                             0, 1, 0, 0, 0,
			                             0, 0, 1, 0, 0,
			                             0, 0, 0, 1, 0
	};
	private Bitmap bitmap;
	private Paint paint = new Paint();
	private Paint paint1 = new Paint();

	public ColorMatrixView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	public ColorMatrixView(Context context) {
		super(context);
		this.context = context;
//		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rect);
		paint1 = new Paint();
	}

	public ColorMatrixView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.set(colorArray);
		ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(colorMatrixColorFilter);
		canvas.drawBitmap(bitmap, 0, 0, paint1);
		canvas.drawRect(10, 10, 1200, 400, paint);
	}
}
