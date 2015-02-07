package thahn.java.agui.view;


public interface OnTouchListener {
	/**
	 * 
	 * @param view
	 * @param event
	 * @return if true, consumed this event
	 */
	boolean onTouch(View view, MotionEvent event);
}
