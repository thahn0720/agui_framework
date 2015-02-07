package thahn.java.agui;


import java.io.File;

import thahn.java.agui.app.ApplicationController;

public class Main {
	
	public static void main(String[] arguments) {
		// TODO : implements real type
//		if(arguments.length != 3) {
//			Log.e("wrong arguments. : projectPath, package, mainActivity fullname is needed.");
//		} else {
//			ApplicationController app = new ApplicationController();
//			app.create(arguments[0], arguments[1], arguments[2]);
//		}
		{
			File projectPath = new File("D:/Workspace/Java/AGUI/DemoApplication/AGUI_ApiDemo");
			ApplicationController app = new ApplicationController();
			app.create(
					projectPath.getAbsolutePath(),
					"thahn.java.agui.test",
					"thahn.java.agui.test." +
					"ApiDemos"
//					"viewpager.TestViewPagerActivity"
//					"CompoundButtonTestActivity"
//					"app.ActionBarTestActivity"
//					"app.ToastTestActivity"
//					"ContextMenuTestActivity"
//					"StartServiceTestActivity"
//					"ImageViewTestActivity"
//					"RadioGroupTestActivity"
//					"HorizontalScrollViewTestActivity"
//					"ScrollViewTestActivity"
//					"LinearLayoutGravityTestActivity"
//					"LinearLayoutTestActivity"
//					"LinearLayoutTestActivity2"
//					"LinearLayoutTestActivity3"
//					"ListViewTestActivity"
//					"view.PaddingTestActivity"
//					"view.PaddingTestActivity2"
//					"view.PaddingTestActivity3"
//					"anim.RotateAnimTestActivity"
//					"anim.TranslateAnimTestActivity"
//					"app.MediaPlayerTestActivity"
//					"EditTextTestActivity"							// modi -> back, del key
//					"view.SeekBarTestActivity"
//					"view.TextViewTestActivity"
//					"StartActivityTestActivity"
//					"view.CodeViewTestActivity"
//					"SetResultTestActivity"
//					"anim.InterpolatorTestActivity"
//					"anim.AlphaAnimTestActivity"
//					"GridViewTestActivity"
//					"RelativeLayoutTestActivity"
//					"TestActivity"
					);
		} 
		return ;
	}
}
			