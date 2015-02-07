package thahn.java.agui.utils;

public class DimensionUtils {
	public static String toPixel(String value) {
		String ret = value;
		
		if(value.startsWith("@")) {
			
		} else {
			if(value.contains("px")) {
				ret = value.replace("px", "");
			} else if(value.contains("dp")) {
				ret = value.replace("dp", "");
			} else if(value.contains("dip")) {
				ret = value.replace("dip", "");
			} else if(value.contains("sp")) {
				ret = value.replace("sp", "");
			}  
		}
		
		return ret;
	}
}
