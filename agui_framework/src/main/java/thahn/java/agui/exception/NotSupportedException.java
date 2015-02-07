package thahn.java.agui.exception;

public class NotSupportedException extends RuntimeException {

	private static final long serialVersionUID = -540382849417652332L;

	public NotSupportedException() {
        super();
    }

    public NotSupportedException(String message) {
        super(message);
    }

    public NotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedException(Throwable cause) {
        super(cause);
    }
}
