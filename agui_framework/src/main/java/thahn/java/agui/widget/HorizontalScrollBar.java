package thahn.java.agui.widget;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.View;

/**
 *
 * @author thAhn
 * 
 */
public class HorizontalScrollBar extends ScrollBar {

	public HorizontalScrollBar(Context context, View parent, int avgItemHeight, int itemAllCount, int scrollAmount) {
		super(context, parent, avgItemHeight, itemAllCount, scrollAmount);
	}

	@Override
	protected boolean isHorizontal() {
		return true;
	}

	@Override
	protected int[] getViewLtrb() {
		int[] ltrb = new int[] { 
				mParent.getDrawingLeftWithoutScroll()
				, mParent.getDrawingTopWithoutScroll()
				, mParent.getDrawingRightWithoutScroll()
				, mParent.getDrawingTopWithoutScroll() + THUMB_SIZE
		};
		return ltrb;
	}
	
	@Override
	protected int getParentSize() {
		return mParent.getDrawingWidth() - getParentCompoundPadding();
	}

	@Override
	protected int getParentCompoundPadding() {
		int widthPadding = 0; 
		if (mParent instanceof TextView) {
			TextView view = (TextView) mParent;
			widthPadding = (view.getCompoundPaddingWidthLeft() - view.getPaddingLeft() + view.getCompoundPaddingWidthRight() - view.getPaddingRight());
		}
		return widthPadding;
	}
	
	@Override
	
	protected void locateThumbView(View thumbView, int thumbSize) {
		int l = mParent.getDrawingLeftWithoutScroll();
		int t = mParent.getDrawingTopWithoutScroll();
		thumbView.onLayout(true, l, t, l + thumbSize, t + mHeight);
	}
}
