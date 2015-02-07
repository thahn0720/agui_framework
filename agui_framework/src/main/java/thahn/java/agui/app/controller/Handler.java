package thahn.java.agui.app.controller;

import thahn.java.agui.utils.Log;



public class Handler {
		
	private Callback															mCallback;
	private MessageQueue 														mQueue;
	private Looper																mLooper;
//	private HandlerTask															mHandlerTask;
	
    /**
     * Callback interface you can use when instantiating a Handler to avoid
     * having to implement your own subclass of Handler.
     */
    public interface Callback {
        public boolean handleMessage(Message msg);
    }
    
    public Handler() {
		init();
	}
    
    public Handler(Looper looper) {
    	mLooper = looper;
    	mQueue = looper.mMessageQueue;
//    	init();
//        mCallback = callback;
//        mAsynchronous = async;
    }

	public Handler(Callback callback) {
		init();
    	mCallback = callback;
	}
	
	private void init() {
        mLooper = Looper.myLooper();
        if (mLooper == null) {
        	// mHandlerTask = new HandlerTask();
        	// mHandlerTask.start();
        	mLooper = Looper.getMainLooper();
        	if(mLooper == null) {
        		throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
        	} else {
        		// Log.i("Handler : use main looper.");
        	}
        }
        mQueue = mLooper.mMessageQueue;
        mCallback = null;
	}
	
	public final Looper getLooper() {
        return mLooper;
    }
	
	public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
	
    /**
     * Subclasses must implement this to receive messages.
     */
    public void handleMessage(Message msg) {
    }
    
    private final void handleCallback(Message message) {
        message.callback.run();
    }
    
    /**
     * Returns a new {@link Message.os.Message Message} from the global message pool. More efficient than
     * creating and allocating new instances. The retrieved message has its handler set to this instance (Message.target == this).
     *  If you don't want that facility, just call Message.obtain() instead.
     */
    public final Message obtainMessage()
    {
        return Message.obtain(this);
    }

    /**
     * Same as {@link #obtainMessage()}, except that it also sets the what member of the returned Message.
     * 
     * @param what Value to assign to the returned Message.what field.
     * @return A Message from the global message pool.
     */
    public final Message obtainMessage(int what)
    {
        return Message.obtain(this, what);
    }
    
    /**
     * 
     * Same as {@link #obtainMessage()}, except that it also sets the what and obj members 
     * of the returned Message.
     * 
     * @param what Value to assign to the returned Message.what field.
     * @param obj Value to assign to the returned Message.obj field.
     * @return A Message from the global message pool.
     */
    public final Message obtainMessage(int what, Object obj)
    {
        return Message.obtain(this, what, obj);
    }

    /**
     * 
     * Same as {@link #obtainMessage()}, except that it also sets the what, arg1 and arg2 members of the returned
     * Message.
     * @param what Value to assign to the returned Message.what field.
     * @param arg1 Value to assign to the returned Message.arg1 field.
     * @param arg2 Value to assign to the returned Message.arg2 field.
     * @return A Message from the global message pool.
     */
    public final Message obtainMessage(int what, int arg1, int arg2)
    {
        return Message.obtain(this, what, arg1, arg2);
    }
    
    /**
     * 
     * Same as {@link #obtainMessage()}, except that it also sets the what, obj, arg1,and arg2 values on the 
     * returned Message.
     * @param what Value to assign to the returned Message.what field.
     * @param arg1 Value to assign to the returned Message.arg1 field.
     * @param arg2 Value to assign to the returned Message.arg2 field.
     * @param obj Value to assign to the returned Message.obj field.
     * @return A Message from the global message pool.
     */
    public final Message obtainMessage(int what, int arg1, int arg2, Object obj)
    {
        return Message.obtain(this, what, arg1, arg2, obj);
    }
    
    /**
     * Check if there are any pending posts of messages with code 'what' in
     * the message queue.
     */
    public final boolean hasMessages(int what) {
        return mQueue.hasMessages(this, what, null);
    }

