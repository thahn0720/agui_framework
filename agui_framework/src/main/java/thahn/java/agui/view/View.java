package thahn.java.agui.view;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.lang.ref.WeakReference;

import thahn.java.agui.ContextMenu;
import thahn.java.agui.ContextMenu.ContextMenuInfo;
import thahn.java.agui.Global;
import thahn.java.agui.animation.Animation;
import thahn.java.agui.animation.Transformation;
import thahn.java.agui.annotation.AguiDifferent;
import thahn.java.agui.annotation.AguiSpecific;
import thahn.java.agui.app.ApplicationSetting;
import thahn.java.agui.app.Context;
import thahn.java.agui.graphics.ColorDrawable;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.os.Parcelable;
import thahn.java.agui.res.Resources;
import thahn.java.agui.utils.LayoutDirection;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.SparseArray;
import thahn.java.agui.view.View.DragShadowBuilder;
import thahn.java.agui.view.ViewGroup.LayoutParams;
import thahn.java.agui.widget.AbsListView;
import thahn.java.agui.widget.AdapterView;
import thahn.java.agui.widget.Button;
import thahn.java.agui.widget.ScrollBar;
import thahn.java.agui.widget.ViewName;
import agui.support.v4.internal.view.TitleActionMode;

/**
 * onMeasure();<br>
 * onPostMeasure();<br>
 * layout();<br>
 * arrange();<br>
 * @author thAhn
 *
 */
public class View implements LayoutObserver {

	public static final String											TAG							 = "View";

	/**
     * Used to mark a View that has no ID.
     */
    public static final int 											NO_ID 						 = -1;

    /**
     * Horizontal layout direction of this view is from Left to Right.
     * Use with {@link #setLayoutDirection}.
     */
    public static final int LAYOUT_DIRECTION_LTR = LayoutDirection.LTR;

    /**
     * Horizontal layout direction of this view is from Right to Left.
     * Use with {@link #setLayoutDirection}.
     */
    public static final int LAYOUT_DIRECTION_RTL = LayoutDirection.RTL;

    /**
     * Horizontal layout direction of this view is inherited from its parent.
     * Use with {@link #setLayoutDirection}.
     */
    public static final int LAYOUT_DIRECTION_INHERIT = LayoutDirection.INHERIT;

    /**
     * Horizontal layout direction of this view is from deduced from the default language
     * script for the locale. Use with {@link #setLayoutDirection}.
     */
    public static final int LAYOUT_DIRECTION_LOCALE = LayoutDirection.LOCALE;
    
    /**
     * Bit shift to get the horizontal layout direction. (bits after DRAG_HOVERED)
     * @hide
     */
    static final int PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT = 2;

    /**
     * Mask for use with private flags indicating bits used for horizontal layout direction.
     * @hide
     */
    static final int PFLAG2_LAYOUT_DIRECTION_MASK = 0x00000003 << PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT;

    /**
     * Indicates whether the view horizontal layout direction has been resolved and drawn to the
     * right-to-left direction.
     * @hide
     */
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_RTL = 4 << PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT;

    /**
     * Indicates whether the view horizontal layout direction has been resolved.
     * @hide
     */
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED = 8 << PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT;

    /**
     * Mask for use with private flags indicating bits used for resolved horizontal layout direction.
     * @hide
     */
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK = 0x0000000C
            << PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT;

    /*
     * Array of horizontal layout direction flags for mapping attribute "layoutDirection" to correct
     * flag value.
     * @hide
     */
    private static final int[] LAYOUT_DIRECTION_FLAGS = {
            LAYOUT_DIRECTION_LTR,
            LAYOUT_DIRECTION_RTL,
            LAYOUT_DIRECTION_INHERIT,
            LAYOUT_DIRECTION_LOCALE
    };

    /**
     * Default horizontal layout direction.
     */
    private static final int LAYOUT_DIRECTION_DEFAULT = LAYOUT_DIRECTION_INHERIT;

    /**
     * Default horizontal layout direction.
     * @hide
     */
    static final int LAYOUT_DIRECTION_RESOLVED_DEFAULT = LAYOUT_DIRECTION_LTR;
    
	/**
     * Indicates that this view has reported that it can accept the current drag's content.
     * Cleared when the drag operation concludes.
     * @hide
     */
    protected static final int 											DRAG_CAN_ACCEPT              = 0x00000001;

    /**
     * Indicates that this view is currently directly under the drag location in a
     * drag-and-drop operation involving content that it can accept.  Cleared when
     * the drag exits the view, or when the drag operation concludes.
     * @hide
     */
    protected static final int 											DRAG_HOVERED                 = 0x00000002;
    protected static final int 											DRAG_MASK = DRAG_CAN_ACCEPT | DRAG_HOVERED;
    protected static final int 											DRAG_ING		             = 0x00000004;
    /**
     * Flag used to indicate that this view should be drawn once more (and only once
     * more) after its animation has completed.
     * {@hide}
     */
    protected static final int 											ANIMATION_STARTED           = 0x00010000;    
    private static final int 											PFLAG_SAVE_STATE_CALLED   	= 0x00020000;
    //
    public static final int 											VISIBLE 					= 0x00000000;
    public static final int 											INVISIBLE 					= 0x00000001;
    public static final int 											GONE 						= 0x00000002;
    static final int 													VISIBILITY_MASK 			= 0x00000003;

    /**
     * Use with {@link #focusSearch(int)}. Move focus to the previous selectable
     * item.
     */
    public static final int 											FOCUS_BACKWARD 				= 0x00000001;

    /**
     * Use with {@link #focusSearch(int)}. Move focus to the next selectable
     * item.
     */
    public static final int 											FOCUS_FORWARD 				= 0x00000002;

    /**
     * Use with {@link #focusSearch(int)}. Move focus to the left.
     */
    public static final int 											FOCUS_LEFT 					= 0x00000011;

    /**
     * Use with {@link #focusSearch(int)}. Move focus up.
     */
    public static final int 											FOCUS_UP 					= 0x00000021;

    /**
     * Use with {@link #focusSearch(int)}. Move focus to the right.
     */
    public static final int 											FOCUS_RIGHT 				= 0x00000042;

    /**
     * Use with {@link #focusSearch(int)}. Move focus down.
     */
    public static final int 											FOCUS_DOWN 					= 0x00000082;
    
    private static final int 											PRESSED                		= 0x00004000;
    
    /**
     * <p>Indicates this view can be clicked. When clickable, a View reacts
     * to clicks by notifying the OnClickListener.<p>
     * {@hide}
     */
    /*pacakge*/ static final int 										CLICKABLE 					= 0x00004000;
    
    /**
     * <p>
     * Indicates this view can be long clicked. When long clickable, a View
     * reacts to long clicks by notifying the OnLongClickListener or showing a
     * context menu.
     * </p>
     * {@hide}
     */
    /*pacakge*/ static final int 										LONG_CLICKABLE 				= 0x00200000;
    
	protected Context 													mContext;
	protected Drawable 													mBackground;
	protected Font														mFont;
	protected FontMetrics 												mFontMetrics;
	public LayoutPolicy													mPolicy;

	protected int 														mId;
	protected int 														mWidth 				= 0;
	protected int 														mHeight 			= 0;
	protected int 														mPaddingLeft 		= 0;
	protected int 														mPaddingTop 		= 0;
	protected int 														mPaddingRight 		= 0;
	protected int 														mPaddingBottom 		= 0;
	protected int 														mMinWidth	 		= 0;
	protected int 														mMinHeight	 		= 0;
	protected int 														mGravity	 		= Gravity.CENTER;
	protected int														mScrollX 			= 0;
	protected int														mScrollY			= 0;
	protected int														mScrollableX		= 0;
	protected int														mScrollableY		= 0;
	private boolean[]													mDrawableState		= new boolean[Drawable.VIEW_STATE_SETS.length];
	private int															mViewFlags;
	
	private boolean														mHasPerformedLongPress;
	/*package*/ boolean													isFocus;
	/*package*/ boolean													isFocusable;
	public boolean														isClip				= true;
	
	protected Object													mTag;
	protected LayoutParams 												mLayoutParam;
	protected boolean 													mHasScroll			= false;
	protected boolean 													mOverScreen			= false;
	
	private ContextMenu													mContextMenu;
	private int															mContextBtnCode		= -1;
	/*package*/ View													mParent;
	/*** @hide */
	private Animation													mCurrentAnimation;
	private boolean														mFillAfter;
	private Transformation	 											mFillAfterMatrix; 
	private Transformation	 											mCurTrans;
	public static AffineTransform 										mDefaultMatrix		= new AffineTransform();
	
	private StateCallback												mStateCallback;
	protected OnKeyListener												mKeyListener;
	protected OnClickListener											mOnClickListener;
	protected OnLongClickListener 										mOnLongClickListener;
	protected OnTouchListener											mTouchListener;
	protected OnDragListener 											mOnDragListener;
	protected OnCreateContextMenuListener								mCreateContextMenuListener;
	protected CheckForLongPress 										mPendingCheckForLongPress;
	
