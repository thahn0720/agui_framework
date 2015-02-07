package thahn.java.agui.content.pm;

import java.util.ArrayList;
import java.util.List;

import thahn.java.agui.app.ActivityManager;
import thahn.java.agui.app.ComponentName;
import thahn.java.agui.app.Intent;
import thahn.java.agui.app.IntentFilter;
import thahn.java.agui.app.IntentManager;
import thahn.java.agui.res.ManifestParser.ManagedComponent;
import thahn.java.agui.res.Resources;
import thahn.java.agui.res.ResourcesManager;

public class AguiPackageManager extends PackageManager {

	private static AguiPackageManager								mPM;
	
	public static AguiPackageManager getInstance() {
		if(mPM == null) {
			mPM = new AguiPackageManager();
		}
		return mPM;
	}
	
	@Override
	public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
		String action = intent.getAction();
		List<ManagedComponent> list = IntentManager.getInstance().get(action, null);
		List<ResolveInfo> ret = new ArrayList<ResolveInfo>();
		for (int i = 0; i < list.size(); i++) {
			ManagedComponent managed = list.get(i);
			if(managed.which == ManagedComponent.ACTIVITY) {
				ResolveInfo info = new ResolveInfo();
//				ResourcesManager.getResources().getIdentifier("icon", "Drawable", managed.component.getPackageName());
//				ResourcesManager.getResources().getIdentifier("", "String", managed.component.getPackageName());
//				info.icon = ;
//				info.labelRes = ;
				info.filter = new IntentFilter(intent.getAction());
				info.activityInfo = ActivityManager.getInstance().getActivityInfo(managed.component.getClassName());
				ret.add(info);
			}
		}
		return ret;
	}

	@Override
	public List<ResolveInfo> queryIntentActivitiesAsUser(Intent intent,
			int flags, int userId) {
		return null;
	}

	@Override
	public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller,
			Intent[] specifics, Intent intent, int flags) {
		return null;
	}
}
