package thahn.java.agui.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityManager {
	
	private HashMap<String, ActivityInfo> 									mActivityInfoList; 
	private static ActivityManager 											sInstance;

	public static final ActivityManager getInstance() {
		if(sInstance == null) {
			sInstance = new ActivityManager();
		}
		return sInstance;
	}
	
	private ActivityManager() {
		mActivityInfoList = new HashMap<>();
	}
	
	public void put(String key, ActivityInfo info) {
		mActivityInfoList.put(key, info);
	}
	
	/**
	 * 
	 * @param key activity full name
	 * @return
	 */
	public ActivityInfo getActivityInfo(String key) {
		return mActivityInfoList.get(key);
	}
}
