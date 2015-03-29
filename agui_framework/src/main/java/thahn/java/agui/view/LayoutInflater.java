package thahn.java.agui.view;

import thahn.java.agui.app.Context;
import thahn.java.agui.app.Context;
import thahn.java.agui.res.ResourcesManager;

import com.google.common.base.Preconditions;


public class LayoutInflater {
//	static
	private LayoutParser										mLayoutParser;
	private Context												mContext;
	private Factory2											mFactory2;
	
    public interface Factory {
        /**
         * Hook you can supply that is called when inflating from a LayoutInflater.
         * You can use this to customize the tag names available in your XML
         * layout files.
         * 
         * <p>
         * Note that it is good practice to prefix these custom names with your
         * package (i.e., com.coolcompany.apps) to avoid conflicts with system
         * names.
         * 
         * @param name Tag name to be inflated.
         * @param context The context the view is being created in.
         * @param attrs Inflation attributes as specified in XML file.
         * 
         * @return View Newly created view. Return null for the default
         *         behavior.
         */
        public View onCreateView(String name, Context context, AttributeSet attrs);
    }

    public interface Factory2 extends Factory {
        /**
         * Version of {@link #onCreateView(String, Context, AttributeSet)}
         * that also supplies the parent that the view created view will be
         * placed in.
         *
         * @param parent The parent that the created view will be placed
         * in; <em>note that this may be null</em>.
         * @param name Tag name to be inflated.
         * @param context The context the view is being created in.
         * @param attrs Inflation attributes as specified in XML file.
         *
         * @return View Newly created view. Return null for the default
         *         behavior.
         */
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs);
    }
	
	private LayoutInflater(Context context) {
//		if(!(context instanceof Activity) && !(context instanceof TitleContext)) {
//			throw new WrongFormatException("shoulb be activity context.");
//		}
		mContext = context;
	}

	public static LayoutInflater from(Context context) {
		return new LayoutInflater(context);
	}
	
    public void setFactory2(Factory2 factory) {
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        mFactory2 = factory;
    }

//    /**
//     * @hide for use by framework
//     */
//    public void setPrivateFactory(Factory2 factory) {
//        mPrivateFactory = factory;
//    }
	
	public View inflate(int resource, ViewGroup parent) {
		return inflate(resource, parent, parent != null);
	}
	
    /**
     * Inflate a new view hierarchy from the specified xml resource. Throws
     * {@link InflateException} if there is an error.
     * 
     * @param resource ID for an XML layout resource to load (e.g.,
     *        <code>R.layout.main_page</code>)
     * @param root Optional view to be the parent of the generated hierarchy (if
     *        <em>attachToRoot</em> is true), or else simply an object that
     *        provides a set of LayoutParams values for root of the returned
     *        hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *        the root parameter? If false, root is only used to create the
     *        correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     *         attachToRoot is true, this is root; otherwise it is the root of
     *         the inflated XML file.
     */
    public synchronized View inflate(int resource, ViewGroup root, boolean attachToRoot) {
    	if(mLayoutParser == null) {
			mLayoutParser = new LayoutParser();
		}
    	if(mFactory2 != null) {
    		mLayoutParser.setFactory2(mFactory2);
    	}
		String layoutPath = ResourcesManager.getInstance().getLayout(resource);
		Preconditions.checkNotNull(layoutPath, "layoutPath is wrong");
		return mLayoutParser.getViewFromXML(mContext, layoutPath, root);
    }
	
	public static View inflate(Context context, int layoutId, ViewGroup parent) {
		return from(context).inflate(layoutId, parent);
	}
}
