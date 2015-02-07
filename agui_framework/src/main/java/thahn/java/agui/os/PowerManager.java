package thahn.java.agui.os;

public class PowerManager {
	
	private static PowerManager mInstance;

	private PowerManager() {
	}

	public static PowerManager getInstance() {
		if (mInstance == null) {
			mInstance = new PowerManager();
		}
		return mInstance;
	}
}
