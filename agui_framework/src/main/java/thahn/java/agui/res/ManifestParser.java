package thahn.java.agui.res;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.Global;
import thahn.java.agui.app.ActivityInfo;
import thahn.java.agui.app.ActivityManager;
import thahn.java.agui.app.ApplicationInfo;
import thahn.java.agui.app.ApplicationSetting;
import thahn.java.agui.app.BroadcastReceiver;
import thahn.java.agui.app.ComponentName;
import thahn.java.agui.app.Intent;
import thahn.java.agui.app.IntentFilter;
import thahn.java.agui.app.IntentManager;
import thahn.java.agui.app.ReceiverManager;
import thahn.java.agui.app.ServiceInfo;
import thahn.java.agui.app.ServiceManager;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.jmx.JmxConnectorHelper;
import thahn.java.agui.utils.AguiUtils;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.utils.ReflectionUtils;

/**
 * 
 * @author thAhn
 *
 */
public class ManifestParser {
//	<manifest xmlns:android="http://schemas.android.com/apk/res/android"
//		    package="wifi.smartphone.lge"
//		    android:versionCode="1"
//		    android:versionName="1.0" >
//		    <uses-sdk android:minSdkVersion="8" />
//		    <uses-feature android:name="android.hardware.wifi" />
//		    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//		    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
//	
//			<application
//			android:name="MyApp"
//		    android:icon="@drawable/ic_launcher"
//		    android:label="@string/app_name" >
//		    <activity
//		        android:name="wifi.smartphone.lge.MainActivity"
//		        android:label="@string/app_name"
//		        android:screenOrientation="portrait"
//		        android:windowSoftInputMode="stateHidden" >
//		        <intent-filter>
//		            <action android:name="android.intent.action.MAIN" />
//		
//		            <category android:name="android.intent.category.LAUNCHER" />
//		        </intent-filter>
//		        <intent-filter>
//		            <action android:name="wifi.smartphone.lge.WifiLogger" />
//		        </intent-filter>
//		    </activity>
//		    
//		    <activity
//		        android:name="com.sec.eca.dialog.BootCompleteAttrScene"
//		        android:label="@string/title_activity_boot_complete"
//		        android:theme="@style/Theme.Dialog.Transparent" >
//		    </activity>

	private Resources												mResources;
	private ManifestInfo											mManifestInfo;
	
	public ManifestParser(Resources res) {
		if (res == null) res = new Resources();
		mResources = res;
		mManifestInfo = new ManifestInfo();
	}
	
