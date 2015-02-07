package thahn.java.agui.animation;

import thahn.java.agui.app.Context;
import thahn.java.agui.res.Resources;
import thahn.java.agui.view.AttributeSet;

/**
 * An animation that controls the scale of an object. You can specify the point
 * to use for the center of scaling.
 * 
 */
public class ScaleAnimation extends Animation {
    private final Resources mResources;

    private float mFromX;
    private float mToX;
    private float mFromY;
    private float mToY;

    private int mFromXType = TypedValue.TYPE_NULL;
    private int mToXType = TypedValue.TYPE_NULL;
    private int mFromYType = TypedValue.TYPE_NULL;
    private int mToYType = TypedValue.TYPE_NULL;

    private float mFromXData = 0;
    private float mToXData = 0;
    private float mFromYData = 0;
    private float mToYData = 0;

    private int mPivotXType = ABSOLUTE;
    private int mPivotYType = ABSOLUTE;
    private float mPivotXValue = 0.0f;
    private float mPivotYValue = 0.0f;

    private float mPivotX;
    private float mPivotY;

    /**
     * Constructor used when a ScaleAnimation is loaded from a resource.
     * 
     * @param context Application context to use
     * @param attrs Attribute set from which to read values
     */
    public ScaleAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);

        mResources = context.getResources();

        Description des = Description.toDescription(attrs.getString(thahn.java.agui.R.attr.ScaleAnimation_fromXScale, "0"));
        mFromX = 0.0f;
        if (des != null) {
            mFromXType = des.type;
            mFromXData = des.value;
        }
        des = Description.toDescription(attrs.getString(thahn.java.agui.R.attr.ScaleAnimation_toXScale, "0"));
        mToX = 0.0f;
        if (des != null) {
            mToXType = des.type;
            mToXData = des.value;
        }

        des = Description.toDescription(attrs.getString(thahn.java.agui.R.attr.ScaleAnimation_fromYScale, "0"));
        mFromY = 0.0f;
        if (des != null) {
            mFromYType = des.type;
            mFromYData = des.value;
        }
        des = Description.toDescription(attrs.getString(thahn.java.agui.R.attr.ScaleAnimation_toYScale, "0"));
        mToY = 0.0f;
        if (des != null) {
            mToYType = des.type;
            mToYData = des.value;
        }

        des = Description.toDescription(attrs.getString(thahn.java.agui.R.attr.ScaleAnimation_pivotX, "0"));
        mPivotXType = des.type;
        mPivotXValue = des.value;

        des = Description.toDescription(attrs.getString(thahn.java.agui.R.attr.ScaleAnimation_pivotY, "0"));
        mPivotYType = des.type;
        mPivotYValue = des.value;
    }

    /**
     * Constructor to use when building a ScaleAnimation from code
     * 
     * @param fromX Horizontal scaling factor to apply at the start of the
     *        animation
     * @param toX Horizontal scaling factor to apply at the end of the animation
     * @param fromY Vertical scaling factor to apply at the start of the
     *        animation
     * @param toY Vertical scaling factor to apply at the end of the animation
     */
    public ScaleAnimation(float fromX, float toX, float fromY, float toY) {
        mResources = null;
        mFromX = fromX;
        mToX = toX;
        mFromY = fromY;
        mToY = toY;
        mPivotX = 0;
        mPivotY = 0;
    }

    /**
     * Constructor to use when building a ScaleAnimation from code
     * 
     * @param fromX Horizontal scaling factor to apply at the start of the
     *        animation
     * @param toX Horizontal scaling factor to apply at the end of the animation
     * @param fromY Vertical scaling factor to apply at the start of the
     *        animation
     * @param toY Vertical scaling factor to apply at the end of the animation
     * @param pivotX The X coordinate of the point about which the object is
     *        being scaled, specified as an absolute number where 0 is the left
     *        edge. (This point remains fixed while the object changes size.)
     * @param pivotY The Y coordinate of the point about which the object is
     *        being scaled, specified as an absolute number where 0 is the top
     *        edge. (This point remains fixed while the object changes size.)
     */
    public ScaleAnimation(float fromX, float toX, float fromY, float toY,
            float pivotX, float pivotY) {
        mResources = null;
        mFromX = fromX;
        mToX = toX;
        mFromY = fromY;
        mToY = toY;

        mPivotXType = ABSOLUTE;
        mPivotYType = ABSOLUTE;
        mPivotXValue = pivotX;
        mPivotYValue = pivotY;
    }

    /**
     * Constructor to use when building a ScaleAnimation from code
     * 
     * @param fromX Horizontal scaling factor to apply at the start of the
     *        animation
     * @param toX Horizontal scaling factor to apply at the end of the animation
     * @param fromY Vertical scaling factor to apply at the start of the
     *        animation
     * @param toY Vertical scaling factor to apply at the end of the animation
     * @param pivotXType Specifies how pivotXValue should be interpreted. One of
     *        Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     *        Animation.RELATIVE_TO_PARENT.
     * @param pivotXValue The X coordinate of the point about which the object
     *        is being scaled, specified as an absolute number where 0 is the
     *        left edge. (This point remains fixed while the object changes
     *        size.) This value can either be an absolute number if pivotXType
     *        is ABSOLUTE, or a percentage (where 1.0 is 100%) otherwise.
     * @param pivotYType Specifies how pivotYValue should be interpreted. One of
     *        Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     *        Animation.RELATIVE_TO_PARENT.
     * @param pivotYValue The Y coordinate of the point about which the object
     *        is being scaled, specified as an absolute number where 0 is the
     *        top edge. (This point remains fixed while the object changes
     *        size.) This value can either be an absolute number if pivotYType
     *        is ABSOLUTE, or a percentage (where 1.0 is 100%) otherwise.
     */
    public ScaleAnimation(float fromX, float toX, float fromY, float toY,
            int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        mResources = null;
        mFromX = fromX;
        mToX = toX;
        mFromY = fromY;
        mToY = toY;

        mPivotXValue = pivotXValue;
        mPivotXType = pivotXType;
        mPivotYValue = pivotYValue;
        mPivotYType = pivotYType;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float sx = 1.0f;
        float sy = 1.0f;
        float scale = getScaleFactor();

        if (mFromX != 1.0f || mToX != 1.0f) {
            sx = mFromX + ((mToX - mFromX) * interpolatedTime);
        }
        if (mFromY != 1.0f || mToY != 1.0f) {
            sy = mFromY + ((mToY - mFromY) * interpolatedTime);
        }

        if (mPivotX == 0 && mPivotY == 0) {
            t.getMatrix().setScale(sx, sy);
        } else {
            t.getMatrix().setScale(sx, sy,scale * mPivotX, scale * mPivotY);
        }
    }

//    float resolveScale(float scale, int type, int data, int size, int psize) {
//        float targetSize;
//        if (type == TypedValue.TYPE_FRACTION) {
//            targetSize = TypedValue.complexToFraction(data, size, psize);
//        } else if (type == TypedValue.TYPE_DIMENSION) {
//            targetSize = TypedValue.complexToDimension(data, mResources.getDisplayMetrics());
//        } else {
//            return scale;
//        }
//
//        if (size == 0) {
//            return 1;
//        }
//
//        return targetSize/(float)size;
//    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

        mFromX = resolveSize(mFromXType, mFromXData, width, parentWidth);
        mToX = resolveSize(mToXType, mToXData, width, parentWidth);
        mFromY = resolveSize(mFromYType, mFromYData, height, parentHeight);
        mToY = resolveSize(mToYType, mToYData, height, parentHeight);

        mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth);
        mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight);
    }
}