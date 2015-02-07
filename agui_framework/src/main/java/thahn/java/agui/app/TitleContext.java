package thahn.java.agui.app;

import thahn.java.agui.view.LayoutParser;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.view.ViewGroup.LayoutParams;


public class TitleContext extends ContextThemeWrapper {

	private Window															mWindow;
	
	public TitleContext(Window window, MyPanel panel) {
		mWindow = window;
		mPanel = panel;
	}

	@Override
	public Object getSystemService(String name) {
		return null;
	}

	@Override
	public int getWidth() {
		if(ApplicationSetting.getInstance().isAutoResize()) {
			return mPanel.getWidth()!=0?mPanel.getWidth():mWindow.getWidth();
		} else {
			return mWindow.getWidth();//500;
		}
	}

	@Override
	public int getHeight() {
		if(ApplicationSetting.getInstance().isAutoResize()) { 
			return mPanel.getHeight()!=0?mPanel.getHeight():mWindow.getHeight();
		} else {
			int height = 0;
			if(mPanel.mDecorView != null && mPanel.mDecorView.getHeight() != 0) {
				height = mPanel.mDecorView.getHeight();
			} else {
				height = mWindow.getHeight();//30; 
			}
			return height;
		}
	}

	@Override
	void resize() {
		if(mPanel.mDecorView != null) {
			int height = mPanel.mDecorView.getHeight();
			ViewGroup.LayoutParams params = new LayoutParams(getWidth(), height);
			mPanel.mDecorView.setLayoutParams(params);
			LayoutParser.calcurateLocation(mPanel.mDecorView, 0, 0, getWidth(), height);
			mPanel.setSize(getWidth(), height);
		}
	}
}