package thahn.java.agui.app;

import thahn.java.agui.app.controller.HandlerThread;
import thahn.java.agui.app.controller.Looper;
import thahn.java.agui.utils.Log;


public class LooperManager {
	
	public static void start() {
		mainHandlerThread.start();
		Log.i("looper start");
	}
	
	public static void stop() {
		mainHandlerThread.quit();
		mainHandlerThread.interrupt();
		Log.i("looper stop");
	}

	/**
	 * main looper
	 */
	private static HandlerThread mainHandlerThread = new HandlerThread("LooperManager") {

		@Override
		public void run() {
			Log.i("create main looper");
			Looper.prepareMainLooper();
	        synchronized (this) {
	            mLooper = Looper.myLooper();
	            notifyAll();
	        }
	        onLooperPrepared();
	        Looper.loop();
		}
	};
}
