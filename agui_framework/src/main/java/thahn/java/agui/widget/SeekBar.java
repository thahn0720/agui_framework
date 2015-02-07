package thahn.java.agui.widget;

import java.awt.Color;
import java.awt.Graphics;

import thahn.java.agui.R;
import thahn.java.agui.app.Context;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.graphics.Rect;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup.LayoutParams;

//<SeekBar
//android:id="@+id/frequency_slider"
//android:layout_width="fill_parent"
//android:layout_height="wrap_content"
//android:max="20"
//android:progress="0"
//android:secondaryProgress="0"
//android:progressDrawable="@drawable/seekbar_progress"
//android:thumb="@drawable/seek_thumb"
///>
public class SeekBar extends View {

	private Drawable												mProgressDrawable;
	private Drawable												mThumbDrawable;
	private int														mSecondaryProgress;
	private int														mProgress;
	private int														mMax;
	
	private OnSeekBarChangeListener 								mOnSeekBarChangeListener;
	
	public SeekBar(Context context) {
		this(context, new AttributeSet(context));
	}
	
	public SeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_SEEK_BAR_CODE);
	}
	
	public SeekBar(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		mProgressDrawable = attrs.getDrawable(R.attr.SeekBar_progressDrawable, thahn.java.agui.R.drawable.btn_sel);
		mThumbDrawable = attrs.getDrawable(R.attr.SeekBar_thumb, thahn.java.agui.R.drawable.progressbar0);
		mMax = attrs.getInt(R.attr.SeekBar_max, 100);
		mProgress = attrs.getInt(R.attr.SeekBar_progress, 0);
		mSecondaryProgress = attrs.getInt(R.attr.SeekBar_secondaryProgress, 0);
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
		int tempHeight = mThumbDrawable.getIntrinsicHeight()>mProgressDrawable.getIntrinsicHeight()?mThumbDrawable.getIntrinsicHeight():mProgressDrawable.getIntrinsicHeight();
		tempHeight += getPaddingTop() + getPaddingBottom();
		if(mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			if(tempHeight > parentHeight) {
				mHeight= parentHeight;
			} else {
				mHeight = tempHeight;;	
			}
		}
	}

	@Override
	public void arrange() {
		super.arrange();
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		int progressWidth = mProgressDrawable.getIntrinsicWidth();
		int progressHeight = mProgressDrawable.getIntrinsicHeight();
		int left = getDrawingLeft();
		int top = getDrawingTop();
		int progress = (int)(((float)getDrawingWidth()/(float)mMax)*mProgress);
		Log.i(""+progress);
		mProgressDrawable.setBounds(left, top + getDrawingHeight()/2 - progressHeight/2, left + progress, top + getDrawingHeight()/2 - progressHeight/2 + progressHeight);
		mProgressDrawable.draw(g);
		//
		int thumbWidth = mThumbDrawable.getIntrinsicWidth();
		int thumbHeight = mThumbDrawable.getIntrinsicHeight();
		mThumbDrawable.setBounds(left + progress - thumbWidth/2, top + getDrawingHeight()/2 - thumbHeight/2, left + progress - thumbWidth/2 + thumbWidth, top + getDrawingHeight()/2 - thumbHeight/2 + thumbHeight);
		mThumbDrawable.draw(g);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = super.onTouchEvent(event);
		int action = event.getAction();
		int x = event.getX();
		int y = event.getY();
		// TODO : do'
		Rect thumbBounds = mThumbDrawable.getBounds();
		if(thumbBounds.contains(x, y)) {
			ret = true;
			
			mProgress = (int)((x-getDrawingLeft())/((float)getDrawingWidth()/(float)mMax));
			if(mProgress > mMax) mProgress = mMax;;
			if(mProgress < 0) mProgress = 0;
			
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				if(mOnSeekBarChangeListener != null) mOnSeekBarChangeListener.onStartTrackingTouch(this);
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				if(mOnSeekBarChangeListener != null) mOnSeekBarChangeListener.onStopTrackingTouch(this);
				break;
			}
			if(mOnSeekBarChangeListener != null) mOnSeekBarChangeListener.onProgressChanged(this, mProgress, true);
			
			invalidate();
		}
		return ret;
	}
	
	/**
     * <p>Set the current progress to the specified value. Does not do anything
     * if the progress bar is in indeterminate mode.</p>
     *
     * @param progress the new progress, between 0 and {@link #getMax()}
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        }

        if (progress > mMax) {
            progress = mMax;
        }

        if (progress != mProgress) {
            mProgress = progress;
            invalidate();
        }
    }
    
    /**
     * <p>Get the progress bar's current level of progress. Return 0 when the
     * progress bar is in indeterminate mode.</p>
     *
     * @return the current progress, between 0 and {@link #getMax()}
     */
    public synchronized int getProgress() {
        return mProgress;
    }
    
    /**
     * <p>Set the range of the progress bar to 0...<tt>max</tt>.</p>
     *
     * @param max the upper range of this progress bar
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            max = 0;
        }
        if (max != mMax) {
            mMax = max;

            if (mProgress > max) {
                mProgress = max;
            }
            invalidate();
        }
    }
	
	/**
     * Sets a listener to receive notifications of changes to the SeekBar's progress level. Also
     * provides notifications of when the user starts and stops a touch gesture within the SeekBar.
     * 
     * @param l The seek bar notification listener
     * 
     * @see SeekBar.OnSeekBarChangeListener
     */
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }
	
	/**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnSeekBarChangeListener {
        
        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         * 
         * @param seekBar The SeekBar whose progress has changed
         * @param progress The current progress level. This will be in the range 0..max where max
         *        was set by {@link ProgressBar#setMax(int)}. (The default value for max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    
        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar. 
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(SeekBar seekBar);
        
        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar. 
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(SeekBar seekBar);
    }
}
