package thahn.java.agui.app;

import java.util.UUID;

import thahn.java.agui.annotation.AguiSpecific;
import thahn.java.agui.utils.Log;

/**
 * 
 * @author thAhn
 * 
 */
public abstract class BroadcastReceiver {

	private String	mId;
	private int		mResultCode;
	private String	mResultData;
	private Bundle	mResultExtras;
	private boolean	mAbortBroadcast;
	private boolean	mDebugUnregister;
	private boolean	mOrderedHint;
	private boolean	mInitialStickyHint;
	private boolean	mFinished	= false;

	public BroadcastReceiver() {
		mId = UUID.randomUUID().toString();
	}

	/**
	 * This method is called when the BroadcastReceiver is receiving an Intent
	 * broadcast. During this time you can use the other methods on
	 * BroadcastReceiver to view/modify the current result values. The function
	 * is normally called within the main thread of its process, so you should
	 * never perform long-running operations in it (there is a timeout of 10
	 * seconds that the system allows before considering the receiver to be
	 * blocked and a candidate to be killed). You cannot launch a popup dialog
	 * in your implementation of onReceive().
	 * 
	 * <p>
	 * <b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
	 * then the object is no longer alive after returning from this
	 * function.</b> This means you should not perform any operations that
	 * return a result to you asynchronously -- in particular, for interacting
	 * with services, you should use {@link Context#startService(Intent)}
	 * instead of {@link Context#bindService(Intent, ServiceConnection, int)}.
	 * If you wish to interact with a service that is already running, you can
	 * use {@link #peekService}.
	 * 
	 * <p>
	 * The Intent filters used in
	 * {@link android.content.Context#registerReceiver} and in application
	 * manifests are <em>not</em> guaranteed to be exclusive. They are hints to
	 * the operating system about how to find suitable recipients. It is
	 * possible for senders to force delivery to specific recipients, bypassing
	 * filter resolution. For this reason, {@link #onReceive(Context, Intent)
	 * onReceive()} implementations should respond only to known actions,
	 * ignoring any unexpected Intents that they may receive.
	 * 
	 * @param context
	 *            The Context in which the receiver is running.
	 * @param intent
	 *            The Intent being received.
	 */
	public abstract void onReceive(Context context, Intent intent);

	/**
	 * Provide a binder to an already-running service. This method is
	 * synchronous and will not start the target service if it is not present,
	 * so it is safe to call from {@link #onReceive}.
	 * 
	 * @param myContext
	 *            The Context that had been passed to
	 *            {@link #onReceive(Context, Intent)}
	 * @param service
	 *            The Intent indicating the service you wish to use. See
	 *            {@link Context#startService(Intent)} for more information.
	 */
	// public IBinder peekService(Context myContext, Intent service) {
	// IActivityManager am = ActivityManagerNative.getDefault();
	// IBinder binder = null;
	// try {
	// binder = am.peekService(service, service.resolveTypeIfNeeded(
	// myContext.getContentResolver()));
	// } catch (RemoteException e) {
	// }
	// return binder;
	// }

	public String getId() {
		return mId;
	}

	/**
	 * Change the current result code of this broadcast; only works with
	 * broadcasts sent through
	 * {@link Context#sendOrderedBroadcast(Intent, String)
	 * Context.sendOrderedBroadcast}. Often uses the Activity
	 * {@link android.app.Activity#RESULT_CANCELED} and
	 * {@link android.app.Activity#RESULT_OK} constants, though the actual
	 * meaning of this value is ultimately up to the broadcaster.
	 * 
	 * <p>
	 * <strong>This method does not work with non-ordered broadcasts such as
	 * those sent with {@link Context#sendBroadcast(Intent)
	 * Context.sendBroadcast}</strong>
	 * </p>
	 * 
	 * @param code
	 *            The new result code.
	 * 
	 * @see #setResult(int, String, Bundle)
	 */
	public final void setResultCode(int code) {
		checkSynchronousHint();
		mResultCode = code;
	}

