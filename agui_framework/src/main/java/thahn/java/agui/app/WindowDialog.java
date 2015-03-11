package thahn.java.agui.app;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import thahn.java.agui.settings.AguiSettings;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup;

/**
 * 
 * @author thAhn
 *
 */
public class WindowDialog extends Dialog implements DialogInterface {
	
	private JFrame														mFrame;
	private MyPanel														mPanel;
	private	InputController												mInputController;
	// ActivityInputController

	public WindowDialog(Context context) {
		super(context);
		init();
		
		if (context instanceof ContextWrapper) { // without view
			((ContextWrapper) context).setPanel(mPanel);  
		} else if (context instanceof ContextThemeWrapper) {
			ContextWrapper newContext = new ContextWrapper();
			newContext.setPanel(mPanel);
			mContext = newContext;
		}
	}

	public WindowDialog(Dialog dialog) {
		super(dialog.mContext);
		init();
		
		if (dialog.mContext instanceof ContextWrapper) { // without view
			((ContextWrapper) dialog.mContext).setPanel(mPanel);  
		} else if (dialog.mContext instanceof ContextThemeWrapper) { // application controller or activity
			ContextWrapper newContext = new ContextWrapper();
			newContext.setPanel(mPanel);
			dialog.mContext = newContext;
		}
		
		mContext = dialog.mContext;
		mId = dialog.mId;
		mOwnerActivity = dialog.mOwnerActivity;
		mDialogView = dialog.mDialogView;
		mBundle = dialog.mBundle;
		mOnDismissListener = dialog.mOnDismissListener;
		
		replaceContext(mDialogView);
		setView(mDialogView);
	}
	
	private void init() {
		mFrame = new JFrame();
	    mFrame.addWindowListener(new WindowAdapter(){  

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				dismiss();
			}
		});
	    mFrame.addComponentListener(componentListener);
		mPanel = new MyPanel();
		mFrame.add(mPanel);
		mInputController = new InputController(mPanel);
		// panel listener not working. so add listener in frame
		for (KeyListener keyListener : mPanel.getKeyListeners()) {
			mFrame.addKeyListener(keyListener);
		}
	}

	private void replaceContext(View dialogView) {
		if (dialogView instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) dialogView;
			for (int i = 0; i < group.getChildCount(); i++) {
				View view = group.getChildAt(i);
				if (view instanceof ViewGroup) {
					replaceContext(view);
				} else {
					view.setContext(mContext);
				}
			}
		} else {
			dialogView.setContext(mContext);
		}
	}
	
	@Override
	void setView(View view) {
		super.setView(view);
		
		mDialogView.onMeasure(AguiSettings.getInstance().getDefaultWindowDialogWidth()
				, AguiSettings.getInstance().getDefaultWindowDialogHeight());
		mDialogView.onPostMeasure(AguiSettings.getInstance().getDefaultWindowDialogWidth()
				, AguiSettings.getInstance().getDefaultWindowDialogHeight());
		mDialogView.onLayout(true, 0, 0, mDialogView.getWidth(), mDialogView.getHeight());
		mDialogView.arrange();
		mPanel.setView((ViewGroup) mDialogView);
	}

	@Override
	public void show() {
		super.show();
		mFrame.setSize(mDialogView.getWidth(), mPanel.mDecorView.getHeight());
		mFrame.setVisible(true);
	}

	@Override
	public void dismiss() {
		// super.dismiss();
		mFrame.dispose();
		mFrame.setVisible(false);
		if(mOnDismissListener != null) mOnDismissListener.onDismiss(this);
	}
	
	private ComponentListener componentListener = new ComponentListener() {
		
		@Override
		public void componentShown(ComponentEvent e) {
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			mContext.resize();
		}
		
		@Override
		public void componentMoved(ComponentEvent e) {
		}
		
		@Override
		public void componentHidden(ComponentEvent e) {
		}
	};
}
