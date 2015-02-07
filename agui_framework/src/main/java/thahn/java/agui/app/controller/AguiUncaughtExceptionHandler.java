package thahn.java.agui.app.controller;

import thahn.java.agui.utils.Log;

public class AguiUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Log.e("uncaughtException");
		System.out.println(e.getMessage());
	}
}
