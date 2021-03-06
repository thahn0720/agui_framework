/*
 * Main.java - main class for the Hello MBean and QueueSampler MXBean example.
 * Create the Hello MBean and QueueSampler MXBean, register them in the platform
 * MBean server, then wait forever (or until the program is interrupted).
 */

package thahn.java.agui.controller;

import thahn.java.agui.app.ApplicationController;
import thahn.java.agui.utils.MyUtils;


public class Main {
	
    public static void main(String[] args) throws Exception {
//    	AguiController controller = new AguiController();
//    	controller.start();
    	
//		Global.corePath = "D:/Dropbox/Workspace/Java/AGUI/AGUI_SDK";
//		RBuilder r = new RBuilder("test2", "D:/Workflow/runtime-EclipseApplication/test2");
//		r.parse();
    	
		try {
			String projectPath = System.getProperty("user.dir");
			ApplicationController cont = new ApplicationController();
			cont.create(projectPath
					, "thahn.java.agui.controller"
//					, "thahn.java.agui.controller."+
//					"IntroActivity"
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
