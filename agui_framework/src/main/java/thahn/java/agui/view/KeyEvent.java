package thahn.java.agui.view;


/**
 * use KeyEvent's key code
 * @author thAhn
 * @see KeyEvent 
 */
public class KeyEvent {
	
	/**
     * {@link #getAction} value: the key has been pressed down.
     */
    public static final int 										ACTION_DOWN             = 0;
    /**
     * {@link #getAction} value: the key has been released.
     */
    public static final int 										ACTION_UP               = 1;
    /**
     * {@link #getAction} value: multiple duplicate key events have
     * occurred in a row, or a complex string is being delivered.  If the
     * key code is not {#link {@link #KEYCODE_UNKNOWN} then the
     * {#link {@link #getRepeatCount()} method returns the number of times
     * the given key code should be executed.
     * Otherwise, if the key code is {@link #KEYCODE_UNKNOWN}, then
     * this is a sequence of characters as returned by {@link #getCharacters}.
     */
    public static final int 										ACTION_MULTIPLE         = 2;
    /** Key code constant: Directional Pad Up key.
     * May also be synthesized from trackball motions. */
    public static final int 										KEYCODE_DPAD_UP         = 19;
    /** Key code constant: Directional Pad Down key.
     * May also be synthesized from trackball motions. */
    public static final int 										KEYCODE_DPAD_DOWN       = 20;
    /** Key code constant: Directional Pad Left key.
     * May also be synthesized from trackball motions. */
    public static final int 										KEYCODE_DPAD_LEFT       = 21;
    /** Key code constant: Directional Pad Right key.
     * May also be synthesized from trackball motions. */
    public static final int 										KEYCODE_DPAD_RIGHT      = 22;
    /** Key code constant: Directional Pad Center key.
     * May also be synthesized from trackball motions. */
    public static final int 										KEYCODE_DPAD_CENTER     = 23;
    /** Key code constant: Tab key. */
    public static final int 										KEYCODE_TAB             = java.awt.event.KeyEvent.VK_TAB;//61;
    
	public static final int											ACTION_KEY_PRESSED		= 10;
	public static final int											ACTION_KEY_RELEASED		= 11;
	
	java.awt.event.KeyEvent											mKeyEvent;
	int																mAction;
	public KeyEvent(int action, java.awt.event.KeyEvent mKeyEvent) {
		mAction = action;
		this.mKeyEvent = mKeyEvent;
	}

	public int getAction() {
		return mAction;
	}
	
	public int getKeyCode() {
		return mKeyEvent.getKeyCode();
	}

	public char getKeyChar() {
		return mKeyEvent.getKeyChar();
	}

	public String getKetText(int keyCode) {
		return java.awt.event.KeyEvent.getKeyText(keyCode);
	}
}