    /**
     * Check if there are any pending posts of messages with code 'what' and
     * whose obj is 'object' in the message queue.
     */
    public final boolean hasMessages(int what, Object object) {
        return mQueue.hasMessages(this, what, object);
    }
    
    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run on the thread to which this handler is 
     * attached. 
     *  
     * @param r The Runnable that will be executed.
     * 
     * @return Returns true if the Runnable was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.
     */
    public final void post(Runnable r)
    {
       sendMessageDelayed(getPostMessage(r), 0);
    }
    
    /**
     * Causes the Runnable r to be added to the message queue, to be run
     * at a specific time given by <var>uptimeMillis</var>.
     * <b>The time-base is {@link android.os.SystemClock#uptimeMillis}.</b>
     * The runnable will be run on the thread to which this handler is attached.
     *
     * @param r The Runnable that will be executed.
     * @param uptimeMillis The absolute time at which the callback should run,
     *         using the {@link android.os.SystemClock#uptimeMillis} time-base.
     *  
     * @return Returns true if the Runnable was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.  Note that a
     *         result of true does not mean the Runnable will be processed -- if
     *         the looper is quit before the delivery time of the message
     *         occurs then the message will be dropped.
     */
    public final void postAtTime(Runnable r, long uptimeMillis)
    {
        sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }
    
    /**
     * Causes the Runnable r to be added to the message queue, to be run
     * at a specific time given by <var>uptimeMillis</var>.
     * <b>The time-base is {@link android.os.SystemClock#uptimeMillis}.</b>
     * The runnable will be run on the thread to which this handler is attached.
     *
     * @param r The Runnable that will be executed.
     * @param uptimeMillis The absolute time at which the callback should run,
     *         using the {@link android.os.SystemClock#uptimeMillis} time-base.
     * 
     * @return Returns true if the Runnable was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.  Note that a
     *         result of true does not mean the Runnable will be processed -- if
     *         the looper is quit before the delivery time of the message
     *         occurs then the message will be dropped.
     *         
     * @see android.os.SystemClock#uptimeMillis
     */
    public final void postAtTime(Runnable r, Object token, long uptimeMillis)
    {
        sendMessageAtTime(getPostMessage(r, token), uptimeMillis);
    }
    
    /**
     * Causes the Runnable r to be added to the message queue, to be run
     * after the specified amount of time elapses.
     * The runnable will be run on the thread to which this handler
     * is attached.
     *  
     * @param r The Runnable that will be executed.
     * @param delayMillis The delay (in milliseconds) until the Runnable
     *        will be executed.
     *        
     * @return Returns true if the Runnable was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.  Note that a
     *         result of true does not mean the Runnable will be processed --
     *         if the looper is quit before the delivery time of the message
     *         occurs then the message will be dropped.
     */
    public final void postDelayed(Runnable r, long delayMillis)
    {
        sendMessageDelayed(getPostMessage(r), delayMillis);
    }
    
//    /**
//     * Posts a message to an object that implements Runnable.
//     * Causes the Runnable r to executed on the next iteration through the
//     * message queue. The runnable will be run on the thread to which this
//     * handler is attached.
//     * <b>This method is only for use in very special circumstances -- it
//     * can easily starve the message queue, cause ordering problems, or have
//     * other unexpected side-effects.</b>
//     *  
//     * @param r The Runnable that will be executed.
//     * 
//     * @return Returns true if the message was successfully placed in to the 
//     *         message queue.  Returns false on failure, usually because the
//     *         looper processing the message queue is exiting.
//     */
//    public final void boolean postAtFrontOfQueue(Runnable r)
//    {
//        sendMessageAtFrontOfQueue(getPostMessage(r));
//    }

    /**
     * Remove any pending posts of Runnable r that are in the message queue.
     */
    public final void removeCallbacks(Runnable r)
    {
        mQueue.removeMessages(this, r, null);
    }

