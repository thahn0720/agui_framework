package thahn.java.agui.app;

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.ContextMenu;
import thahn.java.agui.Global;
import thahn.java.agui.annotation.AguiSpecific;
import thahn.java.agui.content.pm.AguiPackageManager;
import thahn.java.agui.content.pm.PackageManager;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.jmx.JmxConnectorHelper;
import thahn.java.agui.os.PowerManager;
import thahn.java.agui.res.ManifestParser.ManagedComponent;
import thahn.java.agui.res.Resources;
import thahn.java.agui.res.ResourcesManager;
import thahn.java.agui.settings.AguiSettings;
import thahn.java.agui.sqlite.SQLiteDatabase;
import thahn.java.agui.telephony.TelephonyManager;
import thahn.java.agui.utils.FileUtils;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.view.StateCallback;
import thahn.java.agui.widget.ShellView;

public abstract class Context implements StateCallback {
	
	/**
     * File creation mode: the default mode, where the created file can only
     * be accessed by the calling application (or all applications sharing the
     * same user ID).
     * @see #MODE_WORLD_READABLE
     * @see #MODE_WORLD_WRITEABLE
     */
    public static final int MODE_PRIVATE = 0x0000;
    /**
     * @deprecated Creating world-readable files is very dangerous, and likely
     * to cause security holes in applications.  It is strongly discouraged;
     * instead, applications should use more formal mechanism for interactions
     * such as {@link ContentProvider}, {@link BroadcastReceiver}, and
     * {@link android.app.Service}.  There are no guarantees that this
     * access mode will remain on a file, such as when it goes through a
     * backup and restore.
     * File creation mode: allow all other applications to have read access
     * to the created file.
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_WRITEABLE
     */
    @Deprecated
    public static final int MODE_WORLD_READABLE = 0x0001;
    /**
     * @deprecated Creating world-writable files is very dangerous, and likely
     * to cause security holes in applications.  It is strongly discouraged;
     * instead, applications should use more formal mechanism for interactions
     * such as {@link ContentProvider}, {@link BroadcastReceiver}, and
     * {@link android.app.Service}.  There are no guarantees that this
     * access mode will remain on a file, such as when it goes through a
     * backup and restore.
     * File creation mode: allow all other applications to have write access
     * to the created file.
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_READABLE
     */
    @Deprecated
    public static final int MODE_WORLD_WRITEABLE = 0x0002;
    /**
     * File creation mode: for use with {@link #openFileOutput}, if the file
     * already exists then write data to the end of the existing file
     * instead of erasing it.
     * @see #openFileOutput
     */
    public static final int MODE_APPEND = 0x8000;

    /**
     * SharedPreference loading flag: when set, the file on disk will
     * be checked for modification even if the shared preferences
     * instance is already loaded in this process.  This behavior is
     * sometimes desired in cases where the application has multiple
     * processes, all writing to the same SharedPreferences file.
     * Generally there are better forms of communication between
     * processes, though.
     *
     * <p>This was the legacy (but undocumented) behavior in and
     * before Gingerbread (Android 2.3) and this flag is implied when
     * targetting such releases.  For applications targetting SDK
     * versions <em>greater than</em> Android 2.3, this flag must be
     * explicitly set if desired.
     *
     * @see #getSharedPreferences
     */
    public static final int MODE_MULTI_PROCESS = 0x0004;

    /**
     * Database open flag: when set, the database is opened with write-ahead
     * logging enabled by default.
     *
     * @see #openOrCreateDatabase(String, int, CursorFactory)
     * @see #openOrCreateDatabase(String, int, CursorFactory, DatabaseErrorHandler)
     * @see SQLiteDatabase#enableWriteAheadLogging
     */
    public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 0x0008;
	
