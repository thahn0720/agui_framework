package thahn.java.agui.res;

import java.util.HashMap;

public class EnumResources {

	private static HashMap<Integer, HashMap<Integer, String>> 		mEnumRes;

	EnumResources() {
		mEnumRes = new HashMap<>();
	}
	
	public void put(Integer key, HashMap<Integer, String> value) {
		mEnumRes.put(key, value);
	}
	
	public String get(Integer groupName, Integer valueName) {
		String ret = null;
		HashMap<Integer, String> temp = mEnumRes.get(groupName);
		if(temp != null) ret = temp.get(valueName);
		return ret;
	}
}
