package thahn.java.agui.widget;

import java.awt.Graphics;

import thahn.java.agui.R;
import thahn.java.agui.animation.Animation;
import thahn.java.agui.animation.AnimationUtils;
import thahn.java.agui.app.Context;
import thahn.java.agui.app.controller.Handler;
import thahn.java.agui.app.controller.Message;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.OnScrollListener;
import thahn.java.agui.view.OnTouchListener;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;

/**
 * 
 * @author thAhn
 *
 */
public abstract class ScrollBar extends ViewGroup implements OnScrollListener {

	public static final int											X_AXIS					= 0;
	public static final int											Y_AXIS					= 1;
	
	public static final int											HANDLER_FADE_IN			= 0;
	public static final int											HANDLER_FADE_OUT		= 1;
	
	private static final int										MOVING_DISCOUNT_FACTOR  = 2;
	
	/**
	 * the time maintaining the fade in state. nano sec unit. 
	 */
	private static final int										MAINTAIN_IN_TIME  		= 5000;

	protected static final int										THUMB_SIZE				= 20;
	
	private Button 													mThumbView;
	private int														mThumbSize;
	private int														mRemainingScroll;
	private int 													mParentSize;
	private OnThumbListener 										onThumbListener;
	private int														mScrollAmount;
	protected View 													mParent;
	private double													mRelativeStepHeight;
	private boolean													isFade					= true;
	private Animation												mFadeIn;
	private Animation												mFadeOut;
	private long													mLastTouchTime;
	private int 													mPrevX;
	private int 													mPrevY;
	private boolean 												isChanged = false;
	
	public ScrollBar(Context context, View parent, int avgItemHeight, int itemAllCount, int scrollAmount) {
		super(context);
		mParent = parent;
		mScrollAmount = scrollAmount;
		
		int[] ltrb = getViewLtrb();
		int l = ltrb[0], t = ltrb[1], r = ltrb[2], b = ltrb[3];
		setLayoutParams(new LayoutParams(l, t, r, b));
		
		mParentSize = getParentSize();
		int size = mParentSize;
		int listSize = avgItemHeight * itemAllCount;
		calcurateRemainingScroll(listSize, size);
		calcurateThumbSize(listSize, size);
		
		mThumbView = new Button(context);
		if (isHorizontal()) {
			mThumbView.setMinHeight(THUMB_SIZE);
		} else {
			mThumbView.setMinWidth(THUMB_SIZE);
		}
		locateThumbView(mThumbView, mThumbSize);
		mThumbView.setOnTouchListener(onTouchListener);
		addViewInternal(mThumbView);

		calcurateRelativeStepSize();
		
		startScrollFadeAnim();
	}
	
	/**
	 * 
	 * @return left, top, right, bottom
	 */
	protected abstract int[] getViewLtrb();
	protected abstract boolean isHorizontal();
	
	public void setThumbSize(int listHeight) {
		setThumbSize(mParent, listHeight);
	}
	
	public void setThumbSize(View parent, int listHeight) {
		if (mParent != parent) {
			throw new WrongFormatException("parent is different");
		}
		mParentSize = getParentSize();
		
		int[] ltrb = getViewLtrb();
		int l = ltrb[0], t = ltrb[1], r = ltrb[2], b = ltrb[3];
		// FIXME : doing this when different with previous location and size.
		setLayoutParams(new LayoutParams(l, t, r, b));
		
		computeThumbSize(listHeight);
	}
	
	public void computeThumbSize(int listSize) {
		int size = mParentSize;
		int temp = (int)(((double)size * (double)size) / (double)listSize);

		if(isHorizontal()) {
			if(mThumbSize != temp) {
				calcurateThumbSize(listSize, size);
				calcurateRemainingScroll(listSize, size);
				locateThumbView(mThumbView, mThumbSize);
				calcurateRelativeStepSize();
				mThumbView.invalidate();
			}
		} else {
			if (mThumbView.getLeft() < 0 || mThumbSize != temp) {
				calcurateThumbSize(listSize, size);
				calcurateRemainingScroll(listSize, size);
				locateThumbView(mThumbView, mThumbSize);
				calcurateRelativeStepSize();
				mThumbView.invalidate();
			}
		}
	}
	
	protected abstract int getParentSize();
	protected abstract void locateThumbView(View thumbView, int thumbSize);
	
	private void calcurateRemainingScroll(int listHeight, int height) {
		mRemainingScroll = listHeight - height; 
	}
	
	private void calcurateThumbSize(int listSize, int size) {
		mThumbSize = (int)(((double)size * (double)size) / (double)listSize);
	}
	
