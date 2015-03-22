package thahn.java.agui.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 *
 * @author thAhn
 * 
 */
public class SwtHelper {
	
	private static SwtHelper	mInstance;
	private Display				mDisplay;
	
	private SwtHelper() {
		mDisplay = new Display();
	}

	public static SwtHelper getInstance() {
		if (mInstance == null) {
			mInstance = new SwtHelper();
		}
		return mInstance;
	}
	
	public static Display getDisaply() {
		return getInstance().mDisplay;
	}
	
	public static Shell getShell() {
		Shell shell = new Shell(getDisaply());
		return shell;
	}
}
