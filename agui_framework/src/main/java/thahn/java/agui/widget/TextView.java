package thahn.java.agui.widget;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import thahn.java.agui.annotation.AguiSpecific;
import thahn.java.agui.app.ApplicationSetting;
import thahn.java.agui.app.Context;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.graphics.Color;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.graphics.Typeface;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.Gravity;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.MouseWheelEvent;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.view.ViewGroup.LayoutParams;
import thahn.java.agui.widget.ScrollBar.OnThumbListener;
import thahn.java.agui.widget.ScrollHelper.ScrollCondition;
import thahn.java.agui.widget.TextView.TextController.TextLine;

import com.google.common.base.Strings;


public class TextView extends View {

	static final int 											SCROLL_ENABLE_MASK 		= 0x00000001;
	
	/*package*/ TextController									mTextController;
	/*package*/ int												mTextFlag;
	private java.awt.Color										mTextColor;
	private int													mTextSize;
	/*package*/ boolean 										isSingleLine;
	/*package*/ boolean 										isAutoScroll;

	private Drawables 											mDrawables;
	
	public TextView(Context context) {
		this(context, new AttributeSet(context));
	}

	public TextView(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_TEXT_VIEW_CODE);
	}
	
	public TextView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	public void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		String text = attrs.getString(thahn.java.agui.R.attr.TextView_text, "");
		isSingleLine = attrs.getBoolean(thahn.java.agui.R.attr.TextView_singleLine, false);
		
		mTextColor = Color.toAwtColor(attrs.getColor(thahn.java.agui.R.attr.TextView_textColor, Color.BLACK));
		mTextSize = attrs.getDimensionPixelSize(thahn.java.agui.R.attr.TextView_textSize, 12);
		
		String hint = attrs.getString(thahn.java.agui.R.attr.TextView_hint, null);
		int style = attrs.getIntFromEnum(thahn.java.agui.R.attr.TextView_textStyle, Typeface.NORMAL);
		
        Drawable drawableLeft = attrs.getDrawable(thahn.java.agui.R.attr.TextView_drawableLeft);
        Drawable drawableTop = attrs.getDrawable(thahn.java.agui.R.attr.TextView_drawableTop);
        Drawable drawableRight = attrs.getDrawable(thahn.java.agui.R.attr.TextView_drawableRight);
        Drawable drawableBottom = attrs.getDrawable(thahn.java.agui.R.attr.TextView_drawableBottom);
