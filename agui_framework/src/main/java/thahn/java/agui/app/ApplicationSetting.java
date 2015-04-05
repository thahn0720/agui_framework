package thahn.java.agui.app;

import java.io.Serializable;


public class ApplicationSetting implements Serializable {
	private static final long			serialVersionUID		= -4845987348964690878L;
	//
	public static final int				SCROLL_AMOUNT			= 3;
	public static final int				WINDOW_WIDTH_PADDING	= 7;
	public static final int				WINDOW_HEIGHT_PADDING	= 37;
	//
	public static ApplicationInfo		applicationInfo			= new ApplicationInfo();
	/** decide whether terminate service or not when application is finished  */
	private boolean						forceServiceTerminate;
	private boolean						autoResize;
	private boolean						isDebug					= true;

	private int							applicationFlags;

	private static ApplicationSetting	mApplicationSetting;
	
	private ApplicationSetting() {
		forceServiceTerminate = true;
		autoResize = true;
	}
	
	public static ApplicationSetting getInstance() {
		if(mApplicationSetting == null) {
			mApplicationSetting = new ApplicationSetting();
		} 
		return mApplicationSetting;
	}

	public boolean isForceServiceTerminate() {
		return forceServiceTerminate;
	}

	public void setForceServiceTerminate(boolean forceServiceTerminate) {
		this.forceServiceTerminate = forceServiceTerminate;
	}

	private void setFlags(int flags, int mask) {
		applicationFlags = (applicationFlags & ~mask) | (flags & mask);
	}

	public boolean isAutoResize() {
		return autoResize;
	}

	public void setAutoResize(boolean autoResize) {
		this.autoResize = autoResize;
	}
	
	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public static int getDefaultWidth() {
		return 500 - 10;
	}
	
	public static int getDefaultHeight() {
		return 500 - 40;
	}
}
