package thahn.java.agui.view;

import javax.security.auth.login.Configuration;

import thahn.java.agui.app.Context;
import thahn.java.agui.os.RemoteException;
import thahn.java.agui.res.Resources;
import thahn.java.agui.utils.DisplayMetrics;
import thahn.java.agui.utils.SparseArray;

/**
 * Contains methods to standard constants used in the UI for timeouts, sizes, and distances.
 */
public class ViewConfiguration {
    /**
     * Expected bit depth of the display panel.
     * 
     * @hide
     */
    public static final float PANEL_BIT_DEPTH = 24;

    /**
     * Minimum alpha required for a view to draw.
     * 
     * @hide
     */
    public static final float ALPHA_THRESHOLD = 0.5f / PANEL_BIT_DEPTH;
    /**
     * @hide
     */
    public static final float ALPHA_THRESHOLD_INT = 0x7f / PANEL_BIT_DEPTH;

    /**
     * Defines the width of the horizontal scrollbar and the height of the vertical scrollbar in
     * pixels
     */
    private static final int SCROLL_BAR_SIZE = 10;

    /**
     * Duration of the fade when scrollbars fade away in milliseconds
     */
    private static final int SCROLL_BAR_FADE_DURATION = 250;

    /**
     * Default delay before the scrollbars fade in milliseconds
     */
    private static final int SCROLL_BAR_DEFAULT_DELAY = 300;

    /**
     * Defines the length of the fading edges in pixels
     */
    private static final int FADING_EDGE_LENGTH = 12;

    /**
     * Defines the duration in milliseconds of the pressed state in child
     * components.
     */
    private static final int PRESSED_STATE_DURATION = 125;
    
    /**
     * Defines the default duration in milliseconds before a press turns into
     * a long press
     */
    private static final int DEFAULT_LONG_PRESS_TIMEOUT = 500;

    /**
     * Defines the time between successive key repeats in milliseconds.
     */
    private static final int KEY_REPEAT_DELAY = 50;

    /**
     * Defines the duration in milliseconds a user needs to hold down the
     * appropriate button to bring up the global actions dialog (power off,
     * lock screen, etc).
     */
    private static final int GLOBAL_ACTIONS_KEY_TIMEOUT = 500;
    
    /**
     * Defines the duration in milliseconds we will wait to see if a touch event 
     * is a tap or a scroll. If the user does not move within this interval, it is
     * considered to be a tap. 
     */
    private static final int TAP_TIMEOUT = 180;
    
    /**
     * Defines the duration in milliseconds we will wait to see if a touch event 
     * is a jump tap. If the user does not complete the jump tap within this interval, it is
     * considered to be a tap. 
     */
    private static final int JUMP_TAP_TIMEOUT = 500;

    /**
     * Defines the duration in milliseconds between the first tap's up event and
     * the second tap's down event for an interaction to be considered a
     * double-tap.
     */
    private static final int DOUBLE_TAP_TIMEOUT = 300;

    /**
     * Defines the maximum duration in milliseconds between a touch pad
     * touch and release for a given touch to be considered a tap (click) as
     * opposed to a hover movement gesture.
     */
    private static final int HOVER_TAP_TIMEOUT = 150;

    /**
     * Defines the maximum distance in pixels that a touch pad touch can move
     * before being released for it to be considered a tap (click) as opposed
     * to a hover movement gesture.
     */
    private static final int HOVER_TAP_SLOP = 20;

    /**
     * Defines the duration in milliseconds we want to display zoom controls in response 
     * to a user panning within an application.
     */
    private static final int ZOOM_CONTROLS_TIMEOUT = 3000;

    /**
     * Inset in pixels to look for touchable content when the user touches the edge of the screen
     */
    private static final int EDGE_SLOP = 12;
    
    /**
     * Distance a touch can wander before we think the user is scrolling in pixels
     */
    private static final int TOUCH_SLOP = 16;
    
    /**
     * Distance a touch can wander before we think the user is attempting a paged scroll
     * (in dips)
     */
    private static final int PAGING_TOUCH_SLOP = TOUCH_SLOP * 2;
    
    /**
     * Distance between the first touch and second touch to still be considered a double tap
     */
    private static final int DOUBLE_TAP_SLOP = 100;
    
