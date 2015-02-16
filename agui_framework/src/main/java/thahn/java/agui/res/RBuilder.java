package thahn.java.agui.res;



/**
 * 
 * @author thAhn
 *
 */
public class RBuilder extends RBase {
	
	public RBuilder(String corePackageName, String corePath, String packageName, String projectPath) {
		mCorePackageName = corePackageName;
		mCorePath = corePath;
		mPackageName = packageName;
		mAbsoluteResBasePath = projectPath;
		mAbsoluteGenBasePath = projectPath;
		mResources = new ResourcesContainer();
		mStartIndex = RMaker.PROJECT_R_START_INDEX;
		mEnumRes = ResourcesManager.getInstance().getEnumResources();
	}
}
