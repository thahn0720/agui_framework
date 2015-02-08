package thahn.java.agui.view;

 import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import thahn.java.agui.Global;
import thahn.java.agui.app.Context;
import thahn.java.agui.exception.NotSupportedException;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.view.LayoutInflater.Factory2;
import thahn.java.agui.view.ViewGroup.LayoutParams;
import thahn.java.agui.widget.AdapterView;
import thahn.java.agui.widget.FrameLayout;

/**
 * 2012-04-07
 * @author thAhn
 *
 */
public class LayoutParser {

	private Stack<View> 													mParentStack;
	private Stack<String> 													mParentName;
	private Factory2 														mFactory2;
	
	/**
	 * @param xml
	 * @param context
	 */
	public LayoutParser() {
	}
	
    public void setFactory2(Factory2 factory) {
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        mFactory2 = factory;
    }
	
	public View getViewFromXML(Context context, String xml, View parent) {
		View ret = null;
		mParentStack = new Stack<View>();
		mParentName = new Stack<String>();
		
		parseFromSAX(context, xml);
		ret = mParentStack.pop();
		if (ret != null) {
			if (parent != null) {
				if (parent instanceof AdapterView) {
					ret.assignParent(parent);
				} else {
					((ViewGroup)parent).addViewInternal(ret);
				}
			}
			calcurateLocation(ret, 0, 0, context.getWidth(), context.getHeight());//Global.windowWidth, Global.windowHeight);
		}
		return ret;
	}
	
