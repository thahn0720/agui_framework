package thahn.java.agui.res;

import thahn.java.agui.Global;

/**
 * public class R { <br>
	   public static final class drawable { <br>
	        public static final int ic_launcher=0x7f020000; <br>
	    } <br>
	    public static final class id { <br>
	        public static final int root=0x7f050004; <br>
	        public static final int text1=0x7f050005; <br>
	        public static final int text2=0x7f050006; <br>
	        public static final int text3=0x7f050007; <br>
	        public static final int view1=0x7f050002; <br>
	        public static final int view2=0x7f050000; <br>
	        public static final int view3=0x7f050001; <br>
	        public static final int view4=0x7f050003; <br>
	    } <br>
	    public static final class layout { <br>
	        public static final int download_rate=0x7f030000; <br>
	        public static final int intro=0x7f030001; <br>
	        public static final int main=0x7f030002; <br>
	    } <br>
	    public static final class string { <br>
	        public static final int app_name=0x7f040001; <br>
	        public static final int hello=0x7f040000; <br>
	    } <br>
	}
 * @author thAhn
 * @see {@link dksxogudsla.java.agui.internal.R} 
 *
 */
public class RMaker extends RBase {
	
	public static final int													PROJECT_R_START_INDEX 		= INDEX_GAP * 20; 
	
	public RMaker(Resources res, String pName) {
		mPackageName = pName;
		if(res == null) {
			res = new Resources();
		}
		if(pName.equals(Global.corePackageName)) {
			mAbsoluteResBasePath = Global.coreResBasePath;
			mAbsoluteGenBasePath = Global.corePath;
			mResources = res.getResources(Resources.AGUI_RESOURCE);
			mStartIndex = 0;
		} else {
			mAbsoluteResBasePath = Global.projectResBasePath;
			mAbsoluteGenBasePath = Global.projectPath;
			mResources = res.getResources(Resources.PROJECT_RESOURCE);
			mStartIndex = PROJECT_R_START_INDEX;
		}
		mEnumRes = ResourcesManager.getInstance().getEnumResources();
	}
}
