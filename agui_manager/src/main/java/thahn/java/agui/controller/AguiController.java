package thahn.java.agui.controller;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import thahn.java.agui.Global;
import thahn.java.agui.jmx.ApplicationRpcMBean;
import thahn.java.agui.jmx.JmxGlobal;
import thahn.java.agui.jmx.JmxUtils;
import thahn.java.agui.jmx.MainIntentManager;
import thahn.java.agui.jmx.MainIntentManagerMBean;
import thahn.java.agui.utils.AguiUtils;
import thahn.java.agui.utils.Log;


public class AguiController {
	
	private static final String														TAG = "AguiController";
	
	private ApplicationListener														mApplicationListener;
	private JMXConnectorServer 														mCs;
	
	public AguiController() {
		init();
	}
	
	private void init() {
		mApplicationListener = new ApplicationListener();
	}
	
	public void start() {	
  	    // Get the platform MBeanServer
		try {
			Global.init();
			//
			MBeanServer mbs = MBeanServerFactory.createMBeanServer(JmxGlobal.MBEAN_SERVER_NAME);
		    // Unique identification of MBeans
		    MainIntentManager mainIntentManagerBean = new MainIntentManager(mbs);
		    ObjectName mainIntentManagerName = new ObjectName(JmxUtils.makeObjectName(JmxGlobal.MBEAN_SERVER_NAME
	    					, MainIntentManagerMBean.BEAN_NAME));//"SimpleAgent:name=hellothere");
	    	mbs.registerMBean(mainIntentManagerBean, mainIntentManagerName);
	    	
	    	// Create an RMI connector and start it
	    	// FIXME : seperate window into linux.
	    	Runtime.getRuntime().exec("cmd /c rmiregistry "+JmxGlobal.MBEAN_SERVER_PORT);
	    	JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+JmxGlobal.MBEAN_SERVER_PORT+"/server");
	       	mCs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
	       	Log.i("Lock");
	       	if (AguiUtils.isRunnable()) {
	       		throw new RuntimeException("already started.");
	       	} else {
	       		Log.i(TAG, "Agui manager started");
	       		AguiUtils.tryLock();
	       		AguiUtils.registerUnlockOnTerminate();
	       	}
	       	Log.i("Waiting for incoming requests...");
	       	mCs.addNotificationListener(mApplicationListener, null, null);
//	       	cs.getConnectionIds()
	       	mCs.start();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	class ApplicationListener implements NotificationListener {

		@Override
		public void handleNotification(Notification notification, Object handback) {
			Log.i("handleNotification : ApplicationListener.");
		}
	}
}