    /**
     * Distance a touch needs to be outside of a window's bounds for it to
     * count as outside for purposes of dismissing the window.
     */
    private static final int WINDOW_TOUCH_SLOP = 16;

    /**
     * Minimum velocity to initiate a fling, as measured in pixels per second
     */
    private static final int MINIMUM_FLING_VELOCITY = 50;
    
    /**
     * Maximum velocity to initiate a fling, as measured in pixels per second
     */
    private static final int MAXIMUM_FLING_VELOCITY = 8000;

    /**
     * Distance between a touch up event denoting the end of a touch exploration
     * gesture and the touch up event of a subsequent tap for the latter tap to be
     * considered as a tap i.e. to perform a click.
     */
    private static final int TOUCH_EXPLORATION_TAP_SLOP = 80;

    /**
     * Delay before dispatching a recurring accessibility event in milliseconds.
     * This delay guarantees that a recurring event will be send at most once
     * during the {@link #SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS} time
     * frame.
     */
    private static final long SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS = 400;

    /**
     * The maximum size of View's drawing cache, expressed in bytes. This size
     * should be at least equal to the size of the screen in ARGB888 format.
     */
    @Deprecated
    private static final int MAXIMUM_DRAWING_CACHE_SIZE = 480 * 800 * 4; // ARGB8888

    /**
     * The coefficient of friction applied to flings/scrolls.
     */
    private static final float SCROLL_FRICTION = 0.015f;

    /**
     * Max distance to overscroll for edge effects
     */
    private static final int OVERSCROLL_DISTANCE = 0;

    /**
     * Max distance to overfling for edge effects
     */
    private static final int OVERFLING_DISTANCE = 6;

    private final int mEdgeSlop;
    private final int mFadingEdgeLength;
    private final int mMinimumFlingVelocity;
    private final int mMaximumFlingVelocity;
    private final int mScrollbarSize;
    private final int mTouchSlop;
    private final int mPagingTouchSlop;
    private final int mDoubleTapSlop;
    private final int mScaledTouchExplorationTapSlop;
    private final int mWindowTouchSlop;
    private final int mMaximumDrawingCacheSize;
    private final int mOverscrollDistance;
    private final int mOverflingDistance;
    private final boolean mFadingMarqueeEnabled;

    private boolean sHasPermanentMenuKey;
    private boolean sHasPermanentMenuKeySet;

    static final SparseArray<ViewConfiguration> sConfigurations =
            new SparseArray<ViewConfiguration>(2);

    /**
     * @deprecated Use {@link android.view.ViewConfiguration#get(android.content.Context)} instead.
     */
    @Deprecated
    public ViewConfiguration() {
        mEdgeSlop = EDGE_SLOP;
        mFadingEdgeLength = FADING_EDGE_LENGTH;
        mMinimumFlingVelocity = MINIMUM_FLING_VELOCITY;
        mMaximumFlingVelocity = MAXIMUM_FLING_VELOCITY;
        mScrollbarSize = SCROLL_BAR_SIZE;
        mTouchSlop = TOUCH_SLOP;
        mPagingTouchSlop = PAGING_TOUCH_SLOP;
        mDoubleTapSlop = DOUBLE_TAP_SLOP;
        mScaledTouchExplorationTapSlop = TOUCH_EXPLORATION_TAP_SLOP;
        mWindowTouchSlop = WINDOW_TOUCH_SLOP;
        //noinspection deprecation
        mMaximumDrawingCacheSize = MAXIMUM_DRAWING_CACHE_SIZE;
        mOverscrollDistance = OVERSCROLL_DISTANCE;
        mOverflingDistance = OVERFLING_DISTANCE;
        mFadingMarqueeEnabled = true;
    }

