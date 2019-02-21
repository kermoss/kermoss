package io.kermoss.saga.pizzashop.exception;

public class ExpensiveException extends RuntimeException {

	public ExpensiveException() {
		super();
	}

	public ExpensiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpensiveException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpensiveException(String message) {
		super(message);
	}

	public ExpensiveException(Throwable cause) {
		super(cause);
	}

}
