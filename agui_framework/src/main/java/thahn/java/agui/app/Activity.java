package thahn.java.agui.app;

import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.jdom2.Element;

import thahn.java.agui.ContextMenu;
import thahn.java.agui.ContextMenu.ContextMenuInfo;
import thahn.java.agui.app.controller.Handler;
import thahn.java.agui.exception.NotExistException;
import thahn.java.agui.jmx.JmxConnectorHelper;
import thahn.java.agui.res.ResourcesManager;
import thahn.java.agui.res.ManifestParser.ManagedComponent;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.LayoutInflater;
import thahn.java.agui.view.MenuInflater;
import thahn.java.agui.view.MenuItem;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.View;
import thahn.java.agui.view.View.OnCreateContextMenuListener;
import thahn.java.agui.view.ViewGroup;

/**
 * 
 * @author thAhn
 *
 */
@SuppressWarnings("serial")
public class Activity extends ContextThemeWrapper implements LayoutInflater.Factory2, OnCreateContextMenuListener {

	public static final String													TAG				 	= "Activity";
	
	public static final String													LAYOUT_INFLATER 	= "LayoutInflater";
	
	/** Standard activity result: operation canceled. */
    public static final int 													RESULT_CANCELED    	= 0;
    /** Standard activity result: operation succeeded. */
    public static final int 													RESULT_OK           = -1;
    /** Start of user-defined activity results. */
    public static final int 													RESULT_FIRST_USER   = 1;
	
    /*package*/ int 															mRequestCode;
    /*package*/ int 															mResultCode 		= RESULT_CANCELED;
    /*package*/ Intent 															mResultData 		= null;
    /*package*/ Activity														mPrevActivity;
    /*package*/ int																mNextEnterAni		= -1;
    /*package*/ int																mNextExitAni		= -1;
    /*package*/ boolean 														mFinished			= false;
	
	private MenuInflater														mMenuInflater;
	private Thread 																mUiThread;

	/*package*/ ActivityInfo													mActivityInfo;
	/*package*/ Stack<ManagedDialog>											mManagedDialogs;
    /*package*/ Intent															mIntent;
	/*package*/ final Handler													mHandler = new Handler();
	
	/*package*/ final FragmentManagerImpl 										mFragments 			= new FragmentManagerImpl();
	/*package*/ final FragmentContainer 										mContainer 			= new FragmentContainer() {
		
        @Override
        public View findViewById(int id) {
            return Activity.this.findViewById(id);
        }
    };
	
    /*package*/ boolean 														mCheckedForLoaderManager;
    /*package*/ boolean 														mLoadersStarted;
    /*package*/ HashMap<String, LoaderManagerImpl> 								mAllLoaderManagers;
    /*package*/ LoaderManagerImpl 												mLoaderManager;
	
    /*package*/ boolean 														mOptionsMenuInvalidated;
    
	public Activity() {
		super();
		mManagedDialogs = new Stack<>();
//		mDropTarget = new DropTarget(mPanel, this);
	}