    /**
     * Creates a new configuration for the specified context. The configuration depends on
     * various parameters of the context, like the dimension of the display or the density
     * of the display.
     *
     * @param context The application context used to initialize this view configuration.
     *
     * @see #get(android.content.Context) 
     * @see android.util.DisplayMetrics
     */
    private ViewConfiguration(Context context) {
        final Resources res = context.getResources();
        final DisplayMetrics metrics = res.getDisplayMetrics();
//        final Configuration config = res.getConfiguration();
        final float density = metrics.density;
        final float sizeAndDensity = 1;
//        if (config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
//            sizeAndDensity = density * 1.5f;
//        } else {
//            sizeAndDensity = density;
//        }

        mEdgeSlop = (int) (sizeAndDensity * EDGE_SLOP + 0.5f);
        mFadingEdgeLength = (int) (sizeAndDensity * FADING_EDGE_LENGTH + 0.5f);
        mMinimumFlingVelocity = (int) (density * MINIMUM_FLING_VELOCITY + 0.5f);
        mMaximumFlingVelocity = (int) (density * MAXIMUM_FLING_VELOCITY + 0.5f);
        mScrollbarSize = (int) (density * SCROLL_BAR_SIZE + 0.5f);
        mTouchSlop = (int) (sizeAndDensity * TOUCH_SLOP + 0.5f);
        mPagingTouchSlop = (int) (sizeAndDensity * PAGING_TOUCH_SLOP + 0.5f);
        mDoubleTapSlop = (int) (sizeAndDensity * DOUBLE_TAP_SLOP + 0.5f);
        mScaledTouchExplorationTapSlop = (int) (density * TOUCH_EXPLORATION_TAP_SLOP + 0.5f);
        mWindowTouchSlop = (int) (sizeAndDensity * WINDOW_TOUCH_SLOP + 0.5f);

        // Size of the screen in bytes, in ARGB_8888 format
        mMaximumDrawingCacheSize = 4 * metrics.widthPixels * metrics.heightPixels;

        mOverscrollDistance = (int) (sizeAndDensity * OVERSCROLL_DISTANCE + 0.5f);
        mOverflingDistance = (int) (sizeAndDensity * OVERFLING_DISTANCE + 0.5f);

//        if (!sHasPermanentMenuKeySet) {
//            IWindowManager wm = Display.getWindowManager();
//            try {
//                sHasPermanentMenuKey = wm.canStatusBarHide() && !wm.hasNavigationBar();
//                sHasPermanentMenuKeySet = true;
//            } catch (RemoteException ex) {
//                sHasPermanentMenuKey = false;
//            }
//        }
//
        mFadingMarqueeEnabled = true;
        			//res.getBoolean(com.android.internal.R.bool.config_ui_enableFadingMarquee);
    }

    /**
     * Returns a configuration for the specified context. The configuration depends on
     * various parameters of the context, like the dimension of the display or the
     * density of the display.
     *
     * @param context The application context used to initialize the view configuration.
     */
    public static ViewConfiguration get(Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int density = (int) (100.0f * metrics.density);

        ViewConfiguration configuration = sConfigurations.get(density);
        if (configuration == null) {
            configuration = new ViewConfiguration(context);
            sConfigurations.put(density, configuration);
        }

        return configuration;
    }

    /**
     * @return The width of the horizontal scrollbar and the height of the vertical
     *         scrollbar in pixels
     *
     * @deprecated Use {@link #getScaledScrollBarSize()} instead.
     */
    @Deprecated
    public static int getScrollBarSize() {
        return SCROLL_BAR_SIZE;
    }

    /**
     * @return The width of the horizontal scrollbar and the height of the vertical
     *         scrollbar in pixels
     */
    public int getScaledScrollBarSize() {
        return mScrollbarSize;
    }

    /**
     * @return Duration of the fade when scrollbars fade away in milliseconds
     */
    public static int getScrollBarFadeDuration() {
        return SCROLL_BAR_FADE_DURATION;
    }

    /**
     * @return Default delay before the scrollbars fade in milliseconds
     */
    public static int getScrollDefaultDelay() {
        return SCROLL_BAR_DEFAULT_DELAY;
    }
    
    /**
     * @return the length of the fading edges in pixels
     *
     * @deprecated Use {@link #getScaledFadingEdgeLength()} instead.
     */
    @Deprecated
    public static int getFadingEdgeLength() {
        return FADING_EDGE_LENGTH;
    }

    /**
     * @return the length of the fading edges in pixels
     */
    public int getScaledFadingEdgeLength() {
        return mFadingEdgeLength;
    }

    /**
     * @return the duration in milliseconds of the pressed state in child
     * components.
     */
    public static int getPressedStateDuration() {
        return PRESSED_STATE_DURATION;
    }

