package thahn.java.agui.animation;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;

/**
 * An interpolator where the rate of change starts out quickly and 
 * and then decelerates.
 *
 */
public class DecelerateInterpolator implements Interpolator {
    public DecelerateInterpolator() {
    }

    /**
     * Constructor
     * 
     * @param factor Degree to which the animation should be eased. Setting factor to 1.0f produces
     *        an upside-down y=x^2 parabola. Increasing factor above 1.0f makes exaggerates the
     *        ease-out effect (i.e., it starts even faster and ends evens slower)
     */
    public DecelerateInterpolator(float factor) {
        mFactor = factor;
    }
    
    public DecelerateInterpolator(Context context, AttributeSet attrs) {
        mFactor = attrs.getFloat(thahn.java.agui.R.attr.DecelerateInterpolator_factor, 1.0f);
    }
    
    public float getInterpolation(float input) {
        float result;
        if (mFactor == 1.0f) {
            result = (float)(1.0f - (1.0f - input) * (1.0f - input));
        } else {
            result = (float)(1.0f - Math.pow((1.0f - input), 2 * mFactor));
        }
        return result;
    }
    
    private float mFactor = 1.0f;
}