    public static final String 											WINDOW_SERVICE 			= "window";
	public static final String 											NOTIFICATION_SERVICE 	= "notification";
	public static final String 											LAYOUT_INFLATER_SERVICE	= "layoutInflater";
	public static final String 											TELEPHONY_SERVICE 		= "phone";
	public static final String											POWER_SERVICE 			= "power";
//	public static final java.lang.String ACCOUNT_SERVICE = "account";
//	public static final java.lang.String ACTIVITY_SERVICE = "activity";
//	public static final java.lang.String ALARM_SERVICE = "alarm";
//	public static final java.lang.String ACCESSIBILITY_SERVICE = "accessibility";
//	public static final java.lang.String KEYGUARD_SERVICE = "keyguard";
//	public static final java.lang.String LOCATION_SERVICE = "location";
//	public static final java.lang.String SEARCH_SERVICE = "search";
//	public static final java.lang.String SENSOR_SERVICE = "sensor";
//	public static final java.lang.String STORAGE_SERVICE = "storage";
//	public static final java.lang.String WALLPAPER_SERVICE = "wallpaper";
//	public static final java.lang.String VIBRATOR_SERVICE = "vibrator";
//	public static final java.lang.String CONNECTIVITY_SERVICE = "connectivity";
//	public static final java.lang.String WIFI_SERVICE = "wifi";
//	public static final java.lang.String AUDIO_SERVICE = "audio";
//	public static final java.lang.String CLIPBOARD_SERVICE = "clipboard";
//	public static final java.lang.String INPUT_METHOD_SERVICE = "input_method";
//	public static final java.lang.String DROPBOX_SERVICE = "dropbox";
//	public static final java.lang.String DEVICE_POLICY_SERVICE = "device_policy";
//	public static final java.lang.String UI_MODE_SERVICE = "uimode";
//	public static final java.lang.String DOWNLOAD_SERVICE = "download";
//	public static final java.lang.String NFC_SERVICE = "nfc";
    
	/** {@link ApplicationController} */
	private static ThreadLocal<Context>									mApplicationLocalContext = new ThreadLocal<Context>();
	/** {@link ApplicationController} */
	private static Context												mApplicationContext;
	
	private static final HashMap<String, SharedPreferencesImpl> 		sSharedPrefs 	= new HashMap<String, SharedPreferencesImpl>();
	private final Object 												mSync 			= new Object();
	private File 														mPreferencesDir;
	
	public static class ManagedDialog {
		Dialog dialog;
		Bundle bundle;
	}
	
	/*package*/ void setApplicationContext(Context context) {
		mApplicationContext = context;
	}
	
	public Context getApplicationContext() {
		if (mApplicationLocalContext.get() == null) {
			mApplicationLocalContext.set(mApplicationContext);
		}
		return mApplicationLocalContext.get();
	}
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract void arrangeView();
	
	public abstract void invalidate();
	
	public abstract Font getFont();
	
	public abstract FontMetrics getFontMetrics(Font font);
	
	public abstract void setContextMenu(ContextMenu contextMenu);
	
	public abstract void removeContextMenuVisible();
	
	public abstract void removeShellView(ShellView view);
	
	public abstract Drawable getBackgroundDrawable();
	
	/*package*/ abstract void resize();
	
	public abstract void addShellView(ShellView view);
	
	/**
     * Retrieve and hold the contents of the preferences file 'name', returning
     * a SharedPreferences through which you can retrieve and modify its
     * values.  Only one instance of the SharedPreferences object is returned
     * to any callers for the same name, meaning they will see each other's
     * edits as soon as they are made.
     *
     * @param name Desired preferences file. If a preferences file by this name
     * does not exist, it will be created when you retrieve an
     * editor (SharedPreferences.edit()) and then commit changes (Editor.commit()).
     * @param mode Operating mode.  Use 0 or {@link #MODE_PRIVATE} for the
     * default operation, {@link #MODE_WORLD_READABLE}
     * and {@link #MODE_WORLD_WRITEABLE} to control permissions.  The bit
     * {@link #MODE_MULTI_PROCESS} can also be used if multiple processes
     * are mutating the same SharedPreferences file.  {@link #MODE_MULTI_PROCESS}
     * is always on in apps targetting Gingerbread (Android 2.3) and below, and
     * off by default in later versions.
     *
     * @return Returns the single SharedPreferences instance that can be used
     *         to retrieve and modify the preference values.
     *
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_READABLE
     * @see #MODE_WORLD_WRITEABLE
     * @see #MODE_MULTI_PROCESS
     */
	public SharedPreferences getSharedPreferences(String name, int mode) {
		SharedPreferencesImpl sp;
		synchronized (sSharedPrefs) {
			sp = sSharedPrefs.get(name);
			if (sp == null) {
				File prefsFile = getSharedPrefsFile(name);
				sp = new SharedPreferencesImpl(prefsFile, mode);
				sSharedPrefs.put(name, sp);
				return sp;
			}
		}
		if ((mode & Context.MODE_MULTI_PROCESS) != 0) {
			// If somebody else (some other process) changed the prefs
			// file behind our back, we reload it. This has been the
			// historical (if undocumented) behavior.
			sp.startReloadIfChangedUnexpectedly();
		}
		return sp;
	}
	 
