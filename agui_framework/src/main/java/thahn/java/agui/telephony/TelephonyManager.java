package thahn.java.agui.telephony;

public class TelephonyManager {
	
	private static TelephonyManager mInstance;

	private TelephonyManager() {
	}

	public static TelephonyManager getInstance() {
		if (mInstance == null) {
			mInstance = new TelephonyManager();
		}
		return mInstance;
	}
	
	public String getLine1Number() {
		// TODO : implements this service
		return null;
	}
}
