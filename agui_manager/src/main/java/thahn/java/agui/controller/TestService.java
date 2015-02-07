package thahn.java.agui.controller;

import thahn.java.agui.app.Dialog;
import thahn.java.agui.app.Intent;
import thahn.java.agui.app.Service;
import thahn.java.agui.controller.tray.TrayMenuManager;

public class TestService extends Service {
	
	private TrayMenuManager mTrayMenuManager = new TrayMenuManager();
	
	@Override
	public void onCreate() {
		super.onCreate();
		mTrayMenuManager.setTray(this, this);
	}
	
	@Override	
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			//
			showDialog(1);			// non-blocking
//			showDialog(0, true);	// blocking
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public Dialog onCreateDialog(int i) {
		return mTrayMenuManager.onCreateDialog(i);
	}

	@Override
	public void stopSelf() {
		super.stopSelf();
	}
}
