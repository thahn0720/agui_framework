package thahn.java.agui.animation;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;

public class AlphaAnimation extends Animation {
    
	private float mFromAlpha;
    private float mToAlpha;

    /**
     * Constructor used when an AlphaAnimation is loaded from a resource. 
     * 
     * @param context Application context to use
     * @param attrs Attribute set from which to read values
     */
    public AlphaAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mFromAlpha = attrs.getFloat(thahn.java.agui.R.attr.AlphaAnimation_fromAlpha, 1.0f);
        mToAlpha = attrs.getFloat(thahn.java.agui.R.attr.AlphaAnimation_toAlpha, 1.0f);
    }
    
    /**
     * Constructor to use when building an AlphaAnimation from code
     * 
     * @param fromAlpha Starting alpha value for the animation, where 1.0 means
     *        fully opaque and 0.0 means fully transparent.
     * @param toAlpha Ending alpha value for the animation.
     */
    public AlphaAnimation(float fromAlpha, float toAlpha) {
    	super();
        mFromAlpha = fromAlpha;
        mToAlpha = toAlpha;
    }
    
    /**
     * Changes the alpha property of the supplied {@link Transformation}
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float alpha = mFromAlpha;
        t.setAlpha(alpha + ((mToAlpha - alpha) * interpolatedTime));
//        Log.e("AlphaAnimation", "time : " + interpolatedTime + ", alpha : " + t.getAlpha());
    }

    @Override
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override
    public boolean willChangeBounds() {
        return false;
    }

    /**
     * @hide
     */
    @Override
    public boolean hasAlpha() {
        return true;
    }
}

