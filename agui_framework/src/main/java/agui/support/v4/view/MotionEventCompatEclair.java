package agui.support.v4.view;

import thahn.java.agui.view.MotionEvent;

/**
 * Implementation of motion event compatibility that can call Eclair APIs.
 */
class MotionEventCompatEclair {
    public static int findPointerIndex(MotionEvent event, int pointerId) {
        return event.findPointerIndex(pointerId);
    }
    public static int getPointerId(MotionEvent event, int pointerIndex) {
        return event.getPointerId(pointerIndex);
    }
    public static float getX(MotionEvent event, int pointerIndex) {
        return event.getX(pointerIndex);
    }
    public static float getY(MotionEvent event, int pointerIndex) {
        return event.getY(pointerIndex);
    }
    public static int getPointerCount(MotionEvent event) {
        return event.getPointerCount();
    }
}