package thahn.java.agui.app;

import java.awt.Graphics;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;

import thahn.java.agui.utils.Log;


public class ToastManager extends JComponent {
	
	private ConcurrentLinkedQueue<Toast>										mToastQueue;
//	private BlockingQueue<Toast>												mToastQueue;
	private static ToastManager 												mInstance;
	// TODO : instead, use util.concurrent
	private VolatileLooper 														mLoop;
	private Toast																mCurrentToast;
	private ReentrantLock														mReentrantLock	= new ReentrantLock();
	
	public ToastManager() {
		mToastQueue = new ConcurrentLinkedQueue<>();
//		mToastQueue = new ArrayBlockingQueue<Toast>(10, true);
		mLoop = new VolatileLooper();
		setOpaque(false);
	}

	public static final ToastManager getInstance() {
		if(mInstance == null) {
			mInstance = new ToastManager();
		}
		return mInstance; 
	}
	
	public void enqueueToast(Toast toast) {
		mToastQueue.add(toast);
	}
	
	public void dequeueToast(Toast toast) {
		mToastQueue.remove(toast);
	}
	
	public void loop() {
		if(!mLoop.isAlive()) {
			mLoop = new VolatileLooper();
			mLoop.start();
		}
	}
	
	@Override
	public void paint(Graphics g) {
//		mReentrantLock.lock();
//		{
			if(mCurrentToast != null) mCurrentToast.getView().draw(g);
//		}
//		mReentrantLock.unlock();
	}
	
	class VolatileLooper extends Thread {

		@Override
		public void run() {
			try {
				for(;;) {
					if(mToastQueue.isEmpty()) {
//						Log.e("Toast Empty");
						setVisible(false);
						return ;
					}
					Toast toast = mToastQueue.poll(); // take();
//					mReentrantLock.lock();
//					{
//						if(mCurrentToast != null) {
//							mCurrentToast.recycle();
//						}
						mCurrentToast = toast;
//					}
//					mReentrantLock.unlock();
					setVisible(true);
					Thread.sleep(toast.getDuration());
				}
			} catch (InterruptedException e) {
				Log.e("Toast Interrupted.");
//				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		if(mLoop.isAlive()) mLoop.interrupt();
		setVisible(false);
	}
}
