package com.sparrow.transfer.exceptions;

public class ProtocolAnalyzerException extends Exception {
	/** serialVersionUID */
	private static final long serialVersionUID = -3988145310232044781L;

	public ProtocolAnalyzerException(Throwable cause) {
		super(cause);
	}

	public ProtocolAnalyzerException(String message) {
		super(message);
	}

	public ProtocolAnalyzerException(String message, Throwable cause) {
		super(message, cause);
	}
}