	protected int														mPrivateFlags;
	/**
	 * now just for relativealayout for removing topologycal sorting
	 */
	private boolean 													isPositioned 		= false;
	
    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onClick(View v);
    }
	
	/**
     * Interface definition for a callback to be invoked when a view has been clicked and held.
     */
    public interface OnLongClickListener {
        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         *
         * @return true if the callback consumed the long click, false otherwise.
         */
        boolean onLongClick(View v);
    }
	
	public View(Context context) {
		init(context);
		initDefault();
		initFromAttributes(new AttributeSet(context), ViewName.WIDGET_VIEW_CODE);
	}
	
	public View(Context context, AttributeSet attrs) {
		init(context);
		initDefault();
		initFromAttributes(attrs, 0);
	}
	
	public View(Context context, AttributeSet attrs, int defaultType) {
		init(context);
		initDefault();
		initFromAttributes(attrs, defaultType);
	} 

	protected void init(Context context) {
		mContext = context;
		mStateCallback = mContext;
		mFont = mContext.getFont();
		mFontMetrics = mContext.getFontMetrics(mFont);
		mPolicy = new LayoutPolicy();
		if (mLayoutParam == null) {
			mLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParam.registerLayoutObserver(this);
		}
	}

	protected void initDefault() {
		
	}
	
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		// button은 default로 background 속성이 없기에 background를 지나가지 않는다.
		// 그렇다면 android view는 다 한번씩 돈다면 루프가 필요없이 전체 attr을 한번씩 아래처럼 값 호출?

		// gravity : linearlayout의 match_parent시 child의 정렬 - realted with view
		// layout_gravity : 자신을 정렬, 부모로부터 자식 정렬				   - realted with viewgroup
		
//		AAttribute[] attrArray = attrs.obtainRelatedAttr(thahn.java.agui.R.attr.View, defaultType);
//		for (AAttribute a : attrArray) {
		int[][] defaultValues = attrs.obtainDefaultValue(defaultType);
		for (int i=0;i<defaultValues.length;++i) {
			int attrNameHash = defaultValues[i][0];//a.getAttrNameHash();
			int defaultValue = defaultValues[i][1];
			switch (attrNameHash) {
			case thahn.java.agui.R.attr.View_id:
				mId = attrs.getResourceId(attrNameHash, defaultValue);
				break;
			case thahn.java.agui.R.attr.View_background:
				mBackground = attrs.getDrawable(attrNameHash, defaultValue);//new Drawable(attrs.getString(attrNameHash, ""));
				break;
			case thahn.java.agui.R.attr.View_padding:
				int padding = attrs.getDimensionPixelSize(attrNameHash, defaultValue);
				if (padding != 0) {
					setPaddingLeft(padding);
					setPaddingTop(padding);
					setPaddingRight(padding);
					setPaddingBottom(padding);
				}
				break;
			case thahn.java.agui.R.attr.View_paddingLeft:
				int paddingLeft = attrs.getDimensionPixelSize(attrNameHash, defaultValue); // getDimensionPixelSize
				if (paddingLeft != 0) setPaddingLeft(paddingLeft);
				break;
			case thahn.java.agui.R.attr.View_paddingTop:
				int paddingTop = attrs.getDimensionPixelSize(attrNameHash, defaultValue);
				if (paddingTop != 0) setPaddingTop(paddingTop);
				break;
			case thahn.java.agui.R.attr.View_paddingRight:
				int paddingRight = attrs.getDimensionPixelSize(attrNameHash, defaultValue);
				if (paddingRight != 0) setPaddingRight(paddingRight);
				break;
			case thahn.java.agui.R.attr.View_paddingBottom:
				int paddingBottom = attrs.getDimensionPixelSize(attrNameHash, defaultValue);
				if (paddingBottom != 0) setPaddingBottom(paddingBottom);
				break;
			case thahn.java.agui.R.attr.View_minWidth:
				mMinWidth = attrs.getDimensionPixelSize(attrNameHash, defaultValue);
				break;
			case thahn.java.agui.R.attr.View_minHeight:
				mMinHeight = attrs.getDimensionPixelSize(attrNameHash, defaultValue);
				break;
			case thahn.java.agui.R.attr.View_gravity:
				mGravity = attrs.getIntFromEnum(attrNameHash, defaultValue);//getStringHash(attrNameHash, defaultValue);
				break;
			case thahn.java.agui.R.attr.View_focusable:
				isFocusable = attrs.getBoolean(attrNameHash, defaultValue>0?true:false);
				break;
			case thahn.java.agui.R.attr.View_clickable:
				setClickable(attrs.getBoolean(attrNameHash, defaultValue>0?true:false));
				break;
			case thahn.java.agui.R.attr.View_visibility:
				int visibility = attrs.getIntFromEnum(attrNameHash, defaultValue);
				if (visibility != GONE) {
					setFlags(visibility, VISIBILITY_MASK);
				} else {
					setVisibility(visibility);
				}
				break;
			}  
		}
//		attrs.recycle();
	}

	/**
	 * v.onMeasure(getWidth(), getHeight());<br>
	 * v.onPostMeasure();<br>
	 * v.layout(getLeft(), getTop(), getLeft() + v.getWidth(), getTop() + v.getHeight());<br>
	 * @param parentWidth
	 * @param parentHeight
	 */
	public void onMeasure(int parentWidth, int parentHeight) {
		if (mLayoutParam.width == LayoutParams.MATCH_PARENT || mLayoutParam.width == LayoutParams.FILL_PARENT) {
			mWidth = parentWidth;
		} else if (mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			mWidth = mBackground == null ? Global.NOT_YET : mBackground.getIntrinsicWidth();
			int temp = mWidth + mPaddingLeft + mPaddingRight;
			if (temp > parentWidth) mWidth = parentWidth;
			else mWidth = temp;
		} else {
			mWidth = mLayoutParam.width;
		}
		
		if (mLayoutParam.height == LayoutParams.MATCH_PARENT || mLayoutParam.height == LayoutParams.FILL_PARENT) {
			mHeight = parentHeight;
		} else if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			mHeight = mBackground == null ? Global.NOT_YET : mBackground.getIntrinsicHeight();
			int temp = mHeight + mPaddingTop + mPaddingBottom;
			if (temp > parentHeight) mHeight = parentHeight;
			else mHeight = temp;
		} else {
			mHeight = mLayoutParam.height;
		}
		
		mLayoutParam.registerLayoutObserver(this);
	}
	
	public void onPostMeasure(int availWidth, int availHeight) {
		if (getDrawingWidth() < mMinWidth) {
//			int temp = getWidth() + (mMinWidth - getDrawingWidth());
//			if (getParent() == null || temp <= getParent().getWidth()) {
//				mWidth = temp;
//			}
			if (mMinWidth > availWidth) {
				mWidth = availWidth;
			} else {
				mWidth = mMinWidth;
			}
		} 
		
		if (getDrawingHeight() < mMinHeight) {
//			int temp = getHeight() + (mMinHeight - getDrawingHeight());
//			if (getParent() == null || temp <= getParent().getHeight()) {
//				mHeight = temp;
//			}
			if (mMinHeight > availHeight) {
				mHeight = availHeight;
			} else {
				mHeight = mMinHeight;
			}
		}
		
		mLayoutParam.right = mLayoutParam.left + getWidth();
		mLayoutParam.bottom = mLayoutParam.top + getHeight();
	}
	
	
	public void onLayout(boolean isChanged, int l, int t, int r, int b) {
		mLayoutParam.setLayout(l, t, r, b);
	}

