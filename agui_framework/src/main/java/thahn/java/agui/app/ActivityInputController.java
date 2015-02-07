package thahn.java.agui.app;

import java.awt.event.KeyEvent;

public class ActivityInputController extends InputController {

	/*package*/ Activity											mActivity;
	
	public ActivityInputController(Activity activity, MyPanel panel) {
		super(panel);
		addInputListener(activity, panel);
	}

	/**
	 * instead, use {@link #addInputListener(Activity, MyPanel)}
	 */
	@Deprecated
	@Override
	/*package*/ void addInputListener(MyPanel panel) {
		super.addInputListener(panel);
	}

	/*package*/ void addInputListener(Activity activity, MyPanel panel) {
		removeCurrentInputListener();
		mActivity = activity;
		super.addInputListener(panel);
	}

	@Override
	/*package*/ void preprocessKeyEvent(KeyEvent e) {
		super.preprocessKeyEvent(e);
		
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_ESCAPE:
			// TODO : implements exit action when last activity stack
			if (mActivity.mManagedDialogs.isEmpty()) {
				Window parent = (Window) mActivity.getParent();
				mActivity.finish();
				if (parent.getCurrentActivity() == null) {
					parent.destroy();
				}
			} else {
				mActivity.dismissDialog(mActivity.mManagedDialogs.peek().dialog.getId());
			}
			break;
		}
	}
}
