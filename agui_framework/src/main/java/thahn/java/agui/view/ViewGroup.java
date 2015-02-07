
package thahn.java.agui.view;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import thahn.java.agui.ContextMenu;
import thahn.java.agui.animation.Animation;
import thahn.java.agui.animation.Animation.AnimationListener;
import thahn.java.agui.animation.LayoutAnimationController;
import thahn.java.agui.app.Context;
import thahn.java.agui.exception.NotExistException;
import thahn.java.agui.graphics.Rect;
import thahn.java.agui.widget.ViewAttribute;


/**
 * onMeasure();<br>
 * onPostMeasure();<br>
 * layout();<br>
 * arrange();<br>
 * @author thAhn
 *
 */
public abstract class ViewGroup extends View implements ViewParent {
	
	// When set, dispatchDraw() will run the layout animation and unset the flag
    private static final int 													FLAG_RUN_ANIMATION = 0x8;

    // When set, there is either no layout animation on the ViewGroup or the layout
    // animation is over
    // Set by default
    private static final int													FLAG_ANIMATION_DONE = 0x10;

    // When set, this ViewGroup invokes mAnimationListener.onAnimationEnd() and removes
    // the children's Bitmap caches if necessary
    // This flag is set when the layout animation is over (after FLAG_ANIMATION_DONE is set)
    private static final int 													FLAG_NOTIFY_ANIMATION_LISTENER = 0x200;
	
	private List<View> 															mContainer;
	protected View																mPressedView;
	private LayoutAnimationController 											mLayoutAnimationController;
	protected int 																mGroupFlags;
	private AnimationListener													mAnimationListener;
	
	public ViewGroup(Context context) {
		super(context);
	}

