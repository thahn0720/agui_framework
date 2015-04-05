package thahn.java.agui.res;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.Global;
import thahn.java.agui.app.ActivityInfo;
import thahn.java.agui.app.ActivityManager;
import thahn.java.agui.app.ApplicationSetting;
import thahn.java.agui.app.BroadcastReceiver;
import thahn.java.agui.app.ComponentName;
import thahn.java.agui.app.Intent;
import thahn.java.agui.app.IntentFilter;
import thahn.java.agui.app.IntentManager;
import thahn.java.agui.app.ReceiverManager;
import thahn.java.agui.app.ServiceInfo;
import thahn.java.agui.app.ServiceManager;
import thahn.java.agui.exception.NotSupportedException;
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

	private Resources												mResources;
	private ManifestInfo											mManifestInfo;
	
	public ManifestParser(Resources res) {
		if (res == null) res = new Resources();
		mResources = res;
		mManifestInfo = new ManifestInfo();
	}
	
	/**
	 * 
	 * @param projectPath project directory
	 */
	public void parse(String projectPath) {
		try {
			File xmlFile = Paths.get(projectPath, AguiConstants.AGUI_MANIFEST_NAME).toFile();
			parse(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void parse(InputStream is) {
		parse(is, false);
	}
	
	public void parseHeader(InputStream is) {
		parse(is, true);
	}
	
	private void parse(InputStream is, boolean onlyHeader) {
		try (BufferedInputStream bi = new BufferedInputStream(is)) {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(bi);
			Element root = doc.getRootElement();
			if (!"manifest".equals(root.getName())) logWrongFormat();
			Global.projectPackageName = mManifestInfo.packageName = root.getAttributeValue("package");
			mManifestInfo.versionCode = getAttributeValue(root, "versionCode");
			mManifestInfo.versionName = getAttributeValue(root, "versionName");
			
			Element appElement = root.getChild("application");
			String appName = getAttributeValue(appElement, "name");
			String appLabel = getAttributeValue(appElement, "label");
			String width = getAttributeValue(appElement, "width");
			String height = getAttributeValue(appElement, "height");
			mManifestInfo.width = width==null?ApplicationSetting.getDefaultWidth():Integer.parseInt(width);
			mManifestInfo.height = height==null?ApplicationSetting.getDefaultHeight():Integer.parseInt(height);
			mManifestInfo.appName = appName;
			mManifestInfo.appLabel = appLabel;
			// get main activity
			if (!onlyHeader) {
				parseApplication(appElement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void parseApplication(Element element) {
		// parse component
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
	
	private void parseIntent(Element element, int which, String contextName, Object tag) {
		for (Element a : (List<Element>) element.getChildren()) {
			if ("intent-filter".equals(a.getName())) {
				IntentFilter intentFilter = new IntentFilter();
				for (Element b : (List<Element>) a.getChildren()) {
					String name = b.getName();
					String value = getAttributeValue(b, "name");
					if ("action".equals(name)) {
						intentFilter.addAction(value);
					} else if ("category".equals(name)) {
						intentFilter.addCategory(value);
						if (Intent.CATEGORY_LAUNCHER.equals(value)) {
							mManifestInfo.mainActivity = contextName; 
						}
					} else if ("data".equals(name)) {
						throw new NotSupportedException("data tag not supported");
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
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ManagedComponent other = (ManagedComponent) obj;
			if (component == null) {
				if (other.component != null) {
					return false;
				}
			} else if (!component.equals(other.component)) {
				return false;
			}
			if (which != other.which) {
				return false;
			}
			return true;
		}
	}
	
	public class ManifestInfo {
		public String appName;
		public String appLabel;
		public String mainActivity;
		public String packageName;
		public String versionCode;
		public String versionName;
		public int width;
		public int height;
	}
}
