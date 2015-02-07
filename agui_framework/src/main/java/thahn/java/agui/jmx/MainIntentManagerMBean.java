/*
 * HelloMBean.java - MBean interface describing the management operations and
 * attributes for the Hello World MBean. In this case there are two operations,
 * "sayHello" and "add", and two attributes, "Name" and "CacheSize".
 */

package thahn.java.agui.jmx;

import java.io.Serializable;

import thahn.java.agui.app.ApplicationInfo;
import thahn.java.agui.app.Intent;
import thahn.java.agui.app.IntentFilter;
import thahn.java.agui.res.ManifestParser.ManagedComponent;

/**
 * all used object should be serialized
 * @author thAhn
 *
 */
public interface MainIntentManagerMBean {
	public static final String									BEAN_NAME 		= "MainIntentManager";
    //**************************************
    // operations
    //**************************************
	public void registerIntent(IntentFilter intent, ManagedComponent managedCom);
    public void removeIntent(IntentFilter intent);
    public void sendIntent(Intent Intent);
    //    
    public void registerApplication(ApplicationInfo appInfo);
    public void removeApplication(ApplicationInfo appInfo);
    //**************************************
    // attributes (getter, setter)
    //**************************************
}
