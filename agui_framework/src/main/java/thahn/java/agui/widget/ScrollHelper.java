package thahn.java.agui.widget;

import thahn.java.agui.Global;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.View;

/**
 *
 * @author thAhn
 * 
 */
public class ScrollHelper {
	
	private boolean 											isFirst 		= false;
	private boolean 											isLast 			= false;
	private boolean 											isHorizontal	= false;
	
	private View												mScrolledView;
	private ScrollBar											mScrollBar;
	private ScrollCondition										mScrollCondition;
	
	public ScrollHelper(View scrolledView, ScrollBar scrollBar, ScrollCondition condition) {
		this(scrolledView, scrollBar, condition, false);
	}
	
	public ScrollHelper(View scrolledView, ScrollBar scrollBar, ScrollCondition condition, boolean isHorizontal) {
		this.mScrolledView = scrolledView;
		this.mScrollBar = scrollBar;
		this.mScrollCondition = condition;
		this.isHorizontal = isHorizontal; 
	}

	public boolean scroll(int wheelRotation, int scrollAmount) {
		int amount = getAmount(wheelRotation, scrollAmount);
		if (wheelRotation > 0) { // wheel up
			if (!mScrollCondition.isPossibleToScrollUp(amount, isFirst)) {
				if (!isFirst) {
					isFirst = true;
					mScrollBar.onFirstScrolled();
					mScrollCondition.scrollToFirst();
				}
				return false;
			}
			
		} else {	// wheel down
			if (!mScrollCondition.isPossibleToScrollDown(amount, isLast)) {
				if (!isLast) {
					isLast = true;
					mScrollBar.onLastScrolled();
					mScrollCondition.scrollToEnd();
				}
				return false;
			}
		}
		isFirst = false;
		isLast = false;
		
//		Log.i("mScrolledView : "+(mScrolledView.getScrollY()) + ", mScrollBar : " + mScrollBar.getScrollY());
		if (isHorizontal) {
			mScrollBar.onScroll(-mScrolledView.getScrollX() + amount, mScrolledView.getScrollY(), amount);
			mScrolledView.scrollBy(-amount, 0);
		} else {
			mScrollBar.onScroll(mScrolledView.getScrollX(), -mScrolledView.getScrollY() + amount, amount);
			mScrolledView.scrollBy(0, -amount);
		}
		return true;
	}
	
	public int getAmount(int wheelRotation, int scrollAmount) {
		int amount = 0;
		if (wheelRotation > 0) { 	// wheel up
			amount = scrollAmount * Global.WHEEL_DISCOUT_FACTOR;
		} else {					// wheel down
			amount = -scrollAmount * Global.WHEEL_DISCOUT_FACTOR;
		}
		return amount;
	}
	
	public static interface ScrollCondition {
		
		public void scrollToFirst();
		public void scrollToEnd();
		public boolean isPossibleToScrollUp(int amount, boolean isFirst);
		public boolean isPossibleToScrollDown(int amount, boolean isLast);
	}
}
