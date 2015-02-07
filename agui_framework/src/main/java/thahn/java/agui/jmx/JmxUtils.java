package thahn.java.agui.jmx;

public class JmxUtils {
	public static String makeObjectName(String agentName, String beanName) {
		StringBuilder builder = new StringBuilder(agentName).append(":name=").append(beanName);
		return builder.toString();
	}
}