    /**
     * Remove any pending posts of Runnable <var>r</var> with Object
     * <var>token</var> that are in the message queue.
     */
    public final void removeCallbacks(Runnable r, Object token)
    {
        mQueue.removeMessages(this, r, token);
    }
    
    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
    }

    private static Message getPostMessage(Runnable r, Object token) {
        Message m = Message.obtain();
        m.obj = token;
        m.callback = r;
        return m;
    }
    
    /**
     * Pushes a message onto the end of the message queue after all pending messages
     * before the current time. It will be received in {@link #handleMessage},
     * in the thread attached to this handler.
     */
    public final void sendMessage(Message msg) {
        sendMessageDelayed(msg, 0);
    }

    /**
     * Sends a Message containing only the what value.
     */
    public final void sendEmptyMessage(int what) {
        sendEmptyMessageDelayed(what, 0);
    }

    /**
     * Sends a Message containing only the what value, to be delivered
     * after the specified amount of time elapses.
     * @see #sendMessageDelayed(Message.os.Message, long) 
     */
    public final void sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = new Message();//Message.obtain();
        msg.what = what;
        sendMessageDelayed(msg, delayMillis);
    }

    /**
     * Sends a Message containing only the what value, to be delivered 
     * at a specific time.
     * @see #sendMessageAtTime(Message.os.Message, long)
     */
    public final void sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = new Message();//Message.obtain();
        msg.what = what;
        sendMessageAtTime(msg, uptimeMillis);
    }

    /**
     * Enqueue a message into the message queue after all pending messages
     * before (current time + delayMillis). You will receive it in
     * {@link #handleMessage}, in the thread attached to this handler.
     */
    public final void sendMessageDelayed(Message msg, long delayMillis)
    {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        sendMessageAtTime(msg, System.currentTimeMillis() + delayMillis);
    }

    /**
     * Enqueue a message into the message queue after all pending messages
     * before the absolute time (in milliseconds) <var>uptimeMillis</var>.
     * <b>The time-base is {@link android.os.SystemClock#uptimeMillis}.</b>
     * You will receive it in {@link #handleMessage}, in the thread attached
     * to this handler.
     * 
     * @param uptimeMillis The absolute time at which the message should be
     *         delivered, using the
     *         {@link android.os.SystemClock#uptimeMillis} time-base.
     */
    public void sendMessageAtTime(Message msg, long uptimeMillis)
    {
        boolean sent = false;
        MessageQueue queue = mQueue;
        if (queue != null) {
            msg.target = this;
            msg.when = uptimeMillis;
            queue.enqueueMessage(msg);
        } else {
            RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
//            Log.w("Looper", e.getMessage(), e);
        }
    }
//
//    /**
//     * Enqueue a message at the front of the message queue, to be processed on
//     * the next iteration of the message loop.  You will receive it in
//     * {@link #handleMessage}, in the thread attached to this handler.
//     * <b>This method is only for use in very special circumstances -- it
//     * can easily starve the message queue, cause ordering problems, or have
//     * other unexpected side-effects.</b>
//     *  
//     * @return Returns true if the message was successfully placed in to the 
//     *         message queue.  Returns false on failure, usually because the
//     *         looper processing the message queue is exiting.
//     */
//    public final boolean sendMessageAtFrontOfQueue(Message msg)
//    {
//        boolean sent = false;
//        MessageQueue2 queue = mQueue;
//        if (queue != null) {
//            msg.target = this;
//            sent = queue.enqueueMessage(msg, 0);
//        }
//        else {
//            RuntimeException e = new RuntimeException(
//                this + " sendMessageAtTime() called with no mQueue");
////            Log.w("Looper", e.getMessage(), e);
//        }
//        return sent;
//    }
//
    /**
     * Remove any pending posts of messages with code 'what' that are in the
     * message queue.
     */
    public final void removeMessages(int what) {
        mQueue.removeMessages(this, what, null);
    }

    /**
     * Remove any pending posts of messages with code 'what' and whose obj is
     * 'object' that are in the message queue.
     */
    public final void removeMessages(int what, Object object) {
        mQueue.removeMessages(this, what, object);
    }
//
//    /**
//     * Remove any pending posts of callbacks and sent messages whose
//     * <var>obj</var> is <var>token</var>.
//     */
//    public final void removeCallbacksAndMessages(Object token) {
//        mQueue.removeCallbacksAndMessages(this, token);
//    }
    
//    class HandlerTask extends HandlerThread {
//
//		@Override
//		protected void onLooperPrepared() {
//			super.onLooperPrepared();
//			mLooper = Looper.myLooper();
//			mQueue = mLooper.mMessageQueue;
//	        mCallback = null;
//		}
//	}
}
