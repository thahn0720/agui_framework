package thahn.java.agui.jmx;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import thahn.java.agui.app.ApplicationInfo;
import thahn.java.agui.app.Context;
import thahn.java.agui.app.Intent;
import thahn.java.agui.app.IntentFilter;
import thahn.java.agui.res.ManifestParser.ManagedComponent;
import thahn.java.agui.utils.Log;


public class JmxConnectorHelper {
	
	private static String															TAG			= "JmxConnectorHelper";
	
	private static JmxConnectorHelper												mJmxHelper;
	private MainIntentManagerMBean													mMainIntentManagerMBean;
	private MBeanServerConnection 													mMbsc;
	private Context																	mContext;
	
	public static final JmxConnectorHelper getInstance() {
		if(mJmxHelper == null) {
			mJmxHelper = new JmxConnectorHelper();
		}
		return mJmxHelper;
	}
	
	public JmxConnectorHelper() {
		init();
	}
	
	private void init() {
		
	}
	
	public void setContext(Context context) {
		mContext = context;
	}

	public void connectToAgui() {
		try { 
	        Log.i("Create an RMI connector client and connect it to the RPC connector server");
	        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+JmxGlobal.MBEAN_SERVER_PORT+"/server");
	        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
	        mMbsc = jmxc.getMBeanServerConnection();
	        
	        ObjectName mbeanName = new ObjectName(JmxUtils.makeObjectName(JmxGlobal.MBEAN_SERVER_NAME, MainIntentManagerMBean.BEAN_NAME));
	        mMainIntentManagerMBean = JMX.newMBeanProxy(mMbsc, mbeanName, MainIntentManagerMBean.class, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerIntentInAgui(IntentFilter filter, ManagedComponent com) {
		if(mMainIntentManagerMBean == null) return ;
		mMainIntentManagerMBean.registerIntent(filter, com);
	}
	
	public void removeIntentInAgui(IntentFilter filter) {
		if(mMainIntentManagerMBean == null) return ; 
		mMainIntentManagerMBean.removeIntent(filter);
	}
	
	/**
	 * Intent intent = new Intent();
	 * intent.setAction("action");
	 * JmxConnectorHelper.getInstance().sendIntentInAgui(intent);
	 * @param intent
	 */
	public void sendIntentInAgui(Intent intent) {
		if(mMainIntentManagerMBean == null) {
			Log.e(TAG, "AGUI OS was not started. So, can not use external action");
			return ;
		}
		mMainIntentManagerMBean.sendIntent(intent);
	}
	
	public void registerApplicationInAgui(Context context, ApplicationInfo appInfo) {
        try {
        	if(mMainIntentManagerMBean == null) return ; 
        	mMainIntentManagerMBean.registerApplication(appInfo);
	        ObjectName applicationRmiName = new ObjectName(JmxUtils.makeObjectName(JmxGlobal.MBEAN_SERVER_NAME
					, ApplicationRpcMBean.BEAN_NAME+appInfo.getId()));
	        mMbsc.createMBean(ApplicationRpc.class.getName(), applicationRmiName
	        		, new Object[]{}
	        		, new String[]{});
	        mMbsc.addNotificationListener(applicationRmiName, mNotificationListener, null, true);
//	        ApplicationRmiMBean bean = JMX.newMBeanProxy(mMbsc, applicationRmiName, ApplicationRmiMBean.class, true);
//	        bean.sendIntent(new Intent());
//	        registerMBean(new ApplicationRmi(), applicationRmiName);
			Log.i("registerApplication : name - " + applicationRmiName.getCanonicalName()
					+ ", domnain - " + applicationRmiName.getDomain());
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	NotificationListener mNotificationListener = new NotificationListener() {
		
		@Override
		public void handleNotification(Notification notification, Object handback) {
			Log.i("JmxListener : handleNotification");
			Object obj = notification.getSource();
			
			if(obj instanceof Intent) {
				mContext.sendIntent((Intent)obj);
			}
		}
	};
	
	private NotificationFilter mNotificationFilter = new NotificationFilter() {
		private static final long serialVersionUID = 6641440444844388987L;

		@Override
		public boolean isNotificationEnabled(Notification notification) {
			if(ApplicationRpc.TYPE_SEND_INTENT.equals(notification.getType())) {
				return true;
			}
			return false;
		}
	};
}

