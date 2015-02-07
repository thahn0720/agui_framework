package thahn.java.agui.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Label;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup.LayoutParams;


/**
 * - shell view's purpose is a layout or place to draw swing or awt's component.<br>
 * - shell view can only set internal component by java code.<br>
 * - shell view can not use 'wrap_content' because internal component is set by java code.<br>
 *   so, if you set, the value of left, top, right, bottom, width and height is 0.<br> 
 * @author thAhn
 * @see #onLayout(boolean, int, int, int, int)
 *
 */
public class ShellView extends View {
	
	private Component 														mComponent;

	public ShellView(Context context, Component component, int width, int height) {
		this(context, new AttributeSet(context));
		setLayoutParams(new LayoutParams(width, height));
		setComponent(component);
	}
	
	/**
	 * through {@link ShellView#setLayoutParams()}, set width, height
	 * @param context
	 */
	private ShellView(Context context) {
		this(context, new AttributeSet(context));
	}
	
	public ShellView(Context context, AttributeSet attrs) {
		super(context, attrs, ViewName.WIDGET_SHELL_VIEW_CODE);
	}

	public ShellView(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}
	
	@Override
	protected void init(Context context) {
		super.init(context);
		mComponent = new Label("ShellView");
	}

	public void setComponent(Component component) {
		mContext.removeShellView(this);
		
		mComponent = component;
		mComponent.setBackground(Color.WHITE);
		if(mLayoutParam.width == LayoutParams.WRAP_CONTENT) {
			mWidth = mComponent.getWidth();
		} 
		if(mLayoutParam.height == LayoutParams.WRAP_CONTENT) {
			mHeight = mComponent.getHeight();
		} 
		
		mComponent.setBounds(getDrawingLeft(), getDrawingTop(), getDrawingWidth(), getDrawingHeight());
		mContext.addShellView(this);
		mComponent.setVisible(true);
	}
	
	public Component getComponent() {
		return mComponent;
	}
	
	@Override
	public void onLayout(boolean isChanged, int l, int t, int r, int b) {
		super.onLayout(isChanged, l, t, r, b);
		setComponent(mComponent);
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		mComponent.paint(g);
	}
}
