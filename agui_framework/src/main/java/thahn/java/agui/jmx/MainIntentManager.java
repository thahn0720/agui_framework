package thahn.java.agui.jmx;

import java.io.Serializable;
import java.util.List;

import javax.management.AttributeChangeNotification;
import javax.management.JMX;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import thahn.java.agui.Global;
import thahn.java.agui.app.ApplicationInfo;
import thahn.java.agui.app.ComponentName;
import thahn.java.agui.app.Intent;
import thahn.java.agui.app.IntentFilter;
import thahn.java.agui.app.IntentManager;
import thahn.java.agui.app.controller.ApplicationManager;
import thahn.java.agui.res.ManifestParser.ManagedComponent;
import thahn.java.agui.utils.Log;


public class MainIntentManager extends NotificationBroadcasterSupport implements MainIntentManagerMBean {
	
	private MBeanServer													mbeanServer;
	
	public MainIntentManager(MBeanServer mbs) {
		super();
		mbeanServer = mbs;
	}

	@Override
	public void registerIntent(IntentFilter filter, ManagedComponent managedCom) {
		Log.i("registerIntent");
		IntentManager.getInstance().put(filter, managedCom);
	}
	
	@Override
	public void removeIntent(IntentFilter filter) {
		Log.i("removeIntent");
		IntentManager.getInstance().remove(filter);
	}
	
	@Override
	public void sendIntent(Intent intent) {
		Log.i("MainIntentManager : sendIntent");
		ComponentName component = intent.getComponent();
		if(component == null) {
			String action = intent.getAction();
			List<ManagedComponent> managedComp = IntentManager.getInstance().get(action, Global.projectPackageName);
			if(managedComp == null || managedComp.size() <= 0) {
				Log.e("the requested intent does not exist. - " + action);
				return ;
			} else {
				for (ManagedComponent managedComponent : managedComp) {
					Intent tempIntent = new Intent(intent);
					tempIntent.setComponent(managedComponent.component);
					send(tempIntent);
				}
			}
		} else {
			send(intent);
		}
	}
	
	private void send(Intent intent) {
		try {
			ApplicationInfo appInfo = ApplicationManager.getInstance().getApplicationInfo(intent.getComponent().getPackageName());
			ObjectName mbeanName = new ObjectName(JmxUtils.makeObjectName(JmxGlobal.MBEAN_SERVER_NAME
					, ApplicationRpcMBean.BEAN_NAME+appInfo.getId()));
			ApplicationRpcMBean applicationRmi = JMX.newMBeanProxy(mbeanServer, mbeanName, ApplicationRpcMBean.class, true);
			applicationRmi.sendIntent(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @Override	
	public void registerApplication(ApplicationInfo appInfo) {
    	Log.i("registerApplication : " + appInfo.getId());
		ApplicationManager.getInstance().add(appInfo.getPackageName(), appInfo);
	}

	@Override
	public void removeApplication(ApplicationInfo appInfo) {
		Log.i("removeApplication");
		ApplicationManager.getInstance().remove(appInfo);
	}

	@Override
    public MBeanNotificationInfo[] getNotificationInfo() {
    	Log.i("getNotificationInfo");
		String[] types = new String[] {AttributeChangeNotification.ATTRIBUTE_CHANGE};
		String name = AttributeChangeNotification.class.getName();
		String description = "An attribute of this MBean has changed";
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] {info};
    }
}
