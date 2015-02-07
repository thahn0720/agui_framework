package thahn.java.agui.view;

import java.awt.event.MouseEvent;

public class MouseWheelEvent extends MotionEvent {

	public MouseWheelEvent(MouseEvent e, int action) {
		super(e, action, MotionEvent.BUTTON2);
	}
	
	public int getWheelRotation() {
		return -((java.awt.event.MouseWheelEvent) mMouseEvent).getWheelRotation();
	}
	
	public int getScrollAmount() {
		return ((java.awt.event.MouseWheelEvent) mMouseEvent).getScrollAmount();
	}
}
