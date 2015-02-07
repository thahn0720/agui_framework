package thahn.java.agui.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import thahn.java.agui.res.ManifestParser.ManagedComponent;

import com.google.common.collect.Lists;

public class ReceiverManager {
	
	private HashMap<String, List<BroadcastReceiver>> 						mReceiverManager;
	private static ReceiverManager											sInstance;
	
	public static ReceiverManager getInstance() {
		if (sInstance == null) {
			sInstance = new ReceiverManager();
		}
		return sInstance;
	}
	
	private  ReceiverManager() {
		mReceiverManager = new HashMap<>();
	}
	
	public void put(String key, BroadcastReceiver value) {
		value.setFinished(false);
		
		if (mReceiverManager.containsKey(key)) {
			mReceiverManager.get(key).add(value);
		} else {
			List<BroadcastReceiver> lists = Lists.newArrayList();
			lists.add(value);
			mReceiverManager.put(key, lists);
		}
	}
	
	public List<BroadcastReceiver> get(String key) {
		return mReceiverManager.get(key);
	}

	public BroadcastReceiver remove(Object key, BroadcastReceiver receiver) {
		if (mReceiverManager.containsKey(key)) {
			BroadcastReceiver forDel = null;
			List<BroadcastReceiver> value = mReceiverManager.get(key);
			for (BroadcastReceiver item : value) {
				if (receiver.getId().equals(item.getId())) {
					forDel = item; 
					break;
				}
			}
			if (forDel != null) {
				value.remove(forDel);
				forDel.setFinished(true);
				// remove receiver in IntentManager
				for (Entry<String, List<ManagedComponent>> entry : IntentManager.getInstance().entrySet()) {
					List<ManagedComponent> entryValues = entry.getValue();
					List<ManagedComponent> forDelComponent = null;
					for (ManagedComponent managed : entryValues) {
						if (managed.which == ManagedComponent.RECEIVER && managed.tag != null && managed.tag instanceof BroadcastReceiver) {
							if (((BroadcastReceiver) managed.tag).getId().equals(forDel.getId())) {
								if (forDelComponent == null) {
									forDelComponent = Lists.newArrayList();
								}
								forDelComponent.add(managed);
							}
						}
					}
					if(forDelComponent != null) { 
						for (ManagedComponent del : forDelComponent) {
							entryValues.remove(del);
						}
					}
					
				}
				return forDel;
			}
		}
		return null;
	}
}
