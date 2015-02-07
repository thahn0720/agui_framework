package thahn.java.agui.utils;

public class TextUtils {
	
	/**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
    
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.trim().equals(""))
            return true;
        else
            return false;
    }
    
    public static String makeCiphers(int cipers, int digit) {
		StringBuilder index = new StringBuilder();
		index.append(digit);
		
		int temp = 0;
		int unit = 10;
		int count = 1;
		while((temp = digit/unit) > 0) {
			unit *= 10;
			++count;
		}
		for(int j=0;j<5-count;++j) {
			index.insert(0, "0");
		}
		return index.toString();
	}
    
    public static boolean startWith(StringBuilder builder, String compare) {
    	boolean ret = true;
    	int size = compare.length();
    	for (int i = 0; i < size; i++) {
			if(builder.charAt(i) != compare.charAt(i)) {
				ret = false;
				break;
			}
		}
    	return ret;
    }
}