	public File getSharedPrefsFile(String name) {
		return makeFilename(getPreferencesDir(), name + ".xml");
	}
	 
	private File getPreferencesDir() {
		synchronized (mSync) {
			if (mPreferencesDir == null) {
				mPreferencesDir = new File(Global.aguiProjectPath, "shared_prefs");
			}
			return mPreferencesDir;
		}
	}
	
	private File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException(
                "File " + name + " contains a path separator");
    }
	
	public Resources getResources() {
		return ResourcesManager.getInstance();
	}
	
	/** Return PackageManager instance to find global package information. */
    public PackageManager getPackageManager() {
    	return AguiPackageManager.getInstance();
    }
    
    public Object getSystemService(String name) {
		if (NOTIFICATION_SERVICE.equals(name)) {
			return NotificationManager.getInstance(this);
		} else if (TELEPHONY_SERVICE.equals(name)) {
			return TelephonyManager.getInstance();
		} else if (POWER_SERVICE.equals(name)) {
			return PowerManager.getInstance();
		}
		return null;
	}
    
    /*package*/ static void setFilePermissionsFromMode(String name, int mode,
            int extraPermissions) {
        int perms = FileUtils.S_IRUSR|FileUtils.S_IWUSR
            |FileUtils.S_IRGRP|FileUtils.S_IWGRP
            |extraPermissions;
        if ((mode&MODE_WORLD_READABLE) != 0) {
            perms |= FileUtils.S_IROTH;
        }
        if ((mode&MODE_WORLD_WRITEABLE) != 0) {
            perms |= FileUtils.S_IWOTH;
        }
//        if (DEBUG) {
//            Log.i(TAG, "File " + name + ": mode=0x" + Integer.toHexString(mode)
//                  + ", perms=0x" + Integer.toHexString(perms));
//        }
//        FileUtils.setPermissions(name, perms, -1, -1);
    }
    
	//*******************************************************************
	// start other context method
	//*******************************************************************
	/**
	 * create activity in new window by using {@link #startActivityInNewWIndow(String, Intent)}. <br>
	 * However, if context is in activity, use window's start activity. <br>
	 * So, create activity in the window. 
	 * @param intent
	 * 
	 */
	public void startActivity(Intent intent) {
		// TODO : handle already start
		String prefix = "AGUI";
		int index = WindowManager.getInstance().getWindowList().size();
		for(;;++index) {
			if(!WindowManager.getInstance().contains(prefix+index)) {
				break;
			}
		}
		startActivityInNewWIndow(prefix+index, intent);
    } 
	
	/**
	 * if a window already exists, send to the window. Else, start in new window <br> 
	 * @param windowTitle
	 * @param intent
	 * 
	 */
	public void startActivity(String windowTitle, Intent intent) {
		if(WindowManager.getInstance().contains(windowTitle)) {
			WindowInfo info = WindowManager.getInstance().getWindowInfo(windowTitle);
			try {
				info.window.startActivityForResult(info.window.getActivityStack().peek(), intent, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			startActivityInNewWIndow(windowTitle, intent);
		}
	}
	
	@AguiSpecific
	// TODO : now, redundancy is possible. modify whether to decide redundancy by option
	public void startActivityInNewWIndow(String windowTitle, Intent intent) {
		if(WindowManager.getInstance().contains(windowTitle)) {
			Log.i("window '"+windowTitle+"' already exists.");
		}
		mApplicationContext.startActivityInNewWIndow(windowTitle, intent);
	}
	
	public ComponentName startService(Intent intent) {
		// TODO : handle already start
        return mApplicationContext.startService(intent);
    }
	
	public void sendBroadcast(Intent intent) {
		send(ManagedComponent.RECEIVER, intent);
    }
	
	public void sendIntent(Intent intent) {
		// FIXME : 내부에서 가능한지 검색해서 없으면 외부로 전송
		String action = intent.getAction();
		ComponentName component = intent.getComponent();
		if(component != null) { // explicit
			Class<?> cls = null;
			try {
				cls = MyUtils.getProjectClass(component.getClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			for(;;) {
				if(cls == null) {
					Log.e("action does not exist in local");
					JmxConnectorHelper.getInstance().sendIntentInAgui(intent);
					return ;
				} else if(cls == Activity.class) {
					send(ManagedComponent.ACTIVITY, intent);
					return ;
				} else if(cls == Service.class) {
					send(ManagedComponent.SERVICE, intent);
					return ;
				} else if(cls == BroadcastReceiver.class) {
					send(ManagedComponent.RECEIVER, intent);
					return ;
				} 
				cls = cls.getSuperclass();
			}
		} else {				// implicit
			List<ManagedComponent> managedComponent = IntentManager.getInstance().get(action, Global.projectPackageName);
			if(managedComponent != null && managedComponent.size() > 0) {
				for (ManagedComponent item : managedComponent) {
					intent.setComponent(item.component);
					send(item.which, intent);
				}
			} else {
				Log.e("action does not exist in local, so send to agui os");
				JmxConnectorHelper.getInstance().sendIntentInAgui(intent);
			}
		}
    }
	
	public void sendIntent(String windowName, Intent intent) {
		intent.setName(windowName);
		sendIntent(intent);
	}
	
	// FIXME : extract this method
	private void send(int which, Intent intent) {
		if (intent.getComponent() == null && intent.getAction() == null) {
			Preconditions.checkNotNull(intent.getAction(), "One of component and action must be not null.");
		}
		
		switch (which) {
		case ManagedComponent.ACTIVITY:
			String windowName = intent.getName();
			if(windowName == null) {
				startActivity(intent);
			} else {
				startActivity(windowName, intent);
			}
			break;
		case ManagedComponent.SERVICE:
			startService(intent);
			break;
		case ManagedComponent.RECEIVER:
			String action = intent.getAction();
			ComponentName component = intent.getComponent();
			if (action != null) {
				List<ManagedComponent> managedComponent = IntentManager.getInstance().get(action, Global.projectPackageName);
				if(managedComponent != null && managedComponent.size() > 0) {
					for (ManagedComponent item : managedComponent) {
						if (item.tag != null && !((BroadcastReceiver) item.tag).isFinished()) {
							((BroadcastReceiver) item.tag).onReceive(this, intent);
						}
					}
				} else {
					Log.e("action does not exist in local, so send to agui os");
					JmxConnectorHelper.getInstance().sendIntentInAgui(intent);
				}
			} else {
				for (BroadcastReceiver receiver : ReceiverManager.getInstance().get(component.getClassName())) {
					receiver.onReceive(this, intent);
				}
			}
			break;
		}
	}
	
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		String name = receiver.getClass().getCanonicalName();
		ManagedComponent managedCom = new ManagedComponent();
		managedCom.which = ManagedComponent.RECEIVER;
		managedCom.component = new ComponentName(Global.aguiProjectPath, name);
		managedCom.tag = receiver;
		IntentManager.getInstance().put(filter, managedCom);
		JmxConnectorHelper.getInstance().registerIntentInAgui(filter, managedCom);
		ReceiverManager.getInstance().put(name, receiver);
		return new Intent(this, receiver.getClass());
	}
	
	public void unregisterReceiver(BroadcastReceiver receiver) {
		String name = receiver.getClass().getCanonicalName();
		BroadcastReceiver ret = ReceiverManager.getInstance().remove(name, receiver);
	}
	
	/**
     * Return a class loader you can use to retrieve classes in this package.
     */
    public ClassLoader getClassLoader() {
    	return thahn.java.agui.Main.class.getClassLoader();
    }
    
    public String getPackageName() {
        return Global.projectPackageName;
    }
	
	//*******************************************************************
	// Not Yet
	//*******************************************************************
	public Dialog onCreateDialog(int i) {
		// TODO : implements
		return null;
	}
	
	public void showDialog(int i) {
		// TODO : implements
	}
	
	public void dismissDialog(int i) {
		// TODO : implements
	}
	
	void addManagedDialog(Dialog dialog, Bundle bundle) {
		// TODO : implements
	}
}
