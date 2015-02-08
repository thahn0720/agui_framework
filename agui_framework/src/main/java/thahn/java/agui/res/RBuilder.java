package thahn.java.agui.res;



/**
 * 
 * @author thAhn
 *
 */
public class RBuilder extends RBase {
	
	public RBuilder(String packageName, String projectPath) {
		mPackageName = packageName;
		mAbsoluteResBasePath = projectPath;
		mAbsoluteGenBasePath = projectPath;
		mResources = new ResourcesContainer();
		mStartIndex = RMaker.PROJECT_R_START_INDEX;
		mEnumRes = ResourcesManager.getInstance().getEnumResources();
	}
}
