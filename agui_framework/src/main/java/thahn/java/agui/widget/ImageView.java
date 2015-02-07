package thahn.java.agui.widget;

import java.awt.Graphics;

import thahn.java.agui.Global;
import thahn.java.agui.R;
import thahn.java.agui.app.Context;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.graphics.Matrix;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup.LayoutParams;

public class ImageView extends View {

	private Drawable 															mSrc;
	private ScaleType															mScaleType;
	
	private static final ScaleType[] 											sScaleTypeArray = {
        ScaleType.MATRIX,
        ScaleType.FIT_XY,
        ScaleType.FIT_START,
        ScaleType.FIT_CENTER,
        ScaleType.FIT_END,
        ScaleType.CENTER,
        ScaleType.CENTER_CROP,
        ScaleType.CENTER_INSIDE
    };
	
	public ImageView(Context context) {
		this(context, new AttributeSet(context));
	}

	public ImageView(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_IMAGE_VIEW_CODE);
	}

	public ImageView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	public void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		mSrc = attrs.getDrawable(R.attr.ImageView_src, defaultType);
		setScaleType(sScaleTypeArray[attrs.getIntFromEnum(R.attr.ImageView_scaleType, 1)]);
		
//		setImageDrawable(mSrc);
	}
	
	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
		
		if(mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			int srcWidth = mSrc == null ? Global.NOT_YET : mSrc.getIntrinsicWidth();
			mWidth = Math.max(srcWidth, mWidth);
			int temp = mWidth + mPaddingLeft + mPaddingRight;
			if(temp > parentWidth) mWidth = parentWidth;
			else mWidth = temp;
		}
		
		if(mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			int srcHeight = mSrc == null ? Global.NOT_YET : mSrc.getIntrinsicHeight();
			mHeight = Math.max(srcHeight, mHeight);
			int temp = mHeight + mPaddingLeft + mPaddingRight;
			if(temp > parentHeight) mHeight = parentHeight;
			else mHeight = temp;
		} 
	}

	public void setImageResource(int res) {
		mSrc = mContext.getResources().getDrawable(res);
	}
	
	public void setImageDrawable(Drawable drawable) {
		if(drawable != null) {
			mSrc = drawable;
		}
    }
	
	@Override
	public void arrange() {
		super.arrange();
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if(mSrc != null) {
			configureBound();
			mSrc.draw(g);
		}
	}
	
	// TODO : implements
	private void configureBound() {
		int left = 0;
		int top = 0;
		int width = 0;
		int height = 0;
		if (mScaleType == ScaleType.MATRIX) {
			left = getLeft();
			top = getTop();
			width = mSrc.getIntrinsicWidth();
			height = mSrc.getIntrinsicHeight();
		} else if (mScaleType == ScaleType.FIT_XY) {
			left = getLeft();
			top = getTop();
			width = getWidth();
			height = getHeight();
		} else if (mScaleType == ScaleType.FIT_CENTER) {
		} else if (mScaleType == ScaleType.FIT_END) {
		} else if (mScaleType == ScaleType.FIT_START) {
		} else if (mScaleType == ScaleType.CENTER) {
			left = (getLeft()+getWidth()/2) - mSrc.getIntrinsicWidth()/2;
			top = (getTop()+getHeight()/2) - mSrc.getIntrinsicHeight()/2;
			width = mSrc.getIntrinsicWidth();
			height = mSrc.getIntrinsicHeight();
		} else if (mScaleType == ScaleType.CENTER_CROP) {
		} else if (mScaleType == ScaleType.CENTER_INSIDE) {
		}
		mSrc.setBounds(left - mScrollX, top - mScrollY, left - mScrollX + width, top - mScrollY + height);
	}
	
	/**
     * Options for scaling the bounds of an image to the bounds of this view.
     */
    public enum ScaleType {
        /**
         * Scale using the image matrix when drawing. The image matrix can be set using
         * {@link ImageView#setImageMatrix(Matrix)}. From XML, use this syntax:
         * <code>android:scaleType="matrix"</code>.
         */
        MATRIX      (0),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#FILL}.
         * From XML, use this syntax: <code>android:scaleType="fitXY"</code>.
         */
        FIT_XY      (1),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#START}.
         * From XML, use this syntax: <code>android:scaleType="fitStart"</code>.
         */
        FIT_START   (2),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#CENTER}.
         * From XML, use this syntax:
         * <code>android:scaleType="fitCenter"</code>.
         */
        FIT_CENTER  (3),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#END}.
         * From XML, use this syntax: <code>android:scaleType="fitEnd"</code>.
         */
        FIT_END     (4),
        /**
         * Center the image in the view, but perform no scaling.
         * From XML, use this syntax: <code>android:scaleType="center"</code>.
         */
        CENTER      (5),
        /**
         * Scale the image uniformly (maintain the image's aspect ratio) so
         * that both dimensions (width and height) of the image will be equal
         * to or larger than the corresponding dimension of the view
         * (minus padding). The image is then centered in the view.
         * From XML, use this syntax: <code>android:scaleType="centerCrop"</code>.
         */
        CENTER_CROP (6),
        /**
         * Scale the image uniformly (maintain the image's aspect ratio) so
         * that both dimensions (width and height) of the image will be equal
         * to or less than the corresponding dimension of the view
         * (minus padding). The image is then centered in the view.
         * From XML, use this syntax: <code>android:scaleType="centerInside"</code>.
         */
        CENTER_INSIDE (7);
        
        ScaleType(int ni) {
            nativeInt = ni;
        }
        final int nativeInt;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            throw new NullPointerException();
        }

        if (mScaleType != scaleType) {
            mScaleType = scaleType;
//            setWillNotCacheDrawing(mScaleType == ScaleType.CENTER);            
//            requestLayout();
            invalidate();
        }
    }
}
