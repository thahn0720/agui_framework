package agui.support.v4.widget;

import thahn.java.agui.widget.OverScroller;

class ScrollerCompatIcs {
	public static float getCurrVelocity(Object scroller) {
		return ((OverScroller) scroller).getCurrVelocity();
	}
}