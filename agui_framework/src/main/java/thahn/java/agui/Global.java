package thahn.java.agui;

/**
 * 
 * @author thAhn
 *
 */
public class Global {
	
	public static final int											NOT_YET 				= -1;
	public static final int											WHEEL_DISCOUT_FACTOR	= 3;
	
	public static final String										AGUI_NAMESPACE			= "agui";
	public static final String										ENV_AGUI_HOME			= "AGUI_HOME";
	public static final int											ROOT_ID					= -720;
	
	public static String											aguiSdkPath;
	
	public static int												mTitlebarHeight;
	
	public static int												screenWidth;
	public static int												screenHeight;
	
	public static String											aguiHomePath;
	public static String											aguiProjectPath;
	
	public static String											corePackageName;
	public static String											corePath;
	public static String											projectPackageName;
	public static String											projectPath;
	
	public static String											coreResBasePath;
	public static String											coreDrawableImgPath;
	public static String											coreDrawablePath;
	public static String											coreLayoutPath;
	public static String											coreGenBasePath;
	public static String											coreValuesPath;
	public static String											projectResBasePath;
	public static String											projectDrawableImgPath;
	public static String											projectDrawablePath;
	public static String											projectLayoutPath;
	public static String											projectGenBasePath;
	public static String											projectValuesPath;

	public static void init() {
		Global.aguiSdkPath = System.getenv(ENV_AGUI_HOME);//"D:/agui-sdk-windows/";
	}
}
