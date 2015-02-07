package thahn.java.agui.os;

import thahn.java.agui.utils.AguiException;

/**
 * Parent exception for all Binder remote-invocation errors
 */
public class RemoteException extends AguiException {
    public RemoteException() {
        super();
    }

    public RemoteException(String message) {
        super(message);
    }
}