	/**
	 * Retrieve the current result code, as set by the previous receiver.
	 * 
	 * @return int The current result code.
	 */
	public final int getResultCode() {
		return mResultCode;
	}

	/**
	 * Change the current result data of this broadcast; only works with
	 * broadcasts sent through
	 * {@link Context#sendOrderedBroadcast(Intent, String)
	 * Context.sendOrderedBroadcast}. This is an arbitrary string whose
	 * interpretation is up to the broadcaster.
	 * 
	 * <p>
	 * <strong>This method does not work with non-ordered broadcasts such as
	 * those sent with {@link Context#sendBroadcast(Intent)
	 * Context.sendBroadcast}</strong>
	 * </p>
	 * 
	 * @param data
	 *            The new result data; may be null.
	 * 
	 * @see #setResult(int, String, Bundle)
	 */
	public final void setResultData(String data) {
		checkSynchronousHint();
		mResultData = data;
	}

	/**
	 * Retrieve the current result data, as set by the previous receiver. Often
	 * this is null.
	 * 
	 * @return String The current result data; may be null.
	 */
	public final String getResultData() {
		return mResultData;
	}

	/**
	 * Change the current result extras of this broadcast; only works with
	 * broadcasts sent through
	 * {@link Context#sendOrderedBroadcast(Intent, String)
	 * Context.sendOrderedBroadcast}. This is a Bundle holding arbitrary data,
	 * whose interpretation is up to the broadcaster. Can be set to null.
	 * Calling this method completely replaces the current map (if any).
	 * 
	 * <p>
	 * <strong>This method does not work with non-ordered broadcasts such as
	 * those sent with {@link Context#sendBroadcast(Intent)
	 * Context.sendBroadcast}</strong>
	 * </p>
	 * 
	 * @param extras
	 *            The new extra data map; may be null.
	 * 
	 * @see #setResult(int, String, Bundle)
	 */
	public final void setResultExtras(Bundle extras) {
		checkSynchronousHint();
		mResultExtras = extras;
	}

	/**
	 * Retrieve the current result extra data, as set by the previous receiver.
	 * Any changes you make to the returned Map will be propagated to the next
	 * receiver.
	 * 
	 * @param makeMap
	 *            If true then a new empty Map will be made for you if the
	 *            current Map is null; if false you should be prepared to
	 *            receive a null Map.
	 * 
	 * @return Map The current extras map.
	 */
	public final Bundle getResultExtras(boolean makeMap) {
		Bundle e = mResultExtras;
		if (!makeMap)
			return e;
		if (e == null)
			mResultExtras = e = new Bundle();
		return e;
	}

	/**
	 * Change all of the result data returned from this broadcasts; only works
	 * with broadcasts sent through
	 * {@link Context#sendOrderedBroadcast(Intent, String)
	 * Context.sendOrderedBroadcast}. All current result data is replaced by the
	 * value given to this method.
	 * 
	 * <p>
	 * <strong>This method does not work with non-ordered broadcasts such as
	 * those sent with {@link Context#sendBroadcast(Intent)
	 * Context.sendBroadcast}</strong>
	 * </p>
	 * 
	 * @param code
	 *            The new result code. Often uses the Activity
	 *            {@link android.app.Activity#RESULT_CANCELED} and
	 *            {@link android.app.Activity#RESULT_OK} constants, though the
	 *            actual meaning of this value is ultimately up to the
	 *            broadcaster.
	 * @param data
	 *            The new result data. This is an arbitrary string whose
	 *            interpretation is up to the broadcaster; may be null.
	 * @param extras
	 *            The new extra data map. This is a Bundle holding arbitrary
	 *            data, whose interpretation is up to the broadcaster. Can be
	 *            set to null. This completely replaces the current map (if
	 *            any).
	 */
	public final void setResult(int code, String data, Bundle extras) {
		checkSynchronousHint();
		mResultCode = code;
		mResultData = data;
		mResultExtras = extras;
	}

