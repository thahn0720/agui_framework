package agui.support.v4.app;

import java.lang.reflect.Method;

import thahn.java.agui.R;
import thahn.java.agui.app.ActionBar;
import thahn.java.agui.app.Activity;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.widget.ImageView;

/**
 * This class encapsulates some awful hacks.
 *
 * Before JB-MR2 (API 18) it was not possible to change the home-as-up indicator glyph
 * in an action bar without some really gross hacks. Since the MR2 SDK is not published as of
 * this writing, the new API is accessed via reflection here if available.
 */
class ActionBarDrawerToggleHoneycomb {
    private static final String TAG = "ActionBarDrawerToggleHoneycomb";

    private static final int[] THEME_ATTRS = new int[] {
//            R.attr.homeAsUpIndicator
    };

    public static Object setActionBarUpIndicator(Object info, Activity activity,
            Drawable drawable, int contentDescRes) {
        if (info == null) {
            info = new SetIndicatorInfo(activity);
        }
        final SetIndicatorInfo sii = (SetIndicatorInfo) info;
        if (sii.setHomeAsUpIndicator != null) {
            try {
                final ActionBar actionBar = activity.getActionBar();
                sii.setHomeAsUpIndicator.invoke(actionBar, drawable);
                sii.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
            } catch (Exception e) {
                Log.w(TAG, "Couldn't set home-as-up indicator via JB-MR2 API", e);
            }
        } else if (sii.upIndicatorView != null) {
            sii.upIndicatorView.setImageDrawable(drawable);
        } else {
            Log.w(TAG, "Couldn't set home-as-up indicator");
        }
        return info;
    }

    public static Object setActionBarDescription(Object info, Activity activity,
            int contentDescRes) {
        if (info == null) {
            info = new SetIndicatorInfo(activity);
        }
        final SetIndicatorInfo sii = (SetIndicatorInfo) info;
        if (sii.setHomeAsUpIndicator != null) {
            try {
                final ActionBar actionBar = activity.getActionBar();
                sii.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
            } catch (Exception e) {
                Log.w(TAG, "Couldn't set content description via JB-MR2 API", e);
            }
        }
        return info;
    }

    public static Drawable getThemeUpIndicator(Activity activity) {
//        final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
//        final Drawable result = a.getDrawable(0);
//        a.recycle();
//        return result;
    	return null;
    }

    private static class SetIndicatorInfo {
        public Method setHomeAsUpIndicator;
        public Method setHomeActionContentDescription;
        public ImageView upIndicatorView;

        SetIndicatorInfo(Activity activity) {
            try {
                setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator",
                        Drawable.class);
                setHomeActionContentDescription = ActionBar.class.getDeclaredMethod(
                        "setHomeActionContentDescription", Integer.TYPE);

                // If we got the method we won't need the stuff below.
                return;
            } catch (NoSuchMethodException e) {
                // Oh well. We'll use the other mechanism below instead.
            }

            final View home = null; 
//            		activity.findViewById(thahn.java.agui.R.id.home);
            if (home == null) {
                // Action bar doesn't have a known configuration, an OEM messed with things.
                return;
            }

            final ViewGroup parent = (ViewGroup) home.getParent();
            final int childCount = parent.getChildCount();
            if (childCount != 2) {
                // No idea which one will be the right one, an OEM messed with things.
                return;
            }

//            final View first = parent.getChildAt(0);
//            final View second = parent.getChildAt(1);
//            final View up = first.getId() == thahn.java.agui.R.id.home ? second : first;
//
//            if (up instanceof ImageView) {
//                // Jackpot! (Probably...)
//                upIndicatorView = (ImageView) up;
//            }
        }
    }
}