package thahn.java.agui.app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;
import thahn.java.agui.widget.Button;
import thahn.java.agui.widget.HorizontalScrollView;
import thahn.java.agui.widget.ScrollView;
import agui.support.v4.view.ViewPager;
import agui.support.v4.widget.DrawerLayout;

/**
 * 
 * @author thAhn
 *
 */
public class InputController {
	
	private View																mFocusView;
	private View																mMouseMovingView;
	private MyPanel																mPanel;

	public InputController(MyPanel panel) {
		addInputListener(panel);
	}
	
	/*pacakge*/ void addInputListener(MyPanel panel) {
		mPanel = panel;
		mPanel.addKeyListener(keyListener);
		mPanel.addMouseListener(mouseListener);
		mPanel.addMouseMotionListener(mouseMotionListener);
		mPanel.addMouseWheelListener(mouseWheelListener);
	}
	
	/*pacakge*/ void removeCurrentInputListener() {
		if (mPanel != null) {
			mPanel.removeKeyListener(keyListener);
			mPanel.removeMouseListener(mouseListener);
			mPanel.removeMouseMotionListener(mouseMotionListener);
			mPanel.removeMouseWheelListener(mouseWheelListener);
		}
	}
	
	/*pacakge*/ void removeInputListener(MyPanel panel) {
		panel.removeKeyListener(keyListener);
		panel.removeMouseListener(mouseListener);
		panel.removeMouseMotionListener(mouseMotionListener);
		panel.removeMouseWheelListener(mouseWheelListener);
	}

	protected void preprocessKeyEvent(KeyEvent e) {
		
	}
	
	// keyPressed -> keyTyped -> keyReleased
	private KeyListener	keyListener	= new KeyListener() {

    	@Override
		public void keyPressed(KeyEvent e) {
    		Log.i("InputController", "keyEvent : " + e.getKeyChar());
    		preprocessKeyEvent(e);
			if (mFocusView != null) {
				mFocusView.dispatchKeyEvent(new thahn.java.agui.view.KeyEvent(thahn.java.agui.view.KeyEvent.ACTION_KEY_PRESSED, e));
			}
		}
    	
		@Override
		public void keyTyped(KeyEvent e) {
//			Log.e("keyTyped");
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
//			Log.e("keyReleased");
//			if (mFocusView != null) mFocusView.dispatchKeyEvent(new AKeyEvent(AKeyEvent.ACTION_KEY_RELEASED, e));
		}
	};
	