	public void parse(String projectPath) {
		try {
			File xmlFile = Paths.get(projectPath, AguiConstants.AGUI_MANIFEST_NAME).toFile();
			FileInputStream in = new FileInputStream(xmlFile);
			BufferedInputStream bi = new BufferedInputStream(in);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(bi);
			Element root = doc.getRootElement();
			if (!"manifest".equals(root.getName())) logWrongFormat();
			Global.projectPackageName = mManifestInfo.packageName = root.getAttributeValue("package");
			mManifestInfo.versionCode = getAttributeValue(root, "versionCode");
			mManifestInfo.versionName = getAttributeValue(root, "versionName");
			
			parseApplication(root.getChild("application"));
			
			bi.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseApplication(Element element) {
		String appName = element.getAttributeValue("name");
		String width = getAttributeValue(element, "width");
		String height = getAttributeValue(element, "height");
		mManifestInfo.width = width==null?ApplicationSetting.getDefaultWidth():Integer.parseInt(width);
		mManifestInfo.height = height==null?ApplicationSetting.getDefaultHeight():Integer.parseInt(height);
		//
		ApplicationInfo appInfo = new ApplicationInfo();
		appInfo.id = UUID.randomUUID().toString();
		appInfo.packageName = Global.projectPackageName;
		appInfo.path = Global.projectPackageName;
		appInfo.mainActivity = mManifestInfo.mainActivity;
		appInfo.width = mManifestInfo.width;
		appInfo.height = mManifestInfo.height;
		ApplicationSetting.applicationInfo = appInfo;
		//
		for (Element child : (List<Element>) element.getChildren()) {
			String childName = child.getName();
			if ("activity".equals(childName)) {
				parseActivity(child);
			} else if ("service".equals(childName)) {
				parseService(child);
			} else if ("receiver".equals(childName)) {
				parseReceiver(child);
			} else {
				logWrongFormat();
			}
		}
	}
	
	private void parseActivity(Element child) {
//		String prefix = Global.AGUI_NAMESPACE+":";
		ActivityInfo info = new ActivityInfo();
		info.width = mManifestInfo.width;
		info.height = mManifestInfo.height;
		
		for (Attribute attr : (List<Attribute>)child.getAttributes()) {
			Object attrValue = attr.getValue();
			if (attrValue != null) {
				String attrName = attr.getName();
				if (((String)attrValue).startsWith("@")) {
					String[] values = ((String)attrValue).substring(1).split("/");
					String packageName = AguiUtils.getPackageNameByNS((String)attrValue); // ((String)attrValue).contains(prefix)
					
					if (attrName.equals("theme")) {
//						values[1] = values[1].replace(".", "_");
//						attrValue = mResources.getIdentifier(values[1], values[0], packageName);
					} else if (attrName.equals("label")) {
						int id = mResources.getIdentifier(values[1], values[0], packageName);
						attrValue = mResources.getString(id);
					} else if (attrName.equals("icon")) {
						attrValue = mResources.getIdentifier(values[1], values[0], packageName);
					}
				} else {
					if (attrName.equals("name")) {
						attrValue = MyUtils.makeFullPackageName(((String)attrValue));
					} else if (attrName.equals("label")) {
					} else if (attrName.equals("width")) {
						Object width = getAttributeValue(child, "width");
						info.width = Integer.parseInt((String)width);
						continue;
					} else if (attrName.equals("height")) {
						Object height = getAttributeValue(child, "height");
						info.height = Integer.parseInt((String)height);
						continue;
					}
				}
				
				ReflectionUtils.setFieldValue(info, attrName, attrValue);
			}
		}
		info.applicationInfo = ApplicationSetting.applicationInfo;
		ActivityManager.getInstance().put(info.name, info);
		parseIntent(child, ManagedComponent.ACTIVITY, info.name, null);
	}
	
	private void parseService(Element child) {
		try {
			ServiceInfo info = new ServiceInfo();
			for (Attribute attr : (List<Attribute>)child.getAttributes()) {
				String attrValue = attr.getValue();
				if (attrValue != null) {
					String attrName = attr.getName();
					if (attrValue.startsWith("@")) {
						// FIXME : remove 'continue;' and implement details
						continue ;
//						String[] values = attrValue.substring(1).split("/");
//						String packageName = Global.AGUI_NAMESPACE.equals(attr.getNamespacePrefix())?Global.corePackageName:Global.projectPackageName;
//						if (attrName.equals("label")) {
//							int id = mResources.getIdentifier(values[1], values[0], packageName);
//							attrValue = mResources.getString(id);
//						} 
					} else {
//						String packageName = Global.projectPackageName;
//						StringBuilder builder = new StringBuilder();
						if (attrName.equals("name")) {
							attrValue = MyUtils.makeFullPackageName(attrValue);
						}
					}
					try {
					ReflectionUtils.setFieldValue(info, attrName, attrValue);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			ServiceManager.getInstance().put(info.name, info);
			parseIntent(child, ManagedComponent.SERVICE, info.name, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	<receiver android:name=".BroadcastReceiverTest"
//            android:permission="com.google.android.c2dm.permission.SEND">
//            <intent-filter>
//                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
//                <category android:name="dksxogudsla.java.agui.test" />
//            </intent-filter>
//            <intent-filter>
//                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
//                <category android:name="dksxogudsla.java.agui.test" />
//            </intent-filter>
//        </receiver>
	private void parseReceiver(Element child) {
		try {
			String name = MyUtils.makeFullPackageName(getAttributeValue(child, "name"));
			BroadcastReceiver receiver = (BroadcastReceiver) MyUtils.getProjectClass(name).newInstance();
			parseIntent(child, ManagedComponent.RECEIVER, name, receiver);
			ReceiverManager.getInstance().put(name, receiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	<intent-filter >ManagedComponent.ManagedComponent.
//    <action android:name="android.intent.action.MAIN" />
//    <category android:name="android.intent.category.LAUNCHER" />
//	  <data >
//  </intent-filter>
	private void parseIntent(Element element, int which, String contextName, Object tag) {
		for (Element a : (List<Element>) element.getChildren()) {
			if ("intent-filter".equals(a.getName())) {
				IntentFilter intentFilter = new IntentFilter();
				for (Element b : (List<Element>) a.getChildren()) {
					String name = b.getName();
					String value = getAttributeValue(b, "name");
					if ("action".equals(name)) {
						intentFilter.addAction(value);
						if (Intent.ACTION_MAIN.equals(value)) mManifestInfo.mainActivity = contextName; 
					} else if ("category".equals(name)) {
						intentFilter.addCategory(value);
					} else if ("data".equals(name)) {
						
					}
				}
				ManagedComponent managedCom = new ManagedComponent();
				managedCom.which = which;
				managedCom.component = new ComponentName(mManifestInfo.packageName, contextName);
				managedCom.tag = tag;
				IntentManager.getInstance().put(intentFilter, managedCom);
				JmxConnectorHelper.getInstance().registerIntentInAgui(intentFilter, managedCom);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getAttributeValue(Element e, String name) {
		for (Attribute a : (List<Attribute>)e.getAttributes()) {
			if (a.getName().equals(name)) {
				return a.getValue();
			}
		}
		return null;
	}
	
//	private String makeFullPackageName(String attrValue) {
//		StringBuilder builder = new StringBuilder();
//		if (attrValue.startsWith(".")) { // .activity
//			builder.append(Global.projectPackageName).append(attrValue);
//		} else if (!attrValue.startsWith(Global.projectPackageName)) { // activity
//			builder.append(Global.projectPackageName).append(".").append(attrValue);
//		} 
//		return builder.toString();
//	}
	
	private void logWrongFormat() {
		Log.e("Values Format is not correct");
		throw new WrongFormatException();
	}
	
	public ManifestInfo getManifestInfo() {
		return mManifestInfo;
	}

	public static final class ManagedComponent implements Serializable {
		private static final long 						serialVersionUID = -5125027942844730006L;
		public static final int							ACTIVITY 	= 0;
		public static final int							SERVICE 	= 1;
		public static final int							RECEIVER 	= 2;
		
		public int 										which;
		public ComponentName 							component;
		/** if RECEIVER, receiver obj ref */
		public Object 									tag;
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ManagedComponent other = (ManagedComponent) obj;
			if (component == null) {
				if (other.component != null)
					return false;
			} else if (!component.equals(other.component))
				return false;
			if (which != other.which)
				return false;
			return true;
		}
	}
	
	public class ManifestInfo {
		public String mainActivity;
		public String packageName;
		public String versionCode;
		public String versionName;
		public int width;
		public int height;
	}
}
