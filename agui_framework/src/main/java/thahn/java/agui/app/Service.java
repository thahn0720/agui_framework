package thahn.java.agui.app;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SecondaryLoop;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.util.Stack;

import thahn.java.agui.ContextMenu;
import thahn.java.agui.annotation.AguiSpecific;
import thahn.java.agui.app.DialogInterface.OnDismissListener;
import thahn.java.agui.exception.NotSupportedException;
import thahn.java.agui.utils.Log;


public class Service extends ContextWrapper {

    /**
     * Bits returned by {@link #onStartCommand} describing how to continue
     * the service if it is killed.  May be {@link #START_STICKY},
     * {@link #START_NOT_STICKY}, {@link #START_REDELIVER_INTENT},
     * or {@link #START_STICKY_COMPATIBILITY}.
     */
    public static final int START_CONTINUATION_MASK = 0xf;
    
    /**
     * Constant to return from {@link #onStartCommand}: compatibility
     * version of {@link #START_STICKY} that does not guarantee that
     * {@link #onStartCommand} will be called again after being killed.
     */
    public static final int START_STICKY_COMPATIBILITY = 0;
    public static final int START_STICKY = 1;
    public static final int START_NOT_STICKY = 2;
    public static final int START_REDELIVER_INTENT = 3;
    public static final int START_FLAG_REDELIVERY = 0x0001;
    
    /**
     * This flag is set in {@link #onStartCommand} if the Intent is a
     * a retry because the original attempt never got to or returned from
     * {@link #onStartCommand(Intent, int, int)}.
     */
    public static final int START_FLAG_RETRY = 0x0002;
    
	
	private SystemTray 															mTray;
	private TrayIcon 															mTrayIcon;
	ServiceInfo																	mServiceInfo;
	private Stack<ManagedDialog>												mManagedDialogs;
	
	public Service() {
		mManagedDialogs = new Stack<>();
	}

	public void onCreate() {
		
	} 
	
	/**
	 * 
	 * @param intent
	 * @param flags
	 * @param startId
	 * @see http://arabiannight.tistory.com/247
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
//		START_STICKY : Service가 강제 종료되었을 경우 시스템이 다시 Service를 재시작 시켜 주지만 intent 값을 null로 초기화 시켜서 재시작 합니다. 
//		 Service 실행시 startService(Intent service) 메서드를 호출 하는데 onStartCommand(Intent intent, int flags, int startId) 메서드에 intent로 value를 넘겨 줄 수 있습니다. 기존에 intent에 value값이 설정이 되있다고 하더라도 Service 재시작시 intent 값이 null로 초기화 되서 재시작 됩니다. 
//		START_NOT_STICKY : 이 Flag를 리턴해 주시면, 강제로 종료 된 Service가 재시작 하지 않습니다. 시스템에 의해 강제 종료되어도 괸찮은 작업을 진행 할 때 사용해 주시면 됩니다. 
//		START_REDELIVER_INTENT : START_STICKY와 마찬가지로 Service가 종료 되었을 경우 시스템이 다시 Service를 재시작 시켜 주지만 intent 값을 그대로 유지 시켜 줍니다. startService() 메서드 호출시 Intent value값을 사용한 경우라면 해당 Flag를 사용해서 리턴값을 설정해 주면 됩니다.
		
		return START_STICKY;
	} 
	
	public void onDestroy() {
		
	}
	
	public void setTray(Context context, String tooltip, int imgRes, ContextMenu menu, ActionListener listener) {
		String imgPath = context.getResources().getDrawablePath(imgRes);
		if (imgPath == null) {
			Log.e("tray icon img is null. So set default img icon");
			imgPath = context.getResources().getDrawablePath(thahn.java.agui.R.drawable.ic_launcher);
		}
		mTray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(imgPath);

		mTrayIcon = new TrayIcon(image, tooltip, menu); // 
		mTrayIcon.setImageAutoSize(true);
		if (listener != null) {
			mTrayIcon.addActionListener(listener);
		}
//		mTrayIcon.addMouseListener(new MouseAdapter() {
//
//			public void mouseReleased(MouseEvent e) {
//	            if(e.isPopupTrigger()) {
//	            	contextMenu.setLocation(e.getX() - contextMenu.getWidth() - 5, e.getY() - contextMenu.getHeight() - 5);
//	            	contextMenu.setInvoker(contextMenu);
//	            	contextMenu.setVisible(true);
////	            	contextMenu.show(e.getComponent(), e.getX() - contextMenu.getWidth() - 5, e.getY() - contextMenu.getHeight() - 5);
//	            } else {
//	            	contextMenu.setVisible(false);
//	            }
//	        }
//	    });
		try {
			mTray.add(mTrayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}
	
	public void stopSelf() {
		mTray.remove(mTrayIcon);
		ServiceInfo serviceInfo = ServiceManager.getInstance().getServiceInfo(mServiceInfo.name);
		serviceInfo.serviceLooper.quit();
		serviceInfo.serviceLooper.interrupt();
		Thread.currentThread().interrupt();
	}
	
	@AguiSpecific
	public void showDialog(int i, boolean blocking) {
		if(blocking) {
			final SecondaryLoop loop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
			//
			Dialog dialog = onCreateDialog(i);
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					loop.exit();
				}
			});
			addManagedDialog(dialog, null);
			//
            loop.enter();
		} else {
			showDialog(i);
		}
	}
	
	@Override
	public void showDialog(int i) {
		Dialog dialog = onCreateDialog(i);
		addManagedDialog(dialog, null);
	}

	@Override
	public void dismissDialog(int i) {
		int size = mManagedDialogs.size();
    	for(int n=0;n<size;++n) { //ManagedDialog managed : mManagedDialogs) {
    		ManagedDialog managed = mManagedDialogs.get(n);
    		if(managed != null && managed.dialog.getId() == i) {
    			mManagedDialogs.remove(managed);
    			managed.dialog.dismiss();
    		}
    	}
	}

	@Override
	/*package*/ void addManagedDialog(Dialog dialog, Bundle bundle) {
		// TODO : create & start thread
		dialog.dispatchOnCreate(bundle);
		WindowDialog wDialog = new WindowDialog(dialog);
		ManagedDialog managed = new ManagedDialog();
		managed.dialog = wDialog;
		managed.bundle = bundle;
		mManagedDialogs.add(managed);
		wDialog.show();
	}

	@Override
	public int getWidth() {
		throw new NotSupportedException("only in activity, do call this method");
	}

	@Override
	public int getHeight() {
		throw new NotSupportedException("only in activity, do call this method");
	}
}
