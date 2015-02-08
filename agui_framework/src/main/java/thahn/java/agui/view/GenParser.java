package thahn.java.agui.view;

import java.lang.reflect.Field;

import thahn.java.agui.Global;
import thahn.java.agui.res.RBase;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.MyUtils;

/**
 * 
 * @author thAhn
 *
 */
public class GenParser {
	
	public static final	String												GEN_ID 				= "id";
	public static final	String												GEN_STRING			= "string";
	public static final	String												GEN_ANIM 			= "anim";
	public static final	String												GEN_DRAWABLE 		= "drawable";
	public static final	String												GEN_LAYOUT 			= "layout";
	public static final	String												GEN_DIMEN			= "dimen";
	public static final	String												GEN_BOOL			= "bool";
	public static final	String												GEN_STYLE			= "style";
	public static final	String												GEN_COLOR			= "color";

	private static GenParser 												sGen; 
	public static GenParser getInstance() {
		if (sGen == null) {
			sGen = new GenParser();
		}
		return sGen;
	}
	
	private GenParser() {
		
	}
	
	public int findId(String packageName, String id) {
		int ret = find(id, packageName, GEN_ID);
		return ret;
	}
	
	public int findLayoutId(String packageName, String id) {
		int ret = find(id, packageName, GEN_LAYOUT);
		return ret;
	}
	
	public int findString(String packageName, String id) {
		int ret = find(id, packageName, GEN_STRING);
		return ret;
	}
	
	public int findAnimId(String packageName, String id) {
		int ret = find(id, packageName, GEN_ANIM);
		return ret;
	}
	
	public int findDrawable(String packageName, String id) {
		int ret = find(id, packageName, GEN_DRAWABLE);
		return ret;
	}
	
	public int findDimension(String packageName, String id) {
		int ret = find(id, packageName, GEN_DIMEN);
		return ret;
	}
	
	public int findBool(String packageName, String id) {
		int ret = find(id, packageName, GEN_BOOL);
		return ret;
	}
	
	public int[][] findStyle(String id, String packageName) {
		int[][] ret = null;
		try {
			Class<?> cls = null;
			if (packageName.equals(Global.corePackageName)) cls = Class.forName(packageName+".R$"+GenParser.GEN_STYLE);
			else cls = MyUtils.getProjectClass("R$"+GenParser.GEN_STYLE);
			
			Field f = cls.getField(id);
			ret = (int[][])f.get(new thahn.java.agui.R.style());//(int[][]) Array.newInstance(fieldCls, 2, Array.getLength(f.getDeclaringClass().newInstance()));
		} catch(ClassNotFoundException e) {
			Log.e("theme not exist : " + id);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public int findColor(String packageName, String id) {
		int ret = find(id, packageName, GEN_COLOR);
		return ret;
	}
	
	public int find(String id, String packageName, String type) {
		try {
			Class<?>[] clses = null;
			if (packageName.equals(Global.corePackageName)) {
				clses = Class.forName(packageName+".R").getClasses();
			} else if (packageName.equals(Global.projectPackageName)) {
				Class<?> temp = MyUtils.getProjectClass("R");
				if (temp != null) clses = temp.getClasses();
			}
			if (clses != null) {
				for (Class<?> cls : clses) {
					if (cls.getSimpleName().equals(type)) {
						try {
							String[] realId = id.split(RBase.RES_SEPARATOR);
							Field field = cls.getField(realId.length>1?realId[1]:realId[0]);
							return (int) field.get(cls);
						} catch (NoSuchFieldException e) {
							break;
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		return Global.NOT_YET;
	}
	
	/**
	 * 
	 * @param value @[agui:]type/res_name
	 * @return type
	 */
	public static String getWhichType(String value) {
		String ret = null;
		if (value.contains(RBase.RES_SEPARATOR)) {
			String[] splited1 = value.split(RBase.RES_SEPARATOR);
			if (splited1[0].startsWith(RBase.RES_PREFIX)) {
				if (splited1[0].contains(RBase.RES_NS_SEPARATOR)) {
					String[] splited2 = splited1[0].split(RBase.RES_NS_SEPARATOR);
					ret = splited2[1];
				} else {
					ret = splited1[0].substring(1);
				}
			}
		} 
		return ret;
	}
}
