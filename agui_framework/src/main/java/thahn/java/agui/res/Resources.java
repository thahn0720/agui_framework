package thahn.java.agui.res;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import thahn.java.agui.Global;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.utils.DisplayMetrics;


// FIXME : this is falut. later, change to use reflection because do not use RMaker in real exe.
/**
 * this is fault. later, change to use reflection because do not use RMaker in real exe.
 * @author thAhn
 *
 */
public class Resources {
	public static final int											AGUI_RESOURCE			= 0;
	public static final int											PROJECT_RESOURCE		= 1;
	
	private static ResourcesContainer 								mCoreRes;
	private static ResourcesContainer 								mProjectRes;
	private static EnumResources									mEnumRes;
	
	Resources() {
		if(mCoreRes == null) mCoreRes = new ResourcesContainer();
		if(mProjectRes == null) mProjectRes = new ResourcesContainer();
		if(mEnumRes == null) mEnumRes = new EnumResources();
	}
	
	public DisplayMetrics getDisplayMetrics() {
		return new DisplayMetrics(); 
	}
	
	public String addIdValue(int which, int id, String value) {
		ResourcesContainer res = getResources(which);
		return res.addIdValue(id, value);
	}
	
	public String addLayoutValue(int which, int id, String value) {
		ResourcesContainer res = getResources(which);
		return res.addLayoutValue(id, value);
	}
	
	public String addDrawableValue(int which, int id, String value) {
		ResourcesContainer res = getResources(which);
		return res.addDrawableValue(id, value);
	}
	
	public String addStringValue(int which, int id, String value) {
		ResourcesContainer res = getResources(which);
		return res.addStringValue(id, value);
	}
	
	public Integer addDimensionValue(int which, int id, Integer value) {
		ResourcesContainer res = getResources(which);
		return res.addDimensionValue(id, value);
	}
	
	public Integer addIntegerValue(int which, int id, Integer value) {
		ResourcesContainer res = getResources(which);
		return res.addIntegerValue(id, value);
	}
	
	public Boolean addBooleanValue(int which, int id, Boolean value) {
		ResourcesContainer res = getResources(which);
		return res.addBooleanValue(id, value);
	}
	
	public String addAnimValue(int which, int id, String value) {
		ResourcesContainer res = getResources(which);
		return res.addAnimValue(id, value);
	}
	
	public ResourcesContainer getResources(int which) {
		ResourcesContainer res = null;
		switch (which) {
		case AGUI_RESOURCE:
			res = mCoreRes;
			break;
		case PROJECT_RESOURCE:
			res = mProjectRes;
			break;
		}
		return res;
	}
	
	public Drawable getDrawable(int drawableId) {
		Drawable ret = null;
		if(drawableId >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getDrawable(drawableId);
		} else {
			ret = mCoreRes.getDrawable(drawableId);
		}
		return ret;
	}
	
	public String getDrawablePath(int imgRes) {
		String path = null;
		if(imgRes >= RMaker.PROJECT_R_START_INDEX) {
			path = mProjectRes.getDrawablePath(imgRes);	
		} else {
			path = mCoreRes.getDrawablePath(imgRes);
		}
		return path;
	}
	
	public String getString(int id) {
		String ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getString(id);
		} else {
			ret = mCoreRes.getString(id);
		}
		return ret;
	}
	
	public String[] getStringArray(int id) {
		String[] ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getStringArray(id);
		} else {
			ret = mCoreRes.getStringArray(id);
		}
		return ret;
	}
	
	public String getLayout(int id) {
		String ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getLayout(id);
		} else {
			ret = mCoreRes.getLayout(id);
		}
		return ret;
	}
	
	public Integer getDimension(int id) {
		Integer ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getDimension(id);
		} else {
			ret = mCoreRes.getDimension(id);
		}
		return ret;
	}
	
	public Integer getInteger(int id) {
		Integer ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getInteger(id);
		} else {
			ret = mCoreRes.getInteger(id);
		}
		return ret;
	}
	
	public Boolean getBoolean(int id) {
		Boolean ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getBoolean(id);
		} else {
			ret = mCoreRes.getBoolean(id);
		}
		return ret;
	}
	
	public String getAnimation(int id) {
		String ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getAnimation(id);
		} else {
			ret = mCoreRes.getAnimation(id);
		}
		return ret;
	}
	
	public Integer getColor(int id) {
		Integer ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getColor(id);
		} else {
			ret = mCoreRes.getColor(id);
		}
		return ret;
	}
	
	public String getMenu(int id) {
		String ret = null;
		if(id >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getMenu(id);
		} else {
			ret = mCoreRes.getMenu(id);
		}
		return ret;
	}
	
	public int getIdentifier(String id, String type, String packageName) {
		int ret = -1;
		if(packageName.equals(Global.corePackageName)) {
			ret = mCoreRes.getIdentifier(id, type, packageName);
		} else {
			ret = mProjectRes.getIdentifier(id, type, packageName);
		}
		return ret;
	}
	
	/**
	 * 
	 * @param id "@style/theme" or "@agui:style/theme"
	 * @return
	 */
	public int[][] getStyles(String styleName) {
		int[][] ret = null;
		String[] temp = styleName.substring(1).split("/");
		if(temp[0].contains(Global.AGUI_NAMESPACE)) {
			// agui style
			temp[1] = temp[1].replace(".", "_");
			ret = mCoreRes.getStyles(temp[1], Global.corePackageName);
		} else {
			// custom style
			temp[1] = temp[1].replace(".", "_");
			ret = mProjectRes.getStyles(temp[1], Global.projectPackageName);
		}
		return ret;
	}
	
	public EnumResources getEnumResources() {
		return mEnumRes;
	}
	
	public void putEnum(Integer key, HashMap<Integer, String> value) {
		mEnumRes.put(key, value);
	}
	
	public String getEnum(Integer groupName, Integer valueName) {
		return mEnumRes.get(groupName, valueName);
	}
	
	public InputStream openRawResource(int rawid) throws FileNotFoundException {
		return new FileInputStream(openRawResourcePath(rawid));
	}
	
	public String openRawResourcePath(int rawid) {
		String ret = null;
		if(rawid >= RMaker.PROJECT_R_START_INDEX) {
			ret = mProjectRes.getRawResource(rawid);
		} else {
			ret = mCoreRes.getRawResource(rawid);
		}
		return ret;
	}
}	
