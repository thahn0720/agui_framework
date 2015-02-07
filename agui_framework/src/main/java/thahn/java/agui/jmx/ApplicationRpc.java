package thahn.java.agui.jmx;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import thahn.java.agui.app.Intent;
import thahn.java.agui.utils.Log;


public class ApplicationRpc extends NotificationBroadcasterSupport implements ApplicationRpcMBean {

	public static final String								TYPE_SEND_INTENT			= "sendIntent";
	
	private long											mSequenceNumber;
	
	public ApplicationRpc() {
	}
	
	@Override
	public void sendIntent(Intent intent) {
		Log.i("ApplicationRpc : sendIntent");
		// FIXME : 매번 이렇게 만들 필요가 있을까?
		Notification noti = new Notification(TYPE_SEND_INTENT, intent, mSequenceNumber, System.currentTimeMillis());
		sendNotification(noti);
		incrementSequenceNumber();
	}

	private void incrementSequenceNumber() {
		++mSequenceNumber;
	}
	
	@Override
    public MBeanNotificationInfo[] getNotificationInfo() {
    	Log.i("ApplicationRpc : getNotificationInfo");
		String[] types = new String[] {TYPE_SEND_INTENT};
		String name = TYPE_SEND_INTENT;
		String description = TYPE_SEND_INTENT;
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] {info};
    }
}
