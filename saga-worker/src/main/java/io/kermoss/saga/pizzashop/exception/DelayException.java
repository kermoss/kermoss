package io.kermoss.saga.pizzashop.exception;

public class DelayException extends RuntimeException {

	public DelayException() {
		super();
	}

	public DelayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DelayException(String message, Throwable cause) {
		super(message, cause);
	}

	public DelayException(String message) {
		super(message);
	}

	public DelayException(Throwable cause) {
		super(cause);
	}
	
	 

}
