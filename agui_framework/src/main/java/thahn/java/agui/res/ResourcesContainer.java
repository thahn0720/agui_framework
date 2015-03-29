package thahn.java.agui.res;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import thahn.java.agui.Global;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.graphics.Color;
import thahn.java.agui.graphics.ColorDrawable;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.graphics.ImageDrawable;
import thahn.java.agui.graphics.MultipleDrawable;
import thahn.java.agui.graphics.NinePatchDrawable;
import thahn.java.agui.utils.AguiUtils;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.view.GenParser;

import com.android.ninepatch.NinePatch;


public class ResourcesContainer {
	public static final String										GEN_PATH			= "./gen/";
	
	public HashMap<Integer, String> 								mId;
	public HashMap<Integer, String> 								mLayout;
	public HashMap<Integer, String> 								mDrawable;
	public HashMap<Integer, String>									mString;
	public HashMap<Integer, String[]>								mStringArray;
	public HashMap<Integer, Integer>								mDimension;
	public HashMap<Integer, Integer>								mInteger;
	public HashMap<Integer, Integer[]>								mIntArray;
	public HashMap<Integer, Float>									mFloat;
	public HashMap<Integer, Boolean>								mBoolean;
	public HashMap<Integer, Integer>								mColor;
	public HashMap<Integer, String>									mAnim;
	public HashMap<Integer, String>									mMenu;
	public HashMap<Integer, String>									mRaw;

	ResourcesContainer() {
		mId = new HashMap<>();
		mLayout = new HashMap<>();
		mDrawable = new HashMap<>(); 
		mString = new HashMap<>();
		mStringArray = new HashMap<>();
		mDimension = new HashMap<>();
		mInteger = new HashMap<>();
		mIntArray = new HashMap<>();
		mFloat = new HashMap<>();
		mBoolean = new HashMap<>();
		mColor = new HashMap<>();
		mAnim = new HashMap<>();
		mMenu = new HashMap<>();
		mRaw = new HashMap<>();
	}
	
	public String addIdValue(int id, String value) {
		return mId.put(id, value);
	}
	
	public String addLayoutValue(int id, String value) {
		return mLayout.put(id, value);
	}
	
	public String addDrawableValue(int id, String value) {
		return mDrawable.put(id, value);
	}
	
	public String addStringValue(int id, String value) {
		return mString.put(id, value);
	}
	
	public String[] addStringArrayValue(int id, String[] value) {
		return mStringArray.put(id, value);
	}
	
	public Integer addDimensionValue(int id, Integer value) {
		return mDimension.put(id, value);
	}
	
	public Integer addIntegerValue(int id, Integer value) {
		return mInteger.put(id, value);
	}
	
	public Boolean addBooleanValue(int id, Boolean value) {
		return mBoolean.put(id, value);
	}
	
	public Integer addColorValue(int id, int value) {
		return mColor.put(id, value);
	}
	
	public String addAnimValue(int id, String value) {
		return mAnim.put(id, value);
	}
	
	public String addMenuValue(int id, String value) {
		return mMenu.put(id, value);
	}
	
	public String addRawValue(int id, String value) {
		return mRaw.put(id, value);
	}
	
	public boolean containsId(String value) {
		return  mId.containsValue(value);
	}
	
	public boolean containsLayout(String value) {
		return  mLayout.containsValue(value);
	}
	
	public boolean containsDrawable(String value) {
		return  mDrawable.containsValue(value);
	}
	
	public boolean containsValue(String value) {
		return  mString.containsValue(value);
	}
	
	public boolean containsAnim(String value) {
		return  mAnim.containsValue(value);
	}
	
	public Drawable getDrawable(int drawableId) {
		Drawable drawable = null;
		String absolutePath = mDrawable.get(drawableId);
		if(absolutePath != null) {
			String[] temp = absolutePath.split("/");
			String path = temp[temp.length-1];
			if(path.contains(NinePatch.EXTENSION_9PATCH)) {
				drawable = NinePatchDrawable.load(absolutePath);//+NinePatch.EXTENSION_9PATCH); 
			} else if(path.toLowerCase().endsWith(".xml")) {
				drawable = getDrawableFromXML(drawableId>=RMaker.PROJECT_R_START_INDEX?Global.projectPackageName:Global.corePackageName, 
											absolutePath);
			} else {
				drawable = ImageDrawable.load(absolutePath);
			}
		}
		return drawable;
	}
	
	private Drawable getDrawableFromXML(String packageName, String path) {
		MultipleDrawable drawable = null;
		try {
			BufferedInputStream bi = new BufferedInputStream(MyUtils.getResourceInputStream(path));//in);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(bi);
			Element root = doc.getRootElement();
			List<Element> elements = (List<Element>) root.getChildren();
			drawable = MultipleDrawable.load();
			for(Element e : elements) {
				String dValue = e.getAttributeValue(GenParser.GEN_DRAWABLE);
				List<Integer> states = new ArrayList<>();
				for(Attribute attr : (List<Attribute>) e.getAttributes()) {
					if(GenParser.GEN_DRAWABLE.equals(attr.getName())) {
						dValue = attr.getValue();
					} else {
						states.add((attr.getName() + "_" + attr.getValue()).hashCode());
					}
				}
				
				Drawable d = null;
				String type = GenParser.getWhichType(dValue);
				int id = ResourcesManager.getInstance().getIdentifier(dValue, type, AguiUtils.getPackageNameByNS(dValue));
				if (type.equals(GenParser.GEN_DRAWABLE)) {
					d = getDrawable(GenParser.getInstance().findDrawable(packageName, dValue));
				} else if (type.equals(GenParser.GEN_COLOR)) {
					d = ColorDrawable.load(getColor(GenParser.getInstance().findColor(packageName, dValue)));
				} else {
					throw new WrongFormatException("");
				}
				
				if(states.size() != 0) {
					for(int state : states) {
						drawable.add(state, d);
					}
				} else {
					drawable.add(MultipleDrawable.STATE_NONE, d);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return drawable;
	}
	
	public String getDrawablePath(int drawableId) {
		return mDrawable.get(drawableId);
	}
	
	public String getString(int id) {
		return mString.get(id);
	}
	
	public String[] getStringArray(int id) {
		return mStringArray.get(id);
	}
	
	public String getLayout(int id) {
		return mLayout.get(id);
	}
	
	public Integer getDimension(int id) {
		return mDimension.get(id);
	}
	
	public Integer getInteger(int id) {
		return mInteger.get(id);
	}
	
	public Boolean getBoolean(int id) {
		return mBoolean.get(id);
	}
	
	public Integer getColor(int id) {
		return mColor.get(id);
	}
	
	public String getAnimation(int id) {
		return mAnim.get(id);
	}
	
	public String getMenu(int id) {
		return mMenu.get(id);
	}
	
	public String getRawResource(int id) {
		return mRaw.get(id);
	}
	
	/**
	 * 
	 * @param id
	 * @param type drawble, id, layout
	 * @param packageName
	 * @return
	 */
	public int getIdentifier(String id, String type, String packageName) {
		// view1 
		int ret = GenParser.getInstance().find(id, packageName, type);
		return ret;
	}
	
	public int[][] getStyles(String name, String packageName) {
		return GenParser.getInstance().findStyle(name, packageName);
	}
	
	/**
     * This exception is thrown by the resource APIs when a requested resource
     * can not be found.
     */
    public static class NotFoundException extends RuntimeException {
        public NotFoundException() {
        }

        public NotFoundException(String name) {
            super(name);
        }
    }
}
