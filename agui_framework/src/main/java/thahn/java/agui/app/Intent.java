package thahn.java.agui.app;

import java.io.Serializable;
import java.util.ArrayList;

import thahn.java.agui.utils.ArraySet;
import thahn.java.agui.utils.MyUtils;



public class Intent implements Serializable {
	
	private static final long 															serialVersionUID = 4518484561101935336L;
	// extra
	public static final String															EXTRA_WIDTH				 		= "width";
	public static final String															EXTRA_HEIGHT				 	= "height";
	/** put @see thahn.java.agui.app.Position */
	public static final String															EXTRA_POSITION_RELATIVE_SCREEN	= "relativePositionS";
	/** put @see thahn.java.agui.app.Position */
	public static final String															EXTRA_POSITION_RELATIVE_WINDOW_HORIZONTAL	= "relativePositionWH";
	/** put @see thahn.java.agui.app.Position */
	public static final String															EXTRA_POSITION_RELATIVE_WINDOW_VERTICAL		= "relativePositionWV";
	/** put @see thahn.java.agui.app.Activity */
	public static final String															EXTRA_POSITION_RELATIVE_WINDOW_INSTANCE		= "relativePositionWinIns";
	/** put int */
	public static final String															EXTRA_POSITION_X		 		= "positionX";
	/** put int */
	public static final String															EXTRA_POSITION_Y			 	= "positionY";
	//
	/**
     * Activity Action: Perform a web search.
     * <p>
     * Input: {@link android.app.SearchManager#QUERY
     * getStringExtra(SearchManager.QUERY)} is the text to search for. If it is
     * a url starts with http or https, the site will be opened. If it is plain
     * text, Google search will be applied.
     * <p>
     * Output: nothing.
     */
    public static final String 															ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
	public static final String															ACTION_MAIN 					= "agui.intent.action.MAIN";
	public static final String															CATEGORY_LAUNCHER 				= "agui.intent.category.LAUNCHER";
	/**
     * To be used as a sample code example (not part of the normal user
     * experience).
     */
    public static final String 															CATEGORY_SAMPLE_CODE 			= "agui.intent.category.SAMPLE_CODE";
	
	public static final int																FLAG_ACTIVITY_NEW_TASK 			= 0;
	public static final int																FLAG_ACTIVITY_CURRENT_TASK		= 1;

	/** {@link Context#startActivity(String, Intent)} for window's name */
	private String																		mName;
	private int																			mFlags;
	private Bundle																		mExtras;
	private ComponentName																mComponent;
	private String 																		mAction;
	private ArraySet<String> 															mCategories;
	
	public Intent() {
		init();
	}
	
	public Intent(String action) {
		setAction(action);
		init();
	}
	
	public Intent(Context context, Class<?> targetClass) {
		mComponent = new ComponentName(context, targetClass);
		init();
	}
	
    public Intent(Intent o) {
    	this.mName = o.mName;
        this.mAction = o.mAction;
        this.mComponent = o.mComponent;
        this.mFlags = o.mFlags;
        if (o.mCategories != null) {
            this.mCategories = new ArraySet<String>(o.mCategories);
        }
        if (o.mExtras != null) {
            this.mExtras = new Bundle(o.mExtras);
        } else {
        	this.mExtras = new Bundle();
        }
    }
	
	private void init() {
		mExtras = new Bundle();
		mFlags = FLAG_ACTIVITY_NEW_TASK;
        mCategories = new ArraySet<String>();
	}
	
	Activity createActivity() throws Exception {
		return create();
	}
	
	Service createService() throws Exception {
		return create();
	}
	
	private <T> T create() throws Exception {
		@SuppressWarnings("unchecked")
		T a = (T) MyUtils.getProjectClass(mComponent.getClassName()).getDeclaredConstructor().newInstance();
		return a;
	}
	
	public void setAction(String action) {
		mAction = action;
	}
	
	public String getAction() {
		return mAction;
	}
	
	public void setFlag(int option) {
		mFlags = option;
	}
	
	public int getFlags() {
		return mFlags;
	}
	
	public Bundle getExtras() {
		return mExtras;
	}
	
    /**
     * Convenience for calling {@link #setComponent} with an
     * explicit class name.
     *
     * @param packageContext A Context of the application package implementing
     * this class.
     * @param className The name of a class inside of the application package
     * that will be used as the component for this Intent.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #setComponent
     * @see #setClass
     */
    public Intent setClassName(Context packageContext, String className) {
        mComponent = new ComponentName(packageContext, className);
        return this;
    }

    /**
     * Convenience for calling {@link #setComponent} with an
     * explicit application package name and class name.
     *
     * @param packageName The name of the package implementing the desired
     * component.
     * @param className The name of a class inside of the application package
     * that will be used as the component for this Intent.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #setComponent
     * @see #setClass
     */
    public Intent setClassName(String packageName, String className) {
        mComponent = new ComponentName(packageName, className);
        return this;
    }

