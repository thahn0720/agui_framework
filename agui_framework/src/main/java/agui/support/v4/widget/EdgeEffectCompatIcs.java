package agui.support.v4.widget;

import java.awt.Canvas;
import java.awt.Graphics;

import thahn.java.agui.app.Context;
import thahn.java.agui.widget.EdgeEffect;

/**
 * Stub implementation that contains a real EdgeEffect on ICS.
 *
 * This class is an implementation detail for EdgeEffectCompat
 * and should not be used directly.
 */
class EdgeEffectCompatIcs {
    public static Object newEdgeEffect(Context context) {
        return new EdgeEffect(context);
    }

    public static void setSize(Object edgeEffect, int width, int height) {
        ((EdgeEffect) edgeEffect).setSize(width, height);
    }

    public static boolean isFinished(Object edgeEffect) {
        return ((EdgeEffect) edgeEffect).isFinished();
    }

    public static void finish(Object edgeEffect) {
        ((EdgeEffect) edgeEffect).finish();
    }

    public static boolean onPull(Object edgeEffect, float deltaDistance) {
        ((EdgeEffect) edgeEffect).onPull(deltaDistance);
        return true;
    }

    public static boolean onRelease(Object edgeEffect) {
        EdgeEffect eff = (EdgeEffect) edgeEffect;
        eff.onRelease();
        return eff.isFinished();
    }

    public static boolean onAbsorb(Object edgeEffect, int velocity) {
        ((EdgeEffect) edgeEffect).onAbsorb(velocity);
        return true;
    }

    public static boolean draw(Object edgeEffect, Graphics canvas) {
        return ((EdgeEffect) edgeEffect).draw(canvas);
    }
}