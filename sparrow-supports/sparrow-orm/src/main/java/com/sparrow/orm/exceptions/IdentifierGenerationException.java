package com.sparrow.orm.exceptions;

/**
 * Thrown by <tt>IdentifierGenerator</tt> implementation class when ID
 * generation fails.
 * 
 * @see MsgException
 * @author Gavin King
 */

public class IdentifierGenerationException extends RuntimeException {
	/**
	 * serialVersionUID:(用一句话描述这个变量表示什么).
	 * 
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;

	public IdentifierGenerationException(String msg) {
		super(msg);
	}

	public IdentifierGenerationException(String msg, Throwable t) {
		super(msg, t);
	}

}
