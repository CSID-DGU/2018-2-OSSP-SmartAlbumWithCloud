package com.SmartAlbumWithCloud.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;


public class MatrixImageView extends ImageView {
	public final static String TAG="MatrixImageView";
	private GestureDetector mGestureDetector;
	/**  Template Matrix for initialization */
	private Matrix mMatrix=new Matrix();
	/**  Picture length*/
	private float mImageWidth;
	/**  Picture height */
	private float mImageHeight;
	/**  Original zoom level */
	private float mScale
			;
	private OnMovingListener moveListener;
	private OnSingleTapListener singleTapListener;

	public MatrixImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		MatrixTouchListener mListener=new MatrixTouchListener();
		setOnTouchListener(mListener);
		mGestureDetector=new GestureDetector(getContext(), new GestureListener(mListener));
		//Background set to black
		setBackgroundColor(Color.BLACK);
		//Set the zoom type to CENTER_INSIDE, which means that the image is centered, and the width and height are the width and height of the control.
		setScaleType(ScaleType.FIT_CENTER);
	}
	public MatrixImageView(Context context) {
		super(context, null);
		MatrixTouchListener mListener=new MatrixTouchListener();
		setOnTouchListener(mListener);
		mGestureDetector=new GestureDetector(getContext(), new GestureListener(mListener));
		//Background set to black
		setBackgroundColor(Color.BLACK);
		//Set the zoom type to CENTER_INSIDE, which means that the image is centered, and the width and height are the width and height of the control.
		setScaleType(ScaleType.FIT_CENTER);
	}
	public void setOnMovingListener(OnMovingListener listener){
		moveListener=listener;
	}
	public void setOnSingleTapListener(OnSingleTapListener onSingleTapListener) {
		this.singleTapListener = onSingleTapListener;
	}
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		//A size of 0 means the current control size is not measured. Set the listener function. Assign before drawing.
		if(getWidth()==0){
			ViewTreeObserver vto = getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
			{
				public boolean onPreDraw()
				{
					initData();
					//Remove the listener function after the assignment is over
					MatrixImageView.this.getViewTreeObserver().removeOnPreDrawListener(this);
					return true;
				}
			});
		}else {
			initData();
		}
	}

	/**
	 *   Initialize the template Matrix and other data for the image
	 */
	private void initData() {
		//After setting the image, get the coordinate transformation matrix of the image.
		mMatrix.set(getImageMatrix());
		float[] values=new float[9];
		mMatrix.getValues(values);
		//Image width is the screen width divided by the zoom factor
		mImageWidth=getWidth()/values[Matrix.MSCALE_X];
		mImageHeight=(getHeight()-values[Matrix.MTRANS_Y]*2)/values[Matrix.MSCALE_Y];
		mScale=values[Matrix.MSCALE_X];
	}

	public class MatrixTouchListener implements OnTouchListener {
		/** Drag photo mode */
		private static final int MODE_DRAG = 1;
		/** Zoom in and out photo mode */
		private static final int MODE_ZOOM = 2;
		/**  Matrix is not supported */
		private static final int MODE_UNABLE=3;
		/**   Maximum zoom level*/
		float mMaxScale=6;
		/**   Zoom level when double clicking*/
		float mDobleClickScale=2;
		private int mMode = 0;// 
		/**  Finger spacing at the start of zooming */
		private float mStartDis;
		/**   Current Matrix */
		private Matrix mCurrentMatrix = new Matrix();

		/** Used to record the coordinate position at the beginning */

		/** Correlation with ViewPager to determine whether it is possible to move left or right  */
		boolean mLeftDragable;
		boolean mRightDragable;
		/**  Whether to move for the first time */
		boolean mFirstMove=false;
		private PointF mStartPoint = new PointF();
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					//Set the drag mode
					mMode=MODE_DRAG;
					mStartPoint.set(event.getX(), event.getY());
					isMatrixEnable();
					startDrag();
					checkDragable();
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					reSetMatrix();
					stopDrag();
					break;
				case MotionEvent.ACTION_MOVE:
					if (mMode == MODE_ZOOM) {
						setZoomMatrix(event);
					}else if (mMode==MODE_DRAG) {
						setDragMatrix(event);
					}else {
						stopDrag();
					}
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					if(mMode==MODE_UNABLE) return true;
					mMode=MODE_ZOOM;
					mStartDis = distance(event);
					break;
				case MotionEvent.ACTION_POINTER_UP:

					break;
				default:
					break;
			}
			return mGestureDetector.onTouchEvent(event);
		}

		/**
		 *   The child control begins to move into the state, making ViewPager unable to intercept the touch event on the child control
		 */
		private void startDrag(){
			if(moveListener!=null) moveListener.startDrag();

		}
		/**
		 *   The child control starts to stop moving, ViewPager will intercept the touch event on the child control
		 */
		private void stopDrag(){
			if(moveListener!=null) moveListener.stopDrag();
		}

		/**
		 *   Set the draggable state according to the left and right edges of the current image
		 */
		private void checkDragable() {
			mLeftDragable=true;
			mRightDragable=true;
			mFirstMove=true;
			float[] values=new float[9];
			getImageMatrix().getValues(values);
			//The left edge of the picture leaves the left border, indicating that it cannot be shifted right
			if(values[Matrix.MTRANS_X]>=0)
				mRightDragable=false;
			//The right edge of the picture leaves the right border, indicating that it cannot be shifted to the left
			if((mImageWidth)*values[Matrix.MSCALE_X]+values[Matrix.MTRANS_X]<=getWidth()){
				mLeftDragable=false;
			}
		}

		/**
		 *  Set the Matrix in the dragged state
		 *  @param event
		 */
		public void setDragMatrix(MotionEvent event) {
			if(isZoomChanged()){
				float dx = event.getX() - mStartPoint.x; // Get the moving distance of the x-axis
				float dy = event.getY() - mStartPoint.y; // Get the moving distance of the x-axis
				//Avoid conflicts with double clicks, greater than 10f is considered to be dragging
				if(Math.sqrt(dx*dx+dy*dy)>10f){
					mStartPoint.set(event.getX(), event.getY());
					//Move on current basis
					mCurrentMatrix.set(getImageMatrix());
					float[] values=new float[9];
					mCurrentMatrix.getValues(values);
					dy=checkDyBound(values,dy);
					dx=checkDxBound(values,dx,dy);

					mCurrentMatrix.postTranslate(dx, dy);
					setImageMatrix(mCurrentMatrix);
				}
			}else {
				stopDrag();
			}
		}

		/**
		 *  Determine if the zoom level has changed
		 *  @return   True means non-initial value, false means initial value
		 */
		private boolean isZoomChanged() {
			float[] values=new float[9];
			getImageMatrix().getValues(values);
			//Get the current X-axis zoom level
			float scale=values[Matrix.MSCALE_X];
			//Get the X-axis zoom level of the template, compare the two
			return scale!=mScale;
		}

		/**
		 *  Contrast with the current matrix, check dy so that the image does not move beyond the ImageView boundary
		 *  @param values
		 *  @param dy
		 *  @return
		 */
		private float checkDyBound(float[] values, float dy) {
			float height=getHeight();
			if(mImageHeight*values[Matrix.MSCALE_Y]<height)
				return 0;
			if(values[Matrix.MTRANS_Y]+dy>0)
				dy=-values[Matrix.MTRANS_Y];
			else if(values[Matrix.MTRANS_Y]+dy<-(mImageHeight*values[Matrix.MSCALE_Y]-height))
				dy=-(mImageHeight*values[Matrix.MSCALE_Y]-height)-values[Matrix.MTRANS_Y];
			return dy;
		}

		/**
		 *  Contrast with the current matrix, check dx so that the image does not move beyond the ImageView boundary
		 *  @param values
		 *  @param dx
		 *  @return
		 */
		private float checkDxBound(float[] values,float dx,float dy) {
			float width=getWidth();
			if(!mLeftDragable&&dx<0){
				//Adding a contrast with the y-axis means that the item is not switched when the gesture in the vertical direction is monitored.
				if(Math.abs(dx)*0.4f> Math.abs(dy)&&mFirstMove){
					stopDrag();
				}
				return 0;
			}
			if(!mRightDragable&&dx>0){
				//Adding a contrast with the y-axis means that the item is not switched when the gesture in the vertical direction is monitored.
				if(Math.abs(dx)*0.4f> Math.abs(dy)&&mFirstMove){
					stopDrag();
				}
				return 0;
			}
			mLeftDragable=true;
			mRightDragable=true;
			if(mFirstMove) mFirstMove=false;
			if(mImageWidth*values[Matrix.MSCALE_X]<width){
				return 0;

			}
			if(values[Matrix.MTRANS_X]+dx>0){
				dx=-values[Matrix.MTRANS_X];
			}
			else if(values[Matrix.MTRANS_X]+dx<-(mImageWidth*values[Matrix.MSCALE_X]-width)){
				dx=-(mImageWidth*values[Matrix.MSCALE_X]-width)-values[Matrix.MTRANS_X];
			}
			return dx;
		}

		/**
		 *  Set Zoom Matrix
		 *  @param event
		 */
		private void setZoomMatrix(MotionEvent event) {
			//Execute only when two screens are touched at the same time
			if(event.getPointerCount()<2) return;
			float endDis = distance(event);// End distance
			if (endDis > 10f) { // Pixels greater than 10 when two fingers are brought together
				float scale = endDis / mStartDis;// Get zoom factor
				mStartDis=endDis;//Reset distance
				mCurrentMatrix.set(getImageMatrix());//Initialize Matrix
				float[] values=new float[9];
				mCurrentMatrix.getValues(values);
				scale = checkMaxScale(scale, values);
				PointF centerF=getCenter(scale,values);
				mCurrentMatrix.postScale(scale, scale,centerF.x,centerF.y);
				setImageMatrix(mCurrentMatrix);
			}
		}

		/**
		 *  Get the center point of the zoom.
		 *  @param scale
		 *  @param values
		 *  @return
		 */
		private PointF getCenter(float scale, float[] values) {
			//Returns the ImageView center point as the zoom center point when the zoom level is less than the original zoom level or when it is zoomed in
			if(scale*values[Matrix.MSCALE_X]<mScale||scale>=1){
				return new PointF(getWidth()/2,getHeight()/2);
			}
			float cx=getWidth()/2;
			float cy=getHeight()/2;
			//Use the ImageView center point as the zoom center to determine whether the left edge of the zoomed image
			//will leave the left edge of the ImageView. If so, the left edge is the X-axis center.
			if((getWidth()/2-values[Matrix.MTRANS_X])*scale<getWidth()/2)
				cx=0;
			//Determine if the right edge of the zoom will leave the right edge of the ImageView, if the right edge is the X-axis center
			if((mImageWidth*values[Matrix.MSCALE_X]+values[Matrix.MTRANS_X])*scale<getWidth())
				cx=getWidth();
			return new PointF(cx,cy);
		}

		/**
		 *  Verify the scale so that the image does not scale beyond the maximum multiple
		 *  @param scale
		 *  @param values
		 *  @return
		 */
		private float checkMaxScale(float scale, float[] values) {
			if(scale*values[Matrix.MSCALE_X]>mMaxScale)
				scale=mMaxScale/values[Matrix.MSCALE_X];
			return scale;
		}

		/**
		 *   Reset Matrix
		 */
		private void reSetMatrix() {
			if(checkRest()){
				mCurrentMatrix.set(mMatrix);
				setImageMatrix(mCurrentMatrix);
			}else {
				//Determine if the Y axis needs correction
				float[] values=new float[9];
				getImageMatrix().getValues(values);
				float height=mImageHeight*values[Matrix.MSCALE_Y];
				if(height<getHeight()){
					//When the true height of the picture is less than the height of the container, the Y axis is centered, and the ideal offset of the Y axis is the height difference of /2.
					float topMargin=(getHeight()-height)/2;
					if(topMargin!=values[Matrix.MTRANS_Y]){
						mCurrentMatrix.set(getImageMatrix());
						mCurrentMatrix.postTranslate(0, topMargin-values[Matrix.MTRANS_Y]);
						setImageMatrix(mCurrentMatrix);
					}
				}
			}
		}

		/**
		 *  Determine if you need to reset
		 *  @return  Reset when the current zoom level is less than the template zoom level
		 */
		private boolean checkRest() {
			// TODO Auto-generated method stub
			float[] values=new float[9];
			getImageMatrix().getValues(values);
			//Get the current X-axis zoom level
			float scale=values[Matrix.MSCALE_X];
			//Get the X-axis zoom level of the template, compare the two
			return scale<mScale;
		}

		/**
		 *  Determine if Matrix is supported
		 */
		private void isMatrixEnable() {
			//Unscalable when loading errors
			if(getScaleType()!= ScaleType.CENTER){
				setScaleType(ScaleType.MATRIX);
			}else {
				mMode=MODE_UNABLE;//Set to not support gestures
			}
		}

		/**
		 *  Calculate the distance between two fingers
		 *  @param event
		 *  @return
		 */
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** Use the Pythagorean theorem to return the distance between two points */
			return (float) Math.sqrt(dx * dx + dy * dy);
		}

		/**
		 *   Triggered when double clicked
		 */
		public void onDoubleClick(){
			float scale=isZoomChanged()?1:mDobleClickScale;
			mCurrentMatrix.set(mMatrix);//Initialize Matrix
			mCurrentMatrix.postScale(scale, scale,getWidth()/2,getHeight()/2);
			setImageMatrix(mCurrentMatrix);
		}
	}


	private class  GestureListener extends SimpleOnGestureListener {
		private final MatrixTouchListener listener;
		public GestureListener(MatrixTouchListener listener) {
			this.listener=listener;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			//Capture Down event
			return true;
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			//Trigger a double click event
			listener.onDoubleClick();
			return true;
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onSingleTapUp(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							   float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onShowPress(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDoubleTapEvent(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(singleTapListener!=null) singleTapListener.onSingleTap();
			return super.onSingleTapConfirmed(e);
		}

	}
	/**
	 * @ClassName: OnChildMovingListener
	 * @Description:  matrixImageView mobile listener interface to organize ViewPager interception of Move operation
	 * @author LinJ
	 * @date 2015-1-12 year 4:39:32 pm
	 *
	 */
	public interface OnMovingListener{
		public void  startDrag();
		public void  stopDrag();
	}

	/**
	 * @ClassName: OnSingleTapListener
	 * @Description:  Listening to the ViewPager screen click event, essentially the click event of the listener control MatrixImageView
	 * @author LinJ
	 * @date 2015-1-12 year 4:48:52  pm
	 *
	 */
	public interface OnSingleTapListener{
		public void onSingleTap();
	}
}