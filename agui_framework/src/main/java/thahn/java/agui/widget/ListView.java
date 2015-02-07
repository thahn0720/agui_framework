package thahn.java.agui.widget;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thahn.java.agui.Global;
import thahn.java.agui.app.ApplicationSetting;
import thahn.java.agui.app.Context;
import thahn.java.agui.database.DataSetObserver;
import thahn.java.agui.graphics.Color;
import thahn.java.agui.graphics.ColorDrawable;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.MouseWheelEvent;
import thahn.java.agui.view.View;
import thahn.java.agui.widget.ScrollBar.OnThumbListener;

import com.google.common.base.Joiner;

// TODO : when dataset is deleted, modify itemheight
public class ListView extends AbsListView {
	
	private BaseAdapter												mAdapter;
	private Drawable												mDivider;
	private int														mDividerHeight;
	private int														mItemCountInScreen;
	private int														mStartPosition;
	private int 													mScrollGap 						= 	0;
	
	/**
	 * true : recycle is possible<br>
	 * false : recycle is not possible. rearrange<br>
	 */
	private boolean[]												isScraped;
    
	public ListView(Context context) {
		this(context, new AttributeSet(context));
		isMadeByXml = false;
	}

	public ListView(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_LIST_VIEW_CODE);
		isMadeByXml = true;
	}
	
	public ListView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
		isMadeByXml = true;
	}
	
	@Override
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		Drawable divider = attrs.getDrawable(thahn.java.agui.R.attr.ListView_divider, -1);
        if (divider == null) {
        	divider = ColorDrawable.load(Color.LTGRAY);
            setDivider(divider);
        } else {
        	// If a divider is specified use its intrinsic height for divider height
        	setDivider(divider);
        }
        
        // Use the height specified, zero being the default
        final int dividerHeight = attrs.getDimensionPixelSize(thahn.java.agui.R.attr.ListView_dividerHeight, 2);
        if (dividerHeight != 0) {
            setDividerHeight(dividerHeight);
        }
	}

	@Override
	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mObserver);
		
		initAdaptor();
	}
	
	@Override
	public void arrange() {
//		super.arrange();
		Log.d("arrange");
		if (mAdapter == null) {
			int width = getWidth();
			int height = getHeight();
			if (mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
				width = 0;
			}
			if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
				height = 0;
			}
			onLayout(true, getLeft(), getTop(), getLeft() + width, getTop() + height);
			return ;
		}
		// FIXME : performance 떨어트림.
		if (!isMadeByXml) {
			initAdaptor();	
		}
	}
	
	private void initAdaptor() {
		if (getParent() == null || mAdapter == null) return ;
		
		mScrollGap = 0;
		mStartPosition = 0;
		mItemCountInScreen = 0;
		canScrolled = false;
		
		List<View> viewContainer = new ArrayList<>();
		int left = getDrawingLeft();
		int top = getTop() + getPaddingTop() + mScrollGap;
		int right = 0;
		int bottom = 0;

		int dataCount = mAdapter.getCount();
		mItemHeights = new ItemHeights(dataCount, mHeaderViewInfos.size(), mFooterViewInfos.size());
		// header
		for (int i=0;i<mHeaderViewInfos.size();++i) {
			FixedViewInfo viewInfo = (FixedViewInfo) mHeaderViewInfos.get(i);
			viewInfo.view.onMeasure(getDrawingWidth(), getDrawingHeight());
			viewInfo.view.onPostMeasure(getDrawingWidth(), getDrawingHeight());
			int headerWidth = viewInfo.view.getWidth();
			int headerHeight = viewInfo.view.getHeight();
			right = left + headerWidth + viewInfo.view.getLayoutParams().leftMargin;//convertView.getWidth();
			bottom = top + headerHeight + viewInfo.view.getLayoutParams().topMargin;//convertView.getHeight();
			viewInfo.view.onLayout(true, left, top, right, bottom);
			viewInfo.view.arrange();
			//
			if (mDivider != null && mDividerHeight > 0) {
				bottom += mDividerHeight;
			}
			
			top = bottom;
			
			mItemHeights.setHeaderHeight(i, viewInfo.view.getHeight());
		}
		// list item
		int headerHeight = mItemHeights.getHeaderHeight();
		for (int i=0;i<dataCount;++i) {
			View convertView = null;
			convertView = mAdapter.getView(i, convertView, this);
//			convertView.isClip = false; 
			convertView.onMeasure(getDrawingWidth(), getDrawingHeight());
			convertView.onPostMeasure(getDrawingWidth(), getDrawingHeight());
			int[] wh = measureChildSize(convertView);
			right = left + wh[0] + convertView.getLayoutParams().leftMargin;//convertView.getWidth();
			bottom = top + wh[1] + convertView.getLayoutParams().topMargin;//convertView.getHeight();
			convertView.onLayout(true, left, top, right, bottom);
			convertView.arrange();
			bottom = top + convertView.getHeight();
			//
			if (mDivider != null && mDividerHeight > 0) {
				bottom += mDividerHeight;
			}
			//
			top = bottom + convertView.getLayoutParams().bottomMargin;
			viewContainer.add(convertView);
			wh[0] = convertView.getWidth();
			wh[1] = convertView.getHeight();
			if (i==0) {
				mItemHeights.fillItemHeight(wh[1]);
			} else {
				mItemHeights.setItemHeight(i, wh[1]);
			}
			if (bottom - headerHeight > getDrawingTop() + getDrawingHeight() + convertView.getHeight()) {
				canScrolled = true;
				mItemCountInScreen = i + 1;// i는 시작이 0이라서
				setScrollbar(mContext, (View) this, mItemHeights.getItemHeight(mHeaderViewInfos.size()), dataCount, ApplicationSetting.SCROLL_AMOUNT, onThumbListener);
				setThumbSize(mItemHeights.getTotalHeight());
				break;
			}
		}
		// footer
		for (int i=0;i<mFooterViewInfos.size();++i) {
			FixedViewInfo viewInfo = (FixedViewInfo) mFooterViewInfos.get(i);
			viewInfo.view.onMeasure(getDrawingWidth(), getDrawingHeight());
			viewInfo.view.onPostMeasure(getDrawingWidth(), getDrawingHeight());
			int footerWidth = viewInfo.view.getWidth();
			int footerHeight = viewInfo.view.getHeight();
			right = left + footerWidth + viewInfo.view.getLayoutParams().leftMargin;//convertView.getWidth();
			bottom = top + footerHeight + viewInfo.view.getLayoutParams().topMargin;//convertView.getHeight();
			viewInfo.view.onLayout(true, left, top, right, bottom);
			viewInfo.view.arrange();
			//
			if (mDivider != null && mDividerHeight > 0) {
				bottom += mDividerHeight;
			}
			
			top = bottom;
			
			mItemHeights.setFooterHeight(i, viewInfo.view.getHeight());
		}
		//
		shrinkSize(getLeft(), getTop(), right, bottom);
		
		if (mItemCountInScreen == 0) {
			mItemCountInScreen = dataCount;
		}
		
		mConvertViewContainer = new View[mItemCountInScreen];
		isScraped = new boolean[mItemCountInScreen];
		for (int i=mStartPosition;i<mStartPosition+mItemCountInScreen;++i) {
			mConvertViewContainer[i] = viewContainer.get(i);
			isScraped[i] = false;
		}
	}
	
	@Override
	public BaseAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		int left = getDrawingLeft();
		int top = getTop() + getPaddingTop() + mScrollGap;
		int right = 0;
		int bottom = 0;
		int headerBottom = 0;
		int footerBottom = 0;
		boolean isScreenOver = false;
		if (mAdapter != null) {
			// header
			if (mStartPosition < mHeaderViewInfos.size()) {
				for (int i=mStartPosition;i<mHeaderViewInfos.size();++i) {
					FixedViewInfo viewInfo = (FixedViewInfo) mHeaderViewInfos.get(i);
					int width = viewInfo.view.getWidth();
					int height = viewInfo.view.getHeight();
					right = left + width + viewInfo.view.getLayoutParams().leftMargin;//convertView.getWidth();
					bottom = top + height + viewInfo.view.getLayoutParams().topMargin;//convertView.getHeight();
					
					viewInfo.view.onLayout(true, left, top, right, bottom);
					viewInfo.view.draw(g);
					
					if (mDivider != null && mDividerHeight > 0) {
						mDivider.setBounds(getLeft(), bottom, getRight(), bottom + mDividerHeight);
						mDivider.draw(g);
						bottom += mDividerHeight;
					}
					
					top = bottom;
				}
				headerBottom = bottom;
			}
			// list item
			int startPosition = mStartPosition;
			if (mStartPosition != 0 && mHeaderViewInfos.size() != 0) {
				startPosition -= mHeaderViewInfos.size();
			}
			
			for (int i = startPosition; i < startPosition+mItemCountInScreen; i++) {
				int index = i % mItemCountInScreen;
				if (i >= mAdapter.getCount()) break; 
				if (isScraped[index]) {
					mConvertViewContainer[index] = mAdapter.getView(i, mConvertViewContainer[index], this);
					mConvertViewContainer[index].onMeasure(getDrawingWidth(), getDrawingHeight());
					mConvertViewContainer[index].onPostMeasure(getDrawingWidth(), getDrawingHeight());
					isScraped[index] = false;
				} 
				int[] wh = measureChildSize(mConvertViewContainer[index]);
				right = left + wh[0] + mConvertViewContainer[index].getLayoutParams().leftMargin;
				bottom = top + wh[1] + mConvertViewContainer[index].getLayoutParams().topMargin;
				
				mConvertViewContainer[index].onLayout(true, left, top, right, bottom);
				// TODO : overhead
				mConvertViewContainer[index].arrange();
				mConvertViewContainer[index].draw(g);
				// temp?
				right = left + mConvertViewContainer[index].getWidth() + mConvertViewContainer[index].getLayoutParams().leftMargin;
				bottom = top + mConvertViewContainer[index].getHeight() + mConvertViewContainer[index].getLayoutParams().topMargin;
				//
				if (mDivider != null && mDividerHeight > 0) {
					mDivider.setBounds(getLeft(), bottom, getRight(), bottom + mDividerHeight);
					mDivider.draw(g);
					bottom += mDividerHeight;
				}
				
				top = bottom;
				
				mItemHeights.setItemHeight(i, mConvertViewContainer[index].getHeight());
				setThumbSize(mItemHeights.getTotalHeight());
//								Log.i("draw "+i+" left : "+mConvertViewContainer[index].getLeft()+", top : "+mConvertViewContainer[index].getTop()
//										+", right : "+mConvertViewContainer[index].getRight()+", bottom : "+mConvertViewContainer[index].getBottom()
//										+", width : "+mConvertViewContainer[index].getWidth()+", height : "+mConvertViewContainer[index].getHeight());
				// space is left
				if (i == startPosition+mItemCountInScreen-1) {
					i+=1;
					if (bottom - headerBottom < getTop() + getHeight() + getPaddingTop() + getPaddingBottom()) { // expand
						isScreenOver = true;
						if (i != mItemCountInScreen) {
							mItemCountInScreen = i;
							View[] temp = new View[mItemCountInScreen];
							System.arraycopy(mConvertViewContainer, 0, temp, 0, mConvertViewContainer.length);
							mConvertViewContainer = temp;
							//
							boolean[] tempScraped = new boolean[mItemCountInScreen];
							Arrays.fill(tempScraped, true);
							System.arraycopy(isScraped, 0, tempScraped, 0, isScraped.length);
							tempScraped[i % mItemCountInScreen] = true;
							isScraped = tempScraped;
							//
							invalidate();
							Log.i("mItemCountInScreen is revised : " + mItemCountInScreen);
						}
					} else { // wrap
						isScreenOver = false;
						// stop do draw
						// set unvisible obj to null or true 
					}
				}
			}
			// footer
			if (mStartPosition + mItemCountInScreen >= mAdapter.getCount() + mHeaderViewInfos.size() && !isScreenOver) {
				for (int i=0;i<mFooterViewInfos.size();++i) {
					FixedViewInfo viewInfo = (FixedViewInfo) mFooterViewInfos.get(i);
					int width = viewInfo.view.getWidth();
					int height = viewInfo.view.getHeight();
					right = left + width + viewInfo.view.getLayoutParams().leftMargin;//convertView.getWidth();
					bottom = top + height + viewInfo.view.getLayoutParams().topMargin;//convertView.getHeight();
					//
					viewInfo.view.onLayout(true, left, top, right, bottom);
//					viewInfo.view.arrange();
					viewInfo.view.draw(g);
					//
					if (mDivider != null && mDividerHeight > 0) {
						mDivider.setBounds(getLeft(), bottom, getRight(), bottom + mDividerHeight);
						mDivider.draw(g);
						bottom += mDividerHeight;
					}
					//
					top = bottom;
				}
				footerBottom = bottom;
			}
		}
		//
		if (canScrolled) mScrollBar.draw(g);
	}
	
    /**
     * Sets the drawable that will be drawn between each item in the list. If the drawable does
     * not have an intrinsic height, you should also call {@link #setDividerHeight(int)}
     *
     * @param divider The drawable to use.
     */
    public void setDivider(Drawable divider) {
        if (divider != null) {
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerHeight = 0;
        }
        mDivider = divider;
//        mDividerIsOpaque = divider == null || divider.getOpacity() == PixelFormat.OPAQUE;
//        requestLayout();
        invalidate();
    }
    
    /**
     * Sets the height of the divider that will be drawn between each item in the list. Calling
     * this will override the intrinsic height as set by {@link #setDivider(Drawable)}
     *
     * @param height The new height of the divider in pixels.
     */
    public void setDividerHeight(int height) {
        mDividerHeight = height;
        requestLayout();
        invalidate();
    }
    
    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v The view to add.
     * @param data Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        if (mAdapter != null) {// && ! (mAdapter instanceof HeaderViewListAdapter)) {
            throw new IllegalStateException(
                    "Cannot add header view to list -- setAdapter has already been called.");
        }

        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);

        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
