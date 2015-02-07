package thahn.java.agui.view;

import thahn.java.agui.app.Window;

public class Display {
	
	private Window 													mWindow;
	private int														density;
	
	public Display(Window mWindow) {
		this.mWindow = mWindow;
	}
	
	public int getWidth() {
		return mWindow.getWidth();
	}
	
	public int getHeight() {
		return mWindow.getHeight() - mWindow.getTitleBarHeight();
	}
	
	public int gettitleBarHeight() {
		return mWindow.getTitleBarHeight();
	}
}
