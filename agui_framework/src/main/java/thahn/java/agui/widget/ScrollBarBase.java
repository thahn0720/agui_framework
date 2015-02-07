//package thahn.java.agui.widget;
//
//import java.awt.Graphics;
//
//import thahn.java.agui.R;
//import thahn.java.agui.animation.Animation;
//import thahn.java.agui.animation.AnimationUtils;
//import thahn.java.agui.app.Context;
//import thahn.java.agui.app.controller.Handler;
//import thahn.java.agui.app.controller.Message;
//import thahn.java.agui.utils.Log;
//import thahn.java.agui.view.MotionEvent;
//import thahn.java.agui.view.OnScrollListener;
//import thahn.java.agui.view.OnTouchListener;
//import thahn.java.agui.view.View;
//import thahn.java.agui.view.ViewGroup;
//
///**
// * size means width or height
// * @author thAhn
// *
// */
//public class ScrollBarBase extends ViewGroup implements OnScrollListener {
//
//	public static final int											X_AXIS					= 0;
//	public static final int											Y_AXIS					= 1;
//	
//	public static final int											HANDLER_FADE_IN			= 0;
//	public static final int											HANDLER_FADE_OUT		= 1;
//	
//	private static final int										MOVING_DISCOUNT_FACTOR  = 2;
//	
//	/**
//	 * the time maintaining the fade in state. sec unit. 
//	 */
//	private static final int										MAINTAIN_IN_TIME  		= 5000;
//
//	private static final int										THUMB_SIZE				= 20;
//	
//	private Button 													mThumbView;
//	private int														mThumbSize;
//	private int														mRemainingScroll;
//	private int 													mParentSize;
//	private OnThumbListener 										onThumbListener;
//	private int														mScrollAmount;
//	private View 													mParent;
//	private double													mRelativeStepSize;
//	private boolean													isFade					= true;
//	private Animation												mFadeIn;
//	private Animation												mFadeOut;
//	private long													mLastTouchTime;
//	private boolean													isHorizontal;
//	
//	public ScrollBarBase(Context context, View parent, int avgItemSize, int itemAllCount, int scrollAmount, boolean isHorizontal) {
//		super(context);
//		this.isHorizontal = isHorizontal;
//		mParent = parent;
//		mScrollAmount = scrollAmount;
//		
//		int l,t,r,b;
//		if(isHorizontal) {
//			l = parent.getDrawingLeft() + parent.getScrollX();
//			t = parent.getDrawingTop();
//			r = parent.getDrawingRight() + parent.getScrollX();
//			b = parent.getDrawingTop() + THUMB_SIZE;
//			
//			mParentSize = parent.getDrawingWidth();
//			setLayoutParams(new LayoutParams(l, t, r, b));
//		} else {
//			l = parent.getDrawingRight() - THUMB_SIZE;
//			t = parent.getDrawingTop() + mParent.getScrollY();
//			r = parent.getDrawingRight();
//			b = parent.getDrawingBottom() + mParent.getScrollY();
//			
//			mParentSize = parent.getDrawingHeight();
//			setLayoutParams(new LayoutParams(l, t, r, b));
//		}
//		
//		int size = mParentSize;
//		mRemainingScroll = avgItemSize*itemAllCount - size;
//		mThumbSize = size - (int)(size*((double)(avgItemSize*itemAllCount) / ((double)(avgItemSize*itemAllCount) + size)));
//		
//		mThumbView = new Button(context);
//		if(isHorizontal) {
//			mThumbView.setMinHeight(THUMB_SIZE);
//			mThumbView.setLayoutParams(new LayoutParams(l, t, l + mThumbSize, t + THUMB_SIZE));
//		} else {
//			mThumbView.setMinWidth(THUMB_SIZE);
//			mThumbView.setLayoutParams(new LayoutParams(l, t, l + THUMB_SIZE, t + mThumbSize));
//		}
//		mThumbView.setOnTouchListener(onTouchListener);
//		addViewInternal(mThumbView);
//
//		mRelativeStepSize = (((double)((double)-1 / (double)(mRemainingScroll)))*(mParentSize-mThumbSize));
//		
//		startScrollFadeAnim();
//	}
//	
//	public void setThumbSize(int listHeight) {
//		setThumbSize(mParent, listHeight);
//	}
//	
//	public void setThumbSize(View parent, int listHeight) {
//		int l,t,r,b;
//		
//		// FIXME : doing this when different with previous location and size.
//		if(isHorizontal) {
//			if((parent.getTop() + parent.getPaddingTop()) != getTop() 
//					|| (parent.getLeft() + parent.getPaddingLeft()) != getLeft()) {
//				l = parent.getDrawingLeft() + parent.getScrollX();
//				t = parent.getDrawingTop();
//				r = parent.getDrawingRight() + parent.getScrollX();
//				b = parent.getDrawingTop() + THUMB_SIZE;
//				
//				mParentSize = parent.getDrawingWidth();
//				setLayoutParams(new LayoutParams(l, t, r, b));
//			}
//		} else {
//			if((parent.getTop() + parent.getPaddingTop()) != getTop() 
//					|| (parent.getRight() + parent.getPaddingRight()) != getRight()) {
//				l = parent.getDrawingRight() - THUMB_SIZE;
//				t = parent.getDrawingTop() + mParent.getScrollY();
//				r = parent.getDrawingRight();
//				b = parent.getDrawingBottom() + mParent.getScrollY();
//				
//				mParentSize = parent.getDrawingHeight();
//				setLayoutParams(new LayoutParams(l, t, r, b));
//			}
//		}
//		computeThumbSize(listHeight);
//	}
//	
//	public void computeThumbSize(int listHeight) {
//		mRemainingScroll = listHeight - mParentSize;
//		int temp = mParentSize - (int)(mParentSize*((double)(listHeight) / ((double)(listHeight) + mParentSize)));
//		if(isHorizontal) {
//			if(mThumbSize != temp) { // mThumbView.getTop() >= 0 && 
//				mThumbSize = temp;
//				int l = mParent.getDrawingLeft() + mParent.getScrollX();
//				int t = mParent.getDrawingTop() + mParent.getScrollY();
//				mThumbView.onLayout(true, l, t, l + mThumbSize, t + mHeight);
//				mRelativeStepSize = (((double)((double)-1 / (double)(mRemainingScroll)))*(mParentSize-mThumbSize));
//				
//				mThumbView.invalidate();
//			}
//		} else {
//			if(mThumbView.getTop() != getTop() || mThumbView.getLeft() < 0 || mThumbSize != temp) {
//				mThumbSize = temp;
//				int l = mParent.getDrawingRight()-mWidth + mParent.getScrollX();
//				int t = mParent.getDrawingTop() + mParent.getScrollY();
//				mThumbView.onLayout(true, l, t, l + mWidth, t + mThumbSize);
//				mRelativeStepSize = (((double)((double)-1 / (double)(mRemainingScroll)))*(mParentSize-mThumbSize));
//				
//				mThumbView.invalidate();
//			}
//		}
//	}
//	
//	public void draw(Graphics g) {
//		super.draw(g);
//	}
//	
//	@Override
//	public void onScroll(int scrollX, int scrollY, int amount) {
//		startScrollFadeAnim();
//		//
//		int locationSize;
//		if(isHorizontal) {
//			locationSize = getRelativePosition(scrollX);
//		} else {
//			locationSize = getRelativePosition(scrollY);
//		}
//		scrollTrackTo(locationSize);
//		// Log.e("locationY "+locationY+", scrolly "+scrollY+", remainingScroll "+mRemainingScroll+", mTrackHeight "+mThumbHeight);
//	}
//
//	@Override
//	public void onFirstScrolled() {
//		int locationY = 0;
//		scrollTrackTo(locationY);
//	}
//
//	@Override
//	public void onLastScrolled() {
//		int locationSize = (mParentSize-mThumbSize);
//		scrollTrackTo(locationSize);
//	}
//	
//	private void scrollTrackTo(int size) {
//		if(isHorizontal) {
//			mThumbView.scrollTo(-size, 0);
//		} else {
//			mThumbView.scrollTo(0, -size);
//		}
//	}
//	
//	private int getRelativePosition(int scrollY) {
//		return (int)Math.ceil((mRelativeStepSize*scrollY));
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		startScrollFadeAnim();
//		//
//		super.onTouchEvent(event);
//		mThumbView.onTouchEvent(event);
//		onTouchListener.onTouch(this, event);
//		return true;
//	}
//	
//	public void setOnThumbListener(OnThumbListener listener) {
//		onThumbListener = listener;
//	}
//
//	@Override
//	public boolean contains(int x, int y) {
//		boolean ret = false;
//		if(getDrawingLeft() <= x && getDrawingRight() >= x && getDrawingTop() <= y && getDrawingBottom() >= y) {
//			ret = true;
//		}
//		return ret;
//	}
//
//	int prevX;
//	int prevY;
//	boolean isChanged = false;
//	OnTouchListener onTouchListener = new OnTouchListener() {
//		
//		@Override
//		public boolean onTouch(View view, MotionEvent event) {
//			int amount = mScrollAmount;
////			int id = view.getId();
////			boolean isChanged = false;
//			int action = event.getAction();
//			int x = event.getX();
//			int y = event.getY();
//			switch (action) {
//			case MotionEvent.ACTION_DOWN:
//				prevX = x;
//				prevY = y;
//				break;
//			case MotionEvent.ACTION_MOVE:
//				int gapSize = 0;
//				if(isHorizontal) {
//					gapSize = prevX - x;
//				} else {
//					gapSize = prevY - y;
//				}
//				if(Math.abs(gapSize) >= amount*MOVING_DISCOUNT_FACTOR) {
//					isChanged = true;
////					mThumbView.scrollBy(0, -amount);//-mThumbStepHeight); // gapY
//					if(onThumbListener != null) {
//						int wheelRotation = 1;
//						if(gapSize < 0) {
//							wheelRotation = -1;
//							amount = -amount;
//						}
//						if(onThumbListener.onChanged(wheelRotation, -amount)) {
////							Log.e("amount : " + -amount);
//						}
//					}
//				}
//				break;
//			case MotionEvent.ACTION_UP:
//				break;
//			}
//			
//			if(isChanged) {
//				isChanged = false;
//				prevX = x;
//				prevY = y;
//			}
//			
//			return true;
//		}
//	};
//	
//	private void startScrollFadeAnim() {
//		long currentTime = System.currentTimeMillis();
//		
//		if(currentTime - mLastTouchTime > MAINTAIN_IN_TIME) {
//			mHandler.sendEmptyMessage(HANDLER_FADE_IN);
//			mHandler.sendEmptyMessageDelayed(HANDLER_FADE_OUT, MAINTAIN_IN_TIME);
//		} 
//		mLastTouchTime = currentTime;
//	}
//	
//	private void doFadeIn() {
//		if(isFade) {
//			Log.e("new fade in");
//			if(mFadeIn == null) {
//				mFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
//			} else {
//				mFadeIn.reset();
//			}
//			startAnimation(mFadeIn);
//		}
//	}
//	
//	private void doFadeOut() {
//		if(isFade) {
//			Log.e("new fade out");
//			if(mFadeOut == null) {
//				mFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
//				mFadeOut.setFillAfter(true);
//			} else {
//				mFadeOut.reset();
//			}
//			startAnimation(mFadeOut);
//		}
//	}
//	
//	Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case HANDLER_FADE_IN:
//				Log.e("fade in");
//				doFadeIn();
//				break;
//			case HANDLER_FADE_OUT:
//				Log.e("fade out");
//				long currentTime = System.currentTimeMillis();
//				if(currentTime - mLastTouchTime >= MAINTAIN_IN_TIME - 100) {
//					doFadeOut();
//				} else {
//					mHandler.sendEmptyMessageDelayed(HANDLER_FADE_OUT, - currentTime + mLastTouchTime + MAINTAIN_IN_TIME);
//				}
//				break;
//			}
//		}
//	};
//	
//	public interface OnThumbListener {
//		
//		/**
//		 * 
//		 * @param wheelRotation
//		 * @param amount
//		 * @return {@link ListView#scrollList}
//		 */
//		boolean onChanged(int wheelRotation, int amount);
//	}
//}