//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		
//	}
	
	public void arrange() {
		
	}

	/**
     * Offset this view's horizontal location by the specified amount of pixels.
     *
     * @param offset the number of pixels to offset the view by
     */
    public void offsetLeftAndRight(int offset) {
        if (offset != 0) {
//        	scrollTo(-offset, 0);
        	onLayout(true, getLeft() + offset, getTop(), getRight() + offset, getBottom());
        	
//            updateMatrix();
//            final boolean matrixIsIdentity = mTransformationInfo == null
//                    || mTransformationInfo.mMatrixIsIdentity;
//            if (matrixIsIdentity) {
//                if (mDisplayList != null) {
//                    invalidateViewProperty(false, false);
//                } else {
//                    final ViewGroup p = (ViewGroup) mParent;
//                    if (p != null) {// && mAttachInfo != null) {
//                        Rect r = new Rect();//mAttachInfo.mTmpInvalRect;
//                        int minLeft;
//                        int maxRight;
//                        if (offset < 0) {
//                            minLeft = getLeft() + offset;
//                            maxRight = getRight();
//                        } else {
//                            minLeft = getLeft();
//                            maxRight = getRight() + offset;
//                        }
//                        r.set(0, 0, maxRight - minLeft, getBottom() - getTop());
//                        p.invalidateChild(this, r);
//                    }
//                }
//            } else {
//                invalidateViewProperty(false, false);
//            }
//
//            mLeft += offset;
//            mRight += offset;
//            if (mDisplayList != null) {
//                mDisplayList.offsetLeftAndRight(offset);
//                invalidateViewProperty(false, false);
//            } else {
//                if (!matrixIsIdentity) {
//                    invalidateViewProperty(false, true);
//                }
//                invalidateParentIfNeeded();
//            }
        }
    }
    
    public void offsetTopAndBottom(int offset) {
        if (offset != 0) {
//        	scrollTo(0, -offset);
        	onLayout(true, getLeft(), getTop() + offset, getRight(), getBottom() + offset);
        }
    }
	/**
     * Set the scrolled position of your view. This will cause a call to
     * {@link #onScrollChanged(int, int, int, int)} and the view will be
     * invalidated.
     * @param x the x position to scroll to
     * @param y the y position to scroll to
     */
    public void scrollTo(int x, int y) {
        if (mScrollX != x || mScrollY != y) {
            int oldX = mScrollX;
            int oldY = mScrollY;
            mScrollX = x;
            mScrollY = y;
//            onScrollChanged(mScrollX, mScrollY, oldX, oldY);
//            if (!awakenScrollBars()) {
            invalidate();
//            }
        }
    }

    /**
     * Move the scrolled position of your view. This will cause a call to
     * {@link #onScrollChanged(int, int, int, int)} and the view will be
     * invalidated.
     * @param x the amount of pixels to scroll by horizontally
     * @param y the amount of pixels to scroll by vertically
     */
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX + x, mScrollY + y);
    }
	
    public void requestLayout() {
    	invalidate();
    }
    
	public void invalidate() {
		mContext.invalidate();
	}
	
	public void invalidateSelf() {
		mContext.invalidate();
	}
	
	protected void invalidateParentIfNeeded() {
		if (mParent != null) mParent.invalidate();
    }
	
	public Resources getResources() {
		return mContext.getResources();
	}
	
	public int getScrollX() {
		return mScrollX;
	}

	public int getScrollY() {
		return mScrollY;
	}
	
	public int getScrollableX() {
		return mScrollableX;
	}

	public int getScrollableY() {
		return mScrollableY;
	}

	public int getLeft() {
		return mLayoutParam.left;
	}

	public int getTop() {
		return mLayoutParam.top;
	}

	public int getRight() {
		return mLayoutParam.right; // in this value, pading right, left is calcurated
	}

	public int getBottom() {
		return mLayoutParam.bottom;
	}
	
	@AguiSpecific
	public int getDrawingLeft() {
		return getLeft() - mScrollX + getPaddingLeft();
	}
	
	@AguiSpecific
	public int getDrawingLeftWithoutScroll() {
		return getLeft() + getPaddingLeft();
	}

	@AguiSpecific
	public int getDrawingTop() {
		return getTop() - mScrollY + getPaddingTop();
	}
	
	@AguiSpecific
	public int getDrawingTopWithoutScroll() {
		return getTop() + getPaddingTop();
	}
	
	@AguiSpecific
	public int getDrawingRight() {
		int right = getLeft() + getWidth() + getPaddingRight() - mScrollX;
		if (mLayoutParam.width < 0) {
			right = getDrawingRightInternal(right);
		}
		return right;
	}
	
	@AguiSpecific
	public int getDrawingRightWithoutScroll() {
		int right = getLeft() + getWidth() + getPaddingRight();
		if (mLayoutParam.width < 0) {
			right = getDrawingRightInternal(right);
		}
		return right;
	}
	
	@AguiSpecific
	private int getDrawingRightInternal(int right) {
		int rightMax = 0;
		View parent = getParent();
		if (parent != null) {
			if (isFullExtended()) {
				rightMax = right;
			} else {
				rightMax = parent.getRight();
			}
		} else {
			rightMax = mContext.getWidth();
		}
		if (right > rightMax && getLeft() < rightMax) {
			right = rightMax - getPaddingRight();
			mWidth = rightMax - getLeft();
		} 
		return right;
	}
	
	@AguiSpecific
	public int getDrawingBottom() {
		int bottom = getTop() + getHeight() + getPaddingBottom() - mScrollY;
		if (mLayoutParam.height < 0) {
			bottom = getDrawingBottomInternal(bottom);
		}
		return bottom;
	}
	
	@AguiSpecific
	public int getDrawingBottomWithoutScroll() {
		int bottom = getTop() + getHeight() + getPaddingBottom();
		if (mLayoutParam.height < 0) {
			bottom = getDrawingBottomInternal(bottom);
		}
		return bottom;
	}
	
	@AguiSpecific
	private int getDrawingBottomInternal(int bottom) {
		int bottomMax = 0;
		View parent = getParent();
		if (parent != null) {
			if (isFullExtended()) {
				bottomMax = bottom;
			} else {
				bottomMax = parent.getBottom();
			}
		} else {
			bottomMax = mContext.getHeight();
		}
		if (bottom > bottomMax && getTop() < bottomMax) {
			bottom = bottomMax - getPaddingBottom();
			mHeight = bottomMax - getTop();
		} 
		return bottom;
	}
	
	@AguiSpecific
	public int getDrawingWidth() {
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}

	@AguiSpecific
	public int getDrawingHeight() {
		return getHeight() - getPaddingTop() - getPaddingBottom();
	}
	
	@AguiSpecific
	public int getPaddedLeft() {
		return getLeft() + getPaddingLeft();
	}

	@AguiSpecific
	public int getPaddedTop() {
		return getTop() + getPaddingTop();
	}
	
	@AguiSpecific
	public int getPaddedRight() {
		int right = getLeft() + getWidth() + getPaddingRight();
		int rightMax = 0;
		View parent = getParent();
		if (parent != null) {
			rightMax = parent.getRight();
		} else {
			rightMax = mContext.getWidth();
		}
		if (right > rightMax && getLeft() < rightMax) {
			right = rightMax - getPaddingRight();
			mWidth = rightMax - getLeft();
		} 
		return right;
	}
	
	@AguiSpecific
	public int getPaddedBottom() {
		int bottom = getTop() + getHeight() + getPaddingBottom();
		int bottomMax = 0;
		View parent = getParent();
		if (parent != null) {
			bottomMax = parent.getBottom();
		} else {
			bottomMax = mContext.getHeight();
		}
		if (bottom > bottomMax && getTop() < bottomMax) {
			bottom = bottomMax - getPaddingBottom();
			mHeight = bottomMax - getTop();
		} 
		return bottom;
	}
	
	@AguiSpecific
	protected int getPaddedWidth() {
		return getWidth() + getPaddingLeft() + getPaddingRight();
	}
	
	@AguiSpecific
	protected int getPaddedHeight() {
		return getHeight() + getPaddingTop() + getPaddingBottom();
	}
	
    /**
     * If the View draws content inside its padding and enables fading edges,
     * it needs to support padding offsets. Padding offsets are added to the
     * fading edges to extend the length of the fade so that it covers pixels
     * drawn inside the padding.
     *
     * Subclasses of this class should override this method if they need
     * to draw content inside the padding.
     *
     * @return True if padding offset must be applied, false otherwise.
     *
     * @see #getLeftPaddingOffset()
     * @see #getRightPaddingOffset()
     * @see #getTopPaddingOffset()
     * @see #getBottomPaddingOffset()
     *
     * @since CURRENT
     */
    protected boolean isPaddingOffsetRequired() {
        return false;
    }

    /**
     * Amount by which to extend the left fading region. Called only when
     * {@link #isPaddingOffsetRequired()} returns true.
     *
     * @return The left padding offset in pixels.
     *
     * @see #isPaddingOffsetRequired()
     *
     * @since CURRENT
     */
    protected int getLeftPaddingOffset() {
        return 0;
    }

    /**
     * Amount by which to extend the right fading region. Called only when
     * {@link #isPaddingOffsetRequired()} returns true.
     *
     * @return The right padding offset in pixels.
     *
     * @see #isPaddingOffsetRequired()
     *
     * @since CURRENT
     */
    protected int getRightPaddingOffset() {
        return 0;
    }

    /**
     * Amount by which to extend the top fading region. Called only when
     * {@link #isPaddingOffsetRequired()} returns true.
     *
     * @return The top padding offset in pixels.
     *
     * @see #isPaddingOffsetRequired()
     *
     * @since CURRENT
     */
    protected int getTopPaddingOffset() {
        return 0;
    }

    /**
     * Amount by which to extend the bottom fading region. Called only when
     * {@link #isPaddingOffsetRequired()} returns true.
     *
     * @return The bottom padding offset in pixels.
     *
     * @see #isPaddingOffsetRequired()
     *
     * @since CURRENT
     */
    protected int getBottomPaddingOffset() {
        return 0;
    }

	public void setId(int id) {
		mId = id;
	}

	public int getId() {
		return mId;
	}
	
	/**
     * Returns the context the view is running in, through which it can
     * access the current theme, resources, etc.
     *
     * @return The view's Context.
     */
    public final Context getContext() {
        return mContext;
    }