	private MouseListener mouseListener = new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			MotionEvent event = new MotionEvent(e, MotionEvent.ACTION_UP, e.getButton());
			// action up
			if (mMouseMovingView != null) {
				mMouseMovingView.onTouchEvent(event);
				// bounds 를 벗어나면 아래 거 실행.
				// 모든 마우스를 releaseUp 하면 contextMenu 도 실행된닫. 구분해라.
				// change drawable background into released
				if (event.getButtonCode() == MotionEvent.BUTTON1) {
					releaseMouse(mMouseMovingView, event);
				}
			}
			mMouseMovingView = null;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// pressed -> released -> clicked
			// action down
			dispatchMouseEventByPoint(new MotionEvent(e, MotionEvent.ACTION_DOWN, e.getButton()));
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
		
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			Log.i("x : " + (e.getX()) + ", y : " + (e.getY()));
			dispatchMouseEventByPoint(new MotionEvent(e, MotionEvent.ACTION_CLICK, e.getButton()));
		}
	};
	
	private MouseMotionListener mouseMotionListener = new MouseMotionListener() {
		
		@Override
		public void mouseMoved(MouseEvent e) {
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			// action_move
			if (mMouseMovingView != null) {
				if (mMouseMovingView.contains(e.getX(), e.getY())) {
					mMouseMovingView.onTouchEvent(new MotionEvent(e, MotionEvent.ACTION_MOVE, e.getButton()));
				} else {
					releaseMouse(mMouseMovingView);
				}
			}
		}
	}; 
	
	private MouseWheelListener mouseWheelListener = new MouseWheelListener() {
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			dispatchMouseEventByPoint(new thahn.java.agui.view.MouseWheelEvent(e, MotionEvent.ACTION_WHEEL));
		}
	};

	private void releaseMouse(View view, MotionEvent event) {
		if (view instanceof ViewGroup) {
			if (((ViewGroup) view).getChildCount() > 0) {
				for (View child : ((ViewGroup) view).getChildren()) {
					if (child instanceof ViewGroup) {
						releaseMouse(child, event);
					} else {
						releaseMouse(child);
					}
				}
			} else {
				releaseMouse(view);
			}
		} else {
			releaseMouse(view);
		}
	}
	
	private void releaseMouse(View view) {
		Drawable bg = view.getBackground();
		if (mFocusView == null && bg != null && !bg.getState(Drawable.STATE_PRESSED_FALSE)) {
			bg.setState(Drawable.STATE_ACTIVATED_FALSE);
			bg.setState(Drawable.STATE_PRESSED_FALSE);
			view.setPressed(false);
			view.invalidate();
		}
		
		mMouseMovingView = null;
	}
	
	// FIXME : android 참고해서 touch event 발생 시 버튼 안에서 드래그 해서 밖으로 나가도 계속 따라다니는지 조사.
	private void dispatchMouseEventByPoint(MotionEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (mPanel.mDecorView == null) {
			return; 
		}
		boolean contains = mPanel.mDecorView.contains(x, y);
		if (contains) {
			boolean ret = false;
			if (mPanel.mDecorView instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) mPanel.mDecorView;
				if (group.contains(x, y)) {
					mMouseMovingView = findTouchChild(group, x, y);
					ret = dispatchMouseEvent(mMouseMovingView, e);
					if (ret) return ;
				}
			} else if (mPanel.mDecorView instanceof View) {
				ret = dispatchMouseEvent(mPanel.mDecorView, e);
				mMouseMovingView = mPanel.mDecorView;
				if (ret) return ;
			} 
		}
	}
	
	public static View findTouchChild(ViewGroup container, int x, int y) {
		View[] children = container.getChildren();
		for (int i=children.length-1; i>=0; --i) {
			View child = children[i];
			if (child.getVisibility() != View.GONE && child.contains(x, y)) {
				if (child instanceof ViewPager) {
					View view = findTouchChild((ViewGroup) child, x, y);
					if ((!view.hasOnTouchListeners() && !view.hasOnClickListeners()) && !(view instanceof Button)) {
						return child;
					} else {
						return view;
					}
				} else if (child instanceof ScrollView || child instanceof HorizontalScrollView || child instanceof DrawerLayout) {
					return child;
				} else if (child instanceof ViewGroup) {
					return findTouchChild((ViewGroup) child, x, y);
				} else {
					return child;
				}
			}
		}
		
		return container;
	}
	
	public static View findRealTouchChild(ViewGroup container, int x, int y) {
		View[] children = container.getChildren();
		for (int i=children.length-1; i>=0; --i) {
			View child = children[i];
			if (child.getVisibility() != View.GONE && child.contains(x, y)) {
				if (child instanceof ViewGroup) {
					return findTouchChild((ViewGroup) child, x, y);
				} else {
					return child;
				}
			}
		}
		
		return container;
	}
	
	/**
	 * 
	 * @param view
	 * @param e
	 * @return if true, consumed this event
	 */
	private boolean dispatchMouseEvent(View view, MotionEvent e) {
		boolean ret = false;
		int action = e.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setFocus(view, e.getX(), e.getY());
			ret = view.onTouchEvent(e);
			break;
		case MotionEvent.ACTION_MOVE:
			ret = view.onTouchEvent(e);
			break;
		case MotionEvent.ACTION_UP:
			ret = view.onTouchEvent(e);
			break;
		case MotionEvent.ACTION_CLICK:
			ret = view.onTouchEvent(e);
			break;
		case MotionEvent.ACTION_WHEEL:
			ret = view.onScroll((thahn.java.agui.view.MouseWheelEvent) e);
			break;
		}
		return ret;
	}
	
	private void setFocus(View view, int x, int y) {
		if (view != mFocusView) {
			View tempView = null;
			if (view instanceof ViewGroup) {
				tempView = findRealTouchChild((ViewGroup) view, x, y);
			} 
			if (mFocusView != null) {
				mFocusView.setFocus(false);
			} 
			if (tempView != null) {
				mFocusView = tempView;
			} else {
				mFocusView = view;
			}
			mFocusView.setFocus(true);
		}
	}
}
