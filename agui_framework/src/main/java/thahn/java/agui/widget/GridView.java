package thahn.java.agui.widget;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import thahn.java.agui.Global;
import thahn.java.agui.R;
import thahn.java.agui.app.ApplicationSetting;
import thahn.java.agui.app.Context;
import thahn.java.agui.database.DataSetObserver;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.MouseWheelEvent;
import thahn.java.agui.view.View;
import thahn.java.agui.widget.ScrollBar.OnThumbListener;

// numColumns = autofit
// columnWidth
public class GridView extends AbsListView {
	
	public static final int 										AUTO_FIT 						= -1;
	//
	private int														mNumColumns;
	private int														mColumnWidth;
	//
	private BaseAdapter												mAdapter;
	private int														mItemCountInScreen;
	private int														mStartPosition;
	private int 													mScrollGap 						= 	0;
	
	/**
	 * true : recycle is possible<br>
	 * false : recycle is not possible. rearrange<br>
	 */
	private boolean[]												isScraped;
	
	public GridView(Context context) {
		this(context, new AttributeSet(context));
		isMadeByXml = false;
	}

	public GridView(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_GRID_VIEW_CODE);
		isMadeByXml = true;
	}

	public GridView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
		isMadeByXml = true;
	}
	
	@Override
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		mNumColumns = attrs.getIntFromEnum(R.attr.GridView_numColumns, AUTO_FIT);
		mColumnWidth = attrs.getDimensionPixelSize(R.attr.GridView_columnWidth, -1);
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
		
		if (parentWidth > 0) {
			if (mNumColumns != AUTO_FIT) {
				mColumnWidth = (parentWidth - getPaddingLeft() - getPaddingRight())/mNumColumns;
			} else if (mColumnWidth != -1) {
				// TODO : implements when 'autofit'
				mNumColumns = (parentWidth - getPaddingLeft() - getPaddingRight())/mColumnWidth;
			} else {
				throw new WrongFormatException("GridView : numColumns and columnWidth is not set.");
			}
		} else {
			// TODO : implements
		}
	}

	@Override
	public void arrange() {
//		super.arrange();
		Log.e("arrange");
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
		// TODO : getview의 inflace에 의해서 부모의 arrange 호출에 의해ㅔ서 무한 루프
		if (!isMadeByXml) {
			initAdaptor();
		}
	}

	@Override
	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mObserver);
		
		initAdaptor();
	}
	
	private void initAdaptor() {
		if (getParent() == null || mAdapter == null) return ;
		
		if (mNumColumns < 0 ) {
			View convertView = null;
			convertView = mAdapter.getView(0, convertView, this);
			convertView.onMeasure(getDrawingWidth(), getDrawingHeight());
			convertView.onPostMeasure(getDrawingWidth(), getDrawingHeight());
			int[] wh = measureChildSize(convertView);
			mColumnWidth = wh[0];
			mNumColumns = getParent().getWidth()/mColumnWidth;
		}
		
		mScrollGap = 0;
		mStartPosition = 0;
		mItemCountInScreen = 0;
		canScrolled = false;
		
		List<View> viewContainer = new ArrayList<>();
		int realColumnGap = getDrawingWidth()/mNumColumns;
		int start = getDrawingLeft() + realColumnGap/2;
		int left = getDrawingLeft();
		int top = getTop() + getPaddingTop();
		int right = 0;
		int bottom = 0;
		int dataCount = mAdapter.getCount();
		mItemHeights = new ItemHeights((int)Math.ceil((double)dataCount/mNumColumns), 0, 0);
		for (int i=0;i<dataCount;++i) {
			View convertView = null;
			convertView = mAdapter.getView(i, convertView, this);
			convertView.onMeasure(getDrawingWidth(), getDrawingHeight());
			convertView.onPostMeasure(getDrawingWidth(), getDrawingHeight());
			int[] wh = measureChildSize(convertView);
			int relativeIndex = i % mNumColumns;
			left = start + realColumnGap*relativeIndex - wh[0]/2 + convertView.getLayoutParams().leftMargin;
			right = start + realColumnGap*relativeIndex + wh[0]/2 + convertView.getLayoutParams().topMargin; 
			bottom = top + wh[1];
			convertView.onLayout(true, left, top, right, bottom);
			convertView.arrange();
			wh[0] = convertView.getWidth();
			wh[1] = convertView.getHeight();
			
			if (relativeIndex == mNumColumns-1) {
				top += convertView.getHeight();
			} 
			
			viewContainer.add(convertView);
			if (i==0) {
				mItemHeights.fillItemHeight(wh[1]);
			} else {
				mItemHeights.setItemHeight(i/mNumColumns, Math.max(wh[1], mItemHeights.getItemHeight(i/mNumColumns)));
			}
			if (bottom > getDrawingTop() + getDrawingHeight() + wh[1]) {
				canScrolled = true;
				mItemCountInScreen = i + mNumColumns; // change +1 into +mNumCloumn
				setScrollbar(mContext, (View) this, mItemHeights.getItemHeight(0), dataCount, ApplicationSetting.SCROLL_AMOUNT, onThumbListener);
				setThumbSize(mItemHeights.getTotalHeight());
				break;
			}
		}
		
		shrinkSize(getLeft(), getTop(), right, bottom);
		
		if (mItemCountInScreen == 0) {
			mItemCountInScreen = dataCount;
		}
		
		mConvertViewContainer = new View[mItemCountInScreen];
		isScraped = new boolean[mItemCountInScreen];
		for (int i=0;i<mItemCountInScreen;++i) {
			if (i < viewContainer.size()) {
				mConvertViewContainer[i] = viewContainer.get(i); 
				isScraped[i] = false;
			} else {
				isScraped[i] = true;
			}
		}
	}
	
	@Override
	public BaseAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		int realColumnGap = getDrawingWidth()/mNumColumns;
		int start = getDrawingLeft() + realColumnGap/2;
		int left = getDrawingLeft();//mScrollX;
		int top = getTop() + getPaddingTop() + mScrollGap;
		int right = 0;
		int bottom = 0;
		if (mAdapter != null) {
			for (int i = mStartPosition; i < mStartPosition+mItemCountInScreen; i++) {
				int index = i % mItemCountInScreen;
				if (i >= mAdapter.getCount()) break;
				
				if (isScraped[index]) {
					mConvertViewContainer[index] = mAdapter.getView(i, mConvertViewContainer[index], this);
					mConvertViewContainer[index].onMeasure(getDrawingWidth(), getDrawingHeight());
					mConvertViewContainer[index].onPostMeasure(getDrawingWidth(), getDrawingHeight());
					mConvertViewContainer[index].arrange(); // FIXME : this line is overhead
					try {
						mItemHeights.setItemHeight(i/mNumColumns, mConvertViewContainer[index].getHeight());
					} catch (Exception e) {
						e.printStackTrace();
					}
					setThumbSize(mItemHeights.getTotalHeight());
					isScraped[index] = false;
//					Log.e("start : "+mStartPosition+", arrange : "+index+", position : "+(i));
				}
				int[] wh = measureChildSize(mConvertViewContainer[index]);
				int relativeIndex = i % mNumColumns;
				left = start + realColumnGap*relativeIndex - wh[0]/2 + mConvertViewContainer[index].getLayoutParams().leftMargin;
				right = start + realColumnGap*relativeIndex + wh[0]/2 + mConvertViewContainer[index].getLayoutParams().rightMargin; 
				bottom = top + wh[1];
				mConvertViewContainer[index].onLayout(true, left, top, right, bottom);
				mConvertViewContainer[index].arrange();
				if (relativeIndex == mNumColumns-1) {
					top = bottom;
				} 
				
				mConvertViewContainer[index].draw(g);
				//				Log.e("draw "+i+" left : "+mConvertViewContainer[index].getLeft()+", top : "+mConvertViewContainer[index].getTop()
				//						+", right : "+mConvertViewContainer[index].getRight()+", bottom : "+mConvertViewContainer[index].getBottom()
				//						+", width : "+mConvertViewContainer[index].getWidth()+", height : "+mConvertViewContainer[index].getHeight());
			}
		}
		
		if (canScrolled) mScrollBar.draw(g);
	}
	
    /**
     * Set the width of columns in the grid.
     *
     * @param columnWidth The column width, in pixels.
     *
     * @attr ref android.R.styleable#GridView_columnWidth
     */
    public void setColumnWidth(int columnWidth) {
        if (columnWidth != mColumnWidth) {
        	mColumnWidth = columnWidth;
        	invalidate();
        }
    }

    /**
     * Set the number of columns in the grid
     *
     * @param numColumns The desired number of columns.
     *
     * @attr ref android.R.styleable#GridView_numColumns
     */
    public void setNumColumns(int numColumns) {
        if (numColumns != mNumColumns) {
        	mNumColumns = numColumns;
        	invalidate();
        }
    }
	