//	public void setWidth(int mWidth) {
//		this.mWidth = mWidth;
//	}
//	
//	public void setHeight(int mHeight) {
//		this.mHeight = mHeight;
//	}
	
	public int getWidth() {
		if (mWidth < 0) {
			mWidth = 0;
			onLayout(true, getLeft(), getTop(), getLeft() + getWidth(), getTop() + getHeight());
		}
		return mWidth;
	}

	public int getHeight() {
		if (mHeight < 0) {
			mHeight = 0;
			onLayout(true, getLeft(), getTop(), getLeft() + getWidth(), getTop() + getHeight());
		}
		return mHeight;
	}
	
	public final int getMeasuredWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
	
	public final int getMeasuredHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
	
	public int getPaddingLeft() {
		return mPaddingLeft;
	}

	public void setPaddingLeft(int mLeftPadding) {
		this.mPaddingLeft = mLeftPadding;
	}

	public int getPaddingTop() {
		return mPaddingTop;
	}

	public void setPaddingTop(int mTopPadding) {
		this.mPaddingTop = mTopPadding;
	}

	public int getPaddingRight() {
		return mPaddingRight;
	}

	public void setPaddingRight(int mRightPadding) {
		this.mPaddingRight = mRightPadding;
	}

	public int getPaddingBottom() {
		return mPaddingBottom;
	}

	public void setPaddingBottom(int mBottomPadding) {
		this.mPaddingBottom = mBottomPadding;
	}
	
	protected int getUsableWidth() {
		int ret = 0;
		int padding = getPaddingRight();
		int margin = mLayoutParam.rightMargin;
		View parent = getParent();
		for (;;) {
			if (parent != null) {
				padding += parent.getPaddingRight();
				margin += parent.getLayoutParams().rightMargin;
				if (parent.getLayoutParams().width > 0) {
					ret = parent.getWidth();
					break;
				} 
				parent = parent.getParent();
			} else {
				ret = mContext.getWidth();//Global.windowWidth;
				break;
			}
		}
		return ret - padding - margin - getPaddingRight() - getDrawingLeft();
	}

	public LayoutParams getLayoutParams() {
		return mLayoutParam;
	}

	public void setLayoutParams(LayoutParams params) {
		mLayoutParam = params;
		mLayoutParam.registerLayoutObserver(this);
		onLayoutChanged(mLayoutParam.left, mLayoutParam.top, mLayoutParam.right, mLayoutParam.bottom);
	}
	
	public void draw(Graphics g) {
//		if (isClip) {
//			if (View.this instanceof Button) {
//				g.setClip(getDrawingLeft(), getDrawingTop(), getWidth(), getHeight());
//			} else {
//				g.setClip(getLeft(), getTop(), getWidth(), getHeight());
//			}
//		} else {
//			Log.i("no clip");
//		}
		//
		g.setPaintMode();
		g.setFont(mFont);
		Graphics2D g2 = (Graphics2D) g;
		
		if (mCurrentAnimation != null ) {
			boolean isAniRunning = false;
			mCurTrans = new Transformation();
//			mCurTrans.getMatrix().setTranslate((float)mDefaultMatrix.getTranslateX(), (float)mDefaultMatrix.getTranslateY());
			translate((float) mDefaultMatrix.getTranslateX(), (float) mDefaultMatrix.getTranslateY());
			isAniRunning = mCurrentAnimation.getTransformation(System.currentTimeMillis(), mCurTrans);
			mFillAfter = mCurrentAnimation.getFillAfter();
			
			if (isAniRunning) {
				if ((mPrivateFlags & ANIMATION_STARTED) == 0) {
					onAnimationStart();
				}
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mCurTrans.getAlpha()));
				g2.setTransform(mCurTrans.getMatrix().matrix);
				invalidate();
			} else {
				if ((mPrivateFlags & ANIMATION_STARTED) != 0) {
					onAnimationEnd();
				}
				
				if (mFillAfter) {
					mFillAfterMatrix = mCurTrans;
				} 
			}
		} else {
			if (mParent != null && mParent.getAnimation() != null) {
				inheritParentAnimation(mParent.getAnimation());
			} else if (mFillAfter) { 
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mFillAfterMatrix.getAlpha()));
				g2.setTransform(mFillAfterMatrix.getMatrix().matrix);
			} else {
				g2.setTransform(mDefaultMatrix);
			}
		}
		 
		if (mId == Global.ROOT_ID && mContext.getBackgroundDrawable() != null) {
			mContext.getBackgroundDrawable().setBounds(0, 0, getWidth(), getHeight());
			mContext.getBackgroundDrawable().draw(g);
		}
		
		if (mBackground != null) {
			// FIXME : do only once, imageview에서 처럼 configurebound를 매번 하는 것이 아니라 수정 될 때만
			// maybe, in layout();		
			if (this instanceof Button) {
				mBackground.setBounds(getDrawingLeft(), getDrawingTop(), getDrawingLeft() + getDrawingWidth(), getDrawingTop() + getDrawingHeight());
			} else {
				mBackground.setBounds(getLeft(), getTop(), getLeft() + getWidth(), getTop() + getHeight());
			}
			mBackground.draw(g);
		}
		// FIXME : for debugging
		if (ApplicationSetting.getInstance().isDebug()) {
			g.setColor(Color.RED);
			g.drawRect(getLeft(), getTop(), getWidth(), getHeight());
			// g.setColor(Color.BLUE);
			// g.drawRect(getDrawingLeft(), getDrawingTop(), getDrawingRight()-getDrawingLeft(), getDrawingBottom()-getDrawingTop());
			g.setColor(Color.BLACK);
		}
		
		if (mHasScroll) {
			computeScroll();
		}
