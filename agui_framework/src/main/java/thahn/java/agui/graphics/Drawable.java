package thahn.java.agui.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import thahn.java.agui.annotation.AguiDifferent;
import thahn.java.agui.annotation.AguiSpecific;

public abstract class Drawable { 
	
	public static final int 								STATE_PRESSED_TRUE			= "state_pressed_true".hashCode();
	public static final int 								STATE_PRESSED_FALSE			= "state_pressed_false".hashCode();
	public static final int 								STATE_FOCUSED_TRUE			= "state_focused_true".hashCode();
	public static final int 								STATE_FOCUSED_FALSE			= "state_focused_false".hashCode();
	public static final int 								STATE_CHECKED_TRUE			= "state_checked_true".hashCode();
	public static final int 								STATE_CHECKED_FALSE			= "state_checked_false".hashCode();
	public static final int 								STATE_ENABLED_TRUE			= "state_enabled_true".hashCode();
	public static final int 								STATE_ENABLED_FALSE			= "state_enabled_false".hashCode();
	public static final int 								STATE_HOVERED_TRUE			= "state_hovered_true".hashCode();
	public static final int 								STATE_HOVERED_FALSE			= "state_hovered_false".hashCode();
	public static final int 								STATE_ACTIVATED_TRUE		= "state_activated_true".hashCode();
	public static final int 								STATE_ACTIVATED_FALSE		= "state_activated_false".hashCode();
	public static final int 								STATE_NORMAL 				= STATE_PRESSED_FALSE;
	
    public static final int[]								VIEW_STATE_SETS = new int[] {
    		STATE_PRESSED_TRUE, STATE_PRESSED_FALSE, STATE_FOCUSED_TRUE, STATE_FOCUSED_FALSE, STATE_CHECKED_TRUE
    		, STATE_CHECKED_FALSE, STATE_ENABLED_TRUE, STATE_ENABLED_FALSE, STATE_HOVERED_TRUE, STATE_HOVERED_FALSE
    		, STATE_ACTIVATED_TRUE, STATE_ACTIVATED_FALSE
    };
	
	/*package*/ int											x;
	/*package*/ int 										y;
	/*package*/ int 										width;
	/*package*/ int 										height;
	/*package*/ Rect										bounds;
	/*package*/ boolean[]									currentState				= new boolean[VIEW_STATE_SETS.length];
	/*package*/ int											alpha 						= 255;
	/*package*/ WeakReference<Callback> 					mCallback 					= null;
	
	public Drawable() {
		bounds = new Rect();
	}
	
	public void setBounds(int left, int top, int right, int bottom) {
		this.x = left;
		this.y = top;
		this.width = right - left;
		this.height = bottom - top;
		
		bounds.left = left;
		bounds.top = top;
		bounds.right = right;
		bounds.bottom = bottom;
	}
	
	public void translate(int x, int y) {
		setBounds(this.x + x, this.y + y, this.x + x + width, this.y + y + height);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getIntrinsicWidth() {
		return width;
	}
	
	public int getIntrinsicHeight() {
		return height;
	}

	@AguiDifferent
    public boolean setState(final boolean[] stateSet) {
        if (!Arrays.equals(currentState, stateSet)) {
        	currentState = stateSet;
            return true;
        }
        return false;
    }
	
	@AguiDifferent
	public void setState(int state) {
		for (int i = 0; i < Drawable.VIEW_STATE_SETS.length; i++) {
			if (Drawable.VIEW_STATE_SETS[i] == state) {
				currentState[i] = true;
				currentState[i + (i%2==0?+1:-1)] = false;
				break;
			}
		}
	}
	
	@AguiDifferent
	public boolean[] getState() {
		return currentState;
	}
	
	@AguiSpecific
	public boolean getState(int state) {
		for (int i = 0; i < VIEW_STATE_SETS.length; i++) {
			if (VIEW_STATE_SETS[i] == state) {
				return currentState[i];
			}
		}
		return false;
	}
	
	public Rect getBounds() {
		return bounds;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
    /**
     * Bind a {@link Callback} object to this Drawable.  Required for clients
     * that want to support animated drawables.
     *
     * @param cb The client's Callback implementation.
     * 
     * @see #getCallback() 
     */
    public final void setCallback(Callback cb) {
        mCallback = new WeakReference<Callback>(cb);
    }

    /**
     * Return the current {@link Callback} implementation attached to this
     * Drawable.
     * 
     * @return A {@link Callback} instance or null if no callback was set.
     * 
     * @see #setCallback(android.graphics.drawable.Drawable.Callback) 
     */
    public Callback getCallback() {
        if (mCallback != null) {
            return mCallback.get();
        }
        return null;
    }
	
	/**
     * Return a copy of the drawable's bounds in the specified Rect (allocated
     * by the caller). The bounds specify where this will draw when its draw()
     * method is called.
     *
     * @param bounds Rect to receive the drawable's bounds (allocated by the
     *               caller).
     */
    public final void copyBounds(Rect bounds) {
        bounds.set(bounds);
    }
	
	public abstract void rotate(Graphics2D g, double theta, int x, int y);
	public abstract void draw(Graphics g);
	public abstract Image getImage();
	
	/**
     * Implement this interface if you want to create an animated drawable that
     * extends {@link android.graphics.drawable.Drawable Drawable}.
     * Upon retrieving a drawable, use
     * {@link Drawable#setCallback(android.graphics.drawable.Drawable.Callback)}
     * to supply your implementation of the interface to the drawable; it uses
     * this interface to schedule and execute animation changes.
     */
    public static interface Callback {
        /**
         * Called when the drawable needs to be redrawn.  A view at this point
         * should invalidate itself (or at least the part of itself where the
         * drawable appears).
         *
         * @param who The drawable that is requesting the update.
         */
        public void invalidateDrawable(Drawable who);

        /**
         * A Drawable can call this to schedule the next frame of its
         * animation.  An implementation can generally simply call
         * {@link android.os.Handler#postAtTime(Runnable, Object, long)} with
         * the parameters <var>(what, who, when)</var> to perform the
         * scheduling.
         *
         * @param who The drawable being scheduled.
         * @param what The action to execute.
         * @param when The time (in milliseconds) to run.  The timebase is
         *             {@link android.os.SystemClock#uptimeMillis}
         */
        public void scheduleDrawable(Drawable who, Runnable what, long when);

        /**
         * A Drawable can call this to unschedule an action previously
         * scheduled with {@link #scheduleDrawable}.  An implementation can
         * generally simply call
         * {@link android.os.Handler#removeCallbacks(Runnable, Object)} with
         * the parameters <var>(what, who)</var> to unschedule the drawable.
         *
         * @param who The drawable being unscheduled.
         * @param what The action being unscheduled.
         */
        public void unscheduleDrawable(Drawable who, Runnable what);
    }
}
