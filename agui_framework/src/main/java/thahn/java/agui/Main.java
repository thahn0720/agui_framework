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
		// TODO : implements real type
		if(arguments.length != 3) {
			Log.e("wrong arguments. : projectPath, package, mainActivity fullname is needed.");
		} else {
			ApplicationController app = new ApplicationController();
			app.create(arguments[0], arguments[1], arguments[2]);
		}
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
			