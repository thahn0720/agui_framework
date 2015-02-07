package thahn.java.agui.app;

import thahn.java.agui.widget.RemoteViews;

public class Notification {
	
	public int 													icon = -1;
	public int 													backgroundColor 	= -1;
	public int 													backgroundImgRes	= -1;
	public long													when;
	public CharSequence 										tickerText;
	public CharSequence 										contentTitle;
	public CharSequence 										contentText;
	public int													position;
	
	/**
	 * The view that will represent this notification in the expanded status bar.
	 */
	public RemoteViews 											contentView;
	
	public Notification() {
		init();
	}

//	public Notification(int icon, CharSequence tickerText, long when) {
//		init();
//        this.icon = icon;
//        this.tickerText = tickerText;
//        this.when = when;
//    }

	private void init() {
//		 achivementQueue = new AchievementQueue();
	}
	
	public void setLatestEventInfo(Context context, CharSequence contentTitle, CharSequence contentText, PendingIntent contentIntent) {
//        RemoteViews contentView = new RemoteViews(context.getPackageName(),
//                R.layout.status_bar_latest_event_content);
//        if (this.icon != 0) {
//            contentView.setImageViewResource(R.id.icon, this.icon);
//        }
//        if (contentTitle != null) {
//            contentView.setTextViewText(R.id.title, contentTitle);
//        }
//        if (contentText != null) {
//            contentView.setTextViewText(R.id.text, contentText);
//        }
//        if (this.when != 0) {
//            contentView.setLong(R.id.time, "setTime", when);
//        }
//        this.contentView = contentView;
//        this.contentIntent = contentIntent;
		this.contentTitle = contentTitle;
		this.contentText = contentText;
    }
	
	class Builder {
		
	}
}