	/**
	 * Returns the flag indicating whether or not this receiver should abort the
	 * current broadcast.
	 * 
	 * @return True if the broadcast should be aborted.
	 */
	public final boolean getAbortBroadcast() {
		return mAbortBroadcast;
	}

	/**
	 * Sets the flag indicating that this receiver should abort the current
	 * broadcast; only works with broadcasts sent through
	 * {@link Context#sendOrderedBroadcast(Intent, String)
	 * Context.sendOrderedBroadcast}. This will prevent any other broadcast
	 * receivers from receiving the broadcast. It will still call
	 * {@link #onReceive} of the BroadcastReceiver that the caller of
	 * {@link Context#sendOrderedBroadcast(Intent, String)
	 * Context.sendOrderedBroadcast} passed in.
	 * 
	 * <p>
	 * <strong>This method does not work with non-ordered broadcasts such as
	 * those sent with {@link Context#sendBroadcast(Intent)
	 * Context.sendBroadcast}</strong>
	 * </p>
	 */
	public final void abortBroadcast() {
		checkSynchronousHint();
		mAbortBroadcast = true;
	}

	/**
	 * Clears the flag indicating that this receiver should abort the current
	 * broadcast.
	 */
	public final void clearAbortBroadcast() {
		mAbortBroadcast = false;
	}

	/**
	 * Returns true if the receiver is currently processing an ordered
	 * broadcast.
	 */
	public final boolean isOrderedBroadcast() {
		return mOrderedHint;
	}

	/**
	 * Returns true if the receiver is currently processing the initial value of
	 * a sticky broadcast -- that is, the value that was last broadcast and is
	 * currently held in the sticky cache, so this is not directly the result of
	 * a broadcast right now.
	 */
	public final boolean isInitialStickyBroadcast() {
		return mInitialStickyHint;
	}

	/**
	 * For internal use, sets the hint about whether this BroadcastReceiver is
	 * running in ordered mode.
	 */
	public final void setOrderedHint(boolean isOrdered) {
		mOrderedHint = isOrdered;
	}

	/**
	 * For internal use, sets the hint about whether this BroadcastReceiver is
	 * receiving the initial sticky broadcast value. @hide
	 */
	public final void setInitialStickyHint(boolean isInitialSticky) {
		mInitialStickyHint = isInitialSticky;
	}

	/**
	 * Control inclusion of debugging help for mismatched calls to @
	 * Context#registerReceiver(BroadcastReceiver, IntentFilter)
	 * Context.registerReceiver()} . If called with true, before given to
	 * registerReceiver(), then the callstack of the following
	 * {@link Context#unregisterReceiver(BroadcastReceiver)
	 * Context.unregisterReceiver()} call is retained, to be printed if a later
	 * incorrect unregister call is made. Note that doing this requires
	 * retaining information about the BroadcastReceiver for the lifetime of the
	 * app, resulting in a leak -- this should only be used for debugging.
	 */
	public final void setDebugUnregister(boolean debug) {
		mDebugUnregister = debug;
	}

	/**
	 * Return the last value given to {@link #setDebugUnregister}.
	 */
	public final boolean getDebugUnregister() {
		return mDebugUnregister;
	}

	/* package */void checkSynchronousHint() {
		// Note that we don't assert when receiving the initial sticky value,
		// since that may have come from an ordered broadcast. We'll catch
		// them later when the real broadcast happens again.
		if (mOrderedHint || mInitialStickyHint) {
			return;
		}
		RuntimeException e = new RuntimeException(
				"BroadcastReceiver trying to return result during a non-ordered broadcast");
		e.fillInStackTrace();
		Log.e("BroadcastReceiver" + e.getMessage());
	}

	/* package */void setFinished(boolean is) {
		mFinished = is;
	}

	/* package */boolean isFinished() {
		return mFinished;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BroadcastReceiver) {
			if (mId.equals(((BroadcastReceiver) obj).getId())) {
				return true;
			}
		}
		return false;
	}
}