//        if (mAdapter != null && mDataSetObserver != null) {
//            mDataSetObserver.onChanged();
//        }
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v The view to add.
     */
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    @Override
    public int getHeaderViewsCount() {
        return mHeaderViewInfos.size();
    }
    
    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v The view to add.
     * @param data Data to associate with this view
     * @param isSelectable true if the footer view can be selected
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {

        // NOTE: do not enforce the adapter being null here, since unlike in
        // addHeaderView, it was never enforced here, and so existing apps are
        // relying on being able to add a footer and then calling setAdapter to
        // force creation of the HeaderViewListAdapter wrapper

        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterViewInfos.add(info);

        // in the case of re-adding a footer view, or adding one later on,
        // we need to notify the observer
//        if (mAdapter != null && mDataSetObserver != null) {
//            mDataSetObserver.onChanged();
//        }
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is called more
     * than once, the views will appear in the order they were added. Views added using
     * this call can take focus if they want.
     * <p>NOTE: Call this before calling setAdapter. This is so ListView can wrap the supplied
     * cursor with one that will also account for header and footer views.
     *
     *
     * @param v The view to add.
     */
    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    @Override
    public int getFooterViewsCount() {
        return mFooterViewInfos.size();
    }

	@Override
	public boolean onScroll(MouseWheelEvent event) {
		if (canScrolled) {
			isMadeByXml = true;
			int wheelRotation = event.getWheelRotation();
			int amount = event.getScrollAmount();
			scrollList(wheelRotation, amount);
		}
		return true;
	}
	
	/**
	 * 
	 * @param wheelRotation
	 * @param scrollAmount
	 * @return true : scroll is possible, false : not possible
	 */
	private boolean scrollList(int wheelRotation, int scrollAmount) {
		boolean ret = mScrollHelper.scroll(wheelRotation, scrollAmount);
		if (!ret) {
			return false;
		}
		
		mScrollGap += mScrollHelper.getAmount(wheelRotation, scrollAmount);
		
		if (wheelRotation > 0) {
			if (mScrollGap > 0) {
				do {
					mStartPosition -= 1;
					if (mStartPosition < mHeaderViewInfos.size()) {
						mScrollGap -= mItemHeights.getHeaderHeight(mStartPosition);
					} else {
						mScrollGap -= mItemHeights.getItemHeight(mStartPosition);
					}	
				} while (mScrollGap > 0);
				
				if (mStartPosition >= mHeaderViewInfos.size()) {
					int index = (mStartPosition-mHeaderViewInfos.size())%mItemCountInScreen;
					isScraped[(index)%mItemCountInScreen] = true;
				} 
			} 
		} else {
			int height = 0;
			if (mStartPosition < mHeaderViewInfos.size()) {
				height = mItemHeights.getHeaderHeight(mStartPosition);
			} else {
				height = mItemHeights.getItemHeight(mStartPosition);
			}
			
			if (-mScrollGap > height) {
				do {
					mScrollGap += height;
					mStartPosition += 1;
				} while (-mScrollGap > height);
				
				isScraped[((mStartPosition-mHeaderViewInfos.size())-1+mItemCountInScreen)%mItemCountInScreen] = true;
			}
		}
		
//		Log.i(Joiner.on("").join("mStartPosition : ", mStartPosition, ", mScrollGap : ", mScrollGap, ", mScrollY : ", mScrollY, ", amount : ", scrollAmount));
		return ret;
	}
	
	@Override
	public void scrollToFirst() {
		mScrollGap = 0;
		scrollTo(getScrollX(), 0);
	}

	@Override
	public void scrollToEnd() {
//		int height = getDrawingHeight();
//		int accu = 0;
//		for (int i = mAdapter.getCount() - 1, n = 0; i >= 0 && n < mItemCountInScreen; i--, n++) {
//			accu += mItemHeights.getItemHeight(i);
//		}
//		mScrollGap += height - accu;
//		scrollTo(getScrollX(), mItemHeights.getTotalHeight() - getDrawingHeight());
	}

	@Override
	public boolean isPossibleToScrollUp(int amount, boolean isFirst) {
		if (mStartPosition == 0 && getScrollY() - amount <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isPossibleToScrollDown(int amount, boolean isLast) {
		if ((mStartPosition + mItemCountInScreen 
				>= mAdapter.getCount() + mHeaderViewInfos.size() + mFooterViewInfos.size()) 
				&& mItemHeights.getTotalHeight() <= getScrollY() + getDrawingHeight() - amount) {
			return false;
		}
		return true;
	}

	private OnThumbListener onThumbListener = new OnThumbListener() {
		
		@Override
		public boolean onChanged(int wheelRotation, int amount) {
			if (wheelRotation > 0) {
				amount = -amount;
			}
			return scrollList(wheelRotation, amount);
		}
	};
	
	private DataSetObserver mObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			// notifyDatasetChanged
			initAdaptor();
			invalidate();
		}

		@Override
		public void onInvalidated() {
			// notifyDatasetInvalidated
			initAdaptor();
			invalidate();
		}
	};
}
