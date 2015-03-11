package thahn.java.agui.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JFrame;

import thahn.java.agui.animation.Animation;
import thahn.java.agui.animation.Animation.AnimationListener;
import thahn.java.agui.animation.AnimationUtils;
import thahn.java.agui.app.controller.HandlerThread;
import thahn.java.agui.app.controller.Looper;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.MenuItem;
import thahn.java.agui.view.MenuItemImpl;


public class Window extends JFrame {

    /** Flag for the "options panel" feature.  This is enabled by default. */
    public static final int FEATURE_OPTIONS_PANEL = 0;
    /** Flag for the "no title" feature, turning off the title at the top
     *  of the screen. */
    public static final int FEATURE_NO_TITLE = 1;
//    /** Flag for the progress indicator feature */
//    public static final int FEATURE_PROGRESS = 2;
//    /** Flag for having an icon on the left side of the title bar */
//    public static final int FEATURE_LEFT_ICON = 3;
//    /** Flag for having an icon on the right side of the title bar */
//    public static final int FEATURE_RIGHT_ICON = 4;
//    /** Flag for indeterminate progress */
//    public static final int FEATURE_INDETERMINATE_PROGRESS = 5;
    /** Flag for the context menu.  This is enabled by default. */
    public static final int FEATURE_CONTEXT_MENU = 6;
    /** Flag for custom title. You cannot combine this feature with other title features. */
    public static final int FEATURE_CUSTOM_TITLE = 7;
//    /**
//     * Flag for enabling the Action Bar.
//     * This is enabled by default for some devices. The Action Bar
//     * replaces the title bar and provides an alternate location
//     * for an on-screen menu button on some devices.
//     */
//    public static final int FEATURE_ACTION_BAR = 8;
//    /**
//     * Flag for requesting an Action Bar that overlays window content.
//     * Normally an Action Bar will sit in the space above window content, but if this
//     * feature is requested along with {@link #FEATURE_ACTION_BAR} it will be layered over
//     * the window content itself. This is useful if you would like your app to have more control
//     * over how the Action Bar is displayed, such as letting application content scroll beneath
//     * an Action Bar with a transparent background or otherwise displaying a transparent/translucent
//     * Action Bar over application content.
//     */
//    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
//    /**
//     * Flag for specifying the behavior of action modes when an Action Bar is not present.
//     * If overlay is enabled, the action mode UI will be allowed to cover existing window content.
//     */
//    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
//    /** Flag for setting the progress bar's visibility to VISIBLE */
//    public static final int PROGRESS_VISIBILITY_ON = -1;
//    /** Flag for setting the progress bar's visibility to GONE */
//    public static final int PROGRESS_VISIBILITY_OFF = -2;
//    /** Flag for setting the progress bar's indeterminate mode on */
//    public static final int PROGRESS_INDETERMINATE_ON = -3;
//    /** Flag for setting the progress bar's indeterminate mode off */
//    public static final int PROGRESS_INDETERMINATE_OFF = -4;
//    /** Starting value for the (primary) progress */
//    public static final int PROGRESS_START = 0;
//    /** Ending value for the (primary) progress */
//    public static final int PROGRESS_END = 10000;
//    /** Lowest possible value for the secondary progress */
//    public static final int PROGRESS_SECONDARY_START = 20000;
//    /** Highest possible value for the secondary progress */
//    public static final int PROGRESS_SECONDARY_END = 30000;
    
    /** The default features enabled */
    protected static final int 											DEFAULT_FEATURES = (1 << FEATURE_OPTIONS_PANEL) | (1 << FEATURE_CONTEXT_MENU);
	
	private ToastManager 												mToastManager;
	private Stack<Activity> 											mActivityList;
	private Callback													mCallback;
	private boolean														isFirst 					= true;
	
	private int 														mFeatures 					= DEFAULT_FEATURES;
    private int 														mLocalFeatures 				= DEFAULT_FEATURES;
	
    private BorderManagerImpl											mBorderManager;
    private TitleActionBar												mTitleActionBar;
    private	ActivityInputController										mInputController;
    
    private boolean 													mDestroyed;
    
