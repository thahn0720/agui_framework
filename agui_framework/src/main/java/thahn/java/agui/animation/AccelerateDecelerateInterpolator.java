package thahn.java.agui.animation;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;

/**
 * An interpolator where the rate of change starts and ends slowly but
 * accelerates through the middle.
 * 
 */
public class AccelerateDecelerateInterpolator implements Interpolator {
    public AccelerateDecelerateInterpolator() {
    }
    
    @SuppressWarnings({"UnusedDeclaration"})
    public AccelerateDecelerateInterpolator(Context context, AttributeSet attrs) {
    }
    
    public float getInterpolation(float input) {
        return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }
}
