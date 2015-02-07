package thahn.java.agui.widget;

import thahn.java.agui.app.Context;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.Gravity;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.widget.LinearLayout.LayoutParams;

/**
 * 
 * @author thAhn
 *
 */
public class FrameLayout extends ViewGroup {

	private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
	
	public FrameLayout(Context context) {
		this(context, new AttributeSet(context));
	}
	
	public FrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_FRAME_LAYOUT_CODE);
	}
	
	public FrameLayout(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	private int mMaxX = -1;
	private int mMaxY = -1;
	
	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
	}

	@Override
	public void arrange() {
		mMaxX = mMaxY = -1;

		final int parentLeft = getDrawingLeft();
		final int parentRight = getDrawingRight();
		final int parentTop = getDrawingTop();
		final int parentBottom = getDrawingBottom();
		
		for (View child : getChildren()) {
			if (child.getVisibility() == View.GONE) continue;
			
			child.onMeasure(getDrawingWidth(), getDrawingHeight());
			child.onPostMeasure(getDrawingWidth(), getDrawingHeight());
			int[] wh = measureChildSize(child);
			//
			int childLeft, childTop, childRight, childBottom;
			final LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
			final int layoutDirection = getLayoutDirection();
			final int absoluteGravity = Gravity.getAbsoluteGravity(lp.gravity, layoutDirection);
			final int verticalGravity = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;

//			switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			switch (lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.CENTER_HORIZONTAL:
				childLeft = parentLeft + (parentRight - parentLeft - wh[0]) / 2 +
				lp.leftMargin - lp.rightMargin;
				break;
			case Gravity.RIGHT:
				childLeft = parentRight - wh[0] - lp.rightMargin;
				break;
			case Gravity.LEFT:
			default:
				childLeft = parentLeft + lp.leftMargin;
			}

//			switch (verticalGravity) {
			switch (lp.gravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.TOP:
				childTop = parentTop + lp.topMargin;
				break;
			case Gravity.CENTER_VERTICAL:
				childTop = parentTop + (parentBottom - parentTop - wh[1]) / 2 +
				lp.topMargin - lp.bottomMargin;
				break;
			case Gravity.BOTTOM:
				childTop = parentBottom - wh[1] - lp.bottomMargin;
				break;
			default:
				childTop = parentTop + lp.topMargin;
			}

			child.onLayout(true, childLeft, childTop, childLeft + wh[0], childTop + wh[1]);

			
			//
//			child.onLayout(true, getDrawingLeft() + child.getLayoutParams().leftMargin
//					, getDrawingTop() + child.getLayoutParams().topMargin
//					, getDrawingLeft() + child.getLayoutParams().leftMargin + wh[0]
//					, getDrawingTop() + child.getLayoutParams().topMargin + wh[1]
//					);
			child.arrange();
			if (mMaxX < child.getRight() + child.getLayoutParams().rightMargin) mMaxX = child.getRight() + child.getLayoutParams().rightMargin - getLeft();
			if (mMaxY < child.getBottom() + child.getLayoutParams().bottomMargin) mMaxY = child.getBottom() + child.getLayoutParams().bottomMargin - getTop();
		}
		
		if (mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			mWidth = mMaxX + getPaddingLeft();
		} 
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			mHeight = mMaxY + getPaddingTop();
		}
		//
//		mPolicy.checkChildMaxSizePolicy();
		mPolicy.checkLayoutMaxSizePolicy();
		//
		if (getDrawingWidth() < mMaxX) {
			mScrollableX = mMaxX - getDrawingWidth();
		}
		if (getDrawingHeight() < mMaxY) {
			mScrollableY = mMaxY - getDrawingHeight();
		}
		
//		Log.d(toString());
	}
	
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof FrameLayout.LayoutParams && super.checkLayoutParams(p);
	}
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mContext, attrs);
        return params;
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new FrameLayout.LayoutParams(p);
	}
	
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

	/**
     * Per-child layout information for layouts that support margins.
     * See {@link android.R.styleable#FrameLayout_Layout FrameLayout Layout Attributes}
     * for a list of all child view attributes that this class supports.
     * 
     * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
     */
    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         *
         * @see android.view.Gravity
         * 
         * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
         */
        public int gravity = -1;

        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            gravity = attrs.getInt(thahn.java.agui.R.attr.FrameLayout_layout_gravity, -1);
            // a.recycle();
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int width, int height) {
            super(width, height);
        }

        /**
         * Creates a new set of layout parameters with the specified width, height
         * and weight.
         *
         * @param width the width, either {@link #MATCH_PARENT},
         *        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
         *        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param gravity the gravity
         *
         * @see android.view.Gravity
         */
        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        /**
         * Copy constructor. Clones the width, height, margin values, and
         * gravity of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }
    }
}

