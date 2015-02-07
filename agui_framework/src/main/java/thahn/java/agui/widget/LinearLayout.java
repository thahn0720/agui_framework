package thahn.java.agui.widget;

import java.awt.Graphics;

import thahn.java.agui.R;
import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.Gravity;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;

/**
 * 각 child가 가져야만 하는 특징인 relativelayout에서의 below,above 등은 각 차이들가 가져야 하기에 layoutparam으로 하지만 부모 혼자만 가지는 linearlayout의 orientation은 
 * 각 차이드가 가지는 것이 아니라 linearlayout으로 빼자
 * @author thAhn
 *
 */
public class LinearLayout extends ViewGroup {

	public static final int 									HORIZONTAL = 0;
    public static final int 									VERTICAL = 1;
	
	private static final int 									LINEAR_LAYOUT_ORIENTATION 				= "orientation".hashCode();
	private static final int									LINEAR_LAYOUT_WEIGHT					= "weight".hashCode();
	
	private static final String									LINEAR_LAYOUT_ORIENTATION_HORIZONTAL 	= "horizontal";
	private static final String									LINEAR_LAYOUT_ORIENTATION_VERTICAL		= "vertical";
	
	private boolean 											isHorizontal;
	
	private float												mWeightSum;
	
	public LinearLayout(Context context) {
		this(context, new AttributeSet(context));
	}

