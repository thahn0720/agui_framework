package thahn.java.agui.exception;

public class NotExistException extends RuntimeException {

//	private static final String 														NOT_EXIST_EXCEPTION = "do not exist";
	
	public NotExistException() {
		super();
	}

	public NotExistException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotExistException(String message) {
		super(message);
	}

	public NotExistException(Throwable cause) {
		super(cause);
	}
	
}
