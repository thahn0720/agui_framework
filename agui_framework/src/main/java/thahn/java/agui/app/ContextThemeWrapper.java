package thahn.java.agui.app;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;

import thahn.java.agui.ContextMenu;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.utils.FileUtils;
import thahn.java.agui.view.LayoutParser;
import thahn.java.agui.view.StateCallback;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.view.ViewGroup.LayoutParams;
import thahn.java.agui.widget.ShellView;


/**
 * 
 * @author thAhn
 *
 */
public class ContextThemeWrapper extends Context implements StateCallback, Serializable {
	
	private static final long serialVersionUID = 1882383182161210364L;

	public static final int												NOT_YET 				= -1;
	
	/*package*/ MyPanel													mPanel;
	
	public ContextThemeWrapper() {
		mPanel = new MyPanel();
		mPanel.setFocusable(true);
//		mPanel.setInheritsPopupMenu(false);
		mPanel.requestFocusInWindow();
		mPanel.setLayout(null);
//		mPanel.addComponentListener(mComponentListener);
	}
	
	public void setView(View view) {
		mPanel.setView(LayoutParser.makeDecorView(this, view));
	}
	
	@Override
	/*package*/ void resize() {
		int width = getWidth();
		int height = getHeight();
		ViewGroup.LayoutParams params = new LayoutParams(width, height);
		mPanel.setSize(width, height);
		if(mPanel.mDecorView != null) {
			mPanel.mDecorView.setLayoutParams(params);
			LayoutParser.calcurateLocation(mPanel.mDecorView, 0, 0, width, height);
		}
//		mPanel.mDecorView.arrange();
	}
	
	public View findViewById(int resId) {
		View ret = null;
		if(mPanel.mDecorView.getId() == resId) {
			ret = mPanel.mDecorView;
		} else {
			if(mPanel.mDecorView instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) mPanel.mDecorView;
				ret = group.findViewById(resId);
			}
		}
		return ret;
	}
	
	/*package*/ void setVisible(boolean b) {
		mPanel.setVisible(b);
	}
	
	@Override
	public void setContextMenu(ContextMenu contextMenu) {
		mPanel.add(contextMenu);
	}
	
	@Override
	public void removeContextMenuVisible() {
		mPanel.setComponentPopupMenu(null);
	}
	
	/*package*/ void addKeyListener(KeyListener listener) {
		mPanel.addKeyListener(listener);
	}
	
	/*package*/ void addMouseListener(MouseListener listener) {
		mPanel.addMouseListener(listener);
	}
	
	/*package*/ void addMouseMotionListener(MouseMotionListener listener) {
		mPanel.addMouseMotionListener(listener);
	}
	
	/*package*/ void addMouseWheelListener(MouseWheelListener listener) {
		mPanel.addMouseWheelListener(listener);
	}
	
	@Override
	public Font getFont() {
		return mPanel.getFont();
	}
	
	@Override
	public FontMetrics getFontMetrics(Font font) {
		return mPanel.getFontMetrics(font);
	}
	
	@Override
	public void arrangeView() {
		mPanel.arrangeView();
	}
	
	@Override
	public void invalidate() {
		mPanel.invalidate();
	}
	
	@Override
	public void changedFocusable(View view) {
	}
	
	public Object getSystemService(String name) {
		if(NOTIFICATION_SERVICE.equals(name)) {
			return NotificationManager.getInstance(this);
		} 
		return null;
	}
	
	/*package*/ static void setFilePermissionsFromMode(String name, int mode,
            int extraPermissions) {
        int perms = FileUtils.S_IRUSR|FileUtils.S_IWUSR
            |FileUtils.S_IRGRP|FileUtils.S_IWGRP
            |extraPermissions;
        if ((mode&MODE_WORLD_READABLE) != 0) {
            perms |= FileUtils.S_IROTH;
        }
        if ((mode&MODE_WORLD_WRITEABLE) != 0) {
            perms |= FileUtils.S_IWOTH;
        }
//        if (DEBUG) {
//            Log.i(TAG, "File " + name + ": mode=0x" + Integer.toHexString(mode)
//                  + ", perms=0x" + Integer.toHexString(perms));
//        }
//        FileUtils.setPermissions(name, perms, -1, -1);
    }
	
	public int getWidth() {
		return mPanel.getWidth();
	}
	
	public int getHeight() {
		return mPanel.getHeight();
	}
	
	public void addShellView(ShellView view) {
		boolean included = false;
		Component com = view.getComponent();
		for(Component c : mPanel.getComponents()) {
			if(c == com) included = true;
		}
		if(!included) mPanel.add(com); 
	}
	
	@Override
	public void removeShellView(ShellView view) {
		mPanel.remove(view.getComponent());
	}
	
	@Override
	public Drawable getBackgroundDrawable() {
		return mPanel.mBackgroundDrawable;
	}
}