    /**
     * @return the duration in milliseconds before a press turns into
     * a long press
     */
    public static int getLongPressTimeout() {
//        return AppGlobals.getIntCoreSetting(Settings.Secure.LONG_PRESS_TIMEOUT,
//                DEFAULT_LONG_PRESS_TIMEOUT);
        return DEFAULT_LONG_PRESS_TIMEOUT;
    }

    /**
     * @return the time before the first key repeat in milliseconds.
     */
    public static int getKeyRepeatTimeout() {
        return getLongPressTimeout();
    }

    /**
     * @return the time between successive key repeats in milliseconds.
     */
    public static int getKeyRepeatDelay() {
        return KEY_REPEAT_DELAY;
    }

    /**
     * @return the duration in milliseconds we will wait to see if a touch event
     * is a tap or a scroll. If the user does not move within this interval, it is
     * considered to be a tap. 
     */
    public static int getTapTimeout() {
        return TAP_TIMEOUT;
    }

    /**
     * @return the duration in milliseconds we will wait to see if a touch event
     * is a jump tap. If the user does not move within this interval, it is
     * considered to be a tap. 
     */
    public static int getJumpTapTimeout() {
        return JUMP_TAP_TIMEOUT;
    }
    
    /**
     * @return the duration in milliseconds between the first tap's up event and
     * the second tap's down event for an interaction to be considered a
     * double-tap.
     */
    public static int getDoubleTapTimeout() {
        return DOUBLE_TAP_TIMEOUT;
    }

    /**
     * @return the maximum duration in milliseconds between a touch pad
     * touch and release for a given touch to be considered a tap (click) as
     * opposed to a hover movement gesture.
     * @hide
     */
    public static int getHoverTapTimeout() {
        return HOVER_TAP_TIMEOUT;
    }

    /**
     * @return the maximum distance in pixels that a touch pad touch can move
     * before being released for it to be considered a tap (click) as opposed
     * to a hover movement gesture.
     * @hide
     */
    public static int getHoverTapSlop() {
        return HOVER_TAP_SLOP;
    }

    /**
     * @return Inset in pixels to look for touchable content when the user touches the edge of the
     *         screen
     *
     * @deprecated Use {@link #getScaledEdgeSlop()} instead.
     */
    @Deprecated
    public static int getEdgeSlop() {
        return EDGE_SLOP;
    }

    /**
     * @return Inset in pixels to look for touchable content when the user touches the edge of the
     *         screen
     */
    public int getScaledEdgeSlop() {
        return mEdgeSlop;
    }

    /**
     * @return Distance a touch can wander before we think the user is scrolling in pixels
     *
     * @deprecated Use {@link #getScaledTouchSlop()} instead.
     */
    @Deprecated
    public static int getTouchSlop() {
        return TOUCH_SLOP;
    }

    /**
     * @return Distance a touch can wander before we think the user is scrolling in pixels
     */
    public int getScaledTouchSlop() {
        return mTouchSlop;
    }
    
    /**
     * @return Distance a touch can wander before we think the user is scrolling a full page
     *         in dips
     */
    public int getScaledPagingTouchSlop() {
        return mPagingTouchSlop;
    }

    /**
     * @return Distance between the first touch and second touch to still be
     *         considered a double tap
     * @deprecated Use {@link #getScaledDoubleTapSlop()} instead.
     * @hide The only client of this should be GestureDetector, which needs this
     *       for clients that still use its deprecated constructor.
     */
    @Deprecated
    public static int getDoubleTapSlop() {
        return DOUBLE_TAP_SLOP;
    }
    
    /**
     * @return Distance between the first touch and second touch to still be
     *         considered a double tap
     */
    public int getScaledDoubleTapSlop() {
        return mDoubleTapSlop;
    }

    /**
     * @return Distance between a touch up event denoting the end of a touch exploration
     * gesture and the touch up event of a subsequent tap for the latter tap to be
     * considered as a tap i.e. to perform a click.
     *
     * @hide
     */
    public int getScaledTouchExplorationTapSlop() {
        return mScaledTouchExplorationTapSlop;
    }

    /**
     * Interval for dispatching a recurring accessibility event in milliseconds.
     * This interval guarantees that a recurring event will be send at most once
     * during the {@link #getSendRecurringAccessibilityEventsInterval()} time frame.
     *
     * @return The delay in milliseconds.
     *
     * @hide
     */
    public static long getSendRecurringAccessibilityEventsInterval() {
        return SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS;
    }

