package thahn.java.agui.exception;

public class WrongNameException extends RuntimeException {

//	private static final String 														NOT_EXIST_EXCEPTION = "do not exist";
	
	public WrongNameException() {
		super();
	}

	public WrongNameException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WrongNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongNameException(String message) {
		super(message);
	}

	public WrongNameException(Throwable cause) {
		super(cause);
	}
	
}