    public interface Callback {
		void onWindowCreate();
		void onActivityAddedInWindow(Activity activity);
		void onWindowDestroy(Window window);
	}
    
	public Window() throws HeadlessException {
		super();
		init();
	}
	
	public Window(GraphicsConfiguration gc) {
		super(gc);
		init();
	}

	public Window(String title, GraphicsConfiguration gc) {
		super(title, gc);
		init();
	}

	public Window(String title) throws HeadlessException {
		super(title);
		init();
	}

	private void init() {
		setUndecorated(true);
		setBackground(new Color(0,0,0,0));
		
		mActivityList = new Stack<>();
		mToastManager = ToastManager.getInstance();
		setGlassPane(mToastManager);
		super.addWindowListener(windowListener);
		super.addComponentListener(componentListener);
	}

	/**
	 * called when added activity at first 
	 * @param frame
	 */
	/*package*/ void initBorderSetting(final JFrame frame) {
		mBorderManager = new BorderManagerImpl(this, frame);
		mTitleActionBar = mBorderManager.getTitleActionBar();
		// test
//		mBorderManager.setBorderBackColor(255, 255, 0, 0);
//		mBorderManager.setBorderLineColor(255, 255, 0, 0);
	}
	
	/*package*/ void clickTitle() {
		Activity currentActivity = getCurrentActivity();
		MenuItem menuItem = MenuItemImpl.makeMenuItem(currentActivity, thahn.java.agui.R.id.title);
		currentActivity.onOptionsItemSelected(menuItem);
	}
	
	/*package*/ synchronized void addActivity(Activity activity) {
		Activity currentActivity = null;
		if (!mActivityList.isEmpty()) {
			currentActivity = mActivityList.peek();
		}
		// push in stack
		mActivityList.push(activity);
		// callback
		mCallback.onActivityAddedInWindow(activity);
		// getContentPane().add(activity.mPanel, 0);
		mBorderManager.addContents(activity.mPanel, 0);
		// set input controller
		setInputController(activity, activity.mPanel);
		// onCreate
		activity.setVisible(true);
		// onStart
		activity.performStart();
		// onPostCreate
		activity.performPostCreate();
		// onResume
		activity.performResume();
		// action bar
		setTitleActionBar(activity);
		// animation
		if (currentActivity != null) {
			// set previous view's visibility to gone 
			currentActivity.performPause();
			currentActivity.mPanel.setVisible(false);
			activity.mActivityInfo.enterAni = currentActivity.mNextEnterAni;
			activity.mActivityInfo.exitAni = currentActivity.mNextExitAni;
			currentActivity.mNextEnterAni = -1;
			currentActivity.mNextExitAni = -1;
			
			Animation enterAni = AnimationUtils.loadAnimation((Context)activity, activity.mActivityInfo.enterAni);
			if (enterAni != null) {
				//activity.mPanel.setOpaque(false);
				if (activity.mPanel.mDecorView != null) activity.mPanel.mDecorView.startAnimation(enterAni);
			}
		}
		
		if (!isVisible()) {
			setVisible(true);
		}
	}
	
	/*package*/ synchronized void bringActivityToFront(Activity activity) {
		Activity currentActivity = null;
		if (!mActivityList.isEmpty()) {
			currentActivity = mActivityList.peek();
		}
		// push in stack
		mActivityList.push(activity);
		// callback
		mCallback.onActivityAddedInWindow(activity);
		// getContentPane().add(activity.mPanel, 0);
		mBorderManager.addContents(activity.mPanel, 0);
		// set input controller
		setInputController(activity, activity.mPanel);
		// visible
		activity.mPanel.setVisible(true);
		// onResume
		activity.performResume();
		// action bar
		setTitleActionBar(activity);
		// animation
		if (currentActivity != null) {
			// set previous view's visibility to gone 
			currentActivity.performPause();
			currentActivity.mPanel.setVisible(false);
			activity.mActivityInfo.enterAni = currentActivity.mNextEnterAni;
			activity.mActivityInfo.exitAni = currentActivity.mNextExitAni;
			currentActivity.mNextEnterAni = -1;
			currentActivity.mNextExitAni = -1;
			
			Animation enterAni = AnimationUtils.loadAnimation((Context)activity, activity.mActivityInfo.enterAni);
			if (enterAni != null) {
				//activity.mPanel.setOpaque(false);
				if (activity.mPanel.mDecorView != null) activity.mPanel.mDecorView.startAnimation(enterAni);
			}
		}
		
		if (!isVisible()) {
			setVisible(true);
		}
	}
	
