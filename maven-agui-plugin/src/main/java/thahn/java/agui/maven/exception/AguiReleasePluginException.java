package thahn.java.agui.maven.exception;

/**
 *
 * @author thAhn
 * 
 */
public class AguiReleasePluginException extends RuntimeException {

	public AguiReleasePluginException() {
		super();
	}

	public AguiReleasePluginException(String message) {
		super(message);
	}

	public AguiReleasePluginException(Throwable cause) {
		super(cause);
	}

	public AguiReleasePluginException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AguiReleasePluginException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
