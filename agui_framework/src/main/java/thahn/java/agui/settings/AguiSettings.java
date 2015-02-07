package thahn.java.agui.settings;

public class AguiSettings {
	
	private static AguiSettings mInstance;
	
	public static AguiSettings getInstance() {
		if (mInstance == null) {
			mInstance = new AguiSettings();
		}
		return mInstance;
	}
	
	private AguiSettings() {
	}

	public int getDefaultActivityWidth() {
		return 500;
	}
	
	public int getDefaultActivityHeight() {
		return 500;
	}
	
	public int getDefaultWindowDialogWidth() {
		return 310;
	}
	
	public int getDefaultWindowDialogHeight() {
		return 240;
	}
	
	public int getDialogBackgroundColor() {
		return 0x40000000;
	}
}