	private void setInputController(Activity activity, MyPanel panel) {
		if (mInputController == null) {
			mInputController = new ActivityInputController(activity, panel);
		} else {
			mInputController.addInputListener(activity, panel);
		}
		panel.requestFocus();
	}
	
	private void setTitleActionBar(Activity activity) {
		if (activity.mActivityInfo.label != null) {
			mTitleActionBar.setTitle(activity.mActivityInfo.label);
		}
		if (activity.mActivityInfo.icon != -1) {
			mTitleActionBar.setIcon(activity.mActivityInfo.icon);
		}
		if (mTitleActionBar.mMenu == null || mTitleActionBar.mMenu.size() == 0) {
			mTitleActionBar.mMenu = new MenuContainer();
			activity.onCreateOptionsMenu(mTitleActionBar.mMenu);
		} else {
			activity.onPrepareOptionsMenu(mTitleActionBar.mMenu);
		}
		// set menu in titlebar 
		mTitleActionBar.setMenu();
		mTitleActionBar.mTitleBar.invalidate();
	}

	@Override
	public String getTitle() {
		return (String) mTitleActionBar.getTitle();
	}
	
	@Override
	public void setTitle(String title) {
		mTitleActionBar.setTitle(title);
	}
	
	public void setTitle(int res) {
		mTitleActionBar.setTitle(res);
	}

	/**
	 * Instead, use 
	 */
	@Override
	@Deprecated
	public synchronized void addWindowListener(WindowListener l) {
		Log.e("Instead, use setOnWindowListener ");
		super.addWindowListener(l);
	}

	/*package*/ Activity getCurrentActivity() {
		if (mActivityList.isEmpty()) {
			return null;
		} else {
			return mActivityList.peek();
		}
	}

	/*package*/ void removeCurrentActivity() {
		final Activity a = mActivityList.pop();
		Animation exitAni = AnimationUtils.loadAnimation((Context)a, a.mActivityInfo.exitAni);
		if (exitAni != null) {
			exitAni.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					removeActivityFromWindow(a);
				}
			});
