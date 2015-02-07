package thahn.java.agui.app.controller;


/**
 * Class used to run a message loop for a thread.  Threads by default do
 * not have a message loop associated with them; to create one, call
 * {@link #prepare} in the thread that is to run the loop, and then
 * {@link #loop} to have it process messages until the loop is stopped.
 * 
 * <p>Most interaction with a message loop is through the
 * {@link Handler2} class.
 * 
 * <p>This is a typical example of the implementation of a Looper thread,
 * using the separation of {@link #prepare} and {@link #loop} to create an
 * initial Handler to communicate with the Looper.
 * 
 * <pre>
 *  class LooperThread extends Thread {
 *      public Handler mHandler;
 *      
 *      public void run() {
 *          Looper.prepare();
 *          
 *          mHandler = new Handler() {
 *              public void handleMessage(Message msg) {
 *                  // process incoming messages here
 *              }
 *          };
 *          
 *          Looper.loop();
 *      }
 *  }</pre>
 */
public class Looper {
	
	private static final ThreadLocal<Looper> 								sThreadLocal 		= new ThreadLocal<>();
	final MessageQueue														mMessageQueue; 											
	private static Looper 													mMainLooper 		= null;
	public volatile boolean 												mRun;
	
	private Looper() {
		this.mMessageQueue = new MessageQueue();
		mRun = true;
	}

	public Looper(MessageQueue messageQueue) {
		this.mMessageQueue = messageQueue;
	}

	public static final void prepare() {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }
	
	public static final void prepareMainLooper() {
        prepare();
        setMainLooper(myLooper());
//        if (Process.supportsProcesses()) {
//            myLooper().mQueue.mQuitAllowed = false;
//        }
    }
	
	private synchronized static void setMainLooper(Looper looper) {
        mMainLooper = looper;
    }
	
    /** Returns the application's main looper, which lives in the main thread of the application.
     */
    public synchronized static final Looper getMainLooper() {
        return mMainLooper;
    }
	
	public static final Looper myLooper() {
        return (Looper)sThreadLocal.get();
    }
	
	/**
	 * execute in thread
	 */
	public static final void loop() {
//		mLoopTask.start();
		Looper me = myLooper();
		MessageQueue queue = me.mMessageQueue;
		while (true) {
			if (!me.mRun) {
                break;
            }
			Message msg = queue.poll();//sThreadLocal.get().mMessageQueue.poll();		// blocking
			if(msg != null) {
	            msg.target.dispatchMessage(msg);
	            msg.recycle();
			} 
		}
	}
	
	public void quit() {
		Looper me = myLooper();
		me.mRun = false;
//        Message2 msg = Message2.obtain();
//        mQueue.enqueueMessage(msg, 0);
    }
	
//	Thread mLoopTask = new Thread() {
//
//		@Override
//		public void run() {
//			while (true) {
//				Message msg = sThreadLocal.get().mMessageQueue.poll();		// blocking, sThreadLocal.get().
//	            msg.target.dispatchMessage(msg);
//	            msg.recycle();
//			}
//		}
//	};
}