    /**
     * Convenience for calling {@link #setComponent(ComponentName)} with the
     * name returned by a {@link Class} object.
     *
     * @param packageContext A Context of the application package implementing
     * this class.
     * @param cls The class name to set, equivalent to
     *            <code>setClassName(context, cls.getName())</code>.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.\
     *
     * @see #setComponent
     */
    public Intent setClass(Context packageContext, Class<?> cls) {
        mComponent = new ComponentName(packageContext, cls);
        return this;
    }
	
	public void setComponent(ComponentName component) {
		mComponent = component;
	}
	
	public ComponentName getComponent() {
		return mComponent;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}
	
    /**
     * Add a new category to the intent.  Categories provide additional detail
     * about the action the intent performs.  When resolving an intent, only
     * activities that provide <em>all</em> of the requested categories will be
     * used.
     *
     * @param category The desired category.  This can be either one of the
     *               predefined Intent categories, or a custom category in your own
     *               namespace.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #hasCategory
     * @see #removeCategory
     */
    public Intent addCategory(String category) {
        if (mCategories == null) {
            mCategories = new ArraySet<String>();
        }
        mCategories.add(category.intern());
        return this;
    }
	
	public void putIntExtra(String name, int defaultValue) {
		if (mExtras == null) {
			mExtras = new Bundle();
		}
        mExtras.putInt(name, defaultValue); 
	}
	
	public void putObjectExtra(String name, Object defaultValue) {
		if (mExtras == null) {
			mExtras = new Bundle();
		}
		mExtras.putObject(name, defaultValue); 
	}
	
	public int getIntExtra(String name, int defaultValue) {
		return mExtras == null ? defaultValue :
			mExtras.getInt(name, defaultValue); 
	}
	
	public Object getObjectExtra(String name, Object defaultValue) {
		return mExtras == null ? defaultValue :
			mExtras.getObject(name, defaultValue); 
	}
	
    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no String value was found.
     *
     * @see #putExtra(String, String)
     */
    public String getStringExtra(String name) {
        return mExtras == null ? null : mExtras.getString(name);
    }
	
