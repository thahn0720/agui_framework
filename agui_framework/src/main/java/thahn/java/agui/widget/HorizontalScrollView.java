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


public class HorizontalScrollView extends ScrollViewBase {
	
//	private ScrollBar													mScrollBar;
//	private boolean 													canScroll 		= false;
//	private View														mChild;
//	private boolean														isScrollPressed	= false;
	
	public HorizontalScrollView(Context context) {
		this(context, new AttributeSet(context));
	}
	
	public HorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_HORIZONTAL_SCROLL_VIEW_CODE);
	}
	
	public HorizontalScrollView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}
	
	@Override
	boolean isHorizontal() {
		return true;
	}
	
	@Override
	public void scrollToFirst() {
	}

	@Override
	public void scrollToEnd() {
	}

	@Override
	public boolean isPossibleToScrollUp(int amount, boolean isFirst) {
		if (-mChild.getScrollX() >= 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isPossibleToScrollDown(int amount, boolean isLast) {
		if (getDrawingWidth() + mChild.getScrollX() >= mChild.getWidth()+mChild.getScrollableX()) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onScroll(MouseWheelEvent event) {
		boolean ret = true;
		if(canScroll) {
			int wheelRotation = event.getWheelRotation();
			int scrollAmount = event.getScrollAmount();
			
			ret = mScrollHelper.scroll(wheelRotation, scrollAmount);
//			scroll(wheelRotation, scrollAmount);
		}
		return ret;
	}
	
	private boolean isFirst = false;
	private boolean isLast = false;
	private boolean scroll(int wheelRotation, int scrollAmount) {
		int amount = 0;
		if(wheelRotation > 0) { // wheel down
			if(-mChild.getScrollX() >= 0) {
				if(!isFirst) {
					isFirst = true;
					mScrollBar.onFirstScrolled();
				}
				Log.e("scroll is not possible : scroll " + mScrollX);
				return false;
			}
			amount = scrollAmount * Global.WHEEL_DISCOUT_FACTOR;
		} else {	// wheel up
			if(getDrawingWidth() + mChild.getScrollX() >= mChild.getWidth()+mChild.getScrollableX()) {
				if(!isLast) {
					isLast = true;
					mScrollBar.onLastScrolled();
				}
				Log.e("scroll is not possible : scroll " + mScrollX);
				return false;
			}
			amount = -scrollAmount * Global.WHEEL_DISCOUT_FACTOR;
		}
		isFirst = false;
		isLast = false;
		Log.e(""+ amount + ", " +wheelRotation+", "+mChild.getScrollX());
		
		mScrollBar.onScroll(-mChild.getScrollX()+amount, mChild.getScrollY(), amount);
		mChild.scrollBy(-amount, 0);
		return true;
	}

//	ScrollBar.OnThumbListener onThumbListener = new ScrollBar.OnThumbListener() {
//		
//		@Override
//		public boolean onChanged(int wheelRotation, int amount) {
//			if(wheelRotation > 0) {
//				amount = -amount;
//			}
//			return scroll(wheelRotation, amount);
//		}
//	};
}
