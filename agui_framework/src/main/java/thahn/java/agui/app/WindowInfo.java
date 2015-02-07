
package thahn.java.agui.app;

public class WindowInfo {
	
	public String name;
	public String label;
	public int icon;
	// FIXME : security : 이렇게 되면 api를 통해서 window를 잘 못 건드릴 수 있다. 조심조심.
	public Window window;
	
	public void destoyWindow() {
		for(Activity a : window.getActivityStack()) {
			ActivityInfo info = ActivityManager.getInstance().getActivityInfo(a.getClass().getName());
			info.activityLooper.quit();
			info.activityLooper.interrupt();
		}
		
		name = null;
		label = null;
		window = null;
	}
}