	/**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The boolean data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanExtra(String, boolean)
     */
    public Intent putExtra(String name, boolean value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBoolean(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The byte data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteExtra(String, byte)
     */
    public Intent putExtra(String name, byte value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putByte(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The char data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharExtra(String, char)
     */
    public Intent putExtra(String name, char value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putChar(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The short data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortExtra(String, short)
     */
    public Intent putExtra(String name, short value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putShort(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The integer data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntExtra(String, int)
     */
    public Intent putExtra(String name, int value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putInt(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The long data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongExtra(String, long)
     */
    public Intent putExtra(String name, long value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putLong(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The float data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatExtra(String, float)
     */
    public Intent putExtra(String name, float value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putFloat(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The double data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleExtra(String, double)
     */
    public Intent putExtra(String name, double value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putDouble(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The String data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringExtra(String)
     */
    public Intent putExtra(String name, String value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putString(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The CharSequence data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceExtra(String)
     */
    public Intent putExtra(String name, CharSequence value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharSequence(name, value);
        return this;
    }

//    /**
//     * Add extended data to the intent.  The name must include a package
//     * prefix, for example the app com.android.contacts would use names
//     * like "com.android.contacts.ShowAll".
//     *
//     * @param name The name of the extra data, with package prefix.
//     * @param value The Parcelable data value.
//     *
//     * @return Returns the same Intent object, for chaining multiple calls
//     * into a single statement.
//     *
//     * @see #putExtras
//     * @see #removeExtra
//     * @see #getParcelableExtra(String)
//     */
//    public Intent putExtra(String name, Parcelable value) {
//        if (mExtras == null) {
//            mExtras = new Bundle();
//        }
//        mExtras.putParcelable(name, value);
//        return this;
//    }

//    /**
//     * Add extended data to the intent.  The name must include a package
//     * prefix, for example the app com.android.contacts would use names
//     * like "com.android.contacts.ShowAll".
//     *
//     * @param name The name of the extra data, with package prefix.
//     * @param value The Parcelable[] data value.
//     *
//     * @return Returns the same Intent object, for chaining multiple calls
//     * into a single statement.
//     *
//     * @see #putExtras
//     * @see #removeExtra
//     * @see #getParcelableArrayExtra(String)
//     */
//    public Intent putExtra(String name, Parcelable[] value) {
//        if (mExtras == null) {
//            mExtras = new Bundle();
//        }
//        mExtras.putParcelableArray(name, value);
//        return this;
//    }

//    /**
//     * Add extended data to the intent.  The name must include a package
//     * prefix, for example the app com.android.contacts would use names
//     * like "com.android.contacts.ShowAll".
//     *
//     * @param name The name of the extra data, with package prefix.
//     * @param value The ArrayList<Parcelable> data value.
//     *
//     * @return Returns the same Intent object, for chaining multiple calls
//     * into a single statement.
//     *
//     * @see #putExtras
//     * @see #removeExtra
//     * @see #getParcelableArrayListExtra(String)
//     */
//    public Intent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
//        if (mExtras == null) {
//            mExtras = new Bundle();
//        }
//        mExtras.putParcelableArrayList(name, value);
//        return this;
//    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The ArrayList<Integer> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntegerArrayListExtra(String)
     */
    public Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putIntegerArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The ArrayList<String> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayListExtra(String)
     */
    public Intent putStringArrayListExtra(String name, ArrayList<String> value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putStringArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The ArrayList<CharSequence> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayListExtra(String)
     */
    public Intent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharSequenceArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The Serializable data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getSerializableExtra(String)
     */
    public Intent putExtra(String name, Serializable value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putSerializable(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The boolean array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanArrayExtra(String)
     */
    public Intent putExtra(String name, boolean[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBooleanArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The byte array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteArrayExtra(String)
     */
    public Intent putExtra(String name, byte[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putByteArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The short array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortArrayExtra(String)
     */
    public Intent putExtra(String name, short[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putShortArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The char array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharArrayExtra(String)
     */
    public Intent putExtra(String name, char[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The int array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntArrayExtra(String)
     */
    public Intent putExtra(String name, int[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putIntArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The byte array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongArrayExtra(String)
     */
    public Intent putExtra(String name, long[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putLongArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The float array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatArrayExtra(String)
     */
    public Intent putExtra(String name, float[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putFloatArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The double array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleArrayExtra(String)
     */
    public Intent putExtra(String name, double[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putDoubleArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The String array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayExtra(String)
     */
    public Intent putExtra(String name, String[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putStringArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The CharSequence array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayExtra(String)
     */
    public Intent putExtra(String name, CharSequence[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharSequenceArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The Bundle data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBundleExtra(String)
     */
    public Intent putExtra(String name, Bundle value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBundle(name, value);
        return this;
    }

//    /**
//     * Add extended data to the intent.  The name must include a package
//     * prefix, for example the app com.android.contacts would use names
//     * like "com.android.contacts.ShowAll".
//     *
//     * @param name The name of the extra data, with package prefix.
//     * @param value The IBinder data value.
//     *
//     * @return Returns the same Intent object, for chaining multiple calls
//     * into a single statement.
//     *
//     * @see #putExtras
//     * @see #removeExtra
//     * @see #getIBinderExtra(String)
//     *
//     * @deprecated
//     * @hide
//     */
//    @Deprecated
//    public Intent putExtra(String name, IBinder value) {
//        if (mExtras == null) {
//            mExtras = new Bundle();
//        }
//        mExtras.putIBinder(name, value);
//        return this;
//    }

    /**
     * Copy all extras in 'src' in to this intent.
     *
     * @param src Contains the extras to copy.
     *
     * @see #putExtra
     */
    public Intent putExtras(Intent src) {
        if (src.mExtras != null) {
            if (mExtras == null) {
                mExtras = new Bundle(src.mExtras);
            } else {
                mExtras.putAll(src.mExtras);
            }
        }
        return this;
    }

    /**
     * Add a set of extended data to the intent.  The keys must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param extras The Bundle of extras to add to this intent.
     *
     * @see #putExtra
     * @see #removeExtra
     */
    public Intent putExtras(Bundle extras) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putAll(extras);
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(128);

        b.append("Intent { ");
        toShortString(b, true, true, true, false);
        b.append(" }");

        return b.toString();
    }

    /** @hide */
    public String toInsecureString() {
        StringBuilder b = new StringBuilder(128);

        b.append("Intent { ");
        toShortString(b, false, true, true, false);
        b.append(" }");

        return b.toString();
    }

    /** @hide */
    public String toInsecureStringWithClip() {
        StringBuilder b = new StringBuilder(128);

        b.append("Intent { ");
        toShortString(b, false, true, true, true);
        b.append(" }");

        return b.toString();
    }

    /** @hide */
    public String toShortString(boolean secure, boolean comp, boolean extras, boolean clip) {
        StringBuilder b = new StringBuilder(128);
        toShortString(b, secure, comp, extras, clip);
        return b.toString();
    }

    /** @hide */
    public void toShortString(StringBuilder b, boolean secure, boolean comp, boolean extras,
            boolean clip) {
        boolean first = true;
        if (mAction != null) {
            b.append("act=").append(mAction);
            first = false;
        }
        if (mCategories != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("cat=[");
            for (int i=0; i<mCategories.size(); i++) {
                if (i > 0) b.append(',');
                b.append(mCategories.valueAt(i));
            }
            b.append("]");
        }
        if (mFlags != 0) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("flg=0x").append(Integer.toHexString(mFlags));
        }
        if (comp && mComponent != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("cmp=").append(mComponent.flattenToShortString());
        }
        if (extras && mExtras != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("(has extras)");
        }
    }
}

