package thahn.java.agui.app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import thahn.java.agui.view.LayoutInflater;
import thahn.java.agui.view.MenuItem;
import thahn.java.agui.view.View;
import thahn.java.agui.view.View.OnCreateContextMenuListener;
import thahn.java.agui.view.ViewGroup.LayoutParams;
import thahn.java.agui.widget.Button;
import thahn.java.agui.widget.ImageView;
import thahn.java.agui.widget.LinearLayout;
import thahn.java.agui.widget.TextView;



public class TitleBar extends MyPanel {
	
	private	InputController														mInputController;
	private Window																mWindow;
	/*package*/ ImageView														mLogoView;
	/*package*/ TextView														mTitleView;
	/*package*/ TextView														mSubTitleView;
	/*package*/ Button															mMinView;
	/*package*/ Button															mMaxView;
	/*package*/ Button															mExitView;
	/*package*/ LinearLayout													mItemContainer;
	private Context																mTitleContext;
	
	/*package*/ TitleBar(Window window) {
		super();
		init(window);
	}
	
	private void init(Window window) {
		mWindow = window;
		mInputController = new InputController(this);
		
		DragWindowListener dwl = new DragWindowListener();
		addMouseListener(dwl);
		addMouseMotionListener(dwl);
	}
	
	/*package*/ void setTitleContext(TitleContext context) {
		mTitleContext = context;
	}
	
	/*package*/ void setDefaultTitleBar() {
		View decorView = LayoutInflater.inflate(mTitleContext, thahn.java.agui.R.layout.agui_actionbar, null);
		setPreferredSize(new Dimension(0, decorView.getHeight()));
		
		mLogoView = (ImageView) decorView.findViewById(thahn.java.agui.R.id.logo);
		mTitleView = (TextView) decorView.findViewById(thahn.java.agui.R.id.title);
		mSubTitleView = (TextView) decorView.findViewById(thahn.java.agui.R.id.subtitle);
		mItemContainer = (LinearLayout) decorView.findViewById(thahn.java.agui.R.id.item_container);
		mMinView = (Button) decorView.findViewById(thahn.java.agui.R.id.minimize);
		mMaxView = (Button) decorView.findViewById(thahn.java.agui.R.id.maximize);
		mExitView = (Button) decorView.findViewById(thahn.java.agui.R.id.exit);
		
		mTitleView.setOnClickListener(onClickListener);
		mMinView.setOnClickListener(onClickListener);
		mMaxView.setOnClickListener(onClickListener);
		mExitView.setOnClickListener(onClickListener);
		
		setView(decorView);
	}
	
	/*package*/ void setCustomView(int resId) {
		View decorView = LayoutInflater.inflate(mTitleContext, resId, null);
		// decorView = LayoutParser.makeDecorView(mTitleContext, decorView).getChildren()[0];
		setPreferredSize(new Dimension(0, decorView.getHeight()));
		setView(decorView);
	}
	
	/*package*/ void setCustomView(View view) {
		// view = LayoutParser.makeDecorView(mTitleContext, view).getChildren()[0];
		setPreferredSize(new Dimension(0, view.getHeight()));
		setView(view);
	}
	
	public void addMenuItem(MenuItem item) {
		mItemContainer.addView(item.getActionView());
	}
	
	/*package*/ View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case thahn.java.agui.R.id.title:
				mWindow.clickTitle();
				break;
			case thahn.java.agui.R.id.minimize:
				mWindow.dispatchEvent(new WindowEvent(mWindow, WindowEvent.WINDOW_ICONIFIED));	
				break;
			case thahn.java.agui.R.id.maximize:
				mWindow.setExtendedState(mWindow.getExtendedState()|JFrame.MAXIMIZED_BOTH);
				break;
			case thahn.java.agui.R.id.exit:
				mWindow.dispatchEvent(new WindowEvent(mWindow, WindowEvent.WINDOW_CLOSING));	
				break;
			}
		}
	};

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	/*package*/ void resize() {
		mDecorView.setLayoutParams(new LayoutParams(mTitleContext.getWidth(), mTitleContext.getHeight()));
		mTitleContext.arrangeView();
	}

	/*package*/ void registerForContextMenu(OnCreateContextMenuListener contextMenuListener, View view) {
		view.setOnCreateContextMenuListener(contextMenuListener);
	}
	
	/*package*/ void registerForContextMenu(OnCreateContextMenuListener contextMenuListener, View view, int btnCode) {
		view.setOnCreateContextMenuListener(contextMenuListener, btnCode);
	}
	
	public Context getContext() {
		return mTitleContext;
	}
	
	/*package*/ class DragWindowListener extends MouseAdapter {
		private MouseEvent start;
		private java.awt.Window window;

		@Override
		public void mousePressed(MouseEvent me) {
			if (window == null) {
				Object o = me.getSource();
				if (o instanceof Window) {
					window = (Window) o;
				} else if (o instanceof JComponent) {
					window = SwingUtilities.windowForComponent(me.getComponent());
				}
			}
			start = me;
		}

		@Override
		public void mouseDragged(MouseEvent me) {
			if (window != null) {
				Point eventLocationOnScreen = me.getLocationOnScreen();
				window.setLocation(eventLocationOnScreen.x - start.getX(),
						eventLocationOnScreen.y - start.getY());
			}
		}
	}
	
	@Override
	public String toString() {
		return "TitleBar : "+super.toString();
	}
	
//	class CloseIcon implements Icon {
//		private int width;
//		private int height;
//
//		public CloseIcon() {
//			width = 16;
//			height = 16;
//		}
//
//		@Override
//		public void paintIcon(Component c, Graphics g, int x, int y) {
//			g.translate(x, y);
//			g.setColor(Color.BLACK);
//			g.drawLine(4, 4, 11, 11);
//			g.drawLine(4, 5, 10, 11);
//			g.drawLine(5, 4, 11, 10);
//			g.drawLine(11, 4, 4, 11);
//			g.drawLine(11, 5, 5, 11);
//			g.drawLine(10, 4, 4, 10);
//			g.translate(-x, -y);
//		}
//
//		@Override
//		public int getIconWidth() {
//			return width;
//		}
//
//		@Override
//		public int getIconHeight() {
//			return height;
//		}
//	}
}
