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
		// ex) java -jar agui_sdk.jar D:\Workspace\Java\AGUI\DemoApplication\agui_api_demos thahn.java.agui.test thahn.java.agui.test.ApiDemos
		if(arguments.length != 3) {
			Log.e("wrong arguments. : projectPath, package, mainActivity fullname is needed.");
		} else {
			Log.i("args : " + arguments[0] + " " + arguments[1] + " " + arguments[2]);
			ApplicationController app = new ApplicationController();
			app.create(arguments[0], arguments[1], arguments[2]);
		}
		// for test
//		{
//			File projectPath = new File("D:/Workspace/Java/AGUI/DemoApplication/AGUI_ApiDemo");
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
			