package com.personal.image.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

	public class ZoomRotateImageView extends ImageView {
		// 缩放手势检测
		private ScaleGestureDetector mScaleDetector;
		private Matrix mImageMatrix;
		private int mPivotX, mPivotY;
		private int mLastX, mLastY;
		private int mLastAngle = 0;
		private int mLastPointerCount;
//		private MotionEvent mEvent;

		public ZoomRotateImageView(Context context) {
			super(context);
			init(context);

		}

		public ZoomRotateImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
		}

		public ZoomRotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
			init(context);
		}

		private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleListener =
				new ScaleGestureDetector.SimpleOnScaleGestureListener() {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {

				float scaleFactor = detector.getScaleFactor();
				mImageMatrix.postScale(scaleFactor, scaleFactor, mPivotX, mPivotY);
				setImageMatrix(mImageMatrix);
				return true;
			}
		};

		private void init(Context context) {
		
			mScaleDetector = new ScaleGestureDetector(context, mScaleListener);
			setScaleType(ScaleType.MATRIX);
			mImageMatrix = new Matrix();
		}

		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			if (w != oldw || h != oldh) {
				// Shift the image to the center of the view
				int translateX = (w - getDrawable().getIntrinsicWidth()) / 2;
				int translateY = (h - getDrawable().getIntrinsicHeight()) / 2;
				mImageMatrix.setTranslate(translateX, translateY);
				setImageMatrix(mImageMatrix);
				// Get the center point for future scale and rotate transforms
				mPivotX = w / 2;
				mPivotY = h / 2;
			}
		}
		
		/*
		 * init the data when you change gesture
		 */
		public void initData(MotionEvent event){
			if(event.getPointerCount()!=mLastPointerCount){
				mLastX = (int) event.getX();
				mLastY = (int) event.getY();
				
			}
			
		}

		private boolean doRotationEvent(MotionEvent event) {
			// Calculate the angle between the two fingers
			
			float deltaX = event.getX(0) - event.getX(1);
			float deltaY = event.getY(0) - event.getY(1);
			
			double radians = Math.atan(deltaY / deltaX);
			// Convert to degrees
			int degrees = (int) (radians * 180 / Math.PI);

			/* 
			 * Must use getActionMasked() for switching to pick up pointer events.
			 * These events have the pointer index encoded in them so the return
			 * from getAction() won't match the exact action constant.
			 */
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_UP:
				// Mark the initial angle
				mLastAngle = degrees;
				break;
			case MotionEvent.ACTION_MOVE:
				// ATAN returns a converted value between -90deg and +90deg
				// which creates a point when two fingers are vertical where the
				// angle flips sign. We handle this case by rotating a small amount
				// (5 degrees) in the direction we were traveling
				if ((degrees - mLastAngle) > 45) {
					// Going CCW across the boundary
					mImageMatrix.postRotate(-5, mPivotX, mPivotY);
				} else if ((degrees - mLastAngle) < -45) {
					// Going CW across the boundary
					mImageMatrix.postRotate(5, mPivotX, mPivotY);
				} else {
					// Normal rotation, rotate the difference
					mImageMatrix.postRotate(degrees - mLastAngle, mPivotX, mPivotY);
				}
				// Post the rotation to the image
				setImageMatrix(mImageMatrix);
				// Save the current angle
				mLastAngle = degrees;
				break;
			}

			return true;
		}
		
		private boolean doMoveEvent(MotionEvent event){
			float deltaX = event.getX() - mLastX;
			float deltaY = event.getY() - mLastY;
			
			mLastX = (int) event.getX();
			mLastY = (int) event.getY();
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastX = (int) event.getX();
				mLastY = (int) event.getY();
				
				break;
				
			case MotionEvent.ACTION_MOVE:
				
				mImageMatrix.postTranslate(deltaX, deltaY);
				setImageMatrix(mImageMatrix);
				mLastX = (int) event.getX();
				mLastY = (int) event.getY();
				break;
			}
			
			return true;
		}
		
		/**
		 * 确保系统没有占用这些手势，若系统占用，关闭后可用
		 */

		@Override
		public boolean onTouchEvent(MotionEvent event) {
//			if (event.getAction() == MotionEvent.ACTION_DOWN) {
//				return true;
//			}
			switch (event.getPointerCount()) {
			case 1:
//				单指移动
				initData(event);
				mLastPointerCount = 1;
				return doMoveEvent(event);
			case 2:
//				双指缩放
				initData(event);
				mLastPointerCount = 2;
				return mScaleDetector.onTouchEvent(event);
			case 3:
//				三指旋转
				initData(event);
				mLastPointerCount = 3;
				return doRotationEvent(event);
			
			
			default:
				return super.onTouchEvent(event);
			}
		}
		
	}
