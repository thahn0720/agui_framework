package thahn.java.agui.widget;

import java.awt.Graphics;

import thahn.java.agui.Global;
import thahn.java.agui.app.Context;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.MouseWheelEvent;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.widget.ScrollHelper.ScrollCondition;

/**
 * 
 * @author thAhn
 *
 */
public abstract class ScrollViewBase extends ViewGroup implements ScrollCondition {

	protected ScrollBar													mScrollBar;
	protected ScrollHelper												mScrollHelper;
	protected boolean 													canScroll 		= false;
	protected View														mChild;
	protected boolean													isScrollPressed	= false;
	
	public ScrollViewBase(Context context) {
		super(context);
	}
	
	public ScrollViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ScrollViewBase(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	/*package*/ abstract boolean isHorizontal();
	
	@Override
	public void arrange() {
		if(getChildCount() <= 0) return ;
		
		View child = getChildren()[0];
		// FIXME : doing measure, arrange in every arrange() is not worth.
		child.onMeasure(getDrawingWidth(), getDrawingHeight());
		child.onPostMeasure(getDrawingWidth(), getDrawingHeight());
		child.arrange();
		int l = getDrawingLeft() + child.getLayoutParams().leftMargin;
		int t = getDrawingTop() + child.getLayoutParams().topMargin;
		int childWidth = child.getWidth();
		int childHeight = child.getHeight();
		
		if (childWidth - child.getLayoutParams().rightMargin > getDrawingWidth()) {
			childWidth = getDrawingWidth() - child.getLayoutParams().rightMargin;
		}
		if (childHeight - child.getLayoutParams().bottomMargin > getDrawingHeight()) {
			childWidth = getDrawingHeight() - child.getLayoutParams().bottomMargin;
		}
		
		int r = l + childWidth;
		int b = t + childHeight;
		child.onLayout(true, l, t, r, b);
		
		if (mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			mWidth = mChild.getWidth() + getPaddingLeft();
		}
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			mHeight = mChild.getHeight() + getPaddingTop();
		}
		mPolicy.checkLayoutMaxSizePolicy();
		
		if ((!isHorizontal() && child.getScrollableY() > 0)
				|| (isHorizontal() && child.getScrollableX() > 0)) { 
			canScroll = true;
			initScrollBar();
		} else if (mScrollBar != null) {
			mScrollBar = null;
			mScrollHelper = null;
		}
	}

	private void initScrollBar() {
		// FIXME : 매번 arranege에 의해서 new 를 하는 것이 아니라 이미 객체가 있으면 내부적으로 값만 변화하는 식으로 변경.
		if (mScrollBar == null) {
			if (isHorizontal()) {
				mScrollBar = new HorizontalScrollBar(mContext, this, mChild.getWidth() + mChild.getScrollableX(), 1, 3);
				mScrollHelper = new ScrollHelper(mChild, mScrollBar, this, true);
			} else {
				mScrollBar = new VerticalScrollBar(mContext, this, mChild.getDrawingHeight() + mChild.getScrollableY(), 1, 3);
				mScrollHelper = new ScrollHelper(mChild, mScrollBar, this);
			}
			mScrollBar.setOnThumbListener(onThumbListener);
			mScrollBar.onFirstScrolled();
		} else {
			mScrollBar.setThumbSize(mChild.getDrawingHeight() + mChild.getScrollableY());
		}
	}

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        mChild = child;
        super.addView(child);
    }
	
	@Override
	public void addViewInternal(View v) {
		if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        mChild = v;
        super.addViewInternal(v);
	}

	@Override
	public void draw(Graphics g) {
//		g.translate(mChild.getScrollX(), mChild.getScrollY());//mScrollX, -mScrollY);
		super.draw(g);
		if (canScroll) {
			mScrollBar.draw(g);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = false;
		if (mScrollBar != null) {
			if (isScrollPressed) { 	// move
				ret = mScrollBar.onTouchEvent(event);
			} else {				// down
				if (mScrollBar.contains(event.getX(), event.getY())) {
					isScrollPressed = true;
					ret = mScrollBar.onTouchEvent(event);
				}
			}
			if (isScrollPressed && event.getAction() == MotionEvent.ACTION_UP) {
				isScrollPressed = false;
			} else if (isScrollPressed) {
				return true;
			}
		}
		return ret;
	}
	
	@Override
	public boolean onScroll(MouseWheelEvent event) {
		boolean ret = true;
		if (canScroll) {
			int wheelRotation = event.getWheelRotation();
			int scrollAmount = event.getScrollAmount();
			
			ret = mScrollHelper.scroll(wheelRotation, scrollAmount);
		}
		return ret;
	}
	
	private ScrollBar.OnThumbListener onThumbListener = new ScrollBar.OnThumbListener() {
		
		@Override
		public boolean onChanged(int wheelRotation, int amount) {
			if (wheelRotation > 0) {
				amount = -amount;
			}
			return mScrollHelper.scroll(wheelRotation, amount);
		}
	};
}
