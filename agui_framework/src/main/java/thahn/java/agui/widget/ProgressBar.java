package thahn.java.agui.widget;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import thahn.java.agui.app.Context;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup.LayoutParams;


public class ProgressBar extends View implements ActionListener {

	private Drawable															mIndeterminateDrawable;
	private boolean																isBgFade;

	private boolean 															mIsRunning;
	private boolean 															mIsFadingOut;
	private Timer 																mTimer;
	private int 																mAngle;
	private int 																mFadeCount;
	private int 																mFadeLimit 				= 15;
	
	private Color																mBackgroundColor		= Color.white;
	private Color																mIndeterminateColor 	= Color.black;
	
	public ProgressBar(Context context) {
		this(context, new AttributeSet(context));
	}
	
	public ProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_PROGRESS_BAR_CODE);
	}
	
	public ProgressBar(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}
	
	@Override
	public void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
		Drawable drawable = attrs.getDrawable(thahn.java.agui.R.attr.ProgressBar_indeterminateDrawable, defaultType);
        if (drawable != null) {
            setIndeterminateDrawable(drawable);
        }
        
        isBgFade = attrs.getBoolean(thahn.java.agui.R.attr.ProgressBar_isBackgroundFaded, false);
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
		if(mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			mWidth = mIndeterminateDrawable!=null?mIndeterminateDrawable.getIntrinsicWidth():80;
		}  
		
		if(mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			mHeight = mIndeterminateDrawable!=null?mIndeterminateDrawable.getIntrinsicHeight():80;
		}
	}

	@Override
	public void initDefault() {
		super.initDefault();
		start();
	}

	int a = 0;
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		int left = getDrawingLeft();
		int top = getDrawingTop();
		int w = getWidth();//mIndeterminateDrawable.getWidth();
		int h = getHeight();//mIndeterminateDrawable.getHeight();
		if (!mIsRunning) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g.create();
//		
		float fade = (float) mFadeCount / (float) mFadeLimit;
//		
		if(isBgFade) {
//			g2.setColor(mBackgroundColor);
//			Composite urComposite = g2.getComposite(); // Gray it out.
//			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f * fade));
//			g2.fillRect(0, 0, w, h);
//			g2.setComposite(urComposite);
		}
//		
 		int s = Math.min(w, h) / 5;// Paint the wait indicator.
 		int cx = left + (w/2);
 		int cy = top + (h/2);
 		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 		g2.setStroke(new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 		g2.setPaint(mIndeterminateColor);
 		g2.rotate(Math.PI * mAngle / 180, cx, cy);
 		if(mIndeterminateDrawable != null) {
 			mIndeterminateDrawable.setBounds(getDrawingLeft(), getDrawingTop(), getDrawingLeft() + mWidth, getDrawingTop() + mHeight);
 			mIndeterminateDrawable.draw(g2);
 		} else {
 			for (int i = 0; i < 12; i++) {
				float scale = (11.0f - (float) i) / 11.0f;
				g2.drawLine(cx + s, cy, cx + s * 2, cy);
				g2.rotate(-Math.PI / 6, cx, cy);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scale * fade));
 			}
		}
//		g2.dispose();
	}
	
	public void setIndeterminateDrawable(Drawable d) {
		mIndeterminateDrawable = d;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (mIsRunning) {
			// FIXME : 매번 전체를 invalidate하기 너무 불필요한 낭비 발생 이 view만 invalidate 하자.
			invalidate();
			mAngle += 3;
			if (mAngle >= 360) {
				mAngle = 0;
			}
			if(isBgFade) {
				if (mIsFadingOut) {
					if (--mFadeCount <= 0) {
						mIsRunning = false;
						mTimer.stop();
					}
				} else if (mFadeCount < mFadeLimit) {
					mFadeCount++;
				}
			}
		}
	}

	public void start() {
		if (mIsRunning) {
			return;
		}
		mIsRunning = true;// Run a thread for animation.
		mIsFadingOut = false;
		mFadeCount = 0;
		int fps = 24;
		int tick = 1000 / fps;
		mTimer = new Timer(tick, this);
		mTimer.start();
	}

	public void stop() {
		isBgFade = true;
		mIsFadingOut = true;
	}
	
	public void setBackgroundfade(boolean is) {
		isBgFade = is;
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stop();
	}
}