//			a.mPanel.setOpaque(false);
			a.mPanel.mDecorView.startAnimation(exitAni);
		} else {
			removeActivityFromWindow(a);
		}
	}
	
	private synchronized void removeActivityFromWindow(Activity a) {
		int size = mActivityList.size();
		for (int i=size-1;i>=0;--i) {
			if (mActivityList.get(i) == a) {
				mActivityList.remove(i);
			}
		}
		// destroy
		a.mPanel.setVisible(false);
		a.performDestroy();
		//
		ActivityInfo info = ActivityManager.getInstance().getActivityInfo(a.getClass().getName());
		info.activityLooper.quit();
		info.activityLooper.interrupt();
		mBorderManager.removeContents(a.mPanel); 
		//
		if (getComponentCount()>=1) {
			Activity curActivity = getCurrentActivity();
			if (curActivity != null) {
				// requestFeature(DEFAULT_FEATURES);
				mFeatures = DEFAULT_FEATURES;
				curActivity.setActivityInfo(Window.this, curActivity, curActivity.mActivityInfo);
				refreshActivity(a.mActivityInfo, curActivity.mActivityInfo);
				// set input controller
				curActivity.mPanel.setVisible(true);
				setInputController(curActivity, curActivity.mPanel);
				curActivity.performResume();
			}
		}
	}
	
	private void onCreate() {
		// TODO : 시작 시 startactivitiy 때문에 oncreate가 두 번 중복 된다. 그래서 일단은 여기를 주석. 
		if (mCallback != null) mCallback.onWindowCreate(); 
	}
	
	private void onResume() {
		if (!mActivityList.isEmpty()) getCurrentActivity().performResume();
	}
	
	private void onPause() {		
		if (!mActivityList.isEmpty()) getCurrentActivity().performPause();
	}
	
	private void onStop() {
		if (!mActivityList.isEmpty()) getCurrentActivity().performStop();
	}
	
	private void onDestroy() {
		int size = mActivityList.size();
		for (;!mActivityList.isEmpty();) {
			removeActivityFromWindow(mActivityList.pop());
		}
		dispose();
		if (mCallback != null) mCallback.onWindowDestroy(this);
	}
	
	public ActionBar getActionBar() {
		return mTitleActionBar;
	}
	
	/**
	 * 
	 * @param prevInfo
	 * @param curInfo adjust this info in window
	 */
	private void refreshActivity(ActivityInfo prevInfo, ActivityInfo curInfo) {
		if (prevInfo != null && (prevInfo.width != curInfo.width 
				|| prevInfo.height != curInfo.height)) {
			setSize(curInfo.width, curInfo.height);
			mTitleActionBar.resizeTitleBar();
		}
		mTitleActionBar.refresh();
		mTitleActionBar.mMenu = null;
		mBorderManager.makeTitleBarByFeature(curInfo);
		setTitleActionBar(getCurrentActivity());
	}
	
	public int getTitleBarHeight() {
		return mTitleActionBar.getHeight();
	}
	
	@Override
	public void setSize(Dimension d) {
		d.width += ApplicationSetting.WINDOW_WIDTH_PADDING;
		d.height += ApplicationSetting.WINDOW_HEIGHT_PADDING;
		Log.i("setSize", d.width + ", " + d.height);
		super.setSize(d);
	}
	
	@Override
	public void setSize(int width, int height) {
		width += ApplicationSetting.WINDOW_WIDTH_PADDING;
		if (mTitleActionBar == null || mTitleActionBar.isTitleVisible()) {
			height += ApplicationSetting.WINDOW_HEIGHT_PADDING;
		} else {
			height += ApplicationSetting.WINDOW_WIDTH_PADDING;
		}
		Log.i("setSize", width + ", " + height);
		super.setSize(width, height);
	}

	public void setFeatureInt(int featureId, int value) {
		requestFeature(featureId);
		
		if (hasFeature(FEATURE_CUSTOM_TITLE)) {
			mTitleActionBar.setCustomView(value);
		}
	}
	
	public boolean requestFeature(int featureId) {
		final int flag = 1<<featureId;
        mFeatures |= flag;
//        mLocalFeatures |= mContainer != null ? (flag&~mContainer.mFeatures) : flag;
        
        if (hasFeature(FEATURE_NO_TITLE)) {
			mTitleActionBar.setVisible(false);
		} else {
			mTitleActionBar.setVisible(true);
		}
        
        return (mFeatures&flag) != 0;
	}
	
    /**
     * Return the feature bits that are enabled.  This is the set of features
     * that were given to requestFeature(), and are being handled by this
     * Window itself or its container.  That is, it is the set of
     * requested features that you can actually use.
     *
     * <p>To do: add a public version of this API that allows you to check for
     * features by their feature ID.
     *
     * @return int The feature bits.
     */
    protected final int getFeatures() {
        return mFeatures;
    }
    
    /**
     * Query for the availability of a certain feature.
     * 
     * @param feature The feature ID to check
     * @return true if the feature is enabled, false otherwise.
     */
    public boolean hasFeature(int feature) {
        return (getFeatures() & (1 << feature)) != 0;
    }
	
	//*********************************************************************************
	//	
	//*********************************************************************************
	public void startActivityForResult(Activity prevActivity, Intent intent, int requestCode) throws Exception {
		StartActivityTask task = new StartActivityTask(intent, requestCode, prevActivity);
		task.start();
	}
	
	//*********************************************************************************
	//	
	//*********************************************************************************	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
	}

	@Override
	public void paintAll(Graphics g) {
		super.paintAll(g);
	}
	
	public Stack<Activity> getActivityStack() {
		return mActivityList;
	}
	
    /** @hide */
    public final void destroy() {
    	onStop();
		onDestroy();
        mDestroyed = true;
    }

    /** @hide */
    public final boolean isDestroyed() {
        return mDestroyed;
    }
	
	/*package*/ WindowListener windowListener = new WindowListener() {
		
		/**
		 * 제일 처음 윈도우 열렸을 때, 화면에 보일 때.
		 * @param e
		 */
		@Override
		public void windowOpened(WindowEvent e) {
			onCreate();
		}
		
		/**
		 * minimize
		 */
		@Override
		public void windowIconified(WindowEvent e) {
			Window.this.setState(Frame.ICONIFIED);
			onPause();
		}
		
		@Override
		public void windowDeiconified(WindowEvent e) {
			onResume();
		}
		
		/**
		 * 제일 처음, 아이콘화에서 활성화, 포커스 잃었다가 얻을 때.
		 * @param e
		 */
		@Override
		public void windowActivated(WindowEvent e) {
			onResume();
			// 제일 처음 나오니까 횟수로 구분하여 사전 처리 작업 하게? 
		}
		
		@Override
		public void windowDeactivated(WindowEvent e) {
			onPause();
		}
		
		/**
		 * 닫기 버튼
		 * @param e
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			destroy();
		}
		
		@Override
		public void windowClosed(WindowEvent e) {
		}
	};
	
	private ComponentListener componentListener = new ComponentListener() {
		
		@Override
		public void componentShown(ComponentEvent e) {
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			if (mTitleActionBar != null && mTitleActionBar.getThemedContext() != null) {
				mTitleActionBar.getThemedContext().resize();
			}
			if (getCurrentActivity() != null) {
				getCurrentActivity().resize();
			}
		}
		
		@Override
		public void componentMoved(ComponentEvent e) {
		}
		
		@Override
		public void componentHidden(ComponentEvent e) {
		}
	};
	
	public void setCallback(Window.Callback c) {
		mCallback = c;
	}
	
	/*package*/ class StartActivityTask extends HandlerThread {
		/*package*/ Intent 										intent;
		/*package*/ int 										requestCode;
		/*package*/ Activity 									prevActivity;
		
		public StartActivityTask(Intent intent, int requestCode, Activity prevActivity) {
			super(intent.getComponent().getClassName());
			this.intent = intent;
			this.requestCode = requestCode;
			this.prevActivity = prevActivity;
		}

	    public void run() {
	    	try {
	    		Looper.prepare();
		        synchronized (this) {
		            mLooper = Looper.myLooper();
		            notifyAll();
		        }
		        
		        if (isFirst) {
					isFirst = false;
					initBorderSetting(Window.this);
				}
		        //-------------------------------------------
				ActivityInfo info = ActivityManager.getInstance().getActivityInfo(intent.getComponent().getClassName());
				if (info == null) Log.t("a activity does not defined in manifest. : " + intent.getComponent().getClassName());
				
				if (prevActivity != null) {
					refreshActivity(prevActivity.mActivityInfo, info);
				}
		        //-------------------------------------------
				int option = intent.getFlags();
				// onNewIntent
				if (option != Intent.FLAG_ACTIVITY_NEW_TASK) {
		        	for (Activity activity : mActivityList) {
		        		if (activity.mActivityInfo.name.equals(info.name)) {
		        			activity.onNewIntent(intent);
		        			activity.mPrevActivity = prevActivity;
		        			activity.mRequestCode = requestCode;
		        			bringActivityToFront(activity);
		        			break;
		        		}
					}
		        }
				// process other flag
				if (option == Intent.FLAG_ACTIVITY_NEW_TASK) {
					info.activityLooper = this;
					Activity newA = intent.createActivity();
					newA.mActivityInfo = info;
					newA.setIntent(intent);
					newA.setActivityInfo(Window.this, newA, info);
					if (requestCode >= 0) {
						newA.mPrevActivity = prevActivity;
						newA.mRequestCode = requestCode;
					}
					addActivity(newA);
				} else if (option == Intent.FLAG_ACTIVITY_CURRENT_TASK) {
					
				}
				validate();
				repaint();
		        //--------------------------------------------------				
				onLooperPrepared();
				Looper.loop();
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new RuntimeException();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
}