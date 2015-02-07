package thahn.java.agui.utils;

import org.apache.log4j.Logger;

public class Log {
    public static Logger 														l;

    static {
        synchronized(Log.class){
            if(l == null){
                l = Logger.getLogger("agui");
            }
        }
    }

	public static void e(String msg) {
		l.error(msg);
	}
	
	public static void e(String tag, String msg) {
		l.error(tag + " : " + msg);
	}
	
	public static void e(String tag, String msg, Exception e) {
		l.error(tag + " : " + msg);
	}
	
	public static void w(String msg) {
		l.error(msg);
	}
	
	public static void w(String tag, String msg) {
		l.error(tag + " : " + msg);
	}
	
	public static void w(String tag, String msg, Exception e) {
		l.error(tag + " : " + msg);
	}
	
	public static void i(String msg) {
		l.info(msg);
	}
	
	public static void i(String tag, String msg) {
		l.info(tag + " : " + msg);
	}
	
	public static void d(String msg) {
//		l.debug(msg);
	}
	
	public static void d(String tag, String msg) {
//		l.debug(tag + " : " + msg);
	}
	
	public static void d(String tag, String msg, Exception e) {
//		l.debug(tag + " : " + msg);
	}
	
	public static void v(String msg) {
		l.debug(msg);
	}
	
	public static void v(String tag, String msg) {
		l.debug(tag + " : " + msg);
	}
	
	public static void v(String tag, String msg, Exception e) {
		l.debug(tag + " : " + msg);
	}
	
	public static void t(String msg) {
		e(msg);
		throw new RuntimeException(msg);
	}
}
