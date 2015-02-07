package thahn.java.agui.tools;

import java.io.File;
import java.io.IOException;

import thahn.java.agui.Global;
import thahn.java.agui.res.RMaker;
import thahn.java.agui.res.Resources;
import thahn.java.agui.res.ResourcesManager;


public class RMakerTest {
	public static void main(String[] args) {
		Global.corePackageName = "dksxogudsla.java.agui";
		try {
			Global.corePath = new File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Global.projectPath = "E:/Workspace/Java/AGUI_ApiDemo";
		RMaker rMaker = new RMaker(ResourcesManager.getInstance(), "dksxogudsla.java.agui");
		rMaker.parse();
	}
}
