package thahn.java.agui.animation;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;

/**
 * Repeats the animation for a specified number of cycles. The
 * rate of change follows a sinusoidal pattern.
 *
 */
public class CycleInterpolator implements Interpolator {
    public CycleInterpolator(float cycles) {
        mCycles = cycles;
    }
    
    public CycleInterpolator(Context context, AttributeSet attrs) {
        mCycles = attrs.getFloat(thahn.java.agui.R.attr.CycleInterpolator_cycles, 1.0f);
    }
    
    public float getInterpolation(float input) {
        return (float)(Math.sin(2 * mCycles * Math.PI * input));
    }
    
    private float mCycles;
}

