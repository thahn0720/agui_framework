package thahn.java.agui.app;

import java.io.Serializable;

public class ApplicationInfo implements Serializable {
	private static final long 									serialVersionUID = 4750016928655329789L;

	public String id;
	public String path;
	public String appName;
	public String appLabel;
	public String packageName;
	public String mainActivity;
	public int width;
	public int height;
	
	public ApplicationInfo() {
		
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getAppName() {
		return appName;
	}

	public String getAppLabel() {
		return appLabel;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getMainActivity() {
		return mainActivity;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationInfo [id=").append(id).append(", path=").append(path).append(", appName=")
				.append(appName).append(", appLabel=").append(appLabel).append(", packageName=").append(packageName)
				.append(", mainActivity=").append(mainActivity).append(", width=").append(width).append(", height=")
				.append(height).append("]");
		return builder.toString();
	}
}
