package thahn.java.agui.app;

import thahn.java.agui.settings.AguiSettings;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.KeyEvent;
import thahn.java.agui.view.LayoutInflater;
import thahn.java.agui.view.MotionEvent;
import thahn.java.agui.view.OnTouchListener;
import thahn.java.agui.view.View;
import thahn.java.agui.view.ViewGroup.LayoutParams;
import thahn.java.agui.widget.RelativeLayout;

public class Dialog implements DialogInterface, OnTouchListener, View.OnClickListener {

	public static final String												TAG 			= "dialog";
	
	/*package*/ int															mId;
	/*package*/ Activity													mOwnerActivity;
	/*package*/ Context 													mContext;
	/*package*/ View														mDialogView;
	/*package*/ Bundle 														mBundle;
	/*package*/ OnCancelListener											mOnCancelListener;
	/*package*/ OnDismissListener											mOnDismissListener;
	
	private boolean 														mCreated = false;
    private boolean 														mShowing = false;
    private boolean 														mCanceled = false;
	
	/*** {@hide} */
    private boolean 														mCancelable = true;
	
    public Dialog(Context context) {
        this(context, 0, true);
    }

    public Dialog(Context context, int theme) {
        this(context, theme, true);
    }

    Dialog(Context context, int theme, boolean createContextWrapper) {
    	mContext = context;
//        if (theme == 0) {
//            TypedValue outValue = new TypedValue();
//            context.getTheme().resolveAttribute(com.android.internal.R.attr.dialogTheme,
//                    outValue, true);
//            theme = outValue.resourceId;
//        }
//        mContext = createContextWrapper ? new Context(context, theme) : context;
//        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
//        Window w = PolicyManager.makeNewWindow(mContext);
//        mWindow = w;
//        w.setCallback(this);
//        w.setWindowManager(mWindowManager, null, null);
//        w.setGravity(Gravity.CENTER);
//        mUiThread = Thread.currentThread();
//        mListenersHandler = new ListenersHandler(this);
    }

    protected Dialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        this(context);
        mCancelable = cancelable;
        setOnCancelListener(cancelListener);
    }
	
	@Override
	public void setContentView(int layoutResID) {
		View view = LayoutInflater.inflate(mContext, layoutResID, null);
		setView(view);
    }

	@Override
    public void setContentView(View view) {
    	setView(view);
    }

    /*package*/ void setView(View view) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(view.getLayoutParams());
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(params);
		
		RelativeLayout parent = new RelativeLayout(mContext);
		RelativeLayout.LayoutParams parentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		parent.setLayoutParams(parentParams);
		parent.setBackgroundColor(AguiSettings.getInstance().getDialogBackgroundColor());
		parent.addView(view);
		parent.setOnTouchListener(this);
		parent.setOnClickListener(this);
		mDialogView = parent;
	}
    
    public void setId(int id) {
    	mId = id;
    }
    
    public View findViewById(int id) {
    	return mDialogView.findViewById(id);
    }
    
    protected void dispatchOnCreate(Bundle savedInstanceState) {
    	onCreate(savedInstanceState);
    	if (!mCreated) {
            onCreate(savedInstanceState);
            mCreated = true;
        }
	}
    
	protected void onCreate(Bundle savedInstanceState) {
		
	}

    /**
     * Called when the dialog is starting.
     */
    protected void onStart() {
    }

    /**
     * Called to tell you that you're stopping.
     */
    protected void onStop() {
    }
	
	public void setOwnerActivity(Activity activity) {
		mOwnerActivity = activity;
	}
	
	@Override
	public void show() {
		mContext.addManagedDialog(this, null); 
	}
	
	@Override
	public void dismiss() {
		mContext.dismissDialog(mId);
		if(mOnDismissListener != null) mOnDismissListener.onDismiss(this);
	}
	
    /**
     * Cancel the dialog.  This is essentially the same as calling {@link #dismiss()}, but it will
     * also call your {@link DialogInterface.OnCancelListener} (if registered).
     */
    public void cancel() {
//        if (!mCanceled && mCancelMessage != null) {
//            mCanceled = true;
//            // Obtain a new message so this dialog can be re-used
//            Message.obtain(mCancelMessage).sendToTarget();
//        }
        dismiss();
        if(mOnCancelListener != null) mOnCancelListener.onCancel(this);
    }
    
    /**
     * Hide the dialog, but do not dismiss it.
     */
    public void hide() {
        if (mDialogView != null) {
        	mDialogView.setVisibility(View.GONE);
        }
    }
	
    public void onAttachedToWindow() {
    }
    
    public void onDetachedFromWindow() {
    }
    
    /**
     * Saves the state of the dialog into a bundle.
     *
     * The default implementation saves the state of its view hierarchy, so you'll
     * likely want to call through to super if you override this to save additional
     * state.
     * @return A bundle with the state of the dialog.
     */
    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
//        bundle.putBoolean(DIALOG_SHOWING_TAG, mShowing);
//        if (mCreated) {
//            bundle.putBundle(DIALOG_HIERARCHY_TAG, mWindow.saveHierarchyState());
//        }
        return bundle;
    }
    
    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        final Bundle dialogHierarchyState = savedInstanceState.getBundle(DIALOG_HIERARCHY_TAG);
//        if (dialogHierarchyState == null) {
//            // dialog has never been shown, or onCreated, nothing to restore.
//            return;
//        }
        dispatchOnCreate(savedInstanceState);
//        mWindow.restoreHierarchyState(dialogHierarchyState);
//        if (savedInstanceState.getBoolean(DIALOG_SHOWING_TAG)) {
//            show();
//        }
    }
    
    public int getId() {
		return mId;
	}

	public Bundle getBundle() {
    	return mBundle;
    }
    
	public Context getContext() {
		return mContext;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		return true;
	}

	@Override
	public void onClick(View v) {
	}
	
	public void setOnCancelListener(OnCancelListener listener) {
		mOnCancelListener = listener;
	}
	
	public void setOnDismissListener(OnDismissListener listener) {
		mOnDismissListener = listener;
	}
	
	/**
     * Sets whether this dialog is cancelable with the
     * {@link KeyEvent#KEYCODE_BACK BACK} key.
     */
    public void setCancelable(boolean flag) {
        mCancelable = flag;
    }
    
    /**
     * Called when the dialog has detected the user's press of the back
     * key.  The default implementation simply cancels the dialog (only if
     * it is cancelable), but you can override this to do whatever you want.
     */
    public void onBackPressed() {
        if (mCancelable) {
            cancel();
        }
    }
}
