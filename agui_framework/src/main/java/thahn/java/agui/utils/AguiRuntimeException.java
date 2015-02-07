package thahn.java.agui.utils;

/**
 * Base class for all unchecked exceptions thrown by the Android frameworks.
 */
public class AguiRuntimeException extends RuntimeException {
    public AguiRuntimeException() {
    }

    public AguiRuntimeException(String name) {
        super(name);
    }

    public AguiRuntimeException(String name, Throwable cause) {
        super(name, cause);
    }

    public AguiRuntimeException(Exception cause) {
        super(cause);
    }
};