	private void parseFromSAX(Context context, String xml) {
		try {
			final Context contextF = context;
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {
				
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					AttributeSet attrs = new AttributeSet(contextF, attributes);
					parseAndAddView(contextF, qName, attrs);
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					if (!mParentName.isEmpty() && mParentName.peek() == qName) {//.hashCode()) {
						mParentName.pop();
						mParentStack.pop();
					}
				}

				public void characters(char ch[], int start, int length)
						throws SAXException {
					// only the value is "\n\t"
					// new String(ch, start, length)
				}
			};
			
			saxParser.parse(MyUtils.getResourceInputStream(xml), handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseAndAddView(Context context, String which, AttributeSet attrs) {
		boolean paramsNeeded = true;
		String name = which;
		View view = null;
		try {
			Constructor<?> cons = null;
			if (name.contains(".")) {
				Class<?> cls = null;
				try {
					cls = MyUtils.getClass(Global.corePath, name);//MyUtils.getProjectClass(name);
				} catch(Exception e) {
					cls = MyUtils.getClass(Global.projectPath, name);
				}
				cons =  cls.getConstructor(Context.class, AttributeSet.class);
				view = (View) cons.newInstance(context, attrs);
			} else {
				if ("include".equals(name)) {
					paramsNeeded = false;
					view = IncludeViewHelper.getView(context, attrs
							, mParentStack.isEmpty()?null:((ViewGroup) mParentStack.peek()).generateLayoutParams(attrs));
				} else if ("fragment".equals(name)) {
					throw new NotSupportedException("fragment is not supported in .xml");
				} else {
					Class<?> cls = Class.forName(new StringBuilder(Global.corePackageName).append(".widget.").append(name)
							.toString());
					cons = cls.getConstructor(Context.class, AttributeSet.class);
					view = (View) cons.newInstance(context, attrs);
				}
			}

			if (mFactory2 != null) {
				View createView = mFactory2.onCreateView(name, context, attrs);
				if (createView == null) {
					View parent = null;
					if (!mParentStack.isEmpty()) {
						parent = ((ViewGroup) mParentStack.peek());
					}
					createView = mFactory2.onCreateView(parent, name, context, attrs);
				}
				if (createView != null) {
					view = createView; 
				}
			}
			
			if (view instanceof ViewGroup) {
				addViewGroup(context, view, name, attrs, paramsNeeded);
			} else {
				addView(context, view, attrs, paramsNeeded);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Log.t("XML Error : view class not exist, maybe, spelling is wrong");
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.t("XML Error : project's custom view class not exist, maybe, spelling is wrong");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addViewGroup(Context context, View view, String name, AttributeSet attrSet, boolean paramsNeeded) {
		if (!mParentStack.isEmpty()) {
			if (paramsNeeded) generateLayoutParams(context, mParentName.peek(), view, attrSet);
			((ViewGroup)mParentStack.peek()).addViewInternal(view);
		} else {
			if (paramsNeeded) generateLayoutParams(context, View.class.getSimpleName(), view, attrSet);
			mParentStack.add(view);
		}
		mParentStack.push(view);
		mParentName.push(name);//which);
	}

	private void addView(Context context, View view, AttributeSet attrSet, boolean paramsNeeded) {
		if (!mParentStack.isEmpty()) {
			if (paramsNeeded) generateLayoutParams(context, mParentName.peek(), view, attrSet); // ((ViewGroup)mParentStack.peek()).generateDefaultLayoutParams();//
			((ViewGroup)mParentStack.peek()).addViewInternal(view);
		} else {
			if (paramsNeeded) generateLayoutParams(context, "View", view, attrSet);
			mParentStack.add(view);
		}
	}
	
	// TODO : don't use, instead use generateLayoutParams
	private void generateLayoutParams(Context context, String whichLayout, View view, AttributeSet attrSet) {
		ViewGroup.LayoutParams params = null;
		try {
			Class<?> cls = null;
			if (whichLayout.contains(".")) {
				try {
					cls = MyUtils.getClass(Global.corePath, whichLayout+"$"+LayoutParams.class.getSimpleName()); // "$LayoutParams"
				} catch(Exception e) {
					cls = MyUtils.getClass(Global.projectPath, whichLayout+"$"+LayoutParams.class.getSimpleName()); // "$LayoutParams"
				}
			} else {
				cls = Class.forName(new StringBuilder(Global.corePackageName).append(".widget.").append(whichLayout)
						.append("$").append(LayoutParams.class.getSimpleName()).toString());
			}
			params = (ViewGroup.LayoutParams) cls.getConstructor(Context.class, AttributeSet.class).newInstance(context, attrSet);
		} catch (ClassNotFoundException e) {
			params = new ViewGroup.LayoutParams(context, attrSet);
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.t("project's custom LayoutParams class not exist, maybe, spelling is wrong");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (params != null) {
			view.setLayoutParams(params);
		} else {
			Log.t("creating LayoutParams is failed.");
		}
	}

	public static ViewGroup makeDecorView(Context context, View view) {
		FrameLayout root = new FrameLayout(context);
		root.setPaddingLeft(2);
		root.setPaddingTop(2);
		root.setPaddingRight(2);
		root.setPaddingBottom(2);
		root.setId(Global.ROOT_ID);
		ViewGroup.LayoutParams params = new LayoutParams(context.getWidth(), context.getHeight());
		root.setLayoutParams(params); 
//		root.addView(view);
//		LayoutParser.calcurateLocation(root, 0, 0, context.getWidth(), context.getHeight());
		LayoutParser.calcurateLocation(root, 0, 0, context.getWidth(), context.getHeight());
		root.addView(view);
		return root;
	}
	
	public static void calcurateLocation(View parent, int startX, int startY, int parentWidth, int parentHeight) {
		// padding은 내부적으로 getDrawingTop 등 에서 계산하기에 getTop의 값을 정하는 여기서는 추가하지 않는다
		ViewGroup.LayoutParams parentLayout = parent.getLayoutParams();
		int left = startX + parentLayout.leftMargin;// + parent.getPaddingLeft();
		int top = startY + parentLayout.topMargin;// + parent.getPaddingTop();
		int right = startX + parentWidth - parentLayout.rightMargin;// - parent.getPaddingRight();
		int bottom = startY + parentHeight - parentLayout.bottomMargin;// - parent.getPaddingBottom();

		int width = right - left;
		int height = bottom - top;
		
		parent.onMeasure(width, height);
		parent.onPostMeasure(width, height);
		parent.onLayout(false, left, top, left + parent.getWidth(), top + parent.getHeight());
//		parentLayout.setLayout(left, top, left + parent.getWidth(), top + parent.getHeight());
		parent.arrange();
	}
}

