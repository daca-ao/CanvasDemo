package com.example.canvasdemo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
/**
 * 
 * @author aohuijun
 *
 */
public class PaintBoardView extends View {

	private static final int TOUCH_TOLERANCE = 4;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	private Paint mRubber;
	private Path mPath;
	private DrawPath mDrawPath;
	
	// Record of drawing path
	private List<DrawPath> mDrawPathsList;
	// Record of undo-drawing path
	private List<DrawPath> mBufPathsList;
	
	private float mX, mY;
	private int mBitmapWidth, mBitmapHeight;
	private boolean isUsingRubber;
	
	public PaintBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Get the display parameters of the phone
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mBitmapWidth = displayMetrics.widthPixels;
		mBitmapHeight = displayMetrics.heightPixels;
		initPaintBoard();
	}

	private void initPaintBoard() {
		initCanvas();
		mPaint = setPaint(CanvasPage.INIT_PEN_SIZE, Color.BLACK, false);
		mRubber = setPaint(CanvasPage.INIT_RUBBER_SIZE, Color.TRANSPARENT, true);
		mPath = new Path();
		mDrawPath = new DrawPath();
		mDrawPathsList = new ArrayList<DrawPath>();
		mBufPathsList = new ArrayList<DrawPath>();
	}

	private Paint setPaint(float width, int color, boolean isRubber) {
		Paint paint = new Paint();
		//	Round the edge of the paint
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setPathEffect(new CornerPathEffect(paint.getStrokeWidth()));
		//	Set input params
		paint.setStrokeWidth(width);
		paint.setColor(color);
		if (isRubber) {
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			paint.setAlpha(0);
		}
		return paint;
	}

	public void setPaintWidth(float width) {
		mPaint.setStrokeWidth(width);
	}
	
	public void setPaintColor(int color) {
		mPaint.setColor(color);
	}
	
	public void enableRubber(boolean isRubberEnabled) {
		this.isUsingRubber = isRubberEnabled;
	}
	
	public void setRubberWidth(float width) {
		mRubber.setStrokeWidth(width);
	}
	
	private void initCanvas() {
		mBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.TRANSPARENT);
	}

	class DrawPath {
		Path mSinglePath = new Path();
		Paint mSinglePaint = new Paint();
		boolean mIsRubber;
	}
	
	public void undo() {
		if (mDrawPathsList != null && mDrawPathsList.size() > 0) {
			initCanvas();
			DrawPath buffDrawPath = mDrawPathsList.remove(mDrawPathsList.size() - 1);
			mBufPathsList.add(0, buffDrawPath);
			for (DrawPath drawPath : mDrawPathsList) {
				mCanvas.drawPath(drawPath.mSinglePath, drawPath.mSinglePaint);
			}
			Toast.makeText(getContext().getApplicationContext(), "undo", Toast.LENGTH_SHORT).show();
			invalidate();
		}
	}
	
	public void redo() {
		if (mBufPathsList != null && mBufPathsList.size() > 0) {
			DrawPath bufDrawPath = mBufPathsList.get(0);
			mDrawPathsList.add(bufDrawPath);
			mCanvas.drawPath(bufDrawPath.mSinglePath, bufDrawPath.mSinglePaint);
			mBufPathsList.remove(0);
			Toast.makeText(getContext().getApplicationContext(), "redo", Toast.LENGTH_SHORT).show();
			invalidate();
		}
	}
	
	public void clear() {
		if (mBitmap != null) {
			initCanvas();
		}
		mDrawPathsList.clear();
		mBufPathsList.clear();
		Toast.makeText(getContext().getApplicationContext(), "clear", Toast.LENGTH_SHORT).show();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawBitmap(mBitmap, 0, 0, null);
//		if (isUsingRubber) {
//			if (mPath != null) 
//				canvas.drawPath(mPath, mRubber);
//		} else {
//			if (mPath != null) 
//				canvas.drawPath(mPath, mPaint);
//		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float startX = event.getX();
		float startY = event.getY();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mPath = new Path();
				mDrawPath = new DrawPath();
				mDrawPath.mSinglePath = mPath;
				if (isUsingRubber) {
//					mDrawPath.mSinglePaint = mRubber;	//	Use this statement may cause bugs of redo & undo. 
					mDrawPath.mSinglePaint = setPaint(mRubber.getStrokeWidth(), mRubber.getColor(), true);
					mDrawPath.mIsRubber = true;
				} else {
//					mDrawPath.mSinglePaint = mPaint;
					mDrawPath.mSinglePaint = setPaint(mPaint.getStrokeWidth(), mPaint.getColor(), false);
					mDrawPath.mIsRubber = false;
				}
				mDrawPath.mSinglePath.moveTo(startX, startY);
				mX = startX;
				mY = startY;
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(startX - mX);
				float dy = Math.abs(startY - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					mDrawPath.mSinglePath.quadTo(mX, mY, (startX + mX)/2, (startY + mY)/2);
					mX = startX;
					mY = startY;
				}
				if (isUsingRubber) {
					mCanvas.drawPath(mDrawPath.mSinglePath, mRubber);
				} else {
					mCanvas.drawPath(mDrawPath.mSinglePath, mPaint);
				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				mDrawPath.mSinglePath.lineTo(mX, mY);
				mDrawPathsList.add(mDrawPath);
				mPath = null;
				invalidate();
				break;
			default:
				break;
		}
		return true;
	}

}
