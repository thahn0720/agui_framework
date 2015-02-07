package thahn.java.agui.view;


public interface OnKeyListener {
    /**
     * Called when a key is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v The view the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed
     * @param event The KeyEvent object containing full information about
     *        the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    boolean onKey(View v, int keyCode, KeyEvent event);
}