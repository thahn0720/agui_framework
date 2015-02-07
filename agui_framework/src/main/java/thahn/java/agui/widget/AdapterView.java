package thahn.java.agui.widget;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import thahn.java.agui.animation.Animation;
import thahn.java.agui.annotation.AguiDifferent;
import thahn.java.agui.app.Context;
import thahn.java.agui.exception.NotSupportedException;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.widget.ScrollBar.OnThumbListener;
import thahn.java.agui.widget.ScrollHelper.ScrollCondition;

import com.google.common.collect.Lists;

/**
 * An AdapterView is a view whose children are determined by an {@link Adapter}.
 *
 * <p>
 * See {@link ListView}, {@link GridView}, {@link Spinner} and
 *      {@link Gallery} for commonly used subclasses of AdapterView.
 */
public abstract class AdapterView<T extends Adapter> extends ViewGroup implements ScrollCondition {

	/**
     * Represents an invalid position. All valid positions are in the range 0 to 1 less than the
     * number of items in the current adapter.
     */
    public static final int 											INVALID_POSITION 		= -1;
    public static final int 											HEADER_POSITION	 		= 0;
    public static final int 											LIST_POSITION 			= 1;
    public static final int 											FOOTER_POSITION 		= 2;
	
	/*package*/ View[]													mConvertViewContainer;
	
	/**
     * The listener that receives notifications when an item is clicked.
     */
	/*package*/ OnItemClickListener 									mOnItemClickListener;
	/*package*/ OnItemLongClickListener									mOnItemLongClickListener;
	/*package*/ OnItemSelectedListener									mOnItemSelectedListener;
	/*package*/ ScrollBar												mScrollBar;
	/*package*/ ScrollHelper											mScrollHelper;
	/*package*/ boolean													canScrolled				= false;
	/*package*/ boolean													isScrollPressed			= false;
	/*package*/ boolean													isMadeByXml;
	
	/**
     * The drawable used to draw the selector
     */
	/*package*/ Drawable 												mSelector;
	/*package*/ int	 													mSelectorPosition;
	/*package*/ int	 													mPrevSelectorPosition 	= INVALID_POSITION;
	/*package*/ int	 													mWhichPressed			= INVALID_POSITION;
	/*package*/ int	 													mWhichPrevPressed		= INVALID_POSITION;
	protected ItemHeights 												mItemHeights;
	
	protected ArrayList<FixedViewInfo> 									mHeaderViewInfos 		= Lists.newArrayList();
	protected ArrayList<FixedViewInfo> 									mFooterViewInfos 		= Lists.newArrayList();
	
    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    public class FixedViewInfo {
        /** The view to add to the list */
        public View view;
        /** The data backing the view. This is returned from {@link ListAdapter#getItem(int)}. */
        public Object data;
        /** <code>true</code> if the fixed view should be selectable in the list */
        public boolean isSelectable;
    }
	
	public AdapterView(Context context) {
		super(context);
	}

