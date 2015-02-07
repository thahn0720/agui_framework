
package thahn.java.agui.widget;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.View;

/**
 *
 * @author thAhn
 * 
 */
public class VerticalScrollBar extends ScrollBar {

	public VerticalScrollBar(Context context, View parent, int avgItemHeight, int itemAllCount, int scrollAmount) {
		super(context, parent, avgItemHeight, itemAllCount, scrollAmount);
	}

	@Override
	protected boolean isHorizontal() {
		return false;
	}
	
	@Override
	protected int[] getViewLtrb() {
		int[] ltrb = new int[] { 
				mParent.getDrawingRightWithoutScroll() - THUMB_SIZE
				, mParent.getDrawingTopWithoutScroll()
				, mParent.getDrawingRightWithoutScroll()
				, mParent.getDrawingBottomWithoutScroll()
		};
		return ltrb;
	}
	
	@Override
	protected int getParentSize() {
		return mParent.getDrawingHeight() - getParentCompoundPadding();
	}

	@Override
	protected int getParentCompoundPadding() {
		int heightPadding = 0; 
		if (mParent instanceof TextView) {
			TextView view = (TextView) mParent;
			heightPadding = (view.getCompoundPaddingHeightTop() - view.getPaddingTop() + view.getCompoundPaddingHeightBottom() - view.getPaddingBottom());
		}
		return heightPadding;
	}

	@Override
	protected void locateThumbView(View thumbView, int thumbSize) {
		int l = mParent.getDrawingRightWithoutScroll() - mWidth;
		int t = mParent.getDrawingTopWithoutScroll();
		thumbView.onLayout(true, l, t, l + mWidth, t + thumbSize);
	}
}
