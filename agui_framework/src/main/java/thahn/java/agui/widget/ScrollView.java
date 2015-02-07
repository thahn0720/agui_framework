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
import thahn.java.agui.view.ViewGroup.LayoutParams;

/**
 * 
 * @author thAhn
 *
 */
public class ScrollView extends ScrollViewBase {

	public ScrollView(Context context) {
		this(context, new AttributeSet(context));
	}

	public ScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_SCROLL_VIEW_CODE);
	}

	public ScrollView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	boolean isHorizontal() {
		return false;
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

	@Override
	public void scrollToFirst() {
	}

	@Override
	public void scrollToEnd() {
	}

	@Override
	public boolean isPossibleToScrollUp(int amount, boolean isFirst) {
		if (-mChild.getScrollY() >= 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isPossibleToScrollDown(int amount, boolean isLast) {
		if (getDrawingHeight() + mChild.getScrollY() >= mChild.getHeight() + mChild.getScrollableY() + mChild.getScrollY()) {
			return false;
		}
		return true;
	}
	
	private ScrollBar.OnThumbListener onThumbListener = new ScrollBar.OnThumbListener() {
		
		@Override
		public boolean onChanged(int wheelRotation, int amount) {
			int scrollAmount = mScrollHelper.getAmount(wheelRotation, amount);
			return mScrollHelper.scroll(wheelRotation, scrollAmount);
		}
	};
}