//		Log.i(toString());
	}
	
	public void onDraw(Graphics g) {
	}
	
	protected void translate(float x, float y) {
		mCurTrans.getMatrix().setTranslate(x, y);
	}
	
	/**
     * Called by a parent to request that a child update its values for mScrollX
     * and mScrollY if necessary. This will typically be done if the child is
     * animating a scroll using a {@link android.widget.Scroller Scroller}
     * object.
     */
    public void computeScroll() {
    }
	
    public boolean isLayoutRtl() {
        return false;//(getResolvedLayoutDirection() == LAYOUT_DIRECTION_RTL);
    }
    
	public boolean isPositioned() {
		return isPositioned;
	}

	// TODO : important function. change to private or package
	public void setPositioned(boolean isPositioned) {
		this.isPositioned = isPositioned;
	}

	public Object getTag() {
		return mTag;
	}

	public void setTag(Object mTag) {
		this.mTag = mTag;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mKeyListener != null) mKeyListener.onKey(this, event.getKeyCode(), event);
        return false;
    }
	
	/**
     * Pass the touch screen motion event down to the target view, or this
     * view if it is the target.
     *
     * @param event The motion event to be dispatched.
     * @return True if the event was handled by the view, false otherwise.
     */
	public boolean dispatchTouchEvent(MotionEvent event) {
		return mTouchListener!=null?mTouchListener.onTouch(this, event):false;
	}
	
	public View findViewById(int id) {
		if (id == mId) {
			return this;
		}
		return null;
	}
	
	public boolean performClick() {
        if (mOnClickListener != null) {
//            playSoundEffect(SoundEffectConstants.CLICK);
            mOnClickListener.onClick(this);
            return true;
        }

        return false;
	}
	
    /**
     * Call this view's OnLongClickListener, if it is defined. Invokes the context menu if the
     * OnLongClickListener did not consume the event.
     *
     * @return True if one of the above receivers consumed the event, false otherwise.
     */
    public boolean performLongClick() {
//        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);

        boolean handled = false;
        if (mOnLongClickListener != null) {
            handled = mOnLongClickListener.onLongClick(View.this);
            mHasPerformedLongPress = true; 
            setPressed(false);
			invalidate();
        }
        
//        if (!handled) {
//            handled = showContextMenu();
//        }
//        if (handled) {
//            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
//        }
        return handled;
    }
	
	public boolean contains(int x, int y) {
		boolean ret = false;
		if (getDrawingLeftWithoutScroll() <= x && getDrawingRightWithoutScroll() >= x 
				&& getDrawingTopWithoutScroll() <= y && getDrawingBottomWithoutScroll() >= y) {
			ret = true;
		}
		return ret;
	}

	public void setOnKeyListener(OnKeyListener listener) {
		mKeyListener = listener;
	}
	
	public void setOnClickListener(OnClickListener listener) {
		mOnClickListener = listener;
	}
	
	/**
     * Return whether this view has an attached OnClickListener.  Returns
     * true if there is a listener, false if there is none.
     */
    public boolean hasOnClickListeners() {
        return mOnClickListener != null;
    }
    
	/**
     * Register a callback to be invoked when this view is clicked and held. If this view is not
     * long clickable, it becomes long clickable.
     *
     * @param l The callback that will run
     *
     * @see #setLongClickable(boolean)
     */
    public void setOnLongClickListener(OnLongClickListener l) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        mOnLongClickListener = l;
    }
    
    /**
     * Return whether this view has an attached OnClickListener.  Returns
     * true if there is a listener, false if there is none.
     */
    public boolean hasOnLongClickListeners() {
        return mOnLongClickListener != null;
    }
	
	public void setOnTouchListener(OnTouchListener listener) {
		mTouchListener = listener;
	}
	
	/**
     * Return whether this view has an attached OnTouchListener.  Returns
     * true if there is a listener, false if there is none.
     */
    public boolean hasOnTouchListeners() {
        return mTouchListener != null;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	return false;
    }
    
	/**
	 * 
	 * @param event
	 * @return if true, consumed this event
	 */
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = false;
		int action = event.getAction();
		int buttonCode = event.getButtonCode();
		if (buttonCode == MotionEvent.BUTTON1) {
			ret = checkButton1(action, event);
		} 
		if (buttonCode == mContextBtnCode) {
			ret = checkContextMenu(action, event);
		}
		return ret;
	}
	
	private boolean checkButton1(int action, MotionEvent event) {
		boolean ret = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mHasPerformedLongPress = false;
			
			if (isFocusable) {
				mStateCallback.changedFocusable(this);
			}
			setDrawableState(getDrawableState(Drawable.STATE_ACTIVATED_TRUE)?
					Drawable.STATE_ACTIVATED_FALSE:Drawable.STATE_ACTIVATED_TRUE);
			setDrawableState(Drawable.STATE_FOCUSED_TRUE);
			setPressed(true);
			ret = dispatchTouchEvent(event);
			if (mBackground != null) {
				invalidate();
			}
			if (!isInScrollContainer()) {
				checkForLongClick(0);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// FIXME : overhead?
			setDrawableState(Drawable.STATE_PRESSED_TRUE);
			ret = dispatchTouchEvent(event);
			break;
		case MotionEvent.ACTION_UP:
			if (!mHasPerformedLongPress) {
				setPressed(false);
			}
			ret = dispatchTouchEvent(event);
			if (!mHasPerformedLongPress && mBackground != null) {
				invalidate();
			}
			break;
		case MotionEvent.ACTION_CLICK:
			ret = performClick();
			break;
		}
		return ret;
	}
	
	/**
     * @hide
     */
	@AguiDifferent
    public boolean isInScrollContainer() {
		if (this instanceof ScrollBar) {
			return true;
		}
        return false;
    }
	
	public boolean hasScrollContainer() {
		if (this instanceof AdapterView) {
			return true;
		}
		
		View parent = getParent();
		if (parent != null) {
			if (parent instanceof AdapterView) {
				return true;
			} else if (parent instanceof ViewGroup) {
				return parent.hasScrollContainer();
			}
		}
		return false;
	}

	private void checkForLongClick(int delayOffset) {
		if (isLongClickable()) {
			mHasPerformedLongPress = false;
            if (mPendingCheckForLongPress == null) {
                mPendingCheckForLongPress = new CheckForLongPress();
            }
            postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - delayOffset); 
		}
	}
	
    /**
     * Call this to force a view to update its drawable state. This will cause
     * drawableStateChanged to be called on this view. Views that are interested
     * in the new state should call getDrawableState.
     *
     * @see #drawableStateChanged
     * @see #getDrawableState
     */
    public void refreshDrawableState() {
//        mPrivateFlags |= PFLAG_DRAWABLE_STATE_DIRTY;
        drawableStateChanged();

        if (mParent != null) {
//            parent.childDrawableStateChanged(this);
        }
    }
    
    /**
     * This function is called whenever the state of the view changes in such
     * a way that it impacts the state of drawables being shown.
     *
     * <p>Be sure to call through to the superclass when overriding this
     * function.
     *
     * @see Drawable#setState(int[])
     */
    protected void drawableStateChanged() {
        if (mBackground != null) {
//        	for (int flag : Drawable.VIEW_STATE_SETS) {
//        		if (getPrivateFlags(mPrivateFlags, flag)) {
        			mBackground.setState(mDrawableState);
//        		}
//			}
        }
    }
    
    @AguiDifferent
    protected boolean[] getDrawableState() {
    	return mDrawableState;
    }
    
    @AguiSpecific
    protected boolean getDrawableState(int state) {
		for (int i = 0; i < Drawable.VIEW_STATE_SETS.length; i++) {
			if (Drawable.VIEW_STATE_SETS[i] == state) {
				return mDrawableState[i];
			}
		}
		return false;
	}
    
    @AguiSpecific
	protected void setDrawableState(int state) {
		for (int i = 0; i < Drawable.VIEW_STATE_SETS.length; i++) {
			if (Drawable.VIEW_STATE_SETS[i] == state) {
				mDrawableState[i] = true;
				mDrawableState[i + (i%2==0?+1:-1)] = false;
				break;
			}
		}
	}
	
	private boolean checkContextMenu(int action, MotionEvent event) {
		boolean ret = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (checkPopupMenu(event)) {
				return true;
			} else {
				mContext.removeContextMenuVisible();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (checkPopupMenu(event)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_CLICK:
			break;
		}
		return ret;
	}
	
	private boolean checkPopupMenu(final MotionEvent event) {
		boolean ret = true;
		if (mCreateContextMenuListener != null) {
			if (mContextMenu == null) {
				mContextMenu = new ContextMenu();
				mCreateContextMenuListener.onCreateContextMenu(mContextMenu, this, null);
			}
			if (event.getOriginalEvent().getComponent() != null) {
				if (mContextMenu != null) {
					mContext.setContextMenu(mContextMenu);
					// FIXME : use handler 
					post(new Runnable() {
						
						@Override
						public void run() {
							mContextMenu.show(event.getOriginalEvent().getComponent(), event.getX(), event.getY());		
						}
					});
				}
			}
		} else {
			ret = false;
		}
		
		return ret;
	}
	
	public boolean onScroll(MouseWheelEvent event) {
		return false;
	}
	
//    /**
//     * This is called during layout when the size of this view has changed. If
//     * you were just added to the view hierarchy, you're called with the old
//     * values of 0.
//     *
//     * @param w Current width of this view.
//     * @param h Current height of this view.
//     * @param oldw Old width of this view.
//     * @param oldh Old height of this view.
//     */
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//    	
//    }
	
	@Override
	public void onLayoutChanged(int l, int t, int r, int b) {
		mWidth = r - l;
		mHeight = b - t;
	}
	
    @Override
	public void onArrangeNeeded() {
    	if (getParent() != null) getParent().arrange();
	}
    
	/**
     * Caller is responsible for calling requestLayout if necessary.
     * (This allows addViewInLayout to not request a new layout.)
     */
    /*package*/ void assignParent(View parent) {
        if (mParent == null) {
            mParent = parent;
        } else if (parent == null) {
            mParent = null;
        } else {
        	mParent = parent;
//            throw new RuntimeException("view " + this + " being added, but" + " it already has a parent");
        }
    }
    
    /*package*/ void deassignParent() {
    	if (mParent != null) {
    		mParent = null;
    	}
    }
    
    public View getParent() {
    	return mParent;
    }
	
	public void setBackgroundResource(int resId) {
		setBackgroundDrawable(mContext.getResources().getDrawable(resId));
	}
	
	public void setBackgroundColor(int color) {
		setBackgroundDrawable(ColorDrawable.load(color));
	}
	
	public void setBackgroundDrawable(Drawable drawable) {
		mBackground = drawable;
		invalidate();
	}
	
	public Drawable getBackground() {
		return mBackground;
	}
	
	public void onAttachedToWindow() {
	}
	
	public void onDetachedFromWindow() {
	}

	/**
     * Sets the pressed state for this view.
     *
     * @see #isClickable()
     * @see #setClickable(boolean)
     *
     * @param pressed Pass true to set the View's internal state to "pressed", or false to reverts
     *        the View's internal state from a previously set "pressed" state.
     */
    public void setPressed(boolean pressed) {
        if (pressed) {
        	setDrawableState(Drawable.STATE_PRESSED_TRUE);
        	mPrivateFlags = makePrivateFlags(mPrivateFlags, PRESSED);
        } else {
        	setDrawableState(Drawable.STATE_PRESSED_FALSE);
        	mPrivateFlags = makePrivateFlagsFree(mPrivateFlags, PRESSED);
        }
        refreshDrawableState();
        // dispatchSetPressed(pressed);
    }
    
    public boolean isPressed() {
        return getPrivateFlags(mPrivateFlags, PRESSED);
    }
	
	public void setFocus(boolean focus) {
		this.isFocus = focus;
		if (mBackground != null) {
			if (focus) {
				setDrawableState(Drawable.STATE_FOCUSED_TRUE);
			} else {
				setDrawableState(Drawable.STATE_FOCUSED_FALSE);
			}
			refreshDrawableState();
		}
		invalidate();
	}
	
	public boolean isFocused() {
		return isFocus;
	}
	
	public void setFocusable(boolean isFocusable) {
		this.isFocusable = isFocusable;
	}
	
	public boolean getFocusable() {
		return this.isFocusable;
	}

	/**
	 * for internal
	 * @return
	 */
	@AguiSpecific
	protected boolean isFullExtended() {
		return getParent() instanceof AbsListView;
	}
	
    /**
     * Indicates whether this view reacts to click events or not.
     *
     * @return true if the view is clickable, false otherwise
     *
     * @see #setClickable(boolean)
     * @attr ref android.R.styleable#View_clickable
     */
    public boolean isClickable() {
        return (mViewFlags & CLICKABLE) == CLICKABLE;
    }

    /**
     * Enables or disables click events for this view. When a view
     * is clickable it will change its state to "pressed" on every click.
     * Subclasses should set the view clickable to visually react to
     * user's clicks.
     *
     * @param clickable true to make the view clickable, false otherwise
     *
     * @see #isClickable()
     * @attr ref android.R.styleable#View_clickable
     */
    public void setClickable(boolean clickable) {
        setFlags(clickable ? CLICKABLE : 0, CLICKABLE);
    }	
    
    /**
     * Indicates whether this view reacts to long click events or not.
     *
     * @return true if the view is long clickable, false otherwise
     *
     * @see #setLongClickable(boolean)
     * @attr ref android.R.styleable#View_longClickable
     */
    public boolean isLongClickable() {
        return (mViewFlags & LONG_CLICKABLE) == LONG_CLICKABLE;
    }

    /**
     * Enables or disables long click events for this view. When a view is long
     * clickable it reacts to the user holding down the button for a longer
     * duration than a tap. This event can either launch the listener or a
     * context menu.
     *
     * @param longClickable true to make the view long clickable, false otherwise
     * @see #isLongClickable()
     * @attr ref android.R.styleable#View_longClickable
     */
    public void setLongClickable(boolean longClickable) {
        setFlags(longClickable ? LONG_CLICKABLE : 0, LONG_CLICKABLE);
    }

	
	/**
     * Changes the activated state of this view. A view can be activated or not.
     * Note that activation is not the same as selection.  Selection is
     * a transient property, representing the view (hierarchy) the user is
     * currently interacting with.  Activation is a longer-term state that the
     * user can move views in and out of.  For example, in a list view with
     * single or multiple selection enabled, the views in the current selection
     * set are activated.  (Um, yeah, we are deeply sorry about the terminology
     * here.)  The activated state is propagated down to children of the view it
     * is set on.
     *
     * @param activated true if the view must be activated, false otherwise
     */
    public void setActivated(boolean activated) {
    	setActivated(activated, true);
    }
    
    public void setActivated(boolean activated, boolean invalidated) {
    	boolean is;
    	if (activated) {
    		is = getDrawableState(Drawable.STATE_ACTIVATED_TRUE);
    	} else {
    		is = getDrawableState(Drawable.STATE_ACTIVATED_FALSE)?false:true;
    	}
    	if (is != activated) {
    		setDrawableState(activated?Drawable.STATE_ACTIVATED_TRUE:Drawable.STATE_ACTIVATED_FALSE);
    		refreshDrawableState();
    		if (invalidated) {
    			invalidate();
    		}
    	}
    }
	
	public void setVisibility(int is) {
        setFlags(is, VISIBILITY_MASK);
        mContext.arrangeView();
//        onArrangeNeeded();
    }
	
	public int getVisibility() {
        return mViewFlags & VISIBILITY_MASK;
    }
	
	public int getMinWidth() {
		return mMinWidth;
	}

	public void setMinWidth(int mMinWidth) {
		this.mMinWidth = mMinWidth;
		mWidth = Math.max(mWidth, mMinWidth);
	}

	public int getMinHeight() {
		return mMinHeight;
	}

	public void setMinHeight(int mMinHeight) {
		this.mMinHeight = mMinHeight;
		mHeight = Math.max(mHeight, mMinHeight);
	}
	
	public void setGravity(int gravity) {
		if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
			gravity |= Gravity.START;
		}
		if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
			gravity |= Gravity.TOP;
		}

		boolean newLayout = false;

		if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) !=
				(mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK)) {
			newLayout = true;
		}

		if (gravity != mGravity) {
			mGravity = gravity;
			invalidate();
		}
	}
	
	public int getGravity() {
		return mGravity;
	}

	private void setFlags(int flags, int mask) {
		mViewFlags = (mViewFlags & ~mask) | (flags & mask);
	}
	
	protected int makePrivateFlags(int flags, int value) {
		flags |= value;
		return flags;
	}

	protected int makePrivateFlagsFree(int flags, int value) {
		flags &= ~value;
		return flags;
	}
	
	protected int makePrivateFlagsToggle(int flags, int value) {
		if (getPrivateFlags(flags, value)) {
			flags = makePrivateFlagsFree(flags, value);
		} else {
			flags = makePrivateFlags(flags, value);
		}
		return flags;
	}
	
	protected boolean getPrivateFlags(int flags, int value) {
		return (flags & value) == value;
	}
	
    /**
     * If your view subclass is displaying its own Drawable objects, it should
     * override this function and return true for any Drawable it is
     * displaying.  This allows animations for those drawables to be
     * scheduled.
     *
     * <p>Be sure to call through to the super class when overriding this
     * function.
     *
     * @param who The Drawable to verify.  Return true if it is one you are
     *            displaying, else return the result of calling through to the
     *            super class.
     *
     * @return boolean If true than the Drawable is being displayed in the
     *         view; else false and it is not allowed to animate.
     *
     * @see #unscheduleDrawable(android.graphics.drawable.Drawable)
     * @see #drawableStateChanged()
     */
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground;
    }
	
	public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
		setOnCreateContextMenuListener(l, MotionEvent.BUTTON3);
