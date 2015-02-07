package thahn.java.agui.animation;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;

/**
 * An interpolator where the rate of change is constant
 *
 */
public class LinearInterpolator implements Interpolator {

    public LinearInterpolator() {
    }
    
    public LinearInterpolator(Context context, AttributeSet attrs) {
    }
    
    public float getInterpolation(float input) {
        return input;
    }
}