	public AdapterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdapterView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}
	
	@Override
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		Drawable selector = attrs.getDrawable(thahn.java.agui.R.attr.AdapterView_listSelector, thahn.java.agui.R.drawable.list_selector);
        if (selector != null) {
            setSelector(selector);
        }
	}

	public abstract void setAdapter(BaseAdapter adapter); 
	public abstract BaseAdapter getAdapter();
	
	/**
     * Gets the data associated with the specified position in the list.
     *
     * @param position Which data to get
     * @return The data associated with the specified position in the list
     */
    public Object getItemAtPosition(int position) {
    	BaseAdapter adapter = getAdapter();
        return (adapter == null || position < 0) ? null : adapter.getItem(position);
    }
	
	@Override
	public final void addView(View v) {
		throw new NotSupportedException(); 
	}
	
	@Override
	public final void addViewInternal(View v) {
		throw new NotSupportedException(); 
	}
	
	protected void setScrollbar(Context context, View parent, int avgItemHeight, int itemAllCount, int scrollAmount, OnThumbListener onThumbListener) {
		if (mScrollBar == null) {
			mScrollBar = new VerticalScrollBar(context, parent, avgItemHeight, itemAllCount, scrollAmount);
			mScrollBar.setOnThumbListener(onThumbListener);
			mScrollBar.onFirstScrolled();
			mScrollHelper = new ScrollHelper(this, mScrollBar, this);
		} else {
			mScrollBar.setThumbSize(parent, avgItemHeight * itemAllCount);
		}
	}

	protected void onFirstScrolled() {
		if (mScrollBar != null) mScrollBar.onFirstScrolled();
	}
	
	protected void onLastScrolled() {
		if (mScrollBar != null) mScrollBar.onLastScrolled();
	}
	
	protected void scrollScrollBar(int scrollX, int scrollY, int amount) {
		if (mScrollBar != null) mScrollBar.onScroll(scrollX, scrollY, amount);
	}
	
	void setThumbSize(int listHeight) {
		if (mScrollBar != null) mScrollBar.setThumbSize(listHeight);
	}
	
	@Override
	public boolean contains(int x, int y) {
		boolean ret = false;
		if (mLayoutParam.left + mPaddingLeft <= x && mLayoutParam.right - mPaddingRight  >= x && mLayoutParam.top + mPaddingTop  <= y && mLayoutParam.bottom - mPaddingBottom  >= y) {
			ret = true;
		}
		return ret;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = super.onTouchEvent(event);
		if (ret) {
			return true;
		} else if (mConvertViewContainer == null) {
			return false;
		}
			
		int action = event.getAction();
		int x = event.getX();
		int y = event.getY();
		
		// scroll
		if (canScrolled && mScrollBar != null) {
			if (isScrollPressed) { 	// move
				ret = mScrollBar.onTouchEvent(event);
			} else {				// down
				if (mScrollBar.contains(event.getX(), event.getY())) {
					isMadeByXml = true;
					isScrollPressed = true;
					ret = mScrollBar.onTouchEvent(event);
				}
			}
			if (isScrollPressed && event.getAction() == MotionEvent.ACTION_UP) {
				isScrollPressed = false;
			} else if (isScrollPressed) {
				return true;
			}
		}
		
		// child
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setAndFindTouchView(x, y);
			if (mPressedView != null) {
				mSelector.setState(mSelector.getState(Drawable.STATE_ACTIVATED_TRUE)?
						Drawable.STATE_ACTIVATED_FALSE:Drawable.STATE_ACTIVATED_TRUE);
				mSelector.setState(Drawable.STATE_PRESSED_TRUE);
				mSelector.setBounds(mPressedView.getLeft(), mPressedView.getTop()
									, mPressedView.getRight(), mPressedView.getBottom());
				
				if (isLongClickable() && !mPressedView.isLongClickable()) {
					mPressedView.setLongClickable(true);
				}
				ret = mPressedView.onTouchEvent(event);
				
				if (mBackground == null && mPressedView.getBackground() == null) {
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mPressedView != null) {
				if (mPressedView.contains(x, y)) {
					ret = mPressedView.onTouchEvent(event);
				} else {
					mSelector.setState(Drawable.STATE_PRESSED_FALSE);
					mPressedView.setPressed(false);
					mPressedView = null;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mSelector.setState(Drawable.STATE_PRESSED_FALSE);
			invalidate();
			
			if (mPressedView != null) {
				ret = mPressedView.onTouchEvent(event);
				mPressedView = null;
			}
			
			if (mBackground == null && mPressedView != null && mPressedView.getBackground() == null) {
				invalidate();
			}
			break;
		case MotionEvent.ACTION_CLICK:
			setAndFindTouchView(x, y);
			if (mPressedView != null) {
				ret = mPressedView.onTouchEvent(event);
				performItemClick(mPressedView, mSelectorPosition, mPressedView.getId());
				mPrevSelectorPosition = mSelectorPosition;
				mWhichPrevPressed = mWhichPressed;
				mPressedView = null;
			}
		}
		
		return ret;
	}
	
	/**
	 * then, set the touched view in 'mPressedView'
	 * @param x
	 * @param y
	 */
	private void setAndFindTouchView(int x, int y) {
		// header
		for (int i=0;i<mHeaderViewInfos.size();++i) {
			View v = mHeaderViewInfos.get(i).view;
			if (v.contains(x, y)) {
				mWhichPressed = HEADER_POSITION;
				mSelectorPosition = i;
				mPressedView = v;
				break;
			}
		}
		// list item
		int padPosition = mHeaderViewInfos.size();
		if (mPressedView == null) {
			for (int i=0;i<mConvertViewContainer.length;++i) {
				View v = mConvertViewContainer[i];
				if (v.contains(x, y)) {
					mWhichPressed = LIST_POSITION;
					mSelectorPosition = i + padPosition;
					mPressedView = v;
					break;
				}
			}
		}
		// footer
		padPosition += mConvertViewContainer.length;
		if (mPressedView == null) {
			for (int i=0;i<mFooterViewInfos.size();++i) {
				View v = mFooterViewInfos.get(i).view;
				if (v.contains(x, y)) {
					mWhichPressed = FOOTER_POSITION;
					mSelectorPosition = i + padPosition;
					mPressedView = v;
					break;
				}
			}
		}
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
		if (mSelector != null) { 
			mSelector.draw(g);
		}
		// g.clearRect(getLeft(), getTop(), getWidth(), getHeight());
		// g.clipRect(getLeft(), getTop(), getWidth(), getHeight());
		// g.setClip(getLeft(), getTop(), getWidth(), getHeight());
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
	
    public final OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }
    
    /**
     * Call the OnItemClickListener, if it is defined.
     *
     * @param view The view within the AdapterView that was clicked.
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     * @return True if there was an assigned OnItemClickListener that was
     *         called, false otherwise is returned.
     */
    public boolean performItemClick(View view, int position, long id) {
        if (mOnItemClickListener != null) {
//            playSoundEffect(SoundEffectConstants.CLICK);
            mOnItemClickListener.onItemClick(this, view, position, id);
            return true;
        }

        return false;
    }
    
    @Override
	public boolean performLongClick() {
    	boolean handled = super.performLongClick() 
    			|| performLongPress(mPressedView, mSelectorPosition, mPressedView.getId());
    	if (mOnItemLongClickListener != null || mPressedView.hasOnLongClickListeners()) {
    		mSelector.setState(Drawable.STATE_PRESSED_FALSE);
			invalidate();
    	}
		return handled;
	}

	@AguiDifferent
    /*package*/ boolean performLongPress(final View child, final int longPressPosition, final long longPressId) {
        if (mOnItemLongClickListener != null) {
//          playSoundEffect(SoundEffectConstants.CLICK);
        	mOnItemLongClickListener.onItemLongClick(this, child, longPressPosition, longPressId);
        	return true;
        }

      return false;    	
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked and held
     *
     * @param listener The callback that will run
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        mOnItemLongClickListener = listener;
    }

    /**
     * @return The callback to be invoked with an item in this AdapterView has
     *         been clicked and held, or null id no callback as been set.
     */
    public final OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }
    
    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been selected.
     *
     * @param listener The callback that will run
     */
//    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
//        mOnItemSelectedListener = listener;
//    }
//
//    public final OnItemSelectedListener getOnItemSelectedListener() {
//        return mOnItemSelectedListener;
//    }
    
	@Override
	public void inheritParentAnimation(Animation animation) {
		super.inheritParentAnimation(animation);
		
		if (mConvertViewContainer != null) {
			int size = mConvertViewContainer.length;
			for (int i=0;i<size;++i) {
				mConvertViewContainer[i].inheritParentAnimation(animation);
			}
		}
	}

	@Override
	public void setAnimation(Animation animation) {
		super.setAnimation(animation);
		
		int size = mConvertViewContainer.length;
		Animation ani = getAnimation();
		for (int i=0;i<size;++i) {
			mConvertViewContainer[i].inheritParentAnimation(ani);
		}
	}
	
    /**
     * Set a Drawable that should be used to highlight the currently selected item.
     *
     * @param resID A Drawable resource to use as the selection highlight.
     *
     * @attr ref android.R.styleable#AbsListView_listSelector
     */
    public void setSelector(int resID) {
        setSelector(mContext.getResources().getDrawable(resID));
    }

    public void setSelector(Drawable sel) {
        if (mSelector != null) {
//            mSelector.setCallback(null);
//            unscheduleDrawable(mSelector);
        }
        mSelector = sel;
//        Rect padding = new Rect();
//        sel.getPadding(padding);
//        mSelectionLeftPadding = padding.left;
//        mSelectionTopPadding = padding.top;
//        mSelectionRightPadding = padding.right;
//        mSelectionBottomPadding = padding.bottom;
//        sel.setCallback(this);
//        updateSelectorState();
    }
    
	void shrinkSize(int left, int top, int right, int bottom) {
		int width = right - left;
		int height = bottom - top;
		boolean isChanged = false;
		LayoutParams params = getLayoutParams();
		if (params.width == LayoutParams.WRAP_CONTENT && getWidth() > width) {
			isChanged = true;
		} else {
			width = getWidth();
		}
		if (params.height == LayoutParams.WRAP_CONTENT && getHeight() > height) {
			isChanged = true;
		} else {
			height = getHeight();
		}
		if (isChanged) {
			onLayout(true, left, top, left + width, top + height);
		}
	}
	
    /**
     * Returns the number of header views in the list. Header views are special views
     * at the top of the list that should not be recycled during a layout.
     *
     * @return The number of header views, 0 in the default implementation.
     */
    /*package*/ int getHeaderViewsCount() {
        return mHeaderViewInfos.size();
    }

    /**
     * Returns the number of footer views in the list. Footer views are special views
     * at the bottom of the list that should not be recycled during a layout.
     *
     * @return The number of footer views, 0 in the default implementation.
     */
    /*package*/ int getFooterViewsCount() {
        return mFooterViewInfos.size();
    }
    
    protected View getView(int whichArea, int position) {
    	View view = null;
    	switch (whichArea) {
		case HEADER_POSITION:
			view = mHeaderViewInfos.get(position).view;
			break;
		case LIST_POSITION:
			view = mConvertViewContainer[position];
			break;
		case FOOTER_POSITION:
			view = mFooterViewInfos.get(position).view;
			break;
		}
    	return view;
    }
	
    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * view has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in this view has been
         * clicked and held.
         *
         * Implementers can call getItemAtPosition(position) if they need to access
         * the data associated with the selected item.
         *
         * @param parent The AbsListView where the click happened
         * @param view The view within the AbsListView that was clicked
         * @param position The position of the view in the list
         * @param id The row id of the item that was clicked
         *
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id);
    }

    /**
     * Interface definition for a callback to be invoked when
     * an item in this view has been selected.
     */
    public interface OnItemSelectedListener {
        /**
         * <p>Callback method to be invoked when an item in this view has been
         * selected. This callback is invoked only when the newly selected
         * position is different from the previously selected position or if
         * there was no selected item.</p>
         *
         * Impelmenters can call getItemAtPosition(position) if they need to access the
         * data associated with the selected item.
         *
         * @param parent The AdapterView where the selection happened
         * @param view The view within the AdapterView that was clicked
         * @param position The position of the view in the adapter
         * @param id The row id of the item that is selected
         */
        void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        /**
         * Callback method to be invoked when the selection disappears from this
         * view. The selection can disappear for instance when touch is activated
         * or when the adapter becomes empty.
         *
         * @param parent The AdapterView that now contains no selected item.
         */
        void onNothingSelected(AdapterView<?> parent);
    }
    
	/*package*/ class ItemHeights {
		private int															totalHeight;
		private int[] 														itemHeights;
		private int[]														headerHeights;
		private int[]														footerHeights;
		
		public ItemHeights(int itemCount, int headerCount, int footerCount) {
			itemHeights = new int[itemCount];
			headerHeights = new int[headerCount];
			footerHeights = new int[footerCount];
			Arrays.fill(itemHeights, -1);
			Arrays.fill(headerHeights, -1);
			Arrays.fill(footerHeights, -1);
			totalHeight = 0;
		}
		
		public int getItemHeight(int index) {
			return itemHeights[index - headerHeights.length];
		}
		
		public int getHeaderHeight(int index) {
			return headerHeights[index];
		}
		
		public int getFooterHeight(int index) {
			return footerHeights[index];
		}
		
		public int getTotalHeight() {
			return totalHeight;
		}
		
		public void setItemHeight(int index, int height) {
			setValue(itemHeights, index, height);
		}
		
		public void setHeaderHeight(int index, int height) {
			setValue(headerHeights, index, height);
		}
		
		public void setFooterHeight(int index, int height) {
			setValue(footerHeights, index, height);
		}
		
		private void setValue(int[] container, int index, int height) {
			if (container[index] == -1) {
				container[index] = height;
				totalHeight += height;
			} else if (!(container[index] == height)) {
				totalHeight -= container[index];
				totalHeight += height;
				container[index] = height;
			}
		}
		
		public int getHeaderHeight() {
			int headerHeight = 0;
			for (int i = 0; i < headerHeights.length; i++) {
				headerHeight += headerHeights[i];
			}
			return headerHeight;
		}
		
		public int getFooterHeight() {
			int footerHeight = 0;
			for (int i = 0; i < footerHeights.length; i++) {
				footerHeight += footerHeights[i];
			}
			return footerHeight;
		}
		
		public void fillItemHeight(int height) {
			Arrays.fill(itemHeights, height);
			int headerHeight = 0;
			int footerHeight = 0;
			for (int i = 0; i < headerHeights.length; i++) {
				headerHeight += headerHeights[i];
			}
			for (int i = 0; i < footerHeights.length; i++) {
				footerHeight += footerHeights[i];
			}
			totalHeight = itemHeights.length * height + headerHeight + footerHeight;
		}
	}
}