    /**
     * @return Distance a touch must be outside the bounds of a window for it
     * to be counted as outside the window for purposes of dismissing that
     * window.
     *
     * @deprecated Use {@link #getScaledWindowTouchSlop()} instead.
     */
    @Deprecated
    public static int getWindowTouchSlop() {
        return WINDOW_TOUCH_SLOP;
    }

    /**
     * @return Distance a touch must be outside the bounds of a window for it
     * to be counted as outside the window for purposes of dismissing that
     * window.
     */
    public int getScaledWindowTouchSlop() {
        return mWindowTouchSlop;
    }
    
    /**
     * @return Minimum velocity to initiate a fling, as measured in pixels per second.
     *
     * @deprecated Use {@link #getScaledMinimumFlingVelocity()} instead.
     */
    @Deprecated
    public static int getMinimumFlingVelocity() {
        return MINIMUM_FLING_VELOCITY;
    }

    /**
     * @return Minimum velocity to initiate a fling, as measured in pixels per second.
     */
    public int getScaledMinimumFlingVelocity() {
        return mMinimumFlingVelocity;
    }

    /**
     * @return Maximum velocity to initiate a fling, as measured in pixels per second.
     *
     * @deprecated Use {@link #getScaledMaximumFlingVelocity()} instead.
     */
    @Deprecated
    public static int getMaximumFlingVelocity() {
        return MAXIMUM_FLING_VELOCITY;
    }

    /**
     * @return Maximum velocity to initiate a fling, as measured in pixels per second.
     */
    public int getScaledMaximumFlingVelocity() {
        return mMaximumFlingVelocity;
    }
    
    /**
     * The maximum drawing cache size expressed in bytes.
     *
     * @return the maximum size of View's drawing cache expressed in bytes
     *
     * @deprecated Use {@link #getScaledMaximumDrawingCacheSize()} instead.
     */
    @Deprecated
    public static int getMaximumDrawingCacheSize() {
        //noinspection deprecation
        return MAXIMUM_DRAWING_CACHE_SIZE;
    }

    /**
     * The maximum drawing cache size expressed in bytes.
     *
     * @return the maximum size of View's drawing cache expressed in bytes
     */
    public int getScaledMaximumDrawingCacheSize() {
        return mMaximumDrawingCacheSize;
    }

    /**
     * @return The maximum distance a View should overscroll by when showing edge effects.
     */
    public int getScaledOverscrollDistance() {
        return mOverscrollDistance;
    }

    /**
     * @return The maximum distance a View should overfling by when showing edge effects.
     */
    public int getScaledOverflingDistance() {
        return mOverflingDistance;
    }

    /**
     * The amount of time that the zoom controls should be
     * displayed on the screen expressed in milliseconds.
     * 
     * @return the time the zoom controls should be visible expressed
     * in milliseconds.
     */
    public static long getZoomControlsTimeout() {
        return ZOOM_CONTROLS_TIMEOUT;
    }

    /**
     * The amount of time a user needs to press the relevant key to bring up
     * the global actions dialog.
     *
     * @return how long a user needs to press the relevant key to bring up
     *   the global actions dialog.
     */
    public static long getGlobalActionKeyTimeout() {
        return GLOBAL_ACTIONS_KEY_TIMEOUT;
    }

    /**
     * The amount of friction applied to scrolls and flings.
     * 
     * @return A scalar dimensionless value representing the coefficient of
     *         friction.
     */
    public static float getScrollFriction() {
        return SCROLL_FRICTION;
    }

    /**
     * Report if the device has a permanent menu key available to the user.
     *
     * <p>As of Android 3.0, devices may not have a permanent menu key available.
     * Apps should use the action bar to present menu options to users.
     * However, there are some apps where the action bar is inappropriate
     * or undesirable. This method may be used to detect if a menu key is present.
     * If not, applications should provide another on-screen affordance to access
     * functionality.
     *
     * @return true if a permanent menu key is present, false otherwise.
     */
    public boolean hasPermanentMenuKey() {
        return sHasPermanentMenuKey;
    }

    /**
     * @hide
     * @return Whether or not marquee should use fading edges.
     */
    public boolean isFadingMarqueeEnabled() {
        return mFadingMarqueeEnabled;
    }
}
