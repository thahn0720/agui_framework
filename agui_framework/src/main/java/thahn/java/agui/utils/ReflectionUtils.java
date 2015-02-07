package thahn.java.agui.utils;

public class ReflectionUtils {
	public static void setFieldValue(Object src, String fieldName, Object value) {
		try {
			src.getClass().getField(fieldName).set(src, value);
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
//			e.printStackTrace();
//			throw new RuntimeException("setFieldValue : reflection error.");
		}
	}
}
