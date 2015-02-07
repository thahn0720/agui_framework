package thahn.java.agui.app;

import java.io.Serializable;

public class ApplicationInfo implements Serializable {
	private static final long 									serialVersionUID = 4750016928655329789L;

	public String packageName;
	
	public String id;
	public String path;
	public String mainActivity;
	public int width;
	public int height;
	
	public ApplicationInfo() {
		
	}

	public String getId() {
		return id;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getPath() {
		return path;
	}

	public String getMainActivity() {
		return mainActivity;
	}
}
