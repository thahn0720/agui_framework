package thahn.java.agui.app.controller;

import java.util.concurrent.DelayQueue;

public class MessageQueue {
	public static final int 												TASK_POOL_SIZE = 12;
//	private BlockingQueue<Message> 											mQueue;
	private DelayQueue<Message>   											mQueue;
	
	public MessageQueue() {
//		mQueue = new ArrayBlockingQueue<Message>(TASK_POOL_SIZE, true);
		mQueue = new DelayQueue<>();
	}

	public void enqueueMessage(Message msg) {
		try {
			mQueue.put(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * blocking
	 * @return
	 */
	public Message poll() {
		Message msg = null;
		try {
			msg = mQueue.take();
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
		return msg;
	}
	
    boolean hasMessages(Handler h, int what, Object object) {
    	if (h == null) {
            return false;
        }
		synchronized (this) {
			if(mQueue.size() > 0) {
				java.util.Iterator<Message> iter = mQueue.iterator();
				while(iter.hasNext()) {
					Message msg = iter.next();
					if(msg.target == h && msg.what == what
							&& (object == null || msg.obj == object)) {
						return true;
					}
				}
			}
		}
		return false;
    }

    boolean hasMessages(Handler h, Runnable r, Object object) {
    	if (h == null) {
            return false;
        }
		synchronized (this) {
			if(mQueue.size() > 0) {
				java.util.Iterator<Message> iter = mQueue.iterator();
				while(iter.hasNext()) {
					Message msg = iter.next();
					if(msg.target == h && msg.callback == r
							&& (object == null || msg.obj == object)) {
						return true;
					}
				}
			}
		}
		return false;
    }
	
	public void removeMessages(Handler h, int what, Object object) {
		if (h == null) {
            return;
        }
		synchronized (this) {
			if(mQueue.size() > 0) {
				java.util.Iterator<Message> iter = mQueue.iterator();
				while(iter.hasNext()) {
					Message msg = iter.next();
					if(msg.target == h && msg.what == what
							&& (object == null || msg.obj == object)) {
						mQueue.remove(msg);
					}
				}
			}
		}
	}

    void removeMessages(Handler h, Runnable r, Object object) {
        if (h == null || r == null) {
            return;
        }

        synchronized (this) {
        	if(mQueue.size() > 0) {
				java.util.Iterator<Message> iter = mQueue.iterator();
				while(iter.hasNext()) {
					Message msg = iter.next();
					if(msg.target == h && msg.callback == r  
							&& (object == null || msg.obj == object)) {
						mQueue.remove(msg);
					}
				}
			}
        }
    }
	
	public void clear() {
		mQueue.clear();
	}
}