	public ViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewGroup(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	protected void initDefault() {
		super.initDefault();
		mContainer = new ArrayList<View>();
	}
	
	@Override
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
//	case R.styleable.ViewGroup_layoutAnimation:
//        int id = a.getResourceId(attr, -1);
//        if (id > 0) {
//            setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mContext, id));
//        }
//        break;
		
		mGroupFlags |= FLAG_ANIMATION_DONE;
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
		if (mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			mWidth = parentWidth;
		} 
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			mHeight = parentHeight;
		} 
	}

	@Override
	public View findViewById(int id) {
		View ret = super.findViewById(id);
		if (ret != null) {
			return ret;
		}
		
		for (View v : mContainer) {
			if (v.getId() == id) {
				ret = v;
				break;
			} else {
				if (v instanceof ViewGroup) {
					ViewGroup group = (ViewGroup) v;
					ret = group.findViewById(id);
					if (ret != null) break;
				}
			}
		}
		return ret;
	}
	
	public View[] getChildren() {
		return mContainer.toArray(new View[mContainer.size()]);
	}
	
	public int getChildCount() {
		return mContainer.size();
	}
	
	/**
     * Returns the view at the specified position in the group.
     *
     * @param index the position at which to get the view from
     * @return the view at the specified position or null if the position
     *         does not exist within the group
     */
    public View getChildAt(int index) {
        if (index < 0 || index >= mContainer.size()) {
            return null;
        }
        return mContainer.get(index);
    }
    
    /**
     * Returns the index of the child to draw for this iteration. Override this
     * if you want to change the drawing order of children. By default, it
     * returns i.
     * <p>
     * NOTE: In order for this method to be called, you must enable child ordering
     * first by calling {@link #setChildrenDrawingOrderEnabled(boolean)}.
     *
     * @param i The current iteration.
     * @return The index of the child to draw this iteration.
     *
     * @see #setChildrenDrawingOrderEnabled(boolean)
     * @see #isChildrenDrawingOrderEnabled()
     */
    protected int getChildDrawingOrder(int childCount, int i) {
        return i;
    }
	
	@Override
	public void arrange() {
		for (View child : getChildren()) {
			child.onMeasure(mWidth, mHeight);
			child.onPostMeasure(mWidth, mHeight);
			child.onLayout(true, child.getLeft(), child.getTop(), child.getLeft()+child.getWidth(), child.getTop()+child.getHeight());
			child.arrange();
		}
	}
	
	public void addView(View v) {
		addView(v, getChildCount()-1);
	}
	
	
	public void addView(View v, LayoutParams params) {
		v.setLayoutParams(params);
		addView(v);
	}
	
	public void addView(View v, int index, LayoutParams params) {
		v.setLayoutParams(params);
		addView(v);
	}
	
	public void addView(View v, int index) {
		addViewInternal(v, index);
		// FIXME : performance 를 엄청나게 떨어 뜨린다. 심각..
		mContext.arrangeView();
//		arrange();
//		onArrangeNeeded();
	}
	
	public void addViewInternal(View v) {
		addViewInternal(v, Math.max(getChildCount()-1, 0));
	}
	
	public void addViewInternal(View v, int index) {
		if (v.getParent() != null) {
            throw new IllegalStateException("The specified child already has a parent. " +
                    "You must call removeView() on the child's parent first.");
        }
		
		LayoutParams params = v.getLayoutParams();
        if (params == null || !checkLayoutParams(params)) {
            params = generateLayoutParams(params);//generateDefaultLayoutParams();
            params.registerLayoutObserver(v);
            if (params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
            v.setLayoutParams(params);
        }
        
		v.assignParent(this);
//		if (index < 0) {
			mContainer.add(v);
//		} else {
//			mContainer.add(Math.max(0, index), v);
//		}
	}
	
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
//		g.setClip(getLeft(), getTop(), getWidth(), getHeight());
		
		int flags = mGroupFlags;
		
		if ((flags & FLAG_RUN_ANIMATION) != 0 && canAnimate()) {
			int count = mContainer.size();
			for (int i=0;i<count;++i) {
				View child = mContainer.get(i);
				if (child.getVisibility() == VISIBLE) {
					final LayoutParams params = child.getLayoutParams();
                    attachLayoutAnimationParameters(child, params, i, count);
					bindLayoutAnimation(child);
				}
			}
			
            final LayoutAnimationController controller = mLayoutAnimationController;
//            if (controller.willOverlap()) {
//                mGroupFlags |= FLAG_OPTIMIZE_INVALIDATE;
//            }
            controller.start();
			
			mGroupFlags &= ~FLAG_RUN_ANIMATION;
            mGroupFlags &= ~FLAG_ANIMATION_DONE;
            
            if (mAnimationListener != null) {
                mAnimationListener.onAnimationStart(controller.getAnimation());
            }
		}
		
		drawChild(g);
		
		if ((flags & FLAG_ANIMATION_DONE) == 0 && (flags & FLAG_NOTIFY_ANIMATION_LISTENER) == 0 &&
                mLayoutAnimationController.isDone()) {
            // We want to erase the drawing cache and notify the listener after the
            // next frame is drawn because one extra invalidate() is caused by
            // drawChild() after the animation is over
            mGroupFlags |= FLAG_NOTIFY_ANIMATION_LISTENER;
			final Runnable end = new Runnable() {
				public void run() {
					notifyAnimationListener();
				}
			};
            post(end);
        }
	}
	
	@Override
	public void onDraw(Graphics g) {
		super.onDraw(g);
		drawOnChild(g);
	}

    protected void drawChild(Graphics canvas, View child, long drawingTime) {
        child.draw(canvas);//, this, drawingTime);
    }
	
	protected void drawChild(Graphics g) {
		for (int i=0;i<mContainer.size();++i) {
			View child = mContainer.get(getChildDrawingOrder(mContainer.size(), i));
			if (child.getVisibility() != VISIBLE) continue;
			drawChild(g, child, 0);
		}
	}
	
	protected void drawOnChild(Graphics g) {
		for (int i=0;i<mContainer.size();++i) {
			View child = mContainer.get(getChildDrawingOrder(mContainer.size(), i));
			if (child.getVisibility() != VISIBLE) continue;
			child.onDraw(g);
		}
	}
	
	private void notifyAnimationListener() {
        mGroupFlags &= ~FLAG_NOTIFY_ANIMATION_LISTENER;
        mGroupFlags |= FLAG_ANIMATION_DONE;

        if (mAnimationListener != null) {
           final Runnable end = new Runnable() {
               public void run() {
                   mAnimationListener.onAnimationEnd(mLayoutAnimationController.getAnimation());
               }
           };
           post(end);
        }

        invalidate();
    }
	
	@Override
	public boolean performClick() {
//		boolean ret = false;
//		for (View v : getChildren()) {
//			ret = v.performClick();
//		}
//		if (!ret) super.performClick();
//		return ret;
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		return super.onTouchEvent(event);
		boolean ret = false;
		int x = event.getX();
		int y = event.getY();
		int size;
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			size = getChildCount();
			View[] children1 = getChildren();
			for (int i = size-1;i>=0;--i) {
				if (!ret) {
					View v = children1[i];
					if (v.contains(x, y)) {
						mPressedView = v;
						ret = v.onTouchEvent(event); 
					}
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mPressedView != null) ret = mPressedView.onTouchEvent(event);
			break;
		case MotionEvent.ACTION_UP:
			if (mPressedView != null)  {
				ret = mPressedView.onTouchEvent(event);
				mPressedView = null;
			}
			break;
		case MotionEvent.ACTION_CLICK:
			size = getChildCount();
			View[] children2 = getChildren();
			for (int i = size-1;i>=0;--i) {
				if (!ret) {
					View v = children2[i];
					if (v.contains(x, y)) {
						mPressedView = v;
						ret = v.onTouchEvent(event); 
					}
				}
			}
		}
		if (!ret) ret = super.onTouchEvent(event);
		return ret;
	}
	
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		arrange();
//		Log.e("scroll to x : " + x + ", y : " + y);
	}
	
	@Override
	public boolean onScroll(MouseWheelEvent event) {
		boolean ret = super.onScroll(event);
		int x = event.getX();
		int y = event.getY();
		int size = getChildCount();
		View[] children1 = getChildren();
		for (int i = size-1;i>=0;--i) {
			View v = children1[i];
			if (v.contains(x, y)) {
				ret = v.onScroll(event);
				if (ret) break;
			}
		}
		return ret;
	}

	public void removeView(int index) {
		View view = mContainer.get(index);
		if (view != null) {
			view.deassignParent();
			mContainer.remove(index);
			// FIXME : performance 를 엄청나게 떨어 뜨린다. 심각..
			arrange();
			onArrangeNeeded();
		} else {
			throw new NotExistException("the child not exist");
		}
	}
	
	/**
	 * valid when touching
	 * @return touched view when touching, else null
	 */
	public View getTouchView() {
		if (mPressedView instanceof ViewGroup) {
			return getTouchView(((ViewGroup) mPressedView).mPressedView);
		} else {
			return mPressedView;
		}
	}
	
	private View getTouchView(View view) {
		if (view instanceof ViewGroup) {
			return getTouchView(((ViewGroup) view).mPressedView);
		} else {
			return view;
		}
	}
	
	public void removeView(View view) {
		if (mContainer.remove(view)) {
			view.deassignParent();
			// FIXME : performance 를 엄청나게 떨어 뜨린다. 심각..
			arrange();
			onArrangeNeeded();
		} else {
			throw new NotExistException("the child not exist");
		}
	}
	
	protected int[] measureChildSize(View child) {
		int[] wh = new int[]{child.getWidth(), child.getHeight()}; 
		if (child.getLayoutParams().width < 0) {
			if (wh[0] + child.getLayoutParams().leftMargin + child.getLayoutParams().rightMargin > getWidth()) {
				wh[0] = getWidth() - child.getLayoutParams().leftMargin - child.getLayoutParams().rightMargin;
			}
			if (wh[0] < child.getMinWidth()) wh[0] = child.getMinWidth();
		}
		if (child.getLayoutParams().height < 0) {
			if (wh[1] + child.getLayoutParams().topMargin + child.getLayoutParams().bottomMargin > getWidth()) {
				wh[1] = getHeight() - child.getLayoutParams().topMargin - child.getLayoutParams().bottomMargin;
			}
			if (wh[1] < child.getMinHeight()) wh[1] = child.getMinHeight();
		}
		return wh;
	}
	
	protected int getChildUsableWidth(View child) {
		return mWidth - child.getLayoutParams().leftMargin - child.getLayoutParams().rightMargin;
	}
	
	protected int getChildUsableHeight(View child) {
		return mHeight - getTop() + child.getLayoutParams().topMargin - getTop() + child.getLayoutParams().bottomMargin;
	}
	
    /**
     * Runs the layout animation. Calling this method triggers a relayout of
     * this view group.
     */
    public void startLayoutAnimation() {
        if (mLayoutAnimationController != null) {
            mGroupFlags |= FLAG_RUN_ANIMATION;
            invalidate();
//            requestLayout();
        }
    }

    /**
     * Schedules the layout animation to be played after the next layout pass
     * of this view group. This can be used to restart the layout animation
     * when the content of the view group changes or when the activity is
     * paused and resumed.
     */
    public void scheduleLayoutAnimation() {
        mGroupFlags |= FLAG_RUN_ANIMATION;
    }

    /**
     * Sets the layout animation controller used to animate the group's
     * children after the first layout.
     *
     * @param controller the animation controller
     */
    public void setLayoutAnimation(LayoutAnimationController controller) {
        mLayoutAnimationController = controller;
        if (mLayoutAnimationController != null) {
            mGroupFlags |= FLAG_RUN_ANIMATION;
        }
    }
    
    /**
     * Returns the layout animation controller used to animate the group's
     * children.
     *
     * @return the current animation controller
     */
    public LayoutAnimationController getLayoutAnimation() {
        return mLayoutAnimationController;
    }
	
	/**
     * Indicates whether the view group has the ability to animate its children
     * after the first layout.
     *
     * @return true if the children can be animated, false otherwise
     */
    protected boolean canAnimate() {
        return mLayoutAnimationController != null;
    }
    
    private void bindLayoutAnimation(View child) {
        Animation a = mLayoutAnimationController.getAnimationForView(child);
        child.setAnimation(a);
    }
    
    /**
     * Subclasses should override this method to set layout animation
     * parameters on the supplied child.
     *
     * @param child the child to associate with animation parameters
     * @param params the child's layout parameters which hold the animation
     *        parameters
     * @param index the index of the child in the view group
     * @param count the number of children in the view group
     */
    protected void attachLayoutAnimationParameters(View child, LayoutParams params, int index, int count) {
        LayoutAnimationController.AnimationParameters animationParams = params.layoutAnimationParameters;
        if (animationParams == null) {
            animationParams = new LayoutAnimationController.AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }

        animationParams.count = count;
        animationParams.index = index;
    }
    
    /**
     * Specifies the animation listener to which layout animation events must
     * be sent. Only
     * {@link android.view.animation.Animation.AnimationListener#onAnimationStart(Animation)}
     * and
     * {@link android.view.animation.Animation.AnimationListener#onAnimationEnd(Animation)}
     * are invoked.
     *
     * @param animationListener the layout animation listener
     */
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }
    
    public void bringChildToFront(View child) {
        int index = indexOfChild(child);
        if (index >= 0) {
        	View view = mContainer.get(index);
        	mContainer.remove(index);
        	mContainer.add(view);
        }
    }
    
    public int indexOfChild(View child) {
        for (int i = 0; i < mContainer.size(); i++) {
        	View view = mContainer.get(i);
            if (view == child) {
                return i;
            }
        }
        return -1;
    }
    
    public void removeViewAt(int index) {
    	mContainer.get(index).mParent = null;
    	mContainer.remove(index);
        invalidate();
    }
	
	@Override
	public boolean isLayoutRequested() {
		return false;
	}

	@Override
	public void requestTransparentRegion(View child) {
	}

	@Override
	public void invalidateChild(View child, Rect r) {
		child.invalidate();
	}

	@Override
	public ViewParent invalidateChildInParent(int[] location, Rect r) {
		return null;
	}

	@Override
	public void requestChildFocus(View child, View focused) {
	}

	@Override
	public void recomputeViewAttributes(View child) {
	}

	@Override
	public void clearChildFocus(View child) {
	}

	@Override
	public boolean getChildVisibleRect(View child, Rect r, Point offset) {
		return false;
	}

	@Override
	public View focusSearch(View v, int direction) {
		return null;
	}

	@Override
	public void focusableViewAvailable(View v) {
	}

	@Override
	public boolean showContextMenuForChild(View originalView) {
		return false;
	}

	@Override
	public void createContextMenu(ContextMenu menu) {
	}

	@Override
	public void childDrawableStateChanged(View child) {
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle,
			boolean immediate) {
		return false;
	}

	@Override
	public void childHasTransientStateChanged(View child,
			boolean hasTransientState) {
	}

	@Override
	public void requestFitSystemWindows() {
	}

	@Override
	public ViewParent getParentForAccessibility() {
		return null;
	}

	@Override
	public void childAccessibilityStateChanged(View child) {
	}

	@Override
	public boolean canResolveLayoutDirection() {
		return false;
	}

	@Override
	public boolean isLayoutDirectionResolved() {
		return false;
	}

	@Override
	public int getLayoutDirection() {
		return View.LAYOUT_DIRECTION_LTR;
	}

	@Override
	public boolean canResolveTextDirection() {
		return false;
	}

	@Override
	public boolean isTextDirectionResolved() {
		return false;
	}

	@Override
	public int getTextDirection() {
		return 0;
	}

	@Override
	public boolean canResolveTextAlignment() {
		return false;
	}

	@Override
	public boolean isTextAlignmentResolved() {
		return false;
	}

	@Override
	public int getTextAlignment() {
		return 0;
	}

	/**
     * {@inheritDoc}
     */
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return  p != null;
    }
	
	/**
     * Returns a new set of layout parameters based on the supplied attributes set.
     *
     * @param attrs the attributes to build the layout parameters from
     *
     * @return an instance of {@link android.view.ViewGroup.LayoutParams} or one
     *         of its descendants
     */
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
    	LayoutParams params = new LayoutParams(getContext(), attrs);
        return params;
    }

    /**
     * Returns a safe set of layout parameters based on the supplied layout params.
     * When a ViewGroup is passed a View whose layout params do not pass the test of
     * {@link #checkLayoutParams(android.view.ViewGroup.LayoutParams)}, this method
     * is invoked. This method should return a new set of layout params suitable for
     * this ViewGroup, possibly by copying the appropriate attributes from the
     * specified set of layout params.
     *
     * @param p The layout parameters to convert into a suitable set of layout parameters
     *          for this ViewGroup.
     *
     * @return an instance of {@link android.view.ViewGroup.LayoutParams} or one
     *         of its descendants
     */
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p;
    }

    /**
     * Returns a set of default layout parameters. These parameters are requested
     * when the View passed to {@link #addView(View)} has no layout parameters
     * already set. If null is returned, an exception is thrown from addView.
     *
     * @return a set of default layout parameters or null
     */
    protected LayoutParams generateDefaultLayoutParams() {
    	LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return params;
    }

    public static class MarginLayoutParams extends LayoutParams {

		public MarginLayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public MarginLayoutParams(int left, int top, int right, int bottom) {
			super(left, top, right, bottom);
		}

		public MarginLayoutParams(int width, int height) {
			super(width, height);
		}

		public MarginLayoutParams(LayoutParams source) {
			super(source);
		}
    }
    
	public static class LayoutParams {
		
        @SuppressWarnings({"UnusedDeclaration"})
        @Deprecated
        public static final int 													FILL_PARENT = -1;
        public static final int 													MATCH_PARENT = -1;
        public static final int 													WRAP_CONTENT = -2;

        public int 																	width;
        public int 																	height;
        
        public int 																	left;
        public int 																	top;
        public int 																	right;
        public int 																	bottom;
        public int 																	leftMargin;
        public int 																	topMargin;
        public int 																	rightMargin;
        public int 																	bottomMargin;
        public AttributeSet 														mAttrSet;
        private Context 															mContext;

        /**
         * Used to animate layouts.
         */
        public LayoutAnimationController.AnimationParameters 						layoutAnimationParameters;
        protected LayoutObserver													mLayoutObserver;														
        
        public LayoutParams(Context c, AttributeSet attrs) {
        	mContext = c;
        	mAttrSet = attrs;
        	
        	// margin
        	leftMargin = mAttrSet.getDimensionPixelSize(thahn.java.agui.R.attr.View_layout_marginLeft, 0);
        	topMargin = mAttrSet.getDimensionPixelSize(thahn.java.agui.R.attr.View_layout_marginTop, 0);
        	rightMargin = mAttrSet.getDimensionPixelSize(thahn.java.agui.R.attr.View_layout_marginRight, 0);
        	bottomMargin = mAttrSet.getDimensionPixelSize(thahn.java.agui.R.attr.View_layout_marginBottom, 0);
        	
        	int margin = mAttrSet.getDimensionPixelSize(thahn.java.agui.R.attr.View_layout_margin, 0);
        	if (margin != 0) {
        		leftMargin = margin;
        		topMargin = margin;
        		rightMargin = margin;
        		bottomMargin = margin;
        	}
        	
        	// width & height
        	String attrWidth = mAttrSet.getString(thahn.java.agui.R.attr.View_layout_width, ViewAttribute.WRAP_CONTENT);
        	String attrHeight = mAttrSet.getString(thahn.java.agui.R.attr.View_layout_height, ViewAttribute.WRAP_CONTENT);
        	if (attrWidth.equals(ViewAttribute.WRAP_CONTENT)) {
        		width = WRAP_CONTENT;
        	} else if (attrWidth.equals(ViewAttribute.MATCH_PARENT) || attrWidth.equals(ViewAttribute.FILL_PARENT)) {
        		width = MATCH_PARENT;
        	} else {
        		width = mAttrSet.getDimensionPixelSize(thahn.java.agui.R.attr.View_layout_width, 0);//Integer.parseInt(attrWidth);
        		left = leftMargin;
            	right = left + width;
        	}
        	
        	if (attrHeight.equals(ViewAttribute.WRAP_CONTENT)) {
        		height = WRAP_CONTENT;
        	} else if (attrHeight.equals(ViewAttribute.MATCH_PARENT) || attrHeight.equals(ViewAttribute.FILL_PARENT)) {
        		height = MATCH_PARENT;
        	} else {
        		height = mAttrSet.getDimensionPixelSize(thahn.java.agui.R.attr.View_layout_height, 0);//Integer.parseInt(attrHeight);
        		top = topMargin;
        		bottom = top + height;
        	}
        }

        public LayoutParams(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public LayoutParams(LayoutParams source) {
            this.width = source.width;
            this.height = source.height;
            this.left = source.left;
            this.top = source.top;
            this.right = source.right;
            this.bottom = source.bottom;
            this.leftMargin = source.leftMargin;
            this.topMargin = source.topMargin;
            this.rightMargin = source.rightMargin;
            this.bottomMargin = source.bottomMargin;
            this.mContext = source.mContext;
            this.mAttrSet = source.mAttrSet;
        }
        
        public LayoutParams(int left, int top, int right, int bottom) {
        	setLayout(left, top, right, bottom);
        	this.width = right - left;
        	this.height = bottom - top;
		}

		void registerLayoutObserver(LayoutObserver o) {
        	mLayoutObserver = o;
        }
        
        public String debug(String output) {
            return output + "ViewGroup.LayoutParams={ width="
                    + sizeToString(width) + ", height=" + sizeToString(height) + " }";
        }

        protected static String sizeToString(int size) {
            if (size == WRAP_CONTENT) {
                return "wrap-content";
            }
            if (size == MATCH_PARENT) {
                return "match-parent";
            }
            return String.valueOf(size);
        }
        
        public void setLayout(int left, int top, int right, int bottom) {
        	this.left = left;
        	this.top = top;
        	this.right = right;
        	this.bottom = bottom;
        	
        	if (this.width > 0) this.width = right - left;
        	if (this.height > 0) this.height = bottom - top;
        	
        	if (mLayoutObserver != null) mLayoutObserver.onLayoutChanged(left, top, right, bottom);
        }
        
        protected void recycleAttrs() {
        	mAttrSet.recycle();
        }
        
        public void setMargins(int left, int top, int right, int bottom) {
            leftMargin = left;
            topMargin = top;
            rightMargin = right;
            bottomMargin = bottom;
        }
        
        public int getLeft() {
			return left;
		}

		public int getTop() {
			return top;
		}

		public int getRight() {
			return right;
		}

		public int getBottom() {
			return bottom;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		for (View child : getChildren()) {
			builder.append("\n").append(child.toString());
		}
		return builder.toString();
	}
}
