package thahn.java.agui.app;

import javax.swing.ImageIcon;

import org.jachievement.Achievement;
import org.jachievement.AchievementConfig;
import org.jachievement.AchievementPosition;
import org.jachievement.AchievementQueue;

import thahn.java.agui.app.controller.Handler;
import thahn.java.agui.res.ResourcesManager;


public class NotificationManager {
    private static String 												TAG = "NotificationManager";
//    private static boolean 												localLOGV = false;
    private Context 													context;
    private AchievementQueue											achievementQueue;
    
    private static NotificationManager									instance;
//    private static INotificationManager 						sService;
//
//    /** @hide */
//    static public INotificationManager getService()
//    {
//        if (sService != null) {
//            return sService;
//        }
//        IBinder b = ServiceManager.getService("notification");
//        sService = INotificationManager.Stub.asInterface(b);
//        return sService;
//    }

    public static NotificationManager getInstance(Context context) {
    	if(instance == null) {
    		instance = new NotificationManager(context);
    	}
    	return instance; 
    }
    
    NotificationManager(Context context) {
    	this.context = context;
    	init();
    }
    
    NotificationManager(Context context, Handler handler) {
    	this.context = context;
        init();
    }

    private void init() {
    	achievementQueue = new AchievementQueue();
	}
    
    /**
     * Post a notification to be shown in the status bar. If a notification with
     * the same id has already been posted by your application and has not yet been canceled, it
     * will be replaced by the updated information.
     *
     * @param id An identifier for this notification unique within your
     *        application.
     * @param notification A {@link Notification} object describing what to show the user. Must not
     *        be null.
     */
    public void notify(int id, Notification notification) {
        notify(null, id, notification);
    }

    /**
     * Post a notification to be shown in the status bar. If a notification with
     * the same tag and id has already been posted by your application and has not yet been
     * canceled, it will be replaced by the updated information.
     *
     * @param tag A string identifier for this notification.  May be {@code null}.
     * @param id An identifier for this notification.  The pair (tag, id) must be unique
     *        within your application.
     * @param notification A {@link Notification} object describing what to
     *        show the user. Must not be null.
     */
    public void notify(String tag, int id, Notification notification) {
//        int[] idOut = new int[1];
//        INotificationManager service = getService();
//        String pkg = mContext.getPackageName();
//        if (localLOGV) Log.v(TAG, pkg + ": notify(" + id + ", " + notification + ")");
//        try {
//            service.enqueueNotificationWithTag(pkg, tag, id, notification, idOut);
//            if (id != idOut[0]) {
//                Log.w(TAG, "notify: id corrupted: sent " + id + ", got back " + idOut[0]);
//            }
//        } catch (RemoteException e) {
//        }
    	new NotificationTask(notification).start();
    }

    /**
     * Cancel a previously shown notification.  If it's transient, the view
     * will be hidden.  If it's persistent, it will be removed from the status
     * bar.
     */
//    public void cancel(int id)
//    {
//        cancel(null, id);
//    }

    /**
     * Cancel a previously shown notification.  If it's transient, the view
     * will be hidden.  If it's persistent, it will be removed from the status
     * bar.
     */
//    public void cancel(String tag, int id) {
//        INotificationManager service = getService();
//        String pkg = mContext.getPackageName();
//        if (localLOGV) Log.v(TAG, pkg + ": cancel(" + id + ")");
//        try {
//            service.cancelNotificationWithTag(pkg, tag, id);
//        } catch (RemoteException e) {
//        }
//    }

    /**
     * Cancel all previously shown notifications. See {@link #cancel} for the
     * detailed behavior.
     */
//    public void cancelAll() {
//        INotificationManager service = getService();
//        String pkg = mContext.getPackageName();
//        if (localLOGV) Log.v(TAG, pkg + ": cancelAll()");
//        try {
//            service.cancelAllNotifications(pkg);
//        } catch (RemoteException e) {
//        }
//    }

    class NotificationTask extends Thread {

    	Notification 													notification;
    	
		public NotificationTask(Notification notification) {
			this.notification = notification;
		}

		@Override
		public void run() {
			Achievement	achivement = makeNotification(notification);
	    	achievementQueue.add(achivement);
	    	try {
				achievementQueue.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private Achievement makeNotification(Notification notification) {
			Achievement achivement = null;
			AchievementConfig config = new AchievementConfig(context);
			config.setAchievementPosition(AchievementPosition.BOTTOM_RIGHT);
			
			if(notification.contentView == null) {
				if(notification.backgroundColor != -1) {
					config.setBackgroundColor(thahn.java.agui.graphics.Color.toAwtColor(notification.backgroundColor));
				}
				if(notification.backgroundImgRes != -1) {
					config.setBackgroundImage(new ImageIcon(ResourcesManager.getInstance().getDrawable(notification.backgroundImgRes).getImage()));
				}
				if(notification.icon != -1) {
					config.setIcon(new ImageIcon(ResourcesManager.getInstance().getDrawable(notification.icon).getImage()));
				}
				achivement = new Achievement(notification.contentTitle.toString(), notification.contentText.toString(), config);
			} else {
				achivement = new Achievement(context, notification.contentView, config);
			}
			
			return achivement;
		}
    }
}