//	@Override
//	public boolean onTouchEvent(AMouseEvent event) {
//		boolean ret = super.onTouchEvent(event);
//		if (ret) return true;
//		
//		int action = event.getAction();
//		int x = event.getX();
//		int y = event.getY();// - mScrollY - mScrollGap;
//		
//		switch (action) {
//		case AMouseEvent.ACTION_DOWN:
//			for (View v : mConvertViewContainer) {
//				if (v.contains(x, y)) {
//					mPressedView = v;
//					ret = mPressedView.onTouchEvent(event);
//				}
//			}
//			break;
//		case AMouseEvent.ACTION_MOVE:
//			if (mPressedView != null) ret = mPressedView.onTouchEvent(event);
//			break;
//		case AMouseEvent.ACTION_UP:
//			if (mPressedView != null) {
//				ret = mPressedView.onTouchEvent(event);
//				mPressedView = null;
//			}
//			break;
//		}
//		
//		return ret;
//	}

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
		
		int amount = mScrollHelper.getAmount(wheelRotation, scrollAmount);
		
		mScrollGap += amount;
		
		if (wheelRotation > 0) {
			if (mScrollGap > 0) {
				mStartPosition -= mNumColumns;
				int index = (mStartPosition/mNumColumns)%(mItemCountInScreen/mNumColumns);
				mScrollGap -= mItemHeights.getItemHeight(mStartPosition/mNumColumns);
				for (int n=0;n<mNumColumns;++n) {
					isScraped[(index)%(mItemCountInScreen/mNumColumns)+n] = true;
				}
			} 
		} else {
			if (-mScrollGap > mItemHeights.getItemHeight(mStartPosition/mNumColumns)) {
				mScrollGap += mItemHeights.getItemHeight(mStartPosition/mNumColumns);
				mStartPosition = (mStartPosition + mNumColumns);
				for (int n=0; n<mNumColumns; ++n) {
					isScraped[(mStartPosition/mNumColumns-1+mItemCountInScreen/mNumColumns)%(mItemCountInScreen/mNumColumns)+n] = true;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public void scrollToFirst() {
		scrollTo(getScrollX(), 0);
	}

	@Override
	public void scrollToEnd() {
	}

	@Override
	public boolean isPossibleToScrollUp(int amount, boolean isFirst) {
		if (mStartPosition == 0 && mScrollY == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isPossibleToScrollDown(int amount, boolean isLast) {
		if ((mStartPosition + mItemCountInScreen - 1 >= mAdapter.getCount()) 
				&& mItemHeights.getTotalHeight() <= getScrollY() + getDrawingHeight()) {
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
			initAdaptor();
			invalidate();
		}

		@Override
		public void onInvalidated() {
			initAdaptor();
			invalidate();
		}
	};
}
