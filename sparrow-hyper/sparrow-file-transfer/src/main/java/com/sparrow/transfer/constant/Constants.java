package com.sparrow.transfer.constant;

public class Constants {
	public static final char SPACE = ' ';
	public static final int BUFFER_SIZE = 512;
	/**
	 * CRLF.
	 */
	public static final String CRLF = "\r\n";
	public static final byte CR = (byte) '\r';
	/**
	 * LF.
	 */
	public static final byte LF = (byte) '\n';
	public static final byte SP = (byte) ' ';
	public static final byte HT = (byte) '\t';
	public static final byte COLON = (byte) ':';
	public static final byte QUESTION = (byte) '?';
	public static final byte A = (byte) 'A';
	public static final byte a = (byte) 'a';
	public static final byte Z = (byte) 'Z';
	public static final byte LC_OFFSET = A - a;

	public static final String LINE_SEPARATOR = System.getProperty(
			"line.separator", "\n");
}
