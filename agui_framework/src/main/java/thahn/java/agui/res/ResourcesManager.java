package thahn.java.agui.res;

import thahn.java.agui.app.Context;
import thahn.java.agui.app.WindowManager;
import thahn.java.agui.utils.DisplayMetrics;
import thahn.java.agui.view.Display;

public class ResourcesManager {
	
	private static Resources 							mContainer;
	
	public static final Resources getInstance() {
		if(mContainer == null) {
			mContainer = new Resources();
		}
		return mContainer;
	}
}
