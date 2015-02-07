package thahn.java.agui.sqlite;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class DatabaseUtils {
	public static String bindValue(String sql, Set<Entry<String, Object>> entries) {
		StringBuilder builder = new StringBuilder(sql);
		int size = entries.size();
		int index = 0;
		Iterator<Entry<String, Object>> iter = entries.iterator();
		for (int i = 0; i < size; i++) {
			Object value = iter.next().getValue();
			index = builder.indexOf("?", index);
			builder.deleteCharAt(index);
			if(value instanceof String) {
				builder.insert(index, new StringBuilder("\"").append(value).append("\"").toString());
				index += ((String) value).length() +2;
			} else {
				builder.insert(index, String.valueOf(value));
			}
		}
		return builder.toString();
	}
	
	public static String bindValue(String sql, String[] entries) {
		StringBuilder builder = new StringBuilder(sql);
		for (int i = 0; i < entries.length; i++) {
			Object value = entries[i];
			int index = builder.indexOf("?");
			builder.deleteCharAt(index);
			builder.insert(index, String.valueOf(value));
		}
		return builder.toString();
	}
}
