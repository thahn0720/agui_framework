package thahn.java.agui.exception;

public class WrongFormatException extends RuntimeException {

//	private static final String 														NOT_EXIST_EXCEPTION = "do not exist";
	
	public WrongFormatException() {
		super();
	}

	public WrongFormatException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WrongFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongFormatException(String message) {
		super(message);
	}

	public WrongFormatException(Throwable cause) {
		super(cause);
	}
	
}
