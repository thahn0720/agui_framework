package thahn.java.agui.app;

import java.awt.Font;
import java.awt.FontMetrics;

import thahn.java.agui.ContextMenu;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.settings.AguiSettings;
import thahn.java.agui.utils.AguiException;
import thahn.java.agui.view.View;
import thahn.java.agui.widget.ShellView;

public class ContextWrapper extends Context {

	private MyPanel 														mPanel;		
	
	/*package*/ void setPanel(MyPanel panel) {
		mPanel = panel;
	}
	
	@Override
	public void changedFocusable(View view) {
	}

	@Override
	public int getWidth() {
		if (mPanel != null) {
			return mPanel.getWidth();
		} 
		return AguiSettings.getInstance().getDefaultWindowDialogWidth();
	}

	@Override
	public int getHeight() {
		if (mPanel != null) {
			return mPanel.getHeight();
		} 
		return AguiSettings.getInstance().getDefaultWindowDialogHeight();
	}

	@Override
	public void arrangeView() {
		if (mPanel != null) {
			mPanel.arrangeView();
		}
	}

	@Override
	public void invalidate() {
		if (mPanel != null) {
			mPanel.invalidate();
		}
	}

	@Override
	public Font getFont() {
		if (mPanel != null) {
			mPanel.getFont();
		}
		return getApplicationContext().getFont();
	}

	@Override
	public FontMetrics getFontMetrics(Font font) {
		if (mPanel != null) {
			mPanel.getFontMetrics(font);
		}
		return getApplicationContext().getFontMetrics(font);
	}

	@Override
	public void setContextMenu(ContextMenu contextMenu) {
		throw new AguiException("Don't use in context without screen");
	}

	@Override
	public void removeContextMenuVisible() {
		throw new AguiException("Don't use in context without screen");
	}

	@Override
	public void removeShellView(ShellView view) {
	}

	@Override
	public Drawable getBackgroundDrawable() {
		return null;
	}

	@Override
	void resize() {
		if (mPanel != null) {
			mPanel.arrangeView();
		}
	}

	@Override
	public void addShellView(ShellView view) {
	}
}
