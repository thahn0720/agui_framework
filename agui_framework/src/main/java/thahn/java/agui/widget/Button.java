package thahn.java.agui.widget;

import java.awt.Graphics;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;


public class Button extends TextView {

	public Button(Context context) {
		this(context, new AttributeSet(context));
	}

	public Button(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_BUTTON_CODE);
	}
	
	public Button(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	public void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		setScrollEnabled(false);
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
	}
}
