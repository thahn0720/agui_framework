package thahn.java.agui.widget;

import java.awt.Graphics;

import thahn.java.agui.app.Context;
import thahn.java.agui.utils.SparseArray;
import thahn.java.agui.view.Attribute;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.Gravity;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;


public class RelativeLayout extends ViewGroup {
	
	public static final int 									TRUE					= 1;
	public static final int 									FASLE					= 0;
	
	public static final int 									RULES_COUNT				= 17;
	public static final int 									OVERLAP 				= 0;
	public static final int 									BELOW 					= 1;
	public static final int 									ABOVE 					= 2;
	public static final int 									LEFT_OF 				= 3;	
	public static final int 									RIGHT_OF 				= 4;	
	public static final int 									ALIGN_BASELINE 			= 5;
	public static final int 									ALIGN_BOTTOM 			= 6;
	public static final int 									ALIGN_TOP 				= 7;
	public static final int 									ALIGN_LEFT 				= 8;
	public static final int 									ALIGN_RIGHT 			= 9;
	public static final int 									ALIGN_PARENT_BOTTOM 	= 10;
	public static final int 									ALIGN_PARENT_TOP 		= 11;
	public static final int 									ALIGN_PARENT_LEFT 		= 12;
	public static final int 									ALIGN_PARENT_RIGHT 		= 13;
	public static final int 									CENTER_HORIZONTAL 		= 14;
	public static final int 									CENTER_VERTICAL 		= 15;
	public static final int 									CENTER_IN_PARENT		= 16;
	
	private SparseArray<View> 									mRelativeParams 		= new SparseArray<>();
	
	public RelativeLayout(Context context) {
		this(context, new AttributeSet(context));
	}