	/**
	 * this is different with context.mPanel.setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		if (b) {
			if (mResultData != null) {
				performCreate(mResultData.getExtras());
			} else {
				performCreate(null);
			}
		} else {
			onDestroy();
		}
		super.setVisible(b);
	}

	public void setContentView(int layoutId) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		layoutInflater.setFactory2(this);
		setView(layoutInflater.inflate(layoutId, null));
	}
	
	public void setContentView(View view) {
		setView(view);
	}
	
	final void performCreate(Bundle icicle) {
		mUiThread = Thread.currentThread();
		mFragments.attachActivity(this, mContainer, null);
		
        onCreate(icicle);
        mFragments.dispatchActivityCreated();
    }
	
	final void performStart() {
        onStart();
        mFragments.dispatchStart();
    }
	
	final void performPostCreate() {
		if (mResultData != null) {
			onPostCreate(mResultData.getExtras());
		} else {
			onPostCreate(null);
		}
    }
	
	final void performResume() {
        onResume();
        mFragments.dispatchResume();
    }
	
	final void performPause() {
        onPause();
        mFragments.dispatchPause();
    }
	
	final void performStop() {
        onStop();
        mFragments.dispatchStop();
    }
	
	final void performDestroy() {
        onDestroy();
        mFragments.dispatchDestroy();
    }
	
	protected void onCreate(Bundle bundle) {
		Log.d("onCreate");
		mFragments.dispatchCreate();
	}
	
	protected void onPostCreate(Bundle savedInstanceState) {
		Log.d("onPostCreate");
	}
	
	 /**
     * Standard implementation of
     * {@link android.view.LayoutInflater.Factory#onCreateView} used when
     * inflating with the LayoutInflater returned by {@link #getSystemService}.
     * This implementation does nothing and is for
     * pre-{@link android.os.Build.VERSION_CODES#HONEYCOMB} apps.  Newer apps
     * should use {@link #onCreateView(View, String, Context, AttributeSet)}.
     *
     * @see android.view.LayoutInflater#createView
     * @see android.view.Window#getLayoutInflater
     */
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    /**
     ** this method create view in android.app.fragment *
     * Standard implementation of
     * {@link android.view.LayoutInflater.Factory2#onCreateView(View, String, Context, AttributeSet)}
     * used when inflating with the LayoutInflater returned by {@link #getSystemService}.
     * This implementation handles <fragment> tags to embed fragments inside
     * of the activity.
     *
     * @see android.view.LayoutInflater#createView
     * @see android.view.Window#getLayoutInflater
     */
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//    	return null;
    	if (!"fragment".equals(name)) {
            return onCreateView(name, context, attrs);
        }
        
        String fname = //attrs.getString(thahn.java.agui.R.attr.fragment_class, null);//"class"; 
        		attrs.getAttributeValue(null, "class");
//        TypedArray a = 
//            context.obtainStyledAttributes(attrs, com.android.internal.R.styleable.Fragment);
//        if (fname == null) {
//            fname = a.getString(com.android.internal.R.styleable.Fragment_name);
//        }
        int id = attrs.getResourceId(thahn.java.agui.R.attr.View_id, View.NO_ID);
        String tag = attrs.getString(thahn.java.agui.R.attr.View_tag, null);
//        a.recycle();
        
        int containerId = parent != null ? parent.getId() : 0;
        if (containerId == View.NO_ID && id == View.NO_ID && tag == null) {
            throw new IllegalArgumentException(
//            		attrs.getPositionDescription() + 
            		": Must specify unique android:id, android:tag, or have a parent with an id for " + fname);
        }

        // If we restored from a previous state, we may already have
        // instantiated this fragment from the state and should use
        // that instance instead of making a new one.
        Fragment fragment = id != View.NO_ID ? mFragments.findFragmentById(id) : null;
        if (fragment == null && tag != null) {
            fragment = mFragments.findFragmentByTag(tag);
        }
        if (fragment == null && containerId != View.NO_ID) {
            fragment = mFragments.findFragmentById(containerId);
        }