//        Drawable drawableStart = attrs.getDrawable(thahn.java.agui.R.attr.TextView_drawableStart);
//        Drawable drawableEnd = attrs.getDrawable(thahn.java.agui.R.attr.TextView_drawableEnd);
        int drawablePadding = attrs.getDimensionPixelSize(thahn.java.agui.R.attr.TextView_drawablePadding, 0);

        // This call will save the initial left/right drawables
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
//        setRelativeDrawablesIfNeeded(drawableStart, drawableEnd);
        setCompoundDrawablePadding(drawablePadding);
        
		
		mTextFlag |= SCROLL_ENABLE_MASK;
		if (hint != null) {
			setHint(hint);
		}
		setTextStyle(style);
		setText(text);
	}

	@Override
	public void initDefault() {
		super.initDefault();
		mTextController = new TextController();
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
		
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			if (mTextController.textBuilder.size() == 0 && mTextController.hintText != null) {
				mHeight = mTextController.hintBuilder.size() * mFontMetrics.getAscent();
			} else {
				mTextController.onMeasureWrapHeight();
			}
			
			int paddedHeight = getPaddedHeight();
			if (paddedHeight > parentHeight) {
				mHeight = parentHeight;
			} else {
				mHeight = paddedHeight;
			}
		}
		
		if (mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			if (mTextController.textBuilder.size() == 0 && mTextController.hintText != null) {
				int temp = -1;
				for (TextLine hint : mTextController.hintBuilder) {
					int hintWidth = mFontMetrics.stringWidth(hint.textLine.toString());
					temp = Math.max(hintWidth, temp);
				}
				mWidth = temp;
			} else {
				int textWidth = mTextController.getTextWidth();
				mWidth = textWidth;
			}
			
			int paddedWidth = getPaddedWidth();
			if (paddedWidth > parentWidth || mTextController.getTextBuilder().size() > 1) {
				mWidth = parentWidth;
			} else {
				mWidth = paddedWidth;
			}
		}
	}

	@Override
	public void onLayout(boolean isChanged, int l, int t, int r, int b) {
		super.onLayout(isChanged, l, t, r, b);
	}
	
	@Override
	public void arrange() {
		super.arrange();
		
		if (getDrawingWidth() != mTextController.getLimitWidth() + getHorizontalDrawableHPadding()) {
			mTextController.setLimitWidth(getDrawingWidth() - getHorizontalDrawableHPadding());
			mTextController.setText(getText(), true);
		} else {
			mTextController.setText(getText(), false);
		}
		
		if (!isSingleLine) {
			arrangeScroll();
		}
		
		onPostMeasure(getDrawingWidth(), getDrawingHeight());
	}
	
	private void arrangeScroll() {
		// FIXME : when considering wrap_content,
		if (getScrollEnabled() && getHeight() > 0 && mTextController.getTextHeight() > 0 
				//						&& getDrawingTop() + mTextController.getTextHeight() > getParent().getDrawingBottom() // when under screen, this make all height 0. so listview is incorretly performed
				&& (getScrollableY() > 0 || getScrollableX() > 0)
				) {
			mTextController.setScroll(true);
		} else {
			mTextController.setScroll(false);
		}

		if (mTextController.canScroll()) {
			if (mTextController.scrollBar == null) {
				mTextController.setScrollBar(mContext, this, mTextController.getTextHeight(), 1, ApplicationSetting.SCROLL_AMOUNT);
			} else {
				mTextController.resizeScrollBar(this, mTextController.getTextHeight());
			}
		} else {
			if (mTextController.scrollBar != null) {
				mTextController.scrollBar = null;
			}
		}
	}
	
	public void setSingleLine() {
        setSingleLine(true);
    }
	
	public void setSingleLine(boolean is) {
		isSingleLine = is;
	}
	
	public void setText(int resid) {
		String text = mContext.getResources().getString(resid);
		if (text == null) {
			throw new WrongFormatException("resource id '" + resid + "' does not exist");
		}
		setText(mContext.getResources().getString(resid));
	}
	
	public void setText(CharSequence charseq) {
		setText((String) charseq);
	}
	
	public void setText(String text) {
		if (text == null) {
			throw new WrongFormatException("Null is not allowed");
		}
		
		mTextController.setText(text);
		if (mLayoutParam.height == LayoutParams.WRAP_CONTENT && getParent() != null) {
			mTextController.onMeasureWrapHeight();
		} 
		onPostMeasure(getDrawingWidth(), getDrawingHeight());
		
		// TODO : performance problem 
		// onArrangeNeeded();
		arrangeScroll();
		invalidate();
	}
	
    public void setHint(CharSequence hint) {
    	mTextController.setHint(hint);
    }
    
    public void setHintTextColor(int color) {
    	mTextController.setHintTextColor(color);
    }
	
	public String getText() {
		return mTextController.getText();//mText;
	}
	
	public void setTextColor(int textColor) {
		mTextColor = Color.toAwtColor(textColor);
	}
	
	public void setTextSize(int size) {
		mTextSize = size;
		mFont = mFont.deriveFont((float)size);
		mFontMetrics = mContext.getFontMetrics(mFont);
	}
	
	/**
	 * 
	 * @param style use {@link java.awt.Font}
	 */
	public void setTextStyle(int style) {
		if (mFont.getStyle() != style) {
			int s = Font.PLAIN;
			switch (style) {
			case Font.BOLD:
				s = Font.BOLD;
				break;
			case Font.ITALIC:
				s = Font.ITALIC;
				break;
			case Font.BOLD | Font.ITALIC:
				s = Font.BOLD | Font.ITALIC;
			}
			mFont = mFont.deriveFont(s);
			mFontMetrics = mContext.getFontMetrics(mFont);
		}
	}
	
	/**
	 * 
	 * @param tf did not implement yet.
	 * @param style
	 */
	public void setTypeface(Typeface tf, int style) {
		setTextStyle(style);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = super.onTouchEvent(event);
		mTextController.dispatchTouchEvent(event);
		return ret;
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);

		// final int compoundPaddingLeft = getCompoundPaddingLeft();
		// final int compoundPaddingTop = getCompoundPaddingTop();
		// final int compoundPaddingRight = getCompoundPaddingRight();
		// final int compoundPaddingBottom = getCompoundPaddingBottom();
		int left = getDrawingLeft() + getLeftPaddingOffset();
    	int top = getDrawingTop() + getTopPaddingOffset();
        final Drawables dr = mDrawables;
        
        if (dr != null) {
        	final int right = getDrawingRight();
        	final int bottom = getDrawingBottom();
        	// final boolean isLayoutRtl = isLayoutRtl();
        	// final int offset = getHorizontalOffsetForDrawables();

            refreshCompoundDrawables();

            // IMPORTANT: The coordinates computed are also used in invalidateDrawable()
            // Make sure to up-date invalidateDrawable() when changing this code.
            if (dr.mDrawableLeft != null) {
                dr.mDrawableLeft.draw(g);
                left += dr.mDrawableLeft.getIntrinsicWidth();
            }

            // IMPORTANT: The coordinates computed are also used in invalidateDrawable()
            // Make sure to update invalidateDrawable() when changing this code.
            if (dr.mDrawableRight != null) {
                dr.mDrawableRight.draw(g);
            }

            // IMPORTANT: The coordinates computed are also used in invalidateDrawable()
            // Make sure to update invalidateDrawable() when changing this code.
            if (dr.mDrawableTop != null) {
                dr.mDrawableTop.draw(g);
                top += dr.mDrawableLeft.getIntrinsicWidth();
            }

            // IMPORTANT: The coordinates computed are also used in invalidateDrawable()
            // Make sure to update invalidateDrawable() when changing this code.
            if (dr.mDrawableBottom != null) {
                dr.mDrawableBottom.draw(g);
            }
        }
		 
		int lineCount = mTextController.textBuilder.size();
		if (lineCount > 0 || mTextController.hintText != null) {
			int textSingleHeight = mFontMetrics.getAscent();
			
			int[] offset = null;
			if (!isSingleLine) {
				int i = 1;
				List<TextLine> temp = null;
				if (lineCount > 0) {
					g.setColor(mTextColor);
					temp = mTextController.getTextBuilder();
				} else if (mTextController.hintText != null) {
					g.setColor(mTextController.hintTextColor);
					temp = mTextController.hintBuilder;
					lineCount = temp.size();
				}
				for (int j=0; j<temp.size(); ++j) {
					TextLine builder = temp.get(j);
					offset = getOffset(mFontMetrics.stringWidth(builder.textLine.toString()), textSingleHeight*lineCount, mGravity);
					g.drawString(builder.textLine.toString(), left + offset[0], top + offset[1] + textSingleHeight*i);
					i++;
				}
			} else {
				String text = null;
				if (mTextController.textBuilder.size() > 0) {
					g.setColor(mTextColor);
					text = mTextController.getText();
				} else if (mTextController.hintBuilder.size() > 0) {
					g.setColor(mTextController.hintTextColor);
					text = mTextController.hintText.toString();
				}
				text.replace("\n", " ");
				int textWidth = mFontMetrics.stringWidth(text);
				offset = getOffset(textWidth, textSingleHeight, mGravity);
				g.drawString(text, left + offset[0], top + offset[1] + textSingleHeight);
			}
			
			// mTextController.drawSelection(g);
			if (mTextController.canScroll()) {
				mTextController.drawScrollBar(g);
			}
		}
	}

	/**
	 * 
	 * @param textWidth
	 * @param gravity
	 * @return index 0 = x, 1 = y.
	 */
	private int[] getOffset(int textWidth, int textHeight, int gravity) {
		int[] offset = new int[2];
		int availWidth = mTextController.limitWidth;
		int topBottomPadding = getCompoundPaddingHeightTop() - getPaddingTop() + getCompoundPaddingHeightBottom() - getPaddingBottom();
		
		if (textWidth < availWidth) {
			switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.LEFT:
				offset[0] = 0;
				break;
			case Gravity.RIGHT:
				offset[0] = availWidth - textWidth;
				break;
			case Gravity.CENTER_HORIZONTAL:
				offset[0] = availWidth/2 - textWidth/2;
				break;
			}
		}
		
		if (textHeight < getHeight()) {
			switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.TOP:
				offset[1] = 0;
				break;
			case Gravity.BOTTOM:
				offset[1] = (getDrawingHeight() - topBottomPadding) - textHeight/2;
				break;
			case Gravity.CENTER_VERTICAL:
				offset[1] = (getDrawingHeight() - topBottomPadding)/2 - textHeight/2;
				break;
			}
		}
	
		return offset;
	}
	
    /**
     * Returns the top padding of the view, plus space for the top
     * Drawable if any.
     */
    public int getCompoundPaddingTop() {
        final Drawables dr = mDrawables;
        if (dr == null || dr.mDrawableTop == null) {
            return mPaddingTop;
        } else {
            return mPaddingTop + dr.mDrawablePadding + dr.mDrawableSizeTop;
        }
    }
    
    public int getCompoundPaddingHeightTop() {
    	final Drawables dr = mDrawables;
    	if (dr == null || dr.mDrawableTop == null) {
    		return mPaddingTop;
    	} else {
    		return mPaddingTop + dr.mDrawableTop.getIntrinsicHeight() + dr.mDrawablePadding + dr.mDrawableSizeTop;
    	}
    }

    /**
     * Returns the bottom padding of the view, plus space for the bottom
     * Drawable if any.
     */
    public int getCompoundPaddingBottom() {
        final Drawables dr = mDrawables;
        if (dr == null || dr.mDrawableBottom == null) {
            return mPaddingBottom;
        } else {
            return mPaddingBottom + dr.mDrawablePadding + dr.mDrawableSizeBottom;
        }
    }
    
    public int getCompoundPaddingHeightBottom() {
    	final Drawables dr = mDrawables;
    	if (dr == null || dr.mDrawableBottom == null) {
    		return mPaddingBottom;
    	} else {
    		return mPaddingBottom + dr.mDrawableBottom.getIntrinsicHeight() + dr.mDrawablePadding + dr.mDrawableSizeBottom;
    	}
    }

    /**
     * Returns the left padding of the view, plus space for the left
     * Drawable if any.
     */
    public int getCompoundPaddingLeft() {
        final Drawables dr = mDrawables;
        if (dr == null || dr.mDrawableLeft == null) {
            return mPaddingLeft;
        } else {
            return mPaddingLeft + dr.mDrawablePadding + dr.mDrawableSizeLeft;
        }
    }
    
    public int getCompoundPaddingWidthLeft() {
    	final Drawables dr = mDrawables;
    	if (dr == null || dr.mDrawableLeft == null) {
    		return mPaddingLeft;
    	} else {
    		return mPaddingLeft + dr.mDrawableLeft.getIntrinsicWidth() + dr.mDrawablePadding + dr.mDrawableSizeLeft;
    	}
    }

    /**
     * Returns the right padding of the view, plus space for the right
     * Drawable if any.
     */
    public int getCompoundPaddingRight() {
        final Drawables dr = mDrawables;
        if (dr == null || dr.mDrawableRight == null) {
            return mPaddingRight;
        } else {
            return mPaddingRight + dr.mDrawablePadding + dr.mDrawableSizeRight;
        }
    }
    
    public int getCompoundPaddingWidthRight() {
    	final Drawables dr = mDrawables;
    	if (dr == null || dr.mDrawableRight == null) {
    		return mPaddingRight;
    	} else {
    		return mPaddingRight + dr.mDrawableRight.getIntrinsicWidth() + dr.mDrawablePadding + dr.mDrawableSizeRight;
    	}
    }

    /**
     * Returns the start padding of the view, plus space for the start
     * Drawable if any.
     */
    public int getCompoundPaddingStart() {
//        resolveDrawables();
//        switch(getLayoutDirection()) {
//            default:
//            case LAYOUT_DIRECTION_LTR:
                return getCompoundPaddingLeft();
//            case LAYOUT_DIRECTION_RTL:
//                return getCompoundPaddingRight();
//        }
    }

    /**
     * Returns the end padding of the view, plus space for the end
     * Drawable if any.
     */
    public int getCompoundPaddingEnd() {
//        resolveDrawables();
//        switch(getLayoutDirection()) {
//            default:
//            case LAYOUT_DIRECTION_LTR:
                return getCompoundPaddingRight();
//            case LAYOUT_DIRECTION_RTL:
//                return getCompoundPaddingLeft();
//        }
    }
	
	@Override
	protected int getPaddedWidth() {
		int ret = super.getPaddedWidth();
		if (mDrawables != null && mDrawables.mDrawableLeft != null) {
			ret += mDrawables.mDrawableLeft.getIntrinsicWidth();
		}
		if (mDrawables != null && mDrawables.mDrawableRight != null) {
			ret += mDrawables.mDrawableRight.getIntrinsicWidth();
		}
		return ret;
	}

	@Override
	protected int getPaddedHeight() {
		int ret = super.getPaddedHeight();
		if (mDrawables != null && mDrawables.mDrawableTop != null) {
			ret += mDrawables.mDrawableTop.getIntrinsicHeight();
		}
		if (mDrawables != null && mDrawables.mDrawableBottom != null) {
			ret += mDrawables.mDrawableBottom.getIntrinsicHeight();
		}
		return ret;
	}

	@Override
    protected int getLeftPaddingOffset() {
        return getCompoundPaddingLeft() - mPaddingLeft; 
    }

    @Override
    protected int getTopPaddingOffset() {
    	return getCompoundPaddingTop() - mPaddingTop; 
    }

    @Override
    protected int getBottomPaddingOffset() {
    	return getCompoundPaddingBottom() - mPaddingBottom; 
    }

    @Override
    protected int getRightPaddingOffset() {
        return getCompoundPaddingRight() - mPaddingRight;
    }
	
    /**
     * Sets the size of the padding between the compound drawables and
     * the text.
     */
    public void setCompoundDrawablePadding(int pad) {
        Drawables dr = mDrawables;
        if (pad == 0) {
            if (dr != null) {
                dr.mDrawablePadding = pad;
            }
        } else {
            if (dr == null) {
                mDrawables = dr = new Drawables(getContext());
            }
            dr.mDrawablePadding = pad;
        }

        invalidate();
        requestLayout();
    }

    /**
     * Returns the padding between the compound drawables and the text.
     *
     * @attr ref android.R.styleable#TextView_drawablePadding
     */
    public int getCompoundDrawablePadding() {
        final Drawables dr = mDrawables;
        return dr != null ? dr.mDrawablePadding : 0;
    }
    
    public int getHorizontalDrawableHPadding() {
    	int ret = (getCompoundPaddingLeft() - mPaddingLeft) + (getCompoundPaddingRight() - mPaddingRight);
    	if (mDrawables != null) {
	    	if (mDrawables.mDrawableLeft != null) {
	    		ret += mDrawables.mDrawableLeft.getIntrinsicWidth();
	    	}
	    	if (mDrawables.mDrawableRight != null) {
	    		ret += mDrawables.mDrawableRight.getIntrinsicWidth(); 
	    	}
    	}
    	return ret; 
    }
    
	@Override
	public boolean onScroll(MouseWheelEvent event) {
		super.onScroll(event);
		if (mTextController.canScroll()) {
			int wheelRotation = event.getWheelRotation();
			int scrollAmount = event.getScrollAmount();
			
			mTextController.scroll(wheelRotation, scrollAmount);
		}
		return true;
	}

	@Override
	public void onLayoutChanged(int l, int t, int r, int b) {
		super.onLayoutChanged(l, t, r, b);
		mTextController.setLimitWidth(getDrawingWidth() - getHorizontalDrawableHPadding());
	}
	
	@AguiSpecific
	public void setScrollEnabled(boolean is) {
		if (is) {
			mTextFlag |= SCROLL_ENABLE_MASK;
		} else {
			mTextFlag &= ~SCROLL_ENABLE_MASK;
		}
	}
	
	public boolean getScrollEnabled() {
		return (mTextFlag & SCROLL_ENABLE_MASK) != 0;
	}
	
	/**
     * Sets the Drawables (if any) to appear to the left of, above,
     * to the right of, and below the text.  Use null if you do not
     * want a Drawable there. The Drawables' bounds will be set to
     * their intrinsic bounds.
     */
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        }
        if (right != null) {
            right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
        }
        if (top != null) {
            top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
        }
        setCompoundDrawables(left, top, right, bottom);
    }
	
	/**
     * Sets the Drawables (if any) to appear to the left of, above,
     * to the right of, and below the text.  Use null if you do not
     * want a Drawable there.  The Drawables must already have had
     * {@link Drawable#setBounds} called.
     */
    public void setCompoundDrawables(Drawable left, Drawable top,
                                     Drawable right, Drawable bottom) {
        Drawables dr = mDrawables;

        final boolean drawables = left != null || top != null
                || right != null || bottom != null;

        if (!drawables) {
            // Clearing drawables...  can we free the data structure?
            if (dr != null) {
                if (dr.mDrawablePadding == 0) {
                    mDrawables = null;
                } else {
                    // We need to retain the last set padding, so just clear
                    // out all of the fields in the existing structure.
                    if (dr.mDrawableLeft != null) dr.mDrawableLeft.setCallback(null);
                    dr.mDrawableLeft = null;
                    if (dr.mDrawableTop != null) dr.mDrawableTop.setCallback(null);
                    dr.mDrawableTop = null;
                    if (dr.mDrawableRight != null) dr.mDrawableRight.setCallback(null);
                    dr.mDrawableRight = null;
                    if (dr.mDrawableBottom != null) dr.mDrawableBottom.setCallback(null);
                    dr.mDrawableBottom = null;
                    dr.mDrawableSizeLeft = dr.mDrawableHeightLeft = 0;
                    dr.mDrawableSizeRight = dr.mDrawableHeightRight = 0;
                    dr.mDrawableSizeTop = dr.mDrawableWidthTop = 0;
                    dr.mDrawableSizeBottom = dr.mDrawableWidthBottom = 0;
                }
            }
        } else {
            if (dr == null) {
                mDrawables = dr = new Drawables(getContext());
            }

            mDrawables.mOverride = false;

            if (dr.mDrawableLeft != left && dr.mDrawableLeft != null) {
                dr.mDrawableLeft.setCallback(null);
            }
            dr.mDrawableLeft = left;

            if (dr.mDrawableTop != top && dr.mDrawableTop != null) {
                dr.mDrawableTop.setCallback(null);
            }
            dr.mDrawableTop = top;

            if (dr.mDrawableRight != right && dr.mDrawableRight != null) {
                dr.mDrawableRight.setCallback(null);
            }
            dr.mDrawableRight = right;

            if (dr.mDrawableBottom != bottom && dr.mDrawableBottom != null) {
                dr.mDrawableBottom.setCallback(null);
            }
            dr.mDrawableBottom = bottom;
            
            refreshCompoundDrawables();
        }

        // Save initial left/right drawables
        if (dr != null) {
            dr.mDrawableLeftInitial = left;
            dr.mDrawableRightInitial = right;
        }

        // resetResolvedDrawables();
        // resolveDrawables();
        invalidate();
        requestLayout();
    }
    
    private void refreshCompoundDrawables() {
        boolean[] state = getDrawableState();

        final Drawable left = mDrawables.mDrawableLeft;
        final Drawable top = mDrawables.mDrawableTop;
        final Drawable right = mDrawables.mDrawableRight;
        final Drawable bottom = mDrawables.mDrawableBottom;
        
        int leftCompoundPadding = left != null ? left.getIntrinsicWidth() : 0;
        int topCompoundPadding = top != null ? top.getIntrinsicHeight() : 0;
        
        if (left != null) {
            left.setState(state);
            left.setBounds(getDrawingLeftWithoutScroll()
            				, topCompoundPadding + getDrawingTopWithoutScroll()
            				, getDrawingLeftWithoutScroll() + left.getIntrinsicWidth()
            				, topCompoundPadding + getDrawingTopWithoutScroll() + left.getIntrinsicHeight());
            // left.setCallback(this);
        } else {
            mDrawables.mDrawableSizeLeft = mDrawables.mDrawableHeightLeft = 0;
        }

        if (right != null) {
            right.setState(state);
            right.setBounds(getDrawingRightWithoutScroll() - right.getIntrinsicWidth()
            		, topCompoundPadding + getDrawingTopWithoutScroll() 
            		, getDrawingRightWithoutScroll()
            		, topCompoundPadding + getDrawingTopWithoutScroll() + right.getIntrinsicHeight());
            // right.setCallback(this);
        } else {
        	mDrawables.mDrawableSizeRight = mDrawables.mDrawableHeightRight = 0;
        }

        if (top != null) {
            top.setState(state);
            top.setBounds(leftCompoundPadding + getDrawingLeftWithoutScroll()
            		, getDrawingTopWithoutScroll() 
            		, leftCompoundPadding + getDrawingLeftWithoutScroll() + top.getIntrinsicWidth()
            		, getDrawingTopWithoutScroll() + top.getIntrinsicHeight());
            // top.setCallback(this);
        } else {
        	mDrawables.mDrawableSizeTop = mDrawables.mDrawableWidthTop = 0;
        }

        if (bottom != null) {
            bottom.setState(state);
            bottom.setBounds(leftCompoundPadding + getDrawingLeftWithoutScroll()
            		, getDrawingBottomWithoutScroll() - bottom.getIntrinsicHeight()  
            		, leftCompoundPadding + getDrawingLeftWithoutScroll() + bottom.getIntrinsicWidth()
            		, getDrawingBottomWithoutScroll());
            // bottom.setCallback(this);
        } else {
        	mDrawables.mDrawableSizeBottom = mDrawables.mDrawableWidthBottom = 0;
        }
    }

	/*package*/ class TextController implements ScrollCondition {
		
		private int													limitWidth 				= -1;
		private ArrayList<TextLine>									textBuilder;
		private int 												maxTextWidth;
		private Point												currentPoint;
		private int													textIndexX;
		private int													textIndexY;
		private Caret												caret;
		private Selection											selection;
		private ScrollBar											scrollBar;
		private ScrollHelper										scrollHelper;
		private boolean												isDragged;
		private boolean												isInversed 				= false;
		private boolean												canScroll 				= false;
		/*pacakge*/ boolean 										isScrollPressed			= false;
		private CharSequence										hintText;
		private List<TextLine>										hintBuilder;
		private java.awt.Color										hintTextColor			= java.awt.Color.LIGHT_GRAY;
		
		public TextController() {
			textBuilder = new ArrayList<>();//new StringBuilder[0];
			hintBuilder = new ArrayList<>();
			maxTextWidth = 0;
			currentPoint = new Point();
			caret = new Caret();
			selection = new Selection();
		}
		
		public void setLimitWidth(int width) {
			limitWidth = width;
		}
		
		public int getLimitWidth() {
			return limitWidth;
		}
		
		public void setScroll(boolean is) {
			canScroll = is;
		}
		
		public boolean canScroll() {
			return canScroll;
		}
		
		public void setScrollBar(Context context, View parent, int avgItemHeight, int itemAllCount, int scrollAmount) {
			scrollBar = new VerticalScrollBar(context, parent, avgItemHeight, itemAllCount, scrollAmount);
			scrollBar.setOnThumbListener(onThumbListener);
			scrollHelper = new ScrollHelper(TextView.this, scrollBar, this);
		}
		
		public void resizeScrollBar(View parent, int listHeight) {
			if (scrollBar != null) {
				scrollBar.setThumbSize(parent, listHeight);
			}
		}
		
		public void setHintTextColor(int color) {
			hintTextColor = Color.toAwtColor(color);
	    }
		
	    public void setHint(CharSequence hint) {
	    	hintText = hint;
	    	String[] ret = null;
	    	String temp = hint.toString();
	    	temp = temp.replace("\t", "        ");
			ret = temp.split("\n");
			hintBuilder.clear();
			for (int i=0;i<ret.length;++i) {
				if (i == ret.length-1) hintBuilder.add(new TextLine(ret[i], false));
				else hintBuilder.add(new TextLine(ret[i], true));
			}
	    }
		
		public void setText(String v) {
			setText(v, true);
		}
		
		public void refreshText(boolean isResize) {
			boolean isHint = false;
			List<TextLine> temp = null;
			String[] ret = null;
			
			if (textBuilder.size() == 0) {
				if (hintText != null) {
					isHint = true;
					temp = hintBuilder;
				}
			} else {
				temp = textBuilder;
			}
			
			if (temp == null) {
				return ;
			}
			
			if (isSingleLine) {
				int size = temp.size();
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < size; i++) {
					TextLine textLine = temp.get(i);
					builder.append(textLine.textLine);
					if (textLine.hasNewLine) {
						builder.append(" ");
					}
				}
				ret = new String[]{builder.toString()};
			} else {
				int size = temp.size();
				ArrayList<CharSequence> tempList = new ArrayList<>();
				boolean prevHasNewLine = false;
				for (int i = 0; i < size; i++) {
					TextLine textLine = temp.get(i);
					if ((textLine.hasNewLine || prevHasNewLine) || i==0) {
						tempList.add(textLine.textLine);
					} else {
						CharSequence car = tempList.get(tempList.size()-1);
						car = car.toString() + textLine.textLine.toString();
					}
					prevHasNewLine = textLine.hasNewLine; 
				}
				ret = tempList.toArray(new String[tempList.size()]);
			}
			
			if (getParent() != null && (isSingleLine || isResize && mLayoutParam.width == LayoutParams.WRAP_CONTENT)) {
				View parent = getParent();
				int max = mMinWidth; 
				int availWidth = 0;
				for (String s : ret) {
					max = Math.max(mFontMetrics.stringWidth(s), max);
				}
				if (max > parent.getDrawingWidth()) { // when getting this view's width or height, don't use this padding because width contains padding 
					availWidth = parent.getDrawingWidth() - mLayoutParam.rightMargin - (getLeft() - parent.getDrawingLeft());
				} else {					
					availWidth = parent.getDrawingWidth() - mLayoutParam.rightMargin;//getUsableWidth();
				}
				onMeasureWrapWidth(parent.getLayoutParams(), availWidth, max);
				setLimitWidth(Math.max(getDrawingWidth() - getHorizontalDrawableHPadding(), 0));
			}
			
			if (!isHint) {
				setText(ret);
			} 
		}
		
		public void setText(String v, boolean isResize) {
			boolean isHint = false;
			String temp = null; 
			if (v.length() > 0) {
				temp = v;
			} else {
				if (hintText != null) {
					isHint = true;
					temp = hintText.toString();
				} else {
					temp = v;
				}
			}
			if (Strings.isNullOrEmpty(temp)) {
				return ;
			}
			String[] ret = null;
			temp.replace("\t", "        ");
			if (isSingleLine) {
				ret = new String[]{temp};
			} else {
				ret = temp.split("\n");
			}
			
			View parent = getParent();
			if (parent != null && (isSingleLine || isResize && mLayoutParam.width == LayoutParams.WRAP_CONTENT)) {
				int max = mMinWidth; 
				int availWidth = 0;
				for (String s : ret) {
					max = Math.max(mFontMetrics.stringWidth(s), max);
				}
				
				if (max > parent.getDrawingWidth()) { // when getting this view's width or height, don't use this padding because width contains padding 
					if ((mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
						availWidth = parent.getDrawingRight() - mLayoutParam.rightMargin - (getDrawingLeft()); // now 
//						availWidth = parent.getDrawingRight() - mLayoutParam.rightMargin - (parent.getDrawingLeft()); // linearLayout2Test
//						availWidth = parent.getDrawingWidth() - mLayoutParam.rightMargin - (parent.getDrawingLeft()); // paddingTest3Activity, paddingTest1
//						availWidth = parent.getDrawingWidth() - mLayoutParam.rightMargin;
					} else {
//						availWidth = parent.getDrawingRight() - mLayoutParam.rightMargin - (getLeft() - parent.getDrawingLeft()); // linearLayout2Test
//						availWidth = parent.getDrawingWidth() - mLayoutParam.rightMargin - (getLeft() - parent.getDrawingLeft()); // paddingTestActivity
//						availWidth = parent.getDrawingRight() - mLayoutParam.rightMargin - (getLeft()); // paddingTestActivity
						availWidth = parent.getDrawingWidth() - mLayoutParam.rightMargin - mLayoutParam.leftMargin; // now// paddingTestActivity
					}
				} else {					
					availWidth = parent.getDrawingWidth() - mLayoutParam.rightMargin;//getUsableWidth();
				}
				// view width
				onMeasureWrapWidth(parent.getLayoutParams(), availWidth, max);
				// text width without compound drawable
				setLimitWidth(Math.max(getDrawingWidth() - getHorizontalDrawableHPadding(), 0));
			}
			
			if (!isHint) {
				setText(ret);
			} 
		}
		
		/*package*/ void setText(String[] ret) {
			maxTextWidth = 0;
			textBuilder.clear();
			if (isSingleLine) {
				textBuilder.add(new TextLine(ret[0], false));
			} else {
				for (int i=0; i < ret.length; ++i) {
					int w = mFontMetrics.stringWidth(ret[i]);
					if (limitWidth > 0) {
						if (w > limitWidth) {
							int start = 0;
							int end = 0;
							for (; start < ret[i].length();) {
								end = getProperTextLocation(ret[i].toCharArray(), start, limitWidth);
								if (end == 0) {	// just a char is availale
									end = 1;
								}
								if (start + end >= ret[i].length()) {
									textBuilder.add(new TextLine(ret[i].substring(start, start+end), true));
								} else {
									textBuilder.add(new TextLine(ret[i].substring(start, start+end), false));
								}
								start+=end;
							}
						} else {
							textBuilder.add(new TextLine(ret[i], true));
						}
						if (w > limitWidth) {
							maxTextWidth = limitWidth;
						} else if (w > maxTextWidth) {
							maxTextWidth = w;
						}
					} else {
						textBuilder.add(new TextLine(ret[i], true));
						if (w > maxTextWidth) {
							maxTextWidth = w;
						}
					}
				}
				// rearrange();
			} 
			
			if (mLayoutParam.height == LayoutParams.WRAP_CONTENT && getParent() != null) {
				onMeasureWrapHeight();
			} 
		}
		
		private final void onMeasureWrapWidth(LayoutParams parentParams, int availWidth, int txtWidth) {
			int leftRightPadding = getCompoundPaddingWidthLeft() + getCompoundPaddingWidthRight();
			int width = txtWidth + leftRightPadding;
			if (width < mMinWidth) {
				width = mMinWidth;
			}
			if (width > availWidth 
					&& (parentParams.width == ViewGroup.LayoutParams.MATCH_PARENT 
					|| parentParams.width == ViewGroup.LayoutParams.FILL_PARENT 
					)) {
				mWidth = availWidth;
				if (isSingleLine) {
					mScrollableX = width - availWidth;
				} else {
					mScrollableX = 0;
				}
			} else if (width > availWidth && parentParams.width == ViewGroup.LayoutParams.WRAP_CONTENT ) {
				int usableWidth = getUsableWidth();
				if (getDrawingLeft() + width > usableWidth) {
					mWidth = usableWidth;
				} else {
					mWidth = width;
				}
				if (isSingleLine) {
					mScrollableX = width - availWidth;
				} else {
					mScrollableX = 0;
				}
			} else {
				mWidth = width;
			}
		}
		
		private final void onMeasureWrapHeight() {
			int topBottomPadding = getCompoundPaddingHeightTop() + getCompoundPaddingHeightBottom();
			int textHeight = getTextHeight() + topBottomPadding;
			if (textHeight < mMinHeight) {
				textHeight = mMinHeight; 
			}
			View parent = getParent();
			if (parent != null) {
				int parentHeight = parent.getHeight();
				mScrollableY = 0;
				if (parentHeight <= 0) {
					mHeight = 0;
				} else {
					if (mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
						if (mHeight > textHeight) {
							if (mLayoutParam instanceof LinearLayout.LayoutParams) {
								if (((LinearLayout.LayoutParams) mLayoutParam).getWeight() == 0 
										|| ((LinearLayout) getParent()).getOrientation() == LinearLayout.HORIZONTAL) {
									mHeight = textHeight;
								} 
							} else {
								mHeight = textHeight;
							}
						} else if (mHeight < textHeight) {
							mHeight = textHeight;
						}
						if (parentHeight < textHeight) {
							mScrollableY = textHeight - parentHeight;
							Log.i("mScrollableY : " + mScrollableY);
						}
					} else if (mLayoutParam.height == LayoutParams.MATCH_PARENT) {
						if (parentHeight < textHeight) {
							mScrollableY = textHeight - parentHeight;
							Log.i("mScrollableY : " + mScrollableY);
						}
					} else if (mLayoutParam.height > 0) {
						if (mLayoutParam.height < textHeight) {
							mScrollableY = textHeight - mLayoutParam.height;
							Log.i("mScrollableY : " + mScrollableY);
						}
					}
				}
			} else {
				mHeight = textHeight;
			}
		}
		
		public int getTextWidth() {
			return maxTextWidth;
		}
		
		public int getTextHeight() {
			return mFontMetrics.getAscent()*(textBuilder.size());
		}
		
		public String getText() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < textBuilder.size(); i++) {
				TextLine t = textBuilder.get(i);
				builder.append(t.textLine);
				if (t.hasNewLine) builder.append("\n");
			}
			return builder.toString();
		}
		
		public char[] getCharArray() {
			return getText().toCharArray();
		}
		
		private void dispatchTouchEvent(MotionEvent event) {
			dispatchScrollBar(event);
			dispatchSelection(event);
		}
		
		private void dispatchScrollBar(MotionEvent event) {
			boolean ret = false;
			if (scrollBar != null) {
				if (isScrollPressed) { 	// move
					ret = scrollBar.onTouchEvent(event);
				} else {				// down
					if (scrollBar.contains(event.getX(), event.getY())) {
						isScrollPressed = true;
						ret = scrollBar.onTouchEvent(event);
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					isScrollPressed = false;
				}
			}
		}
		
		private void dispatchSelection(MotionEvent event) {
			int action = event.getAction();
			int x = event.getX();
			int y = event.getY();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				isDragged = true;
				isInversed = false;
				selection.start = getTextPoint(x, y);
				selection.startTextX = textIndexX;
				selection.startTextY = textIndexY;
				selection.end = selection.start;
				selection.endTextX = textIndexX;
				selection.endTextY = textIndexY;
				break;
			case MotionEvent.ACTION_MOVE:
				if (isDragged) {
					if (isInversed) {
						selection.start = getTextPoint(x, y);
						selection.startTextX = textIndexX;
						selection.startTextY = textIndexY;
						
						if ((selection.end.y - selection.start.y < 0)) {
							isInversed = false;
							Point temp = selection.end;
							selection.end = selection.start;
							selection.start = temp;
							selection.startTextX = selection.endTextX;
							selection.startTextY = selection.endTextY;
							selection.endTextX = textIndexX;
							selection.endTextY = textIndexY;
						}
					} else {
						selection.end = getTextPoint(x, y);
						selection.endTextX = textIndexX;
						selection.endTextY = textIndexY;
					
						if ((selection.end.y - selection.start.y < 0)) {
							isInversed = true;
							Point temp = selection.start;
							selection.start = selection.end;
							selection.end = temp;
							selection.endTextX = selection.startTextX;
							selection.endTextY = selection.startTextY;
							selection.startTextX = textIndexX;
							selection.startTextY = textIndexY;
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isDragged) {
					isDragged = false;
					if (isInversed) {
						selection.start = getTextPoint(x, y);
						selection.startTextX = textIndexX;
						selection.startTextY = textIndexY;
					} else {
						selection.end = getTextPoint(x, y);
						selection.endTextX = textIndexX;
						selection.endTextY = textIndexY;
					}
				}
				break;
			}
		}
		
		public void dispatchKeyEvent(thahn.java.agui.view.KeyEvent event) {
			int prevTextIndexX = textIndexX;
			int prevTextIndexY = textIndexY;
			int keyCode = event.getKeyCode();
			if (textBuilder.size() == 0) {
				textBuilder.add(new TextLine("", false));
			}
			if (keyCode >= KeyEvent.VK_BACK_SPACE && keyCode <= KeyEvent.VK_ENTER) {
				switch (keyCode) {
				case KeyEvent.VK_ENTER:
					insertNewLine(textIndexX, textIndexY);
					refreshText();
					++textIndexX;
					break;
				case KeyEvent.VK_TAB:
					break;
				case KeyEvent.VK_BACK_SPACE:
					--textIndexX;
					removeChar(textIndexX, textIndexY);
					refreshText();
					break;
				}
			}
			/**
			 * public static final int VK_SPACE          = 0x20;
		     * public static final int VK_PAGE_UP        = 0x21;
		     * public static final int VK_PAGE_DOWN      = 0x22;
		     * public static final int VK_END            = 0x23;
		     * public static final int VK_HOME           = 0x24;
		     */
			if (keyCode >= KeyEvent.VK_LEFT && keyCode <= KeyEvent.VK_DOWN) {
				switch (keyCode) {
				case KeyEvent.VK_LEFT:
					--textIndexX;
					break;
				case KeyEvent.VK_UP:
					--textIndexY;
					break;
				case KeyEvent.VK_RIGHT:
					++textIndexX;
					break;
				case KeyEvent.VK_DOWN:
					++textIndexY;
					break;
				}
				invalidate();
			}
			/**
			 * public static final int VK_COMMA          = 0x2C;
			 * ~
			 * public static final int VK_DELETE         = 0x7F; // ASCII DEL 
			 */
			if (keyCode >= KeyEvent.VK_COMMA && keyCode <= KeyEvent.VK_DELETE) {
				StringBuilder builder = new StringBuilder(textBuilder.get(textIndexY).textLine);
				builder.insert(textIndexX, event.getKeyChar());
				textBuilder.get(textIndexY).textLine = builder.toString();
				// FIXME : refresh를 전체를 하는 것이 아니라 추가된 행 밑으로만 하자.
				refreshText();
				++textIndexX;
			}
			
			if (textBuilder.size() == 0) {
				textIndexX = 0;
				textIndexY = 0;
			} else{
				if (textIndexY < 0) {
					textIndexY = 0;
				} else if (textIndexY >= textBuilder.size()) {
					textIndexY = textBuilder.size() - 1;
				}  
				if (textIndexX > textBuilder.get(textIndexY).textLine.length()) {
					++textIndexY;
					if (textIndexY >= textBuilder.size()) {
						textIndexY = textBuilder.size() - 1;
						--textIndexX;
					} else {
						textIndexX = 0;
					}
				} else if (textIndexX < 0) {
					// --textIndexY;
					if (textIndexY >= 0) {
						textIndexX = textBuilder.get(textIndexY).textLine.length();
					} else {
						textIndexX = 0;
						textIndexY = 0;
					}
				} 
			}
			
			scrollText(prevTextIndexX, prevTextIndexY, textIndexX, textIndexY);
			moveCaretTo(toTextPoint(textIndexX, textIndexY));
		}
		
		public void scrollText(int prevTextIndexX, int prevTextIndexY, int textIndexX, int textIndexY) {
			currentPoint = toTextPoint(textIndexX, textIndexY);
			int drawingTop = getPaddedTop();
			int drawingBottom = getDrawingBottom();
			
			//	if (isSingleLine) {
			//		int drawingRight = getDrawingRight();			
			//		if (currentPoint.x > drawingRight - mFontMetrics.charWidth(' ') && prevTextIndexX < textIndexX) { // to right
			//			scrollBy(+mFontMetrics.charWidth(' '), 0);
			//		} else if (currentPoint.x <= 0 && prevTextIndexX > textIndexX) { // to left
			//			scrollBy(-mFontMetrics.charWidth(' '), 0);
			//		}
			//	}
			
			int lineHeight = mFontMetrics.getAscent();
			if (currentPoint.y + lineHeight > drawingBottom && prevTextIndexY < textIndexY) { // to down
				TextLine textLine = textBuilder.get(textIndexY);
				int[] offset = getOffset(mFontMetrics.stringWidth(textLine.toString()), lineHeight, mGravity);
				offset[1] += getPaddedTop() + getPaddedBottom();
				if (currentPoint.y + offset[1] < drawingBottom) {
					int gap = currentPoint.y + lineHeight - drawingBottom + offset[1];
					scrollBy(0, gap);
				} else {
					scrollBy(0, +lineHeight);
				}
			} else if (currentPoint.y <= drawingTop && prevTextIndexY > textIndexY) { // to up
				if (currentPoint.y < drawingTop) {
					int gap = currentPoint.y - drawingTop;
					scrollBy(0, gap);
				} else {
					scrollBy(0, -lineHeight);
				}
			}
		}
		
		public void insertChar() {
		}
		
		public void insertNewLine(int indexX, int indexY) {
			if (indexY == textBuilder.size()-1 && indexX == textBuilder.get(indexY).textLine.length()) {
				textBuilder.get(indexY).hasNewLine = true;
				textBuilder.add(new TextLine("", false));
			} else {
				TextLine original = textBuilder.get(indexY);
				CharSequence prev = original.textLine.subSequence(0, indexX);
				CharSequence next = original.textLine.subSequence(indexX, original.textLine.length());
				original.textLine = prev;
				original.hasNewLine = true;
				textBuilder.add(indexY+1, new TextLine(next, false));
			}
		}
		
		public void removeChar(int indexX, int indexY) {
			if (indexX >= 0) {
				StringBuilder builder = new StringBuilder(textBuilder.get(indexY).textLine);
				builder.deleteCharAt(indexX);
				textBuilder.get(indexY).textLine = builder.toString(); 
			} else if (indexY > 0) {
				TextLine prev = textBuilder.get(indexY-1);
				TextLine cur = textBuilder.get(indexY);
				prev.hasNewLine = false;
				cur.hasNewLine = false;
				prev.textLine = prev.textLine.toString() + cur.textLine.toString();
				textBuilder.remove(indexY);
			}
			if (textBuilder.size() == 1 && textBuilder.get(0).textLine.length() == 0) {
				textBuilder.clear();
			}
		}
		
		private void refreshText() {
			// TextView.this.setText(mTextController.getText());
			refreshText(true);
			invalidate();
		}
		
		public List<TextLine> getTextBuilder() {
			return textBuilder;
		}
		
		/**
		 * character count in a line
		 */
		private int tempProperTextLocation = 0;
		private int getProperTextLocation(char[] text, int start, int width) {
			int i = 0;
			
			if (start + tempProperTextLocation > text.length) {
				tempProperTextLocation = 0;
			}
			
			if (tempProperTextLocation == 0) {
				for (;start+i<text.length;++i) {
					if (mFontMetrics.charsWidth(text, start, i) > width) {
						tempProperTextLocation = --i;
						return i;
					}
				}
			} else {
				if (mFontMetrics.charsWidth(text, start, tempProperTextLocation) > width) {//getParent().getWidth()) {
					for (i=tempProperTextLocation-1;i>0;--i) {
						if (mFontMetrics.charsWidth(text, start, i) < width) {
							tempProperTextLocation = ++i;
							return i;
						}
					}
				} else {
					for (i=tempProperTextLocation+1;start+i<text.length;++i) {
						if (mFontMetrics.charsWidth(text, start, i) > width) {
							tempProperTextLocation = --i;
							return i;
						}
					}
					if (start + i > text.length) i = text.length - start;
				}
			}
			return i;
		}
		
		public Point getLastTextPoint() {
			Point ret = new Point();
			String text = textBuilder.get(textBuilder.size()-1).toString();
			ret.x = getDrawingLeft() + mFontMetrics.stringWidth(text);
			ret.y = getDrawingTop() + mFontMetrics.getAscent() * (textBuilder.size()-1);
			textIndexX = text.length();
			textIndexY = textBuilder.size()-1;
			return ret;
		}
		
		/**
		 * from text index to text point
		 * @param indexX
		 * @param indexY
		 * @return
		 */
		public Point toTextPoint(int indexX, int indexY) {
			Point ret = new Point();
			ret.y = getDrawingTop();
			ret.x = getDrawingLeft();
			if (textBuilder.size() != 0) {
				int[] offset = getOffset(mFontMetrics.stringWidth(textBuilder.get(indexY).toString()), getTextHeight(), getGravity());
				ret.y += mFontMetrics.getAscent() * (indexY) + offset[1];
				ret.x += mFontMetrics.stringWidth(textBuilder.get(indexY).textLine.subSequence(0, indexX).toString()) + offset[0];
			} else {
				int[] offset = getOffset(0, mFontMetrics.getAscent(), getGravity());
				ret.y += offset[1];
				ret.x += offset[0];
			}
			return ret;
		}
		
		/**
		 * from x, y to text point
		 * @param x
		 * @param y
		 * @return
		 */
		public Point getTextPoint(int x, int y) {
			Point ret = new Point(getDrawingLeft(), getDrawingTop());
			
			int width = x - getDrawingLeft();
			int height = y - getDrawingTop();
			int textHeight = mFontMetrics.getAscent();
			
			int i = 0;
			for (i = 1; i <= textBuilder.size(); i++) {
				if (textHeight * i >= height) {
					textIndexY = --i;
					break;
				}
			}
			i = i>textBuilder.size()-1?textBuilder.size()-1:i;
			if (i >= 0) {
				ret.y += textHeight * i;
				
				char[] text = textBuilder.get(i).toString().toCharArray();
				int textWidth = 0;
				for (int j = 1; j <= text.length; j++) {
					textWidth = mFontMetrics.charsWidth(text, 0, j);
					if (textWidth > width) {
						textWidth = mFontMetrics.charsWidth(text, 0, j-1);
						textIndexX = j-1;
						break;
					}
				}
				ret.x += textWidth;
				//
				int[] offset = getOffset(mFontMetrics.charsWidth(text, 0, text.length), getTextHeight(), getGravity());
				ret.x += offset[0];	
				ret.y += offset[1];	
			} else {
				int[] offset = getOffset(0, mFontMetrics.getAscent(), getGravity());
				ret.x += offset[0];	
				ret.y += offset[1];
			}
			return ret;
		}
		
		/*package*/ void setTextBuilder(ArrayList<TextLine> list) {
			textBuilder = list;
		}
		
		public void moveCaretTo(Point point) {
			moveCaretTo(point.x, point.y);
		}
		
		public void moveCaretTo(int x, int y) {
			currentPoint.x = x;
			currentPoint.y = y;
		}
		
		public void drawScrollBar(Graphics g) {
			if (scrollBar != null) scrollBar.draw(g);
		}
		
		public void drawSelection(Graphics g) {
//			g.setXORMode(Color.WHITE);
			for (int i = selection.startTextY; i <= selection.endTextY; ++i) {
				if (selection.startTextY == i && selection.startTextY != selection.endTextY) { // single line
					g.fillRect(selection.start.x, selection.start.y, 
							mFontMetrics.stringWidth(textBuilder.get(i).textLine.subSequence(selection.startTextX, textBuilder.get(i).textLine.length()).toString()), mFontMetrics.getAscent()); 
				} else if (selection.startTextY == i && selection.startTextY == selection.endTextY) { // single line
					g.fillRect(selection.start.x, selection.start.y, 
							selection.end.x - selection.start.x, mFontMetrics.getAscent()); 
				} else if (i == selection.endTextY) {			 // end line
					g.fillRect(getDrawingLeft(), getDrawingTop()+i*mFontMetrics.getAscent(), 
							mFontMetrics.stringWidth(textBuilder.get(i).textLine.subSequence(0, selection.endTextX).toString()), mFontMetrics.getAscent());
				} else {										 // common 
					g.fillRect(getDrawingLeft(), getDrawingTop()+i*mFontMetrics.getAscent(), 
							mFontMetrics.stringWidth(textBuilder.get(i).toString()), mFontMetrics.getAscent());
				}
			}
//			g.setXORMode(Color.BLACK);
		}
		
		public void drawCaret(Graphics g) {
			caret.setPosition(currentPoint.x, currentPoint.y);
			caret.drawCaret(g);
		}
		
		private OnThumbListener onThumbListener = new OnThumbListener() {
			
			@Override
			public boolean onChanged(int wheelRotation, int amount) {
				scroll(wheelRotation, amount);
				return true;
			}
		};
		
		private boolean scroll(int wheelRotation, int scrollAmount) {
			return scrollHelper.scroll(wheelRotation, scrollAmount);
		}
		
		@Override
		public boolean isPossibleToScrollUp(int amount, boolean isFirst) {
			if (-getScrollY() + amount >= 0) {
				return false;
			} 
			return true;
		}

		@Override
		public boolean isPossibleToScrollDown(int amount, boolean isLast) {
			int currentY = getHeight() + getScrollY() - (getCompoundPaddingHeightTop() + getCompoundPaddingHeightBottom());
			if (currentY - amount >= getTextHeight()) {
				return false;
			}
			return true;
		}
		
		@Override
		public void scrollToFirst() {
			scrollTo(getScrollX(), 0);
		}

		@Override
		public void scrollToEnd() {
			scrollTo(getScrollX(), getScrollableY());
		}

		/*pacakge*/ class Caret {
			
			int offsetX;
			int offsetY;
			int x;
			int y;
			
			public Caret() {
				x = getDrawingLeft();
				y = getDrawingTop();
			}
			
			public void setOffset(int x, int y) {
				this.offsetX = x;
				this.offsetY = y;
			}
			
			public void setPosition(int x, int y) {
				this.x = x;
				this.y = y;
			}
			
			private void drawCaret(Graphics g) {
				if (isFocused()) {
					int textHeight = mFontMetrics.getAscent();
					g.fillRect(x, y + 2, 3, textHeight);
				}
			}
		}
		
		/*pacakge*/ class Selection {
			Point start;
			Point end;
			int startTextX;
			int startTextY;
			int endTextX;
			int endTextY;
			
			public Selection() {
				start = new Point();
				end = new Point();
			}
		}
		
		/*pacakge*/ class TextLine {
			CharSequence textLine;
			boolean	hasNewLine;

			public TextLine(CharSequence textLine, boolean hasNewLine) {
				this.textLine = textLine;
				this.hasNewLine = hasNewLine;
			}
		}
	}

	@Override
	public String toString() {
		return super.toString().concat(", " + getText());
	}
	
	/*package*/ static class Drawables {
		/*package*/ final static int DRAWABLE_NONE = -1;
		/*package*/ final static int DRAWABLE_RIGHT = 0;
		/*package*/ final static int DRAWABLE_LEFT = 1;

//		/*package*/ final Rect mCompoundRect = new Rect();

		/*package*/ Drawable mDrawableTop, mDrawableBottom, mDrawableLeft, mDrawableRight,
                mDrawableStart, mDrawableEnd, mDrawableError, mDrawableTemp;

		/*package*/ Drawable mDrawableLeftInitial, mDrawableRightInitial;
		/*package*/ boolean mIsRtlCompatibilityMode;
		/*package*/ boolean mOverride;

		/*package*/ int mDrawableSizeTop, mDrawableSizeBottom, mDrawableSizeLeft, mDrawableSizeRight,
                mDrawableSizeStart, mDrawableSizeEnd, mDrawableSizeError, mDrawableSizeTemp;

		/*package*/ int mDrawableWidthTop, mDrawableWidthBottom, mDrawableHeightLeft, mDrawableHeightRight,
                mDrawableHeightStart, mDrawableHeightEnd, mDrawableHeightError, mDrawableHeightTemp;

		/*package*/ int mDrawablePadding;

		/*package*/ int mDrawableSaved = DRAWABLE_NONE;

        public Drawables(Context context) {
//            final int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            mIsRtlCompatibilityMode = false; 
//            		(targetSdkVersion < JELLY_BEAN_MR1 ||
//                !context.getApplicationInfo().hasRtlSupport());
            mOverride = false;
        }

        public void resolveWithLayoutDirection(int layoutDirection) {
            // First reset "left" and "right" drawables to their initial values
            mDrawableLeft = mDrawableLeftInitial;
            mDrawableRight = mDrawableRightInitial;

            if (mIsRtlCompatibilityMode) {
                // Use "start" drawable as "left" drawable if the "left" drawable was not defined
                if (mDrawableStart != null && mDrawableLeft == null) {
                    mDrawableLeft = mDrawableStart;
                    mDrawableSizeLeft = mDrawableSizeStart;
                    mDrawableHeightLeft = mDrawableHeightStart;
                }
                // Use "end" drawable as "right" drawable if the "right" drawable was not defined
                if (mDrawableEnd != null && mDrawableRight == null) {
                    mDrawableRight = mDrawableEnd;
                    mDrawableSizeRight = mDrawableSizeEnd;
                    mDrawableHeightRight = mDrawableHeightEnd;
                }
            } else {
                // JB-MR1+ normal case: "start" / "end" drawables are overriding "left" / "right"
                // drawable if and only if they have been defined
                switch(layoutDirection) {
                    case LAYOUT_DIRECTION_RTL:
                        if (mOverride) {
                            mDrawableRight = mDrawableStart;
                            mDrawableSizeRight = mDrawableSizeStart;
                            mDrawableHeightRight = mDrawableHeightStart;

                            mDrawableLeft = mDrawableEnd;
                            mDrawableSizeLeft = mDrawableSizeEnd;
                            mDrawableHeightLeft = mDrawableHeightEnd;
                        }
                        break;

                    case LAYOUT_DIRECTION_LTR:
                    default:
                        if (mOverride) {
                            mDrawableLeft = mDrawableStart;
                            mDrawableSizeLeft = mDrawableSizeStart;
                            mDrawableHeightLeft = mDrawableHeightStart;

                            mDrawableRight = mDrawableEnd;
                            mDrawableSizeRight = mDrawableSizeEnd;
                            mDrawableHeightRight = mDrawableHeightEnd;
                        }
                        break;
                }
            }
            applyErrorDrawableIfNeeded(layoutDirection);
            updateDrawablesLayoutDirection(layoutDirection);
        }

        private void updateDrawablesLayoutDirection(int layoutDirection) {
//            if (mDrawableLeft != null) {
//                mDrawableLeft.setLayoutDirection(layoutDirection);
//            }
//            if (mDrawableRight != null) {
//                mDrawableRight.setLayoutDirection(layoutDirection);
//            }
//            if (mDrawableTop != null) {
//                mDrawableTop.setLayoutDirection(layoutDirection);
//            }
//            if (mDrawableBottom != null) {
//                mDrawableBottom.setLayoutDirection(layoutDirection);
//            }
        }

        public void setErrorDrawable(Drawable dr, TextView tv) {
//            if (mDrawableError != dr && mDrawableError != null) {
//                mDrawableError.setCallback(null);
//            }
//            mDrawableError = dr;
//
//            final Rect compoundRect = mCompoundRect;
//            int[] state = tv.getDrawableState();
//
//            if (mDrawableError != null) {
//                mDrawableError.setState(state);
//                mDrawableError.copyBounds(compoundRect);
//                mDrawableError.setCallback(tv);
//                mDrawableSizeError = compoundRect.width();
//                mDrawableHeightError = compoundRect.height();
//            } else {
//                mDrawableSizeError = mDrawableHeightError = 0;
//            }
        }

        private void applyErrorDrawableIfNeeded(int layoutDirection) {
            // first restore the initial state if needed
            switch (mDrawableSaved) {
                case DRAWABLE_LEFT:
                    mDrawableLeft = mDrawableTemp;
                    mDrawableSizeLeft = mDrawableSizeTemp;
                    mDrawableHeightLeft = mDrawableHeightTemp;
                    break;
                case DRAWABLE_RIGHT:
                    mDrawableRight = mDrawableTemp;
                    mDrawableSizeRight = mDrawableSizeTemp;
                    mDrawableHeightRight = mDrawableHeightTemp;
                    break;
                case DRAWABLE_NONE:
                default:
            }
            // then, if needed, assign the Error drawable to the correct location
            if (mDrawableError != null) {
                switch(layoutDirection) {
                    case LAYOUT_DIRECTION_RTL:
                        mDrawableSaved = DRAWABLE_LEFT;

                        mDrawableTemp = mDrawableLeft;
                        mDrawableSizeTemp = mDrawableSizeLeft;
                        mDrawableHeightTemp = mDrawableHeightLeft;

                        mDrawableLeft = mDrawableError;
                        mDrawableSizeLeft = mDrawableSizeError;
                        mDrawableHeightLeft = mDrawableHeightError;
                        break;
                    case LAYOUT_DIRECTION_LTR:
                    default:
                        mDrawableSaved = DRAWABLE_RIGHT;

                        mDrawableTemp = mDrawableRight;
                        mDrawableSizeTemp = mDrawableSizeRight;
                        mDrawableHeightTemp = mDrawableHeightRight;

                        mDrawableRight = mDrawableError;
                        mDrawableSizeRight = mDrawableSizeError;
                        mDrawableHeightRight = mDrawableHeightError;
                        break;
                }
            }
        }
    }
}