	public RelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_RELATIVE_LAYOUT_CODE);
	}
	
	public RelativeLayout(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	public void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
	}

	@Override
	public void initDefault() {
		super.initDefault();
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
	}

	@Override
	public void arrange() {
		for(View v : getChildren()) {
			checkPositioned(v);
		}
		
		mPolicy.checkChildMaxSizePolicy();
		mPolicy.checkLayoutMaxSizePolicy();
		
		for(View v : getChildren()) {
			v.setPositioned(false);
		}
		
		for(View v : getChildren()) {
			if(v.getVisibility() == GONE) continue;
			ViewGroup.LayoutParams childParams = v.getLayoutParams();
			if(childParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) childParams;
				int[] rules = params.rules;
				for(int i=RelativeLayout.CENTER_HORIZONTAL;i<RelativeLayout.RULES_COUNT;++i) {
					if(rules[i] != FASLE) {
						v.setPositioned(true);
						applyCenterRelation(i, rules[i], v);
						View relatedView = mRelativeParams.get(v.getId());
						if(relatedView != null) {
							relatedView.setPositioned(true);
							doDirectionRelation(relatedView, (RelativeLayout.LayoutParams)relatedView.getLayoutParams());
						}
					}
				}
			}
		}

		int leftMax = Integer.MAX_VALUE;
		int rightMax = Integer.MIN_VALUE;
		int topMax = Integer.MAX_VALUE;
		int bottomMax = Integer.MIN_VALUE;
		for(View v : getChildren()) {
			v.setPositioned(false);
			leftMax = Math.min(leftMax, v.getLeft());
			topMax = Math.min(topMax, v.getTop());
			rightMax = Math.max(rightMax, v.getRight());
			bottomMax = Math.max(bottomMax, v.getBottom());
		}
		
		// gravity
		if (!((mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT
				&& (mGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP)) {
			measureGravity(leftMax, topMax, rightMax, bottomMax);
		}
	}
	
	private void checkPositioned(View v) {
		if(v.getVisibility() == GONE) return;
		if(!v.isPositioned()) {
			v.setPositioned(true);
			layoutChild(v);
		}
	}
	
	private void layoutChild(View v) {
		ViewGroup.LayoutParams childParams = v.getLayoutParams();
		v.onMeasure(getDrawingWidth(), getDrawingHeight());
		v.onPostMeasure(getDrawingWidth(), getDrawingHeight());
		int[] wh = measureChildSize(v);
		int left = getDrawingLeft() + v.getLayoutParams().leftMargin;
		int top = getDrawingTop() + v.getLayoutParams().topMargin;
		v.onLayout(true, left, top, left + wh[0], top + wh[1]);
		v.arrange();
		if(childParams instanceof RelativeLayout.LayoutParams) {
			doDirectionRelation(v, (RelativeLayout.LayoutParams)childParams);
		} 
	}
	
	private void doDirectionRelation(View v, RelativeLayout.LayoutParams childParams) {
		RelativeLayout.LayoutParams params = childParams;
		int[] rules = params.rules;
		for(int i=0;i<RelativeLayout.CENTER_HORIZONTAL;++i) {
			if(rules[i] != FASLE) {
				mRelativeParams.put(rules[i], v);
				applyDirectionRelation(i, rules[i], v);
			}
//			Log.e(""+v.getLeft() + ", " + v.getTop() + "," + (v.getRight()) + ", " + (v.getBottom()));
		}
	}
	
	private void applyDirectionRelation(int which, int value, View v) {
		if(value == -1) return ;
		
		int x = v.getLeft();
		int y = v.getTop();
		ViewGroup.LayoutParams params = v.getLayoutParams();
		View relatedView = null;
		switch (which) {
		case RelativeLayout.OVERLAP:
			break;
		case RelativeLayout.BELOW:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			y = relatedView.getBottom() + relatedView.getLayoutParams().bottomMargin + params.topMargin;
			break;
		case RelativeLayout.ABOVE:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			y = relatedView.getTop() - relatedView.getLayoutParams().topMargin - v.getHeight() - params.bottomMargin;
			break;
		case RelativeLayout.LEFT_OF:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			x = relatedView.getLeft() - relatedView.getLayoutParams().leftMargin - v.getWidth() - params.rightMargin;
			break;
		case RelativeLayout.RIGHT_OF:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			x = relatedView.getRight() + relatedView.getLayoutParams().rightMargin + params.leftMargin;
			break;
		case RelativeLayout.ALIGN_BASELINE:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			y = relatedView.getTop();
			break;
		case RelativeLayout.ALIGN_BOTTOM:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			y = relatedView.getBottom() - v.getHeight();
			break;
		case RelativeLayout.ALIGN_TOP:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			y = relatedView.getTop();
			break;
		case RelativeLayout.ALIGN_LEFT:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			x = relatedView.getLeft();
			break;
		case RelativeLayout.ALIGN_RIGHT:
			relatedView = getRelatedView(value);
			if(relatedView == null) return ;
			x = relatedView.getRight() - v.getWidth();
			break;
		case RelativeLayout.ALIGN_PARENT_BOTTOM:
			y = getBottom() - v.getHeight() - params.bottomMargin;
			break;
		case RelativeLayout.ALIGN_PARENT_TOP:
			y = getTop() + params.topMargin;
			break;
		case RelativeLayout.ALIGN_PARENT_LEFT:
			x = getLeft() + params.leftMargin;
			break;
		case RelativeLayout.ALIGN_PARENT_RIGHT:
			x = getRight() - v.getWidth() - params.rightMargin; 
			break;
		}
		checkViewSize(x, y, v, params);
	}
	
	private void applyCenterRelation(int which, int value, View v) {
		int x = v.getLeft(); 
		int y = v.getTop(); 
		View relatedView = null;
		switch (which) {
		case RelativeLayout.CENTER_HORIZONTAL:
			x = getDrawingLeft() + getWidth()/2 - v.getWidth()/2; 
			break;
		case RelativeLayout.CENTER_VERTICAL:
			y = getDrawingTop() + getHeight()/2 - v.getHeight()/2;
			break;
		case RelativeLayout.CENTER_IN_PARENT:
			x = getDrawingLeft() + getWidth()/2 - v.getWidth()/2;
			y = getDrawingTop() + getHeight()/2 - v.getHeight()/2;
			break;
		}
		
		checkViewSize(x, y, v, v.getLayoutParams());
	}
	
	private void checkViewSize(int x, int y, View v, ViewGroup.LayoutParams params) {
		int width = v.getWidth(), height = v.getHeight();
		if(x + v.getWidth() > getDrawingWidth() + getDrawingLeft()) {
			width = getDrawingWidth() - x - v.getLayoutParams().rightMargin;
			if(width < 0) {
				width = getDrawingWidth() - x;
				if(width < 0) width = getDrawingWidth();
			}
		}
		if(y + v.getHeight() > getDrawingHeight() + getDrawingTop()) {
			height = getDrawingHeight() - y - v.getLayoutParams().bottomMargin;
			if(height < 0) {
				height = getDrawingHeight() - y;
				if(height < 0) height = getDrawingHeight();
			}
		}
		//
		if(width != v.getWidth() || height != v.getHeight()) {
			v.onMeasure(width, height);
			v.onPostMeasure(width, height);
			int[] wh = measureChildSize(v);
			width = wh[0] + v.getLayoutParams().leftMargin;//convertView.getWidth();
			height = wh[1] + v.getLayoutParams().topMargin;//convertView.getHeight();
		} 
		//
		if(params.width < 0 && x < getDrawingLeft()) { // getParent() != null && 
			width += x;
			x = getDrawingLeft();
		}
		if(params.height < 0 && y < getDrawingTop()) {
			height += y;
			y = getDrawingTop();
		}
		v.onLayout(true, x, y, x + width, y + height);//x + v.getWidth(), y + v.getHeight());
		v.arrange();
	}
	
	private View getRelatedView(int viewId) {
		View view = findViewById(viewId);
		if(view == null || view.getVisibility() == View.GONE) return null;
		checkPositioned(view);
		return view;
	}
	
	private void measureGravity(int leftMax, int topMax, int rightMax, int bottomMax) {
		int sparseX = Math.abs(getDrawingLeft() - leftMax) + Math.abs(getDrawingRight() - rightMax);  
		int sparseY = Math.abs(getDrawingTop() - topMax) + Math.abs(getDrawingBottom() - bottomMax);  
		
		for (View v : getChildren()) {
			boolean horizontalGoing = false;
			boolean verticalGoing = false;
			int offsetX = 0;
			int offsetY = 0;
			
			if (v.getVisibility() == View.GONE) continue;
			// horizontal
			switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.RIGHT:
				horizontalGoing = true;
				offsetX = sparseX;
				break;
			case Gravity.CENTER_HORIZONTAL:
				horizontalGoing = true;
				offsetX = sparseX / 2;
				break;
			}
			// vertical
			switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.BOTTOM:
				verticalGoing = true;
				offsetY = sparseY;
				break;
			case Gravity.CENTER_VERTICAL:
				verticalGoing = true;
				offsetY = sparseY / 2;
				break;
			}
			
			if (horizontalGoing | verticalGoing) {
//				v.onLayout(true, v.getLeft() + offsetX, v.getTop() + offsetY, v.getRight() + offsetX, v.getBottom() + offsetY);
				v.offsetLeftAndRight(offsetX);
				v.offsetTopAndBottom(offsetY);
				v.arrange(); 
			}
		}
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
	}
	
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof RelativeLayout.LayoutParams && super.checkLayoutParams(p);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new RelativeLayout.LayoutParams(mContext, attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new RelativeLayout.LayoutParams(p);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public static class LayoutParams extends ViewGroup.LayoutParams {
		
		private int[] 													rules 					= new int[RULES_COUNT];
		
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			
			Attribute[] attrArray = attrs.obtainRelatedAttr("RelativeLayout", thahn.java.agui.R.attr.RelativeLayout, ViewName.WIDGET_RELATIVE_LAYOUT_CODE);
			for(Attribute a : attrArray) {
				int attrNameHash = a.getAttrNameHash();
				switch(attrNameHash) {
				case thahn.java.agui.R.attr.RelativeLayout_layout_above:
					rules[ABOVE] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_below:
					rules[BELOW] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_toLeftOf:
					rules[LEFT_OF] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_toRightOf:
					rules[RIGHT_OF] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignBaseline:
					rules[ALIGN_BASELINE] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignBottom:
					rules[ALIGN_BOTTOM] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignTop:
					rules[ALIGN_TOP] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignLeft:
					rules[ALIGN_LEFT] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignRight:
					rules[ALIGN_RIGHT] = attrs.getResourceId(attrNameHash, FASLE);
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignParentBottom:
					rules[ALIGN_PARENT_BOTTOM] = attrs.getBoolean(attrNameHash, false) ? 1 : FASLE;
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignParentTop:
					rules[ALIGN_PARENT_TOP] = attrs.getBoolean(attrNameHash, false) ? 1 : FASLE;
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignParentLeft:
					rules[ALIGN_PARENT_LEFT] = attrs.getBoolean(attrNameHash, false) ? 1 : FASLE;
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_alignParentRight:
					rules[ALIGN_PARENT_RIGHT] = attrs.getBoolean(attrNameHash, false) ? 1 : FASLE;
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_centerHorizontal:
					rules[CENTER_HORIZONTAL] = attrs.getBoolean(attrNameHash, false) ? 1 : FASLE;
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_centerVertical:
					rules[CENTER_VERTICAL] = attrs.getBoolean(attrNameHash, false) ? 1 : FASLE;
					break;
				case thahn.java.agui.R.attr.RelativeLayout_layout_centerInParent:
					rules[CENTER_IN_PARENT] = attrs.getBoolean(attrNameHash, false) ? 1 : FASLE;
					break;
				}
			}
			attrs.recycle();
		}
		
		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
			if(source instanceof RelativeLayout.LayoutParams) {
				this.rules = ((RelativeLayout.LayoutParams) source).rules;
			}
		}
		
		public void addRule(int verb) {
			rules[verb] = TRUE;
		}
		
		public void addRule(int verb, int anchor) {
			rules[verb] = anchor;
        }
	}
}