        if (FragmentManagerImpl.DEBUG) Log.v(TAG, "onCreateView: id=0x"
                + Integer.toHexString(id) + " fname=" + fname
                + " existing=" + fragment);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fname);
            fragment.mFromLayout = true;
            fragment.mFragmentId = id != 0 ? id : containerId;
            fragment.mContainerId = containerId;
            fragment.mTag = tag;
            fragment.mInLayout = true;
            fragment.mFragmentManager = mFragments;
            fragment.onInflate(this, attrs, fragment.mSavedFragmentState);
            mFragments.addFragment(fragment, true);

        } else if (fragment.mInLayout) {
            // A fragment already exists and it is not one we restored from
            // previous state.
            throw new IllegalArgumentException(
//            		attrs.getPositionDescription() + 
            		": Duplicate id 0x" + Integer.toHexString(id)
                    + ", tag " + tag + ", or parent id 0x" + Integer.toHexString(containerId)
                    + " with another fragment for " + fname);
        } else {
            // This fragment was retained from a previous instance; get it
            // going now.
            fragment.mInLayout = true;
            // If this fragment is newly instantiated (either right now, or
            // from last saved state), then give it the attributes to
            // initialize itself.
            if (!fragment.mRetaining) {
                fragment.onInflate(this, attrs, fragment.mSavedFragmentState);
            }
            mFragments.moveToState(fragment);
        }

        if (fragment.mView == null) {
            throw new IllegalStateException("Fragment " + fname
                    + " did not create a view.");
        }
        if (id != 0) {
            fragment.mView.setId(id);
        }
        if (fragment.mView.getTag() == null) {
            fragment.mView.setTag(tag);
        }
        return fragment.mView;
    }
    
    /**
     * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when  
     * the activity had been stopped, but is now again being displayed to the 
	 * user.  It will be followed by {@link #onResume}.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     * 
     * @see #onCreate
     * @see #onStop
     * @see #onResume
     */
    protected void onStart() {
//        if (DEBUG_LIFECYCLE) Slog.v(TAG, "onStart " + this);
//        mCalled = true;
//        
//        if (!mLoadersStarted) {
//            mLoadersStarted = true;
//            if (mLoaderManager != null) {
//                mLoaderManager.doStart();
//            } else if (!mCheckedForLoaderManager) {
//                mLoaderManager = getLoaderManager(null, mLoadersStarted, false);
//            }
//            mCheckedForLoaderManager = true;
//        }
//
//        getApplication().dispatchActivityStarted(this);
    }
    
	protected void onResume() {
		Log.d("onResume");
	}
	
    /**
     * Called when activity resume is complete (after {@link #onResume} has
     * been called). Applications will generally not implement this method;
     * it is intended for system classes to do final setup after application
     * resume code has run.
     * 
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     * 
     * @see #onResume
     */
    protected void onPostResume() {
//        final Window win = getWindow();
//        if (win != null) win.makeActive();
//        if (mActionBar != null) mActionBar.setShowHideAnimationEnabled(true);
//        mCalled = true;
    }
	
	protected void onPause() {
		Log.d("onPause");
	}
	
	protected void onStop() {
		Log.d("onStop");
	}
	
	protected void onDestroy() {
		onDetachedViewFromWindow(((ViewGroup)mPanel.mDecorView));
		mFragments.dispatchDestroy();
//		stopLooper();
		Log.d("onDestroy");
	}
	
	/**
     * Called when a Fragment is being attached to this activity, immediately
     * after the call to its {@link Fragment#onAttach Fragment.onAttach()}
     * method and before {@link Fragment#onCreate Fragment.onCreate()}.
     */
    public void onAttachFragment(Fragment fragment) {
    }
	
	private void onDetachedViewFromWindow(ViewGroup viewGroup) {
		if (viewGroup != null) {
			for (View v : viewGroup.getChildren()) {
				if (v instanceof ViewGroup) {
					onDetachedViewFromWindow((ViewGroup) v);
				} else {
					v.onDetachedFromWindow();
				}
			}
		}
	}
	
	public String getTitle() {
    	return getWindow().getTitle();
    }
	
    public void setTitle(CharSequence title) {
    	getWindow().setTitle(title.toString());
    }

    public void setTitle(int titleId) {
    	getWindow().setTitle(titleId);
    }
	
	public void setIntent(Intent intent) {
		mIntent = intent;
	}

	public Intent getIntent() {
		return mIntent;
	}
	
	@Override
	public int getWidth() {
		if (ApplicationSetting.getInstance().isAutoResize()) {
			int width = mPanel.getWidth();
			if (mActivityInfo.width < width) {
				return width;
			} else {
				return mActivityInfo.width;
			}
		} else {
			return mActivityInfo.width;
		}
	}

	@Override
	public int getHeight() {
		if (ApplicationSetting.getInstance().isAutoResize()) { 
			int height = mPanel.getHeight();
			if (mActivityInfo.height < height) {
				return height;
			} else {
				return mActivityInfo.height;
			}
		} else {
			return mActivityInfo.height;
		}
	}

	@Override
	public void startActivity(Intent intent) {
		startActivityForResult(intent, -1);
	}
	
	/**
	 * 
	 * @param intent
	 * @param requestCode should be >=0
	 */
	public void startActivityForResult(Intent intent, int requestCode) {
		try {
			Window window = (Window)getParent();
			window.startActivityForResult(this, intent, requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
	
	public final void setResult(int resultCode) {
		setResult(resultCode, null);
    }
	
	public final void setResult(int resultCode, Intent resultData) {
        synchronized (this) {
            mResultCode = resultCode;
            mResultData = resultData;
        }
    }
	
	/**
     * Call immediately after one of the flavors of {@link #startActivity(Intent)}
     * or {@link #finish} to specify an explicit transition animation to
     * perform next.
     * @param enterAnim A resource ID of the animation resource to use for
     * the incoming activity.  Use 0 for no animation.
     * @param exitAnim A resource ID of the animation resource to use for
     * the outgoing activity.  Use 0 for no animation.
     */
    public void overridePendingTransition(int enterAnim, int exitAnim) {
    	mNextEnterAni = enterAnim;
    	mNextExitAni = exitAnim;
    }
	
	/*package*/ Window getParent() {
		Component parent = mPanel.getParent();
		for (;;) {
			if (parent instanceof Window) {
				break;
			} 
			if (parent == null) throw new NotExistException("Window parent does not exist.");
			parent = parent.getParent();
		}
		return (Window) parent;
	}
	
    /**
     * Return the FragmentManager for interacting with fragments associated
     * with this activity.
     */
    public FragmentManager getFragmentManager() {
        return mFragments;
    }
	
    public void finish() {
    	if (mRequestCode >= 0 && mPrevActivity != null) {
    		mPrevActivity.onActivityResult(mRequestCode, mResultCode, mResultData);
    	}
    	Window window = ((Window) getParent());
    	window.removeCurrentActivity();
    }
    
//    private void setFocusView(View view) {
//    	mFocusView = view;
//    }
    
//	@Override
//	public void changedFocusable(View view) {
//		setFocusView(view);
//	}    
    
    @Override
    public Object getSystemService(String name) {
//        if (getBaseContext() == null) {
//            throw new IllegalStateException(
//                    "System services not available to Activities before onCreate()");
//        }
    	if (LAYOUT_INFLATER_SERVICE.equals(name)) {
			return LayoutInflater.from(this);
		} else if (WINDOW_SERVICE.equals(name)) {
			return WindowManager.getInstance();
		}
//            return mWindowManager;
//        } else if (SEARCH_SERVICE.equals(name)) {
//            ensureSearchManager();
//            return mSearchManager;
//        }
        return super.getSystemService(name);
    }
    
    /**
     * Convenience for calling
     * {@link android.view.Window#getLayoutInflater}.
     */
    public LayoutInflater getLayoutInflater() {
        return ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));//getWindow().getLayoutInflater();
    }
    
    public Window getWindow() {
    	return ((Window)getParent());
    }
    
    public WindowManager getWindowManager() {
    	return WindowManager.getInstance();
    }
    
    public ActionBar getActionBar() {
    	return ((Window)getParent()).getActionBar();
    }
    
    public MenuInflater getMenuInflater() {
    	if (mMenuInflater == null) {
    		if (getActionBar() != null) {
    			mMenuInflater = new MenuInflater(getActionBar().getThemedContext(), this);
            } else {
                mMenuInflater = new MenuInflater(this);
            }
    		
    	} 
    	return mMenuInflater;
    }
    
    /**
     * Return the LoaderManager for this fragment, creating it if needed.
     */
    public LoaderManager getLoaderManager() {
        if (mLoaderManager != null) {
            return mLoaderManager;
        }
        mCheckedForLoaderManager = true;
        mLoaderManager = getLoaderManager(null, mLoadersStarted, true);
        return mLoaderManager;
    }
    
    LoaderManagerImpl getLoaderManager(String who, boolean started, boolean create) {
        if (mAllLoaderManagers == null) {
            mAllLoaderManagers = new HashMap<String, LoaderManagerImpl>();
        }
        LoaderManagerImpl lm = mAllLoaderManagers.get(who);
        if (lm == null) {
            if (create) {
                lm = new LoaderManagerImpl(who, this, started);
                mAllLoaderManagers.put(who, lm);
            }
        } else {
            lm.updateActivity(this);
        }
        return lm;
    }
    
    /**
     * Called by Fragment.startActivityForResult() to implement its behavior.
     */
    public void startActivityFromFragment(Fragment fragment, Intent intent, 
            int requestCode) {
        if (requestCode == -1) {
            this.startActivityForResult(intent, -1);
            return;
        }
        if ((requestCode&0xffff0000) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        }
        this.startActivityForResult(intent, ((fragment.mIndex+1)<<16) + (requestCode&0xffff));
    }
    
    /**
     * Support library version of {@link Activity#invalidateOptionsMenu}.
     *
     * <p>Invalidate the activity's options menu. This will cause relevant presentations
     * of the menu to fully update via calls to onCreateOptionsMenu and
     * onPrepareOptionsMenu the next time the menu is requested.
     */
    public void supportInvalidateOptionsMenu() {
//        if (android.os.Build.VERSION.SDK_INT >= HONEYCOMB) {
//            // If we are running on HC or greater, we can use the framework
//            // API to invalidate the options menu.
//            ActivityCompatHoneycomb.invalidateOptionsMenu(this);
//            return;
//        }

        // Whoops, older platform...  we'll use a hack, to manually rebuild
        // the options menu the next time it is prepared.
        mOptionsMenuInvalidated = true;
    }
    
    void invalidateSupportFragment(String who) {
        //Log.v(TAG, "invalidateSupportFragment: who=" + who);
        if (mAllLoaderManagers != null) {
            LoaderManagerImpl lm = mAllLoaderManagers.get(who);
            if (lm != null && !lm.mRetaining) {
                lm.doDestroy();
                mAllLoaderManagers.remove(who);
            }
        }
    }
    
    /**
     * Runs the specified action on the UI thread. If the current thread is the UI
     * thread, then the action is executed immediately. If the current thread is
     * not the UI thread, the action is posted to the event queue of the UI thread.
     *
     * @param action the action to run on the UI thread
     */
    public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	return true;
    }
    
    /**
     * not yet
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	int menuId = item.getItemId();
    	switch (menuId) {
		case thahn.java.agui.R.id.title:
			// do backPressed
			break;
		}
    	return true;
    }
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	}
	
	/**
     * Default implementation of
     * {@link android.view.Window.Callback#onMenuItemSelected}
     * for activities.  This calls through to the new
     * {@link #onOptionsItemSelected} method for the
     * {@link android.view.Window#FEATURE_OPTIONS_PANEL}
     * panel, so that subclasses of
     * Activity don't need to deal with feature codes.
     */
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	return false;
//        CharSequence titleCondensed = item.getTitleCondensed();
//
//        switch (featureId) {
//            case Window.FEATURE_OPTIONS_PANEL:
//                // Put event logging here so it gets called even if subclass
//                // doesn't call through to superclass's implmeentation of each
//                // of these methods below
//                if (titleCondensed != null) {
//                    EventLog.writeEvent(50000, 0, titleCondensed.toString());
//                }
//                if (onOptionsItemSelected(item)) {
//                    return true;
//                }
//                if (mFragments.dispatchOptionsItemSelected(item)) {
//                    return true;
//                }
//                if (item.getItemId() == android.R.id.home && mActionBar != null &&
//                        (mActionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0) {
//                    if (mParent == null) {
//                        return onNavigateUp();
//                    } else {
//                        return mParent.onNavigateUpFromChild(this);
//                    }
//                }
//                return false;
//                
//            case Window.FEATURE_CONTEXT_MENU:
//                if (titleCondensed != null) {
//                    EventLog.writeEvent(50000, 1, titleCondensed.toString());
//                }
//                if (onContextItemSelected(item)) {
//                    return true;
//                }
//                return mFragments.dispatchContextItemSelected(item);
//
//            default:
//                return false;
//        }
    }
	
    /**
     * This is called for activities that set launchMode to "singleTop" in
     * their package, or if a client used the {@link Intent#FLAG_ACTIVITY_SINGLE_TOP}
     * flag when calling {@link #startActivity}.  In either case, when the
     * activity is re-launched while at the top of the activity stack instead
     * of a new instance of the activity being started, onNewIntent() will be
     * called on the existing instance with the Intent that was used to
     * re-launch it. 
     *  
     * <p>An activity will always be paused before receiving a new intent, so 
     * you can count on {@link #onResume} being called after this method. 
     * 
     * <p>Note that {@link #getIntent} still returns the original Intent.  You 
     * can use {@link #setIntent} to update it to this new Intent. 
     * 
     * @param intent The new intent that was started for the activity. 
     *  
     * @see #getIntent
     * @see #setIntent 
     * @see #onResume 
     */
    protected void onNewIntent(Intent intent) {
    }
	
    /**
     * Default implementation of
     * {@link android.view.Window.Callback#onCreatePanelMenu}
     * for activities.  This calls through to the new
     * {@link #onCreateOptionsMenu} method for the
     * {@link android.view.Window#FEATURE_OPTIONS_PANEL} panel,
     * so that subclasses of Activity don't need to deal with feature codes.
     */
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
//        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
//            boolean show = onCreateOptionsMenu(menu);
//            show |= mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
//            return show;
//        }
        return false;
    }
    
    /**
     * Default implementation of
     * {@link android.view.Window.Callback#onPreparePanel}
     * for activities.  This
     * calls through to the new {@link #onPrepareOptionsMenu} method for the
     * {@link android.view.Window#FEATURE_OPTIONS_PANEL}
     * panel, so that subclasses of
     * Activity don't need to deal with feature codes.
     */
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
//        if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
//            boolean goforit = onPrepareOptionsMenu(menu);
//            goforit |= mFragments.dispatchPrepareOptionsMenu(menu);
//            return goforit;
//        }
    	if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
            if (mOptionsMenuInvalidated) {
                mOptionsMenuInvalidated = false;
                menu.clear();
                onCreatePanelMenu(featureId, menu);
            }
            boolean goforit = false;//onPrepareOptionsPanel(view, menu);
            goforit |= mFragments.dispatchPrepareOptionsMenu(menu);
            return goforit;
        }
        return true;
    }
    
    /**
     * Default implementation of
     * {@link android.view.Window.Callback#onPanelClosed(int, Menu)} for
     * activities. This calls through to {@link #onOptionsMenuClosed(Menu)}
     * method for the {@link android.view.Window#FEATURE_OPTIONS_PANEL} panel,
     * so that subclasses of Activity don't need to deal with feature codes.
     * For context menus ({@link Window#FEATURE_CONTEXT_MENU}), the
     * {@link #onContextMenuClosed(Menu)} will be called.
     */
    public void onPanelClosed(int featureId, Menu menu) {
//        switch (featureId) {
//            case Window.FEATURE_OPTIONS_PANEL:
//                mFragments.dispatchOptionsMenuClosed(menu);
//                onOptionsMenuClosed(menu);
//                break;
//                
//            case Window.FEATURE_CONTEXT_MENU:
//                onContextMenuClosed(menu);
//                break;
//
//            case Window.FEATURE_ACTION_BAR:
//                initActionBar();
//                mActionBar.dispatchMenuVisibilityChanged(false);
//                break;
//        }
    }

	@Override
	public Dialog onCreateDialog(int i) {
    	return null;
	}

	@Override
    public void showDialog(int i) {
    	Dialog dialog = onCreateDialog(i);
    	addManagedDialog(dialog, dialog.mBundle);
    }
	
	@Override
	/*package*/ void addManagedDialog(Dialog dialog, Bundle bundle) {
		ManagedDialog managed = new ManagedDialog();
		managed.dialog = dialog;
		managed.dialog.dispatchOnCreate(bundle);
		if (bundle != null) {
			managed.bundle = bundle;
		}
		mManagedDialogs.add(managed);
		((ViewGroup) mPanel.mDecorView).addView(managed.dialog.mDialogView);
		// mPanel.invalidate();
	}
    
    @Override
    public void dismissDialog(int i) {
    	ManagedDialog managed = getDialog(i);
    	if (managed != null) {
    		mManagedDialogs.remove(managed);
			managed.dialog.onStop();
			((ViewGroup) mPanel.mDecorView).removeView(managed.dialog.mDialogView);
			mPanel.invalidate();
    	}
    }
    
    private ManagedDialog getDialog(int i) {
	    int size = mManagedDialogs.size();
		for (int n=0;n<size;++n) { //ManagedDialog managed : mManagedDialogs) {
			ManagedDialog managed = mManagedDialogs.get(n);
			if (managed != null && managed.dialog.getId() == i) {
				return managed;
			}
		}
		return null;
    }
    
    public void setActivityInfo(Window parent, Activity src, ActivityInfo info) {
    	if (info.theme == null) {
    		info.theme = "@agui:theme/Theme";
    	}
    	
		int[][] styles = ResourcesManager.getInstance().getStyles(info.theme);
		
		if (styles == null) {
			return ;
		}
		
		for (int i=0;i<styles.length;++i) {
			// TODO : add theme style
			switch (styles[i][0]) {
			case thahn.java.agui.R.attr.Window_windowBackground:
				src.mPanel.setBackgroundColor(styles[i][1]);
				break;
			case thahn.java.agui.R.attr.Window_windowIsFloating:
//					Log.e("windowIsFloating : " + styles[i][1]);
//				src.mPanel.mShellView.setBackgroundColor(0x40000000);
				//
//				RelativeLayout parent = new RelativeLayout(src);
//				parent.setBackgroundColor(0x40000000);
//				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//				parent.setLayoutParams(params);
//				if (src.mPanel.mShellView.getChildCount() > 2) Log.t("root view's child is only one.");
//				View child = src.mPanel.mShellView.getChildren()[0];
//				LayoutParams childParmas = child.getLayoutParams();
//				RelativeLayout.LayoutParams tempParams = new RelativeLayout.LayoutParams(childParmas.width, childParmas.height);
//				tempParams.addRule(RelativeLayout.LayoutParams.CENTER);
//				child.setLayoutParams(tempParams);
//				parent.addView(child);
//				src.mPanel.mShellView = parent;
//				src.mPanel.invalidate();
				break;
			case thahn.java.agui.R.attr.Window_windowNoTitle:
				parent.requestFeature(Window.FEATURE_NO_TITLE);
				break;
			}
		}
	}
    
    public void registerForContextMenu(View view) {
        view.setOnCreateContextMenuListener(this, MotionEvent.BUTTON3);
    }
    
    /**
     * 
     * @param view
     * @param btnCode {@link MotionEvent#BUTTON1}, {@link MotionEvent#BUTTON2}, {@link MotionEvent#BUTTON3}
     */
    public void registerForContextMenu(View view, int btnCode) {
        view.setOnCreateContextMenuListener(this, btnCode);
    }
    
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    	return getApplicationContext().registerReceiver(receiver, filter);
    }
    
    /**
     * Enable extended window features.  This is a convenience for calling
     * {@link android.view.Window#requestFeature getWindow().requestFeature()}.
     * 
     * @param featureId The desired feature as defined in
     *                  {@link android.view.Window}.
     * @return Returns true if the requested feature is supported and now
     *         enabled.
     * 
     * @see android.view.Window#requestFeature
     */
    public final boolean requestWindowFeature(int featureId) {
        return getWindow().requestFeature(featureId);
    }
    
    /**
     * Check to see whether this activity is in the process of finishing,
     * either because you called {@link #finish} on it or someone else
     * has requested that it finished.  This is often used in
     * {@link #onPause} to determine whether the activity is simply pausing or
     * completely finishing.
     * 
     * @return If the activity is finishing, returns true; else returns false.
     * 
     * @see #finish
     */
    public boolean isFinishing() {
        return mFinished;
    }
    
//**************************************************************************************
//
//	
//**************************************************************************************


//	DropTarget mDropTarget;
//	@Override
//	public void dragEnter(DropTargetDragEvent dtde) {
//		Log.e(TAG, "dragEnter");
//	}
//
//	@Override
//	public void dragOver(DropTargetDragEvent dtde) {
//		Log.e(TAG, "dragOver");
//	}
//
//	@Override
//	public void dropActionChanged(DropTargetDragEvent dtde) {
//		Log.e(TAG, "dropActionChanged");
//	}
//
//	@Override
//	public void dragExit(DropTargetEvent dte) {
//		Log.e(TAG, "dragExit");
//	}
//
//	@Override
//	public void drop(DropTargetDropEvent dtde) {
//		Log.e(TAG, "drop");
//	}
}
