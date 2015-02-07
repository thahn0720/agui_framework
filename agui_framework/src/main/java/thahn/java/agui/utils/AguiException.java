package thahn.java.agui.utils;

/**
 * Base class for all checked exceptions thrown by the Android frameworks.
 */
public class AguiException extends RuntimeException {
	
    public AguiException() {
    }

    public AguiException(String name) {
        super(name);
    }

    public AguiException(String name, Throwable cause) {
        super(name, cause);
    }

    public AguiException(Exception cause) {
        super(cause);
    }
};