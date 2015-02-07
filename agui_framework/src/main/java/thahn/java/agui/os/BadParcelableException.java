package thahn.java.agui.os;

import thahn.java.agui.utils.AguiRuntimeException;

/**
 * The object you are calling has died, because its hosting process
 * no longer exists.
 */
public class BadParcelableException extends AguiRuntimeException {
    public BadParcelableException(String msg) {
        super(msg);
    }
    public BadParcelableException(Exception cause) {
        super(cause);
    }
}
