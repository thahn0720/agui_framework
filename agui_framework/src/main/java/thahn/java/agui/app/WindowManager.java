package thahn.java.agui.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import thahn.java.agui.view.Display;

public class WindowManager {
	
	private HashMap<String, WindowInfo> 									mWindowInfoList;
	private String															mCurrentWindowName;
	private static WindowManager 											mInstance;
	
	// FIXME : security : 이렇게 하면 모든 api 사용자들이 접근할 수 있다.
	static final WindowManager getInstance() {
		if(mInstance == null) {
			mInstance = new WindowManager();
		}
		return mInstance;
	}
	
	private WindowManager() {
		mWindowInfoList = new HashMap<>();
	}
	
	public Display getDefaultDisplay() {
		return new Display(mWindowInfoList.get(mCurrentWindowName).window); 
	}
	
	void add(String key, WindowInfo info) {
		mCurrentWindowName = key;
		mWindowInfoList.put(key, info);
	}
	
	WindowInfo getWindowInfo(String key) {
		return mWindowInfoList.get(key);
	}
	
	Collection<WindowInfo> getWindowList() {
		return mWindowInfoList.values();
	}
	
	boolean contains(String name) {
		return mWindowInfoList.containsKey(name);
	}
	
	void destroyWindow(Window window) {
		Set<String> windowNames = mWindowInfoList.keySet();
		Iterator<String> iter = windowNames.iterator();
		while(iter.hasNext()) {
			WindowInfo info = mWindowInfoList.get(iter.next());  
			if(info != null && info.window == window) {
				mWindowInfoList.remove(info.name);
				info.destoyWindow();
				info = null;
				iter = windowNames.iterator();
			}
		}
	}
}
