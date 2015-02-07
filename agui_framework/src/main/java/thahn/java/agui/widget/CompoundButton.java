package thahn.java.agui.widget;

import java.awt.Graphics;

import thahn.java.agui.app.Context;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.ViewGroup.LayoutParams;


public abstract class CompoundButton extends TextView implements Checkable {

	private boolean 														mChecked; 
	private Drawable														mButtonDrawable;
	private OnCheckedChangeListener											mOnCheckedChangeListener;
	
	public CompoundButton(Context context) {
		this(context, new AttributeSet(context));
	}

	public CompoundButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CompoundButton(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}
	
    @Override
	public void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		Drawable d = attrs.getDrawable(thahn.java.agui.R.attr.CompoundButton_button, defaultType);
		boolean checked = attrs.getBoolean(thahn.java.agui.R.attr.CompoundButton_checked, false);
		
		setButtonDrawable(d);
		setChecked(checked);
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
		
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			measureHeight(parentHeight);  
		}
	}
	
	@Override
	public void arrange() {
		super.arrange();
		
		if (getParent() != null) {
			int parentHeight = getParent().getHeight();
			measureHeight(parentHeight); 
		} 
		
		mButtonDrawable.setBounds(getDrawingLeft(), getDrawingTop()
				, getDrawingLeft() + mButtonDrawable.getIntrinsicWidth()
				, getDrawingTop() + mButtonDrawable.getIntrinsicHeight());
	}

	private void measureHeight(int parentHeight) {
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			int temp = mButtonDrawable.getIntrinsicHeight();
			if (mHeight > temp) {
				temp = mHeight;
			} else {
				int paddedTemp = temp+getPaddingTop()+getPaddingBottom();
				if (paddedTemp > parentHeight) {
					temp = parentHeight;
				} else {
					temp = paddedTemp;
				}
			}
			if (parentHeight > temp) {
				mHeight = temp;
			} else {
				mHeight = parentHeight; 
			}
			
			mLayoutParam.bottom = getTop() + mHeight;
		}
	}
	
	@Override
	public boolean contains(int x, int y) {
		return super.contains(x, y);
	}

	@Override
	public void setChecked(boolean checked) {
    	mChecked = checked;
    	
    	if (isChecked()) {
    		mButtonDrawable.setState(Drawable.STATE_CHECKED_TRUE);
    	} else {
    		mButtonDrawable.setState(Drawable.STATE_CHECKED_FALSE);
    	}
    	
    	if (mOnCheckedChangeListener != null) {
    		mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
    	}
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}
	
	public void setButtonDrawable(Drawable d) {
		mButtonDrawable = d;
	}

	@Override
    public int getCompoundPaddingLeft() {
        int padding = super.getCompoundPaddingLeft();
        if (!isLayoutRtl()) {
            final Drawable buttonDrawable = mButtonDrawable;
            if (buttonDrawable != null) {
                padding += buttonDrawable.getIntrinsicWidth();
            }
        }
        return padding;
    }

    @Override
    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight();
        if (isLayoutRtl()) {
            final Drawable buttonDrawable = mButtonDrawable;
            if (buttonDrawable != null) {
                padding += buttonDrawable.getIntrinsicWidth();
            }
        }
        return padding;
    }

//    /**
//     * @hide
//     */
//    @Override
//    public int getHorizontalOffsetForDrawables() {
//        final Drawable buttonDrawable = mButtonDrawable;
//        return (buttonDrawable != null) ? buttonDrawable.getIntrinsicWidth() : 0;
//    }

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if (mButtonDrawable != null) {
			mButtonDrawable.draw(g);
		}
	}
	
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			toggle();
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}

	@Override
	public boolean performClick() {
		toggle();
		return super.performClick();
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.mOnCheckedChangeListener = onCheckedChangeListener;
	}

	/**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
}
