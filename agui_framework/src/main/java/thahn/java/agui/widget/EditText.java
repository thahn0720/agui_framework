package thahn.java.agui.widget;

import java.awt.Color;
import java.awt.Graphics;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.KeyEvent;
import thahn.java.agui.view.MotionEvent;


public class EditText extends TextView {

	private String																mHint;
//	private TextController														mTextController;
	
	public EditText(Context context) {
		this(context, new AttributeSet(context));
	}

	public EditText(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_EDIT_TEXT_CODE);
	}

	public EditText(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	public void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		setFocusable(true);
	}

	@Override
	public void draw(Graphics g) {
		if(mBackground == null) {
			g.setColor(Color.WHITE);
			g.fillRoundRect(getLeft(), getTop(), getWidth(), getHeight(), 2, 2);
			g.setColor(Color.BLACK);
		}
		super.draw(g);
		mTextController.drawCaret(g);
//		g.drawRect(getDrawingLeft(), getDrawingTop(), getWidth(), getHeight());
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		mTextController.dispatchKeyEvent(event);
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = event.getX();
		int y = event.getY();
		mTextController.moveCaretTo(mTextController.getTextPoint(x, y));
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	public void setText(String text) {
		super.setText(text);
	}
}
