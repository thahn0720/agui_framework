package thahn.java.agui.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import thahn.java.agui.res.ManifestParser.ManagedComponent;

/**
 * 
 * @author thAhn
 *
 */
public class IntentManager {

	private HashMap<String, List<ManagedComponent>> 								mIntentFilterContainer;
	private static IntentManager													sInstance;
	
	public static final IntentManager getInstance() {
		if (sInstance == null) {
			sInstance = new IntentManager();
		}
		return sInstance;
	}

	private IntentManager() {
		mIntentFilterContainer = new HashMap<>();
	}

	public void put(IntentFilter filter, ManagedComponent value) {
		synchronized (this) {
			// action
			int actionSize = filter.countActions();
			ManagedComponent component = value;
			for (int i = 0; i < actionSize; ++i) {
				String action = filter.getAction(i);
				add(action, component);
			}
			
			// category
			int categorySize = filter.countCategories();
			for (int i = 0; i < categorySize; ++i) {
				String category = filter.getCategory(i);
				add(category, component);
			}
		}
	}
	
	private void add(String key, ManagedComponent component) {
		List<ManagedComponent> list = mIntentFilterContainer.get(key);
		if (list == null) {
			list = new ArrayList<>();
			mIntentFilterContainer.put(key, list);
		}
		if (list.contains(component)) {
			list.remove(list.indexOf(component));
		} 
		list.add(component);
	}

	public void remove(IntentFilter filter) {
		synchronized (this) {
			int actionSize = filter.countActions();
			for (int i = 0; i < actionSize; ++i) {
				mIntentFilterContainer.remove(filter.getAction(i));
			}
	
			int categorySize = filter.countCategories();
			for (int i = 0; i < categorySize; ++i) {
				mIntentFilterContainer.remove(filter.getCategory(i));
			}
		}
	}

	public List<ManagedComponent> get(String actionOrCategory, String packageName) {
		synchronized (this) {
			List<ManagedComponent> list = mIntentFilterContainer .get(actionOrCategory);
			return list;
		}
	}
	
	/*package*/ Set<Entry<String, List<ManagedComponent>>> entrySet() {
		return mIntentFilterContainer.entrySet();
	}
}
