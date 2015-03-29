package thahn.java.agui;


import thahn.java.agui.app.ApplicationController;
import thahn.java.agui.utils.Log;

/**
 * 
 * @author thAhn
 *
 */
public class Main {
	
	public static void main(String[] arguments) {
		// ex) java -classpath D:\agui-sdk-windows\os\data\thahn.java.agui.test -jar agui_sdk.jar D:\agui-sdk-windows\os\data\thahn.java.agui.test thahn.java.agui.test thahn.java.agui.test.ApiDemos
		if(arguments.length != 3) {
			Log.e("wrong arguments. : projectPath, package, mainActivity fullname is needed.");
		} else {
			Log.i("args : " + arguments[0] + " " + arguments[1] + " " + arguments[2]);
			ApplicationController app = new ApplicationController();
			app.create(arguments[0], arguments[1], arguments[2]);
		}
		// for test
//		{
//			File projectPath = new File("D:/agui-sdk-windows/os/data/thahn.java.agui.test");
//			ApplicationController app = new ApplicationController();
//			app.create(
//					projectPath.getAbsolutePath(),
//					"thahn.java.agui.test",
//					"thahn.java.agui.test." +
//					"ApiDemos"
//					);
//		} 
		return ;
	}
}
			