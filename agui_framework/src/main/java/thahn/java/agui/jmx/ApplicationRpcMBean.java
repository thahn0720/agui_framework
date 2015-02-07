/*
 * HelloMBean.java - MBean interface describing the management operations and
 * attributes for the Hello World MBean. In this case there are two operations,
 * "sayHello" and "add", and two attributes, "Name" and "CacheSize".
 */

package thahn.java.agui.jmx;

import thahn.java.agui.app.Intent;

public interface ApplicationRpcMBean {
	public static final String									BEAN_NAME 		= "ApplicationRpc";
    //**************************************
    // operations
    //**************************************
    public void sendIntent(Intent intent);
    //**************************************
    // attributes (getter, setter)
    //**************************************
}