	private void calcurateRelativeStepSize() {
		mRelativeStepHeight = -((double)(mParentSize + getParentCompoundPadding() - mThumbSize) / (double)mRemainingScroll);
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		// g.setClip(getDrawingLeft(), getDrawingTopWithoutScroll(), getPaddedWidth(), getPaddedHeight());
	}
	
	@Override
	public void onScroll(int scrollX, int scrollY, int amount) {
		startScrollFadeAnim();
		int location = 0;
		if (isHorizontal()) {
			location = getRelativePosition(scrollX);
		} else {
			location = getRelativePosition(scrollY);
		}
		scrollTrackTo(location);
//		Log.e("locationY "+locationY+", scrolly "+scrollY+", remainingScroll "+mRemainingScroll+", mTrackHeight "+mThumbSize+", mRelativeSize "+mRelativeStepHeight);
	}

	@Override
	public void onFirstScrolled() {
		int locationY = 0;
		scrollTrackTo(locationY);
	}

	@Override
	public void onLastScrolled() {
		int locationY = (mParentSize - mThumbSize) + getParentCompoundPadding();
		scrollTrackTo(locationY);
	}
	
	private void scrollTrackTo(int y) {
		if(isHorizontal()) {
			mThumbView.scrollTo(-y, 0);
		} else {
			mThumbView.scrollTo(0, -y);
		}
	}
	
	protected abstract int getParentCompoundPadding();
	
	private int getRelativePosition(int scrollY) {
		return (int) Math.round(mRelativeStepHeight * scrollY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		startScrollFadeAnim();
		
		super.onTouchEvent(event);
		mThumbView.onTouchEvent(event);
		onTouchListener.onTouch(this, event);
		return true;
	}
	
	public void setOnThumbListener(OnThumbListener listener) {
		onThumbListener = listener;
	}

	@Override
	public boolean contains(int x, int y) {
		boolean ret = false;
		if (getDrawingLeft() <= x && getDrawingRight() >= x && getDrawingTop() <= y && getDrawingBottom() >= y) {
			ret = true;
		}
		return ret;
	}

	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int amount = mScrollAmount;
			int action = event.getAction();
			int x = event.getX();
			int y = event.getY();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mPrevX = x;
				mPrevY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				int gapSize = 0;
				
				if (isHorizontal()) {
					gapSize = mPrevX - x;
				} else { 
					gapSize = mPrevY - y;
				}
				
				if (Math.abs(gapSize) >= amount*MOVING_DISCOUNT_FACTOR) {
					isChanged = true;
					if (onThumbListener != null) {
						int wheelRotation = 1;
						if (gapSize < 0) {
							wheelRotation = -1;
							amount = -amount;
						}
						if (onThumbListener.onChanged(wheelRotation, -amount)) {
//							Log.e("amount : " + -amount);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			
			if (isChanged) {
				isChanged = false;
				mPrevX = x;
				mPrevY = y;
			}
			
			return true;
		}
	};
	
	private void startScrollFadeAnim() {
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - mLastTouchTime > MAINTAIN_IN_TIME) {
			mHandler.sendEmptyMessage(HANDLER_FADE_IN);
			mHandler.sendEmptyMessageDelayed(HANDLER_FADE_OUT, MAINTAIN_IN_TIME);
		} 
		mLastTouchTime = currentTime;
	}
	
	private void doFadeIn() {
		if (isFade) {
			Log.e("new fade in");
			if (mFadeIn == null) {
				mFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
			} else {
				mFadeIn.reset();
			}
			startAnimation(mFadeIn);
		}
	}
	
	private void doFadeOut() {
		if (isFade) {
			Log.e("new fade out");
			if (mFadeOut == null) {
				mFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
				mFadeOut.setFillAfter(true);
			} else {
				mFadeOut.reset();
			}
			startAnimation(mFadeOut);
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_FADE_IN:
				Log.i("scrollbar fade in");
				doFadeIn();
				break;
			case HANDLER_FADE_OUT:
				Log.i("scrollbar fade out");
				long currentTime = System.currentTimeMillis();
				if (currentTime - mLastTouchTime >= MAINTAIN_IN_TIME - 100) {
					doFadeOut();
				} else {
					mHandler.sendEmptyMessageDelayed(HANDLER_FADE_OUT, - currentTime + mLastTouchTime + MAINTAIN_IN_TIME);
				}
				break;
			}
		}
	};
	
	public interface OnThumbListener {
		
		/**
		 * 
		 * @param wheelRotation
		 * @param amount
		 * @return {@link ListView#scrollList}
		 */
		boolean onChanged(int wheelRotation, int amount);
	}
}
