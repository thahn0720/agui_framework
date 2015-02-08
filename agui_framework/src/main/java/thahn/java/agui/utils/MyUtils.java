package thahn.java.agui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.Global;
import thahn.java.agui.res.ResourcesManager;

/**
 * 
 * @author thAhn
 *
 */
public class MyUtils {
	
	public static String[] newStringArray(Object... values) {
		List<String> ret = new ArrayList<>();
		for (Object value : values) {
			if (value instanceof Object[]) {
				Object[] objs = (Object[]) value;
				for (Object obj : objs) {
					ret.add((String) obj);
				}
			} else {
				ret.add((String) value);
			}
		}
		return ret.toArray(new String[ret.size()]);
	}
	
	public static int booleanToInt(boolean is) {
		return is?1:0;
	}
	
	public static boolean isCoreNS(String name) {
		boolean ret = false;
		if(name.startsWith("@")) {
			String[] temp = name.substring(1).split("/");
			if(temp[0].contains(Global.AGUI_NAMESPACE)) {
				ret = true;
			}
		} 
		return ret;
	}
	
	/**
	 * use '$' for inner class. <br>
	 * ex. R$attr<br>
	 * @param classPath class path based on default package name
	 * @return
	 */
	public static Class<?> getProjectClass(String classPath) throws MalformedURLException, ClassNotFoundException{
		if(Global.projectPath != null) {
			URL classUrl = new File(Global.projectPath+"/bin/").toURL();
			URL[] classUrls = {classUrl};
			URLClassLoader ucl = new URLClassLoader(classUrls);
			return ucl.loadClass(makeFullPackageName(classPath));
		} 
		return null;
	}
	
	public static String makeFullPackageName(String attrValue) {
		StringBuilder builder = new StringBuilder();
		if(attrValue.startsWith(".")) { // .activity
			builder.append(Global.projectPackageName).append(attrValue);
		} else if(!attrValue.startsWith(Global.projectPackageName)) { // activity
			builder.append(Global.projectPackageName).append(".").append(attrValue);
		} else {
			builder.append(attrValue);
		}
		return builder.toString();
	}
	
	/**
	 * 
	 * @param classPath full path based on full package name
	 * @return
	 */
	public static Class<?> getClass(String path, String fullPacakgeName) throws Exception {
		URL classUrl = new File(path+"/bin/").toURL();
		URL[] classUrls = {classUrl};
		URLClassLoader ucl = new URLClassLoader(classUrls);
		return ucl.loadClass(fullPacakgeName);
	}
	
	public static String getResourcePath(String which, Class<?> indicatorClass) {
		try {
		    //Attempt to get the path of the actual JAR file, because the working directory is frequently not where the file is.
		    //Example: file:/D:/all/Java/TitanWaterworks/TitanWaterworks-en.jar!/TitanWaterworks.class
		    //Another example: /D:/all/Java/TitanWaterworks/TitanWaterworks.class
		    String filePath = indicatorClass.getResource(which).getPath();
		    //Find the last ! and cut it off at that location. If this isn't being run from a jar, there is no !, so it'll cause an exception, which is fine.
		    try { filePath = filePath.substring(0, filePath.lastIndexOf('!')); } catch (Exception e) { }
		    //Find the last / and cut it off at that location.
		    //If it starts with /, cut it off.
		    if (filePath.startsWith("/")) filePath = filePath.substring(1, filePath.length());
		    //If it starts with file:/, cut that off, too.
		    if (filePath.startsWith("file:/")) filePath = filePath.substring(6, filePath.length());
		    
		    return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getClassPath(Class<?> indicatorClass) {
		try {
		    String filePath = indicatorClass.getProtectionDomain().getCodeSource().getLocation().getPath();
		    //Find the last ! and cut it off at that location. If this isn't being run from a jar, there is no !, so it'll cause an exception, which is fine.
		    try { filePath = filePath.substring(0, filePath.lastIndexOf('!')); } catch (Exception e) { }
		    if (filePath.startsWith("/")) filePath = filePath.substring(1, filePath.length());
		    if (filePath.startsWith("file:/")) filePath = filePath.substring(6, filePath.length());
		    
		    return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ex : d:/abc.jar/res/drawable/img.png
	 * @param path
	 * @return
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException 
	 */
	public static InputStream getResourceInputStream(String path) throws ClassNotFoundException, MalformedURLException, FileNotFoundException {
		//new JarURLConnection() -> access this jar:file:c:/agui_sdk.jar!/res/values/strings.xml 
		InputStream is = null;
		if(path.contains(AguiConstants.JAR_KEYWORD)) {
			if(path.contains(Global.corePath)) {
				is = Class.forName(Global.corePackageName+".BuildConfig").getResourceAsStream(path.substring(Global.corePath.length()));
			} else {
				is = MyUtils.getProjectClass(Global.projectPackageName+".BuildConfig").getResourceAsStream(path.substring(Global.projectPath.length()));
			}
		} else {
			is = new FileInputStream(path);
		}
		return is;
	}
}

