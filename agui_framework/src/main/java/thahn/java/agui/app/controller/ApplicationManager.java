package thahn.java.agui.app.controller;

import java.util.Collection;
import java.util.HashMap;

import thahn.java.agui.app.ApplicationInfo;


public class ApplicationManager {
	
	private HashMap<String, ApplicationInfo> 									mApplicationInfoList; 
	private static ApplicationManager 											mInstance;
	
	// FIXME : security : 이렇게 하면 모든 api 사용자들이 접근할 수 있다.
	public static final ApplicationManager getInstance() {
		if(mInstance == null) {
			mInstance = new ApplicationManager();
		}
		return mInstance;
	}
	
	private ApplicationManager() {
		mApplicationInfoList = new HashMap<>();
	}
	
	public void add(String key, ApplicationInfo info) {
		mApplicationInfoList.put(key, info);
	}
	
	public ApplicationInfo getApplicationInfo(String key) {
		return mApplicationInfoList.get(key);
	}
	
	public Collection<ApplicationInfo> getApplicationList() {
		return mApplicationInfoList.values();
	}
	
	public boolean contains(String name) {
		return mApplicationInfoList.containsKey(name);
	}
	
	public void remove(ApplicationInfo info) {
		mApplicationInfoList.remove(info.getPackageName());
	}
}