	public LinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_LINEAR_LAYOUT_CODE);
	}
	
	public LinearLayout(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	protected void initDefault() {
		super.initDefault();
	}

	@Override
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		isHorizontal = attrs.getString(LINEAR_LAYOUT_ORIENTATION, LINEAR_LAYOUT_ORIENTATION_HORIZONTAL).equals(LINEAR_LAYOUT_ORIENTATION_HORIZONTAL);
		mWeightSum = attrs.getFloat(thahn.java.agui.R.attr.LinearLayout_weightSum, -1.0f);
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
	}

	private int left = 0;
	private int top = 0;
	private int right = 0;
	private int bottom = 0;
	
	@Override
	public void arrange() {
		View[] views = getChildren();
		left = top = 0;
		right = getDrawingLeft();
		bottom = getDrawingTop();
		mScrollableX = 0;
		mScrollableY = 0;
		int tempBottom = 0;
		int tempRight = 0;
		// 1. at first, measure child's widhth and height
		float weightSum = 0;
		for (int i=0;i<views.length;++i) {
			if (views[i].getVisibility() == GONE) continue;
			
			ViewGroup.LayoutParams params = views[i].getLayoutParams();
			if (params instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) params;
				if (linearParams.weight != 0.0f) {
					weightSum += linearParams.weight;
				}
			}
			int availWidth = 0;
			int availHeight = 0;
			if (weightSum == 0) {
				if (i == 0) {	
					if (isHorizontal) {
						availWidth = getDrawingWidth() - views[i].getLayoutParams().leftMargin; //  - getDrawingLeft()
						availHeight = getDrawingHeight() - views[i].getLayoutParams().topMargin - views[i].getLayoutParams().bottomMargin;
					} else {
						availWidth = getDrawingWidth() - views[i].getLayoutParams().leftMargin - views[i].getLayoutParams().rightMargin;
						availHeight = getDrawingHeight() - views[i].getLayoutParams().topMargin;
									//getBottom() - getDrawingTop() - views[i].getLayoutParams().topMargin;
					}
				} else {
					if (isHorizontal) {
						availWidth = getDrawingRight() - right
									- views[i].getLayoutParams().leftMargin - views[i].getLayoutParams().rightMargin;
						availHeight = getDrawingHeight() - views[i].getLayoutParams().topMargin - views[i].getLayoutParams().bottomMargin;
					} else {
						availWidth = getDrawingWidth() - views[i].getLayoutParams().leftMargin - views[i].getLayoutParams().rightMargin;
						availHeight = getDrawingBottom() - bottom
								- views[i].getLayoutParams().topMargin - views[i].getLayoutParams().bottomMargin;
					}
				}
			} else {
				availWidth = getDrawingWidth() - views[i].getLayoutParams().leftMargin - views[i].getLayoutParams().rightMargin;
				availHeight = getDrawingHeight() - views[i].getLayoutParams().topMargin - views[i].getLayoutParams().bottomMargin;
			}
			
			views[i].onMeasure(availWidth, availHeight);
			views[i].onPostMeasure(availWidth, availHeight);
			views[i].arrange();		
			
			measureChild(i, views, 0, weightSum, false);
			tempBottom = Math.max(tempBottom, bottom);
			tempRight = Math.max(tempRight, right);
		}
		left = top = 0;
		right = getDrawingLeft();
		bottom = getDrawingTop();
		// 2. and gravity only layout, not use arrange
		if (views.length > 0) measureGravity(views, tempRight - getDrawingLeft(), tempBottom - getDrawingTop());
				
		if (mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
 			if (tempRight > getRight()) {
				mWidth = tempRight - getLeft();//getWidth();
			} else {
				if (isHorizontal && weightSum > 0) {
					mWidth = getWidth();
				} else if (getLayoutParams() instanceof LinearLayout.LayoutParams && ((LinearLayout.LayoutParams)getLayoutParams()).getWeight() > 0) {
					mWidth = getWidth();// - getPaddingRight();
				} else {
					mWidth = tempRight - getLeft();
				}
			}
		}
		
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			if (tempBottom > getBottom()) {
				mHeight = tempBottom - getTop();//getWidth();
			} else {
				if (!isHorizontal && weightSum > 0) {
					mHeight = getHeight();
				} else if (getLayoutParams() instanceof LinearLayout.LayoutParams && ((LinearLayout.LayoutParams)getLayoutParams()).getWeight() > 0) {
					mHeight = getHeight();// - getPaddingBottom();
				} else {	// wrap_content, but when parent is linearlayout and this has a weight, this is fault
					mHeight = tempBottom - getTop();
				}
			}
		}
		// 3. calcurate weight if parent's width or height is match_parent, fill_parent, split remaining space by weight
		float minusWeight = 0;
		if (weightSum > 0) { //&& (mLayoutParam.width == LayoutParams.FILL_PARENT || mLayoutParam.height == LayoutParams.MATCH_PARENT)) {
			mScrollableX = 0;
			mScrollableY = 0;
			for (int i=0;i<views.length;++i) {
				if (views[i].getVisibility() == GONE) continue;
				
				float weight = 0;
				ViewGroup.LayoutParams params = views[i].getLayoutParams();
				if (params instanceof LinearLayout.LayoutParams) {
					LinearLayout.LayoutParams linearParam = (LinearLayout.LayoutParams) params;
					weight = linearParam.weight;
				}
				
				int remaining = 0;
				if (isHorizontal) remaining = getDrawingRight() - tempRight;
				else remaining = getDrawingBottom() - tempBottom;
				
				if (remaining > 0) {
					remaining = (int)((remaining/weightSum) * weight); 
				} else if (remaining < 0) {
					if (isHorizontal) {
						if (weightSum == 2) {
							remaining = (int)((remaining/weightSum) * weight);
						} else {
							if (views[i].getWidth() >= getDrawingWidth()) {
								minusWeight = weight;   
							} else {
								remaining = 0;
							}
						}
					} else {
						if (weightSum == 2) {
							remaining = (int)((remaining/weightSum) * weight);
						} else {
							if (views[i].getHeight() >= getDrawingHeight()) {
								minusWeight = weight;   
							} else {
								remaining = 0;
							}
						}
					}
				}
				
				measureChild(i, views, remaining, weightSum, true);
				views[i].arrange();
			}
		}
		//
		if (weightSum > 0) mPolicy.checkChildMaxSizePolicy();
		mPolicy.checkLayoutMaxSizePolicy();
	}
	
	/**
	 * 
	 * @param i
	 * @param views
	 * @param remaining
	 * @param weightSum
	 * @param isArranged true : weight calcuration, false : default
	 */
	private void measureChild(int i, View[] views, int remaining, float weightSum, boolean isArranged) {
		ViewGroup.LayoutParams params = views[i].getLayoutParams();
		
		if (isHorizontal) {
			left = right + params.leftMargin;
			if (isArranged) top = views[i].getTop();
			else top = getDrawingTop() + params.topMargin;
		} else {
			if (isArranged) left = views[i].getLeft();
			else left = getDrawingLeft() + params.leftMargin;
			top = bottom + params.topMargin;
		}
		
		int width = views[i].getWidth(); // getPaddedWidth
		int height = views[i].getHeight(); // getPaddedHeight
		// if previous view's size is over the screen size, the next view's height is set to '0'. 
		// but later, view is again set to corret size. However, view group is not set to corret size and set to 0. 
		// so, view group handles right, bottom, left, top;
//		if (views[i] instanceof ViewGroup) {	
//			width = views[i].getRight() - views[i].getLeft();
//			height = views[i].getBottom() - views[i].getTop();
//		}
		if (params.width < 0 && width + params.leftMargin + params.rightMargin > getDrawingWidth()) {
			width = getDrawingWidth() - params.leftMargin - params.rightMargin;
		}
		if (params.height < 0 && height + params.topMargin + params.bottomMargin > getDrawingHeight()) {
			height = getDrawingHeight() - params.topMargin - params.bottomMargin;
		}
		
		if (isHorizontal) {
			right = left + width + remaining;
			bottom = top + height;
		} else {
			right = left + width;//views[i].getWidth();
			bottom = top + height + remaining;//views[i].getHeight() + remaining;
		}
		
		if (getParent() != null) { 
			View parent = getParent();
			if (params.width < 0) {
				if (right > parent.getDrawingRight()-params.rightMargin && left < parent.getDrawingRight()-params.rightMargin) {
					if (weightSum == 0 && !(getParent() instanceof HorizontalScrollView)) {
						right = parent.getDrawingRight()-params.rightMargin;
					}
					mScrollableX += width;
				} else if (left >= parent.getDrawingRight()-params.rightMargin) {
					mScrollableX += width;
				}
			}
			if (params.height < 0) {
				if (bottom > parent.getDrawingBottom()-params.bottomMargin && top < parent.getDrawingBottom()-params.bottomMargin) {
					if (weightSum == 0 && !(getParent() instanceof ScrollView)) {
						bottom = parent.getDrawingBottom()-params.bottomMargin;
					}
					mScrollableY += height;
				} else if (top >= parent.getDrawingBottom()-params.bottomMargin) {
					mScrollableY += height;
				}
			}
		}
		
		views[i].onLayout(true, left, top, right, bottom);
		if (weightSum == 0) views[i].arrange();
		
		right += params.rightMargin;
		bottom += params.bottomMargin;
	}
	
	// FIXME : weightSum이 0이 아닐 때 horiontal, vertical에 따라서 정렬을 안해도 되는것이 있다.
	private void measureGravity(View[] children, int wrapWidth, int wrapHeight) {
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		
		left = children[0].getDrawingLeft();
		top = children[0].getDrawingTop();
		right = left + children[0].getWidth();//children[i].getDrawingRight();
		bottom = top + children[0].getHeight();//children[i].getDrawingBottom();
		
		for (int i = 0; i < children.length; i++) {
			/**
			 * if true, do layout method, else skip
			 */
			boolean horizontalGoing = false;
			boolean verticalGoing = false;
			if (children[i].getVisibility() == View.GONE) continue;
			// horizontal
			switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.LEFT:
				horizontalGoing = false;
				break;
			case Gravity.RIGHT:
				horizontalGoing = true;
				if (isHorizontal) {
					if (i==0) {
						left = getDrawingRight() - wrapWidth + children[i].getLayoutParams().leftMargin;
					} else {
						left = right + children[i].getLayoutParams().rightMargin + children[i-1].getLayoutParams().leftMargin;
					}
					right = left + children[i].getWidth();
				} else {
					right = getDrawingRight() - children[i].getLayoutParams().rightMargin;
					left = right - children[i].getWidth();
				}
				break;
			case Gravity.CENTER_HORIZONTAL:
				if (children[i].getWidth() < getWidth()) {//wrapWidth < getWidth()) {
					horizontalGoing = true;
					if (isHorizontal) {
						if (i==0) {
							left = getDrawingLeft() + getDrawingWidth()/2 - wrapWidth/2 + children[i].getLayoutParams().leftMargin;
						} else {
							left = children[i-1].getDrawingRight() + children[i-1].getLayoutParams().rightMargin + children[i].getLayoutParams().leftMargin;
						}
						right = left + children[i].getDrawingWidth();
					} else {
						left = getDrawingLeft() + getDrawingWidth()/2 - children[i].getWidth()/2;
						right = getDrawingLeft() + getDrawingWidth()/2 + children[i].getWidth()/2; 
					}
				} else {
					horizontalGoing = false;
				}
				break;
			default:
				left = children[i].getDrawingLeft();
				right = children[i].getDrawingRight();
				break;	
			}
			// vertical
			switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.TOP:
				verticalGoing = false;
				break;
			case Gravity.BOTTOM:
				verticalGoing = true;
				if (isHorizontal) {
					top = getDrawingTop() - (wrapHeight - children[i].getHeight());
					bottom = top - children[i].getHeight();
				} else {
					if (i==0) {
						top = getDrawingTop() + getHeight() - wrapHeight + children[i].getLayoutParams().topMargin;
					} else {
						top = children[i-1].getDrawingBottom() + children[i-1].getLayoutParams().bottomMargin + children[i].getLayoutParams().topMargin;
					}
					bottom = top + children[i].getHeight();
				}
				break;
			case Gravity.CENTER_VERTICAL:
				if (children[i].getHeight() < getHeight()) {//wrapHeight < getHeight()) {
					verticalGoing = true;
					if (isHorizontal) {
						top = getDrawingTop() + getDrawingHeight()/2 - children[i].getHeight()/2;
						bottom = getDrawingTop() + getDrawingHeight()/2 + children[i].getHeight()/2;
					} else {
						if (i==0) {
							top = getDrawingTop() + (getDrawingHeight() - wrapHeight) / 2 + children[i].getLayoutParams().topMargin;
						} else {
							top = children[i-1].getDrawingBottom() + children[i-1].getLayoutParams().bottomMargin + children[i].getLayoutParams().topMargin; 
						}
						bottom = top + children[i].getDrawingHeight();
					}
				} else {
					verticalGoing = false;
				}
				break;
			default:
				top = children[i].getDrawingTop();
				bottom = children[i].getDrawingBottom();
				break;	
			}
			
			if (horizontalGoing | verticalGoing) {
				children[i].onLayout(true, left, top, right, bottom);
				children[i].arrange(); 
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
	}
	
	
	public void setOrientation(int orientation) {
		switch (orientation) {
		case HORIZONTAL:
			isHorizontal = true;
			break;
		case VERTICAL:
			isHorizontal = false;
			break;
		}
	}
	
	/**
     * Returns the current orientation.
     * 
     * @return either {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public int getOrientation() {
    	if (isHorizontal) {
    		return HORIZONTAL;
    	} else {
    		return VERTICAL;
    	}
    }
	
	public float getWeightSum() {
		return mWeightSum;
	}

	public void setWeightSum(float weightSum) {
		this.mWeightSum = Math.max(weightSum, 0.0f);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LinearLayout.LayoutParams && super.checkLayoutParams(p);
		// return p instanceof RelativeLayout.LayoutParams && super.checkLayoutParams(p);
	}
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mContext, attrs);
        return params;
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LinearLayout.LayoutParams(p);
	}
	
    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}
     * and a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * when the layout's orientation is {@link #VERTICAL}. When the orientation is
     * {@link #HORIZONTAL}, the width is set to {@link LayoutParams#WRAP_CONTENT}
     * and the height to {@link LayoutParams#WRAP_CONTENT}.
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        if (getOrientation() == HORIZONTAL) {
            return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        } else if (getOrientation() == VERTICAL) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return null;
    }

	public static class LayoutParams extends ViewGroup.LayoutParams {
		
		private float												weight;
		
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			weight = attrs.getFloat(R.attr.LinearLayout_layout_weight, 0.0f);
			
			attrs.recycle();
		}
		
		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
			if (source instanceof LinearLayout.LayoutParams) {
				this.weight = ((LinearLayout.LayoutParams) source).weight;
			}
		}

		public float getWeight() {
			return weight;
		}

		public void setWeight(float weight) {
			this.weight = weight;
		}
	}
}