//        if (!isLongClickable()) {
//            setLongClickable(true);
//        }
//        getListenerInfo().mOnCreateContextMenuListener = l;
    }
	
	public void setOnCreateContextMenuListener(OnCreateContextMenuListener l, int btnCode) {
		mContextBtnCode = btnCode; 
		mCreateContextMenuListener = l;
    }
	
	/**
     * Start an action mode.
     *
     * @param callback Callback that will control the lifecycle of the action mode
     * @return The new action mode if it is started, null otherwise
     *
     * @see ActionMode
     */
    public ActionMode startActionMode(ActionMode.Callback callback) {
    	// TODO : implements 
    	return new TitleActionMode();
//        return getParent().startActionModeForChild(this, callback);
    }
	
    public Animation getAnimation() {
        return mCurrentAnimation;
    }

    public void startAnimation(Animation animation) {
        animation.setStartTime(Animation.START_ON_FIRST_FRAME);
        setAnimation(animation);
//        invalidateParentIfNeeded();
        invalidate();
    }

    public void clearAnimation() {
        if (mCurrentAnimation != null) {
            mCurrentAnimation.detach();
        }
        mCurrentAnimation = null;
//        invalidateParentIfNeeded();
    }
	
    public void inheritParentAnimation(Animation animation) {
    	mCurrentAnimation = animation;
    }
    
	public void setAnimation(Animation animation) {
        mCurrentAnimation = animation;
        if (animation != null) {
            animation.reset();
            if (mParent != null) { 
            	animation.initialize(getWidth(), getHeight(), mParent.getWidth(), mParent.getHeight());
            } else {
            	animation.initialize(getWidth(), getHeight(), getWidth(), getHeight());
            }
        }
    }

	/**
     * Invoked by a parent ViewGroup to notify the start of the animation
     * currently associated with this view. If you override this method,
     * always call super.onAnimationStart();
     *
     * @see #setAnimation(android.view.animation.Animation)
     * @see #getAnimation()
     */
    protected void onAnimationStart() {
        mPrivateFlags |= ANIMATION_STARTED;
    }

    /**
     * Invoked by a parent ViewGroup to notify the end of the animation
     * currently associated with this view. If you override this method,
     * always call super.onAnimationEnd();
     *
     * @see #setAnimation(android.view.animation.Animation)
     * @see #getAnimation()
     */
    protected void onAnimationEnd() {
        mPrivateFlags &= ~ANIMATION_STARTED;
        mCurrentAnimation = null;
    }
	
    public void post(Runnable action) {
    	new Thread(action).start();
//        Handler handler;
//        AttachInfo attachInfo = mAttachInfo;
//        if (attachInfo != null) {
//            handler = attachInfo.mHandler;
//        } else {
//            // Assume that post will succeed later
//            ViewRootImpl.getRunQueue().post(action);
//            return true;
//        }
//
//        return handler.post(action);
    }

    public void postInvalidateDelayed(long delayMillis) {
    	postDelayed(null, delayMillis, true);
    }
    
    public void postInvalidateDelayed(long delayMillis, int left, int top, int right, int bottom) {
    	postDelayed(null, delayMillis, true);
    }
    
    public void postDelayed(Runnable action, long delayMillis) {
    	postDelayed(action, delayMillis, false);
    }
    
    public void postDelayed(Runnable action, long delayMillis, boolean invalidate) {
    	final Runnable r = action;
    	final long delay = delayMillis;
    	final boolean isInvalidate = invalidate;
    	Thread t = new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (r != null) r.run();
				if (isInvalidate) invalidate();
			}
    	};
        t.start();
    }
    
    public View inflate(Context context, int layoutId, ViewGroup parent) {
    	LayoutInflater factory = LayoutInflater.from(context);
        return factory.inflate(layoutId, parent);
    }
    
    /**
     * Restore this view hierarchy's frozen state from the given container.
     *
     * @param container The SparseArray which holds previously frozen states.
     *
     * @see #saveHierarchyState(android.util.SparseArray)
     * @see #dispatchRestoreInstanceState(android.util.SparseArray)
     * @see #onRestoreInstanceState(android.os.Parcelable)
     */
    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        dispatchRestoreInstanceState(container);
    }

    /**
     * Called by {@link #restoreHierarchyState(android.util.SparseArray)} to retrieve the
     * state for this view and its children. May be overridden to modify how restoring
     * happens to a view's children; for example, some views may want to not store state
     * for their children.
     *
     * @param container The SparseArray which holds previously saved state.
     *
     * @see #dispatchSaveInstanceState(android.util.SparseArray)
     * @see #restoreHierarchyState(android.util.SparseArray)
     * @see #onRestoreInstanceState(android.os.Parcelable)
     */
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        if (mId != NO_ID) {
            Parcelable state = container.get(mId);
            if (state != null) {
                // Log.i("View", "Restoreing #" + Integer.toHexString(mID)
                // + ": " + state);
                mPrivateFlags &= ~PFLAG_SAVE_STATE_CALLED;
                onRestoreInstanceState(state);
                if ((mPrivateFlags & PFLAG_SAVE_STATE_CALLED) == 0) {
                    throw new IllegalStateException(
                            "Derived class did not call super.onRestoreInstanceState()");
                }
            }
        }
    }
    
    /**
     * Hook allowing a view to re-apply a representation of its internal state that had previously
     * been generated by {@link #onSaveInstanceState}. This function will never be called with a
     * null state.
     *
     * @param state The frozen state that had previously been returned by
     *        {@link #onSaveInstanceState}.
     *
     * @see #onSaveInstanceState()
     * @see #restoreHierarchyState(android.util.SparseArray)
     * @see #dispatchRestoreInstanceState(android.util.SparseArray)
     */
    protected void onRestoreInstanceState(Parcelable state) {
        mPrivateFlags |= PFLAG_SAVE_STATE_CALLED;
//        if (state != BaseSavedState.EMPTY_STATE && state != null) {
//            throw new IllegalArgumentException("Wrong state class, expecting View State but "
//                    + "received " + state.getClass().toString() + " instead. This usually happens "
//                    + "when two views of different type have the same id in the same hierarchy. "
//                    + "This view's id is " + ViewDebug.resolveId(mContext, getId()) + ". Make sure "
//                    + "other views do not use the same id.");
//        }
    }
    
    /**
     * Creates an image that the system displays during the drag and drop
     * operation. This is called a &quot;drag shadow&quot;. The default implementation
     * for a DragShadowBuilder based on a View returns an image that has exactly the same
     * appearance as the given View. The default also positions the center of the drag shadow
     * directly under the touch point. If no View is provided (the constructor with no parameters
     * is used), and {@link #onProvideShadowMetrics(Point,Point) onProvideShadowMetrics()} and
     * {@link #onDrawShadow(Canvas) onDrawShadow()} are not overriden, then the
     * default is an invisible drag shadow.
     * <p>
     * You are not required to use the View you provide to the constructor as the basis of the
     * drag shadow. The {@link #onDrawShadow(Canvas) onDrawShadow()} method allows you to draw
     * anything you want as the drag shadow.
     * </p>
     * <p>
     *  You pass a DragShadowBuilder object to the system when you start the drag. The system
     *  calls {@link #onProvideShadowMetrics(Point,Point) onProvideShadowMetrics()} to get the
     *  size and position of the drag shadow. It uses this data to construct a
     *  {@link android.graphics.Canvas} object, then it calls {@link #onDrawShadow(Canvas) onDrawShadow()}
     *  so that your application can draw the shadow image in the Canvas.
     * </p>
     *
     * <div class="special reference">
     * <h3>Developer Guides</h3>
     * <p>For a guide to implementing drag and drop features, read the
     * <a href="{@docRoot}guide/topics/ui/drag-drop.html">Drag and Drop</a> developer guide.</p>
     * </div>
     */
    public static class DragShadowBuilder {
        private final WeakReference<View> mView;

        /**
         * Constructs a shadow image builder based on a View. By default, the resulting drag
         * shadow will have the same appearance and dimensions as the View, with the touch point
         * over the center of the View.
         * @param view A View. Any View in scope can be used.
         */
        public DragShadowBuilder(View view) {
            mView = new WeakReference<View>(view);
        }

        /**
         * Construct a shadow builder object with no associated View.  This
         * constructor variant is only useful when the {@link #onProvideShadowMetrics(Point, Point)}
         * and {@link #onDrawShadow(Canvas)} methods are also overridden in order
         * to supply the drag shadow's dimensions and appearance without
         * reference to any View object. If they are not overridden, then the result is an
         * invisible drag shadow.
         */
        public DragShadowBuilder() {
            mView = new WeakReference<View>(null);
        }

        /**
         * Returns the View object that had been passed to the
         * {@link #View.DragShadowBuilder(View)}
         * constructor.  If that View parameter was {@code null} or if the
         * {@link #View.DragShadowBuilder()}
         * constructor was used to instantiate the builder object, this method will return
         * null.
         *
         * @return The View object associate with this builder object.
         */
        @SuppressWarnings({"JavadocReference"})
        final public View getView() {
            return mView.get();
        }

        /**
         * Provides the metrics for the shadow image. These include the dimensions of
         * the shadow image, and the point within that shadow that should
         * be centered under the touch location while dragging.
         * <p>
         * The default implementation sets the dimensions of the shadow to be the
         * same as the dimensions of the View itself and centers the shadow under
         * the touch point.
         * </p>
         *
         * @param shadowSize A {@link android.graphics.Point} containing the width and height
         * of the shadow image. Your application must set {@link android.graphics.Point#x} to the
         * desired width and must set {@link android.graphics.Point#y} to the desired height of the
         * image.
         *
         * @param shadowTouchPoint A {@link android.graphics.Point} for the position within the
         * shadow image that should be underneath the touch point during the drag and drop
         * operation. Your application must set {@link android.graphics.Point#x} to the
         * X coordinate and {@link android.graphics.Point#y} to the Y coordinate of this position.
         */
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            final View view = mView.get();
            if (view != null) {
                shadowSize.setLocation(view.getWidth(), view.getHeight());
                shadowTouchPoint.setLocation(shadowSize.x / 2, shadowSize.y / 2);
            } else {
                Log.e("View", "Asked for drag thumb metrics but no view");
            }
        }

        /**
         * Draws the shadow image. The system creates the {@link android.graphics.Canvas} object
         * based on the dimensions it received from the
         * {@link #onProvideShadowMetrics(Point, Point)} callback.
         *
         * @param canvas A {@link android.graphics.Canvas} object in which to draw the shadow image.
         */
        public void onDrawShadow(Graphics g) {
            final View view = mView.get();
            if (view != null) {
                view.draw(g);
            } else {
                Log.e("View", "Asked to draw drag shadow but no view");
            }
        }
    }
    
    /**
     * Starts a drag and drop operation. When your application calls this method, it passes a
     * {@link android.view.View.DragShadowBuilder} object to the system. The
     * system calls this object's {@link DragShadowBuilder#onProvideShadowMetrics(Point, Point)}
     * to get metrics for the drag shadow, and then calls the object's
     * {@link DragShadowBuilder#onDrawShadow(Canvas)} to draw the drag shadow itself.
     * <p>
     *  Once the system has the drag shadow, it begins the drag and drop operation by sending
     *  drag events to all the View objects in your application that are currently visible. It does
     *  this either by calling the View object's drag listener (an implementation of
     *  {@link android.view.View.OnDragListener#onDrag(View,DragEvent) onDrag()} or by calling the
     *  View object's {@link android.view.View#onDragEvent(DragEvent) onDragEvent()} method.
     *  Both are passed a {@link android.view.DragEvent} object that has a
     *  {@link android.view.DragEvent#getAction()} value of
     *  {@link android.view.DragEvent#ACTION_DRAG_STARTED}.
     * </p>
     * <p>
     * Your application can invoke startDrag() on any attached View object. The View object does not
     * need to be the one used in {@link android.view.View.DragShadowBuilder}, nor does it need to
     * be related to the View the user selected for dragging.
     * </p>
     * @param data A {@link android.content.ClipData} object pointing to the data to be
     * transferred by the drag and drop operation.
     * @param shadowBuilder A {@link android.view.View.DragShadowBuilder} object for building the
     * drag shadow.
     * @param myLocalState An {@link java.lang.Object} containing local data about the drag and
     * drop operation. This Object is put into every DragEvent object sent by the system during the
     * current drag.
     * <p>
     * myLocalState is a lightweight mechanism for the sending information from the dragged View
     * to the target Views. For example, it can contain flags that differentiate between a
     * a copy operation and a move operation.
     * </p>
     * @param flags Flags that control the drag and drop operation. No flags are currently defined,
     * so the parameter should be set to 0.
     * @return {@code true} if the method completes successfully, or
     * {@code false} if it fails anywhere. Returning {@code false} means the system was unable to
     * do a drag, and so no drag operation is in progress.
     */
    public final boolean startDrag(DragShadowBuilder shadowBuilder,
            Object myLocalState, int flags) {
        if (ViewDebug.DEBUG_DRAG) {
            Log.d(TAG, "startDrag: data=" + " flags=" + flags);
        }
        boolean okay = false;

        Point shadowSize = new Point();
        Point shadowTouchPoint = new Point();
        shadowBuilder.onProvideShadowMetrics(shadowSize, shadowTouchPoint);

        if ((shadowSize.x < 0) || (shadowSize.y < 0) ||
                (shadowTouchPoint.x < 0) || (shadowTouchPoint.y < 0)) {
            throw new IllegalStateException("Drag shadow dimensions must not be negative");
        }

        if (ViewDebug.DEBUG_DRAG) {
            Log.d(TAG, "drag shadow: width=" + shadowSize.x + " height=" + shadowSize.y
                    + " shadowX=" + shadowTouchPoint.x + " shadowY=" + shadowTouchPoint.y);
        }
        try {
            try {
//                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//                shadowBuilder.onDrawShadow(canvas);
            } finally {
            }
            if (ViewDebug.DEBUG_DRAG) Log.d(TAG, "performDrag returned " + okay);
        } catch (Exception e) {
            Log.e(TAG, "Unable to initiate drag");
//            surface.destroy();
        }

        return okay;
    }
    
    /**
     * Handles drag events sent by the system following a call to
     * {@link android.view.View#startDrag(ClipData,DragShadowBuilder,Object,int) startDrag()}.
     *<p>
     * When the system calls this method, it passes a
     * {@link android.view.DragEvent} object. A call to
     * {@link android.view.DragEvent#getAction()} returns one of the action type constants defined
     * in DragEvent. The method uses these to determine what is happening in the drag and drop
     * operation.
     * @param event The {@link android.view.DragEvent} sent by the system.
     * The {@link android.view.DragEvent#getAction()} method returns an action type constant defined
     * in DragEvent, indicating the type of drag event represented by this object.
     * @return {@code true} if the method was successful, otherwise {@code false}.
     * <p>
     *  The method should return {@code true} in response to an action type of
     *  {@link android.view.DragEvent#ACTION_DRAG_STARTED} to receive drag events for the current
     *  operation.
     * </p>
     * <p>
     *  The method should also return {@code true} in response to an action type of
     *  {@link android.view.DragEvent#ACTION_DROP} if it consumed the drop, or
     *  {@code false} if it didn't.
     * </p>
     */
    public boolean onDragEvent(DragEvent event) {
        return false;
    }

    /**
     * Detects if this View is enabled and has a drag event listener.
     * If both are true, then it calls the drag event listener with the
     * {@link android.view.DragEvent} it received. If the drag event listener returns
     * {@code true}, then dispatchDragEvent() returns {@code true}.
     * <p>
     p* For all other cases, the method calls the
     * {@link android.view.View#onDragEvent(DragEvent) onDragEvent()} drag event handler
     * method and returns its result.
     * </p>
     * <p>
     * This ensures that a drag event is always consumed, even if the View does not have a drag
     * event listener. However, if the View has a listener and the listener returns true, then
     * onDragEvent() is not called.
     * </p>
     */
    public boolean dispatchDragEvent(DragEvent event) {
        //noinspection SimplifiableIfStatement
        if (mOnDragListener != null //&& (mViewFlags & ENABLED_MASK) == ENABLED
                && mOnDragListener.onDrag(this, event)) {
            return true;
        }
        return onDragEvent(event);
    }

    boolean canAcceptDrag() {
        return (mPrivateFlags & DRAG_CAN_ACCEPT) != 0;
    }
    
	/**
     * Interface definition for a callback to be invoked when the context menu
     * for this view is being built.
     */
    public interface OnCreateContextMenuListener {
        /**
         * Called when the context menu for this view is being built. It is not
         * safe to hold onto the menu after this method returns.
         *
         * @param menu The context menu that is being built
         * @param v The view for which the context menu is being built
         * @param menuInfo Extra information about the item for which the
         *            context menu should be shown. This information will vary
         *            depending on the class of v.
         */
        void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo);
    }
    
    /**
     * Interface definition for a callback to be invoked when a drag is being dispatched
     * to this view.  The callback will be invoked before the hosting view's own
     * onDrag(event) method.  If the listener wants to fall back to the hosting view's
     * onDrag(event) behavior, it should return 'false' from this callback.
     *
     * <div class="special reference">
     * <h3>Developer Guides</h3>
     * <p>For a guide to implementing drag and drop features, read the
     * <a href="{@docRoot}guide/topics/ui/drag-drop.html">Drag and Drop</a> developer guide.</p>
     * </div>
     */
    public interface OnDragListener {
        /**
         * Called when a drag event is dispatched to a view. This allows listeners
         * to get a chance to override base View behavior.
         *
         * @param v The View that received the drag event.
         * @param event The {@link android.view.DragEvent} object for the drag event.
         * @return {@code true} if the drag event was handled successfully, or {@code false}
         * if the drag event was not handled. Note that {@code false} will trigger the View
         * to call its {@link #onDragEvent(DragEvent) onDragEvent()} handler.
         */
        boolean onDrag(View v, DragEvent event);
    }

	@Override
	public String toString() {
		String ret = new StringBuilder().append(getClass().getName()).append(", id : ").append(getId())
				.append(" / ").append(getLeft())
				.append(", ").append(getTop())
				.append(", ").append(getRight())
				.append(", ").append(getBottom())
				.append(" | ").append(getWidth())
				.append(", ").append(getHeight())
				.toString();
		return ret;
	}
	
    /*package*/ class CheckForLongPress implements Runnable {

        public void run() {
            if (isPressed()) {
                performLongClick();
            }
        }
    }
	
	protected class LayoutPolicy {
		
		public void checkChildMaxSizePolicy() {
			if (View.this instanceof ViewGroup) {
				View[] views = ((ViewGroup) View.this).getChildren();
				int maxRight = -1;
				int maxBottom = -1;
				for (int i = 0; i < views.length; i++) {
					if (views[i].getVisibility() == View.GONE) continue;
					
					int right = views[i].getRight() + views[i].getLayoutParams().rightMargin;
					int bottom = views[i].getBottom() + views[i].getLayoutParams().bottomMargin;;
					maxRight = Math.max(right, maxRight);
					maxBottom = Math.max(bottom, maxBottom);
				}
				if (mLayoutParam.width == LayoutParams.WRAP_CONTENT && getWidth() != maxRight - getLeft()) {
					mWidth = maxRight - getLeft(); 
				}
				if (mLayoutParam.height == LayoutParams.WRAP_CONTENT && getHeight() != maxBottom - getBottom()) {
					mHeight = maxBottom - getTop();
				}
			}
		}
		
		public void checkLayoutMaxSizePolicy() {
			// don't touch because of padding
			int left2 = getLeft();
			int top2 = getTop();
			// because already size is corretly changed by getDrawingRight, so don't use
			// in previous step, paddingright, bottom is not handled;
			int right2 = getLeft() + getWidth() + getPaddingRight();// + mLayoutParam.rightMargin;
			int bottom2 = getTop() + getHeight() + getPaddingBottom();// + mLayoutParam.bottomMargin;
			boolean isChanged = false;
			// maybe, almost relativelayout 
			if (getParent() != null && !isFullExtended()
					&& getParent().getDrawingWidth() > 0 && getParent().getDrawingHeight() > 0) {
				if (getParent().mOverScreen) {
					if (mLayoutParam.width < 0 && right2 > getParent().getDrawingRightWithoutScroll()) {
						isChanged = true;
						right2 = getParent().getDrawingRightWithoutScroll() - mLayoutParam.rightMargin;
					}
				} else if (mLayoutParam.width < 0 && right2 > getParent().getDrawingRight()) {
					isChanged = true;
					 right2 = getParent().getDrawingRight() - mLayoutParam.rightMargin;
				} else if (right2 != getRight()) {
					isChanged = true;
				} else if (right2 - left2 != getWidth()) {
					isChanged = true;
				}
				
				if (getParent().mOverScreen) {
					if (mLayoutParam.height < 0 && bottom2 > getParent().getDrawingBottomWithoutScroll()) {
						isChanged = true;
						bottom2 = getParent().getDrawingBottomWithoutScroll() - mLayoutParam.bottomMargin;
					}
				} else if (mLayoutParam.height < 0 && bottom2 > getParent().getDrawingBottom()) {
					// if ListView is parent 
					isChanged = true;
					bottom2 = getParent().getDrawingBottom() - mLayoutParam.bottomMargin;
				} else if (bottom2 != getBottom()) {
					isChanged = true;
				} else if (bottom2 - top2 != getHeight()) {
					isChanged = true;
				}
			}
			if (isChanged) {
				onLayout(true, left2, top2, right2, bottom2);
			}
		}
	}
}
