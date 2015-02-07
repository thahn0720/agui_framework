package thahn.java.agui.app;

import java.util.Collection;
import java.util.HashMap;

public class ServiceManager {
	
	private HashMap<String, ServiceInfo> 									mServiceInfoList; 
	private static ServiceManager 											mInstance;

	public static final ServiceManager getInstance() {
		if(mInstance == null) {
			mInstance = new ServiceManager();
		}
		return mInstance;
	}
	
	private ServiceManager() {
		mServiceInfoList = new HashMap<>();
	}
	
	public void put(String key, ServiceInfo info) {
		mServiceInfoList.put(key, info);
	}
	
	public ServiceInfo getServiceInfo(String key) {
		return mServiceInfoList.get(key);
	}
	
	public Collection<ServiceInfo> getServiceInfoList() {
		return mServiceInfoList.values();
	}
}

