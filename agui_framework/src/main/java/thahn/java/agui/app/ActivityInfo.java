
package thahn.java.agui.app;

import thahn.java.agui.app.controller.HandlerThread;
import thahn.java.agui.content.pm.ComponentInfo;

public class ActivityInfo extends ComponentInfo {
	public String name;
	public String label;
	public int icon = -1;
	public String theme;
	public int screenOrientation;
	public int windowSoftInputMode;
	public HandlerThread activityLooper;
	public int enterAni = -1;
	public int exitAni = -1;
	public int customTitle = -1;
	public int width;//getDefaultWidth();
	public int height;//getDefaultWidth();
}
