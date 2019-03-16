package io.kermoss.bfm.validator;

public class BusinessFlowManagerValidatorException extends RuntimeException{

	public BusinessFlowManagerValidatorException() {
		super();
	}

	public BusinessFlowManagerValidatorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BusinessFlowManagerValidatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessFlowManagerValidatorException(String message) {
		super(message);
	}

	public BusinessFlowManagerValidatorException(Throwable cause) {
		super(cause);
	}
	
}
