/**  
 * Project Name:scms-stock-manage-core  
 * File Name:ScmsPatternParser.java  
 * Package Name:com.boco.scms.stock.commons.logger  
 * Date:2013-12-5下午6:01:50  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.core.log.layout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sparrow.core.log.LocationInfo;
import com.sparrow.core.log.LoggingEvent;


/**
 * @author YZC
 * @version 1.0 (2013-12-5)
 * @modify
 */
public class PatternParser {
	public final static String LINE_SEP = System.getProperty("line.separator");
	private static final int LITERAL_STATE = 0;
	private static final char ESCAPE_CHAR = '%';

	private static final int CONVERTER_STATE = 1;
	private static final int DOT_STATE = 3;
	private static final int MIN_STATE = 4;
	private static final int MAX_STATE = 5;

	static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss:SSS";
	static final int FULL_LOCATION_CONVERTER = 1000;
	static final int METHOD_LOCATION_CONVERTER = 1001;
	static final int CLASS_LOCATION_CONVERTER = 1002;
	static final int LINE_LOCATION_CONVERTER = 1003;

	static final int THREAD_CONVERTER = 2001;
	static final int THREAD_NO_CONVERTER = 2005;
	static final int LEVEL_CONVERTER = 2002;
	static final int MESSAGE_CONVERTER = 2004;
	static final String UNKNOW_SESSION = "?";

	int state;
	protected StringBuffer currentLiteral = new StringBuffer(32);
	protected int patternLength;
	protected int i;
	PatternConverter head;
	PatternConverter tail;
	protected FormattingInfo formattingInfo = new FormattingInfo();
	protected String pattern;

	public PatternParser(String pattern) {
		this.pattern = pattern;
		patternLength = pattern.length();
		state = LITERAL_STATE;
	}

	protected String extractOption() {
		if ((i < patternLength) && (pattern.charAt(i) == '{')) {
			int end = pattern.indexOf('}', i);
			if (end > i) {
				String r = pattern.substring(i + 1, end);
				i = end + 1;
				return r;
			}
		}
		return null;
	}

	protected int extractPrecisionOption() {
		String opt = extractOption();
		int r = 0;
		if (opt != null) {
			try {
				r = Integer.parseInt(opt);
				if (r <= 0) {
					DefLog.error("Precision option (" + opt
							+ ") isn't a positive integer.");
					r = 0;
				}
			} catch (NumberFormatException e) {
				DefLog.error("Category option \"" + opt
						+ "\" not a decimal integer.", e);
			}
		}
		return r;
	}

	public PatternConverter parse() {
		char c;
		i = 0;
		while (i < patternLength) {
			c = pattern.charAt(i++);
			switch (state) {
			case LITERAL_STATE:
				// In literal state, the last char is always a literal.
				if (i == patternLength) {
					currentLiteral.append(c);
					continue;
				}
				if (c == ESCAPE_CHAR) {
					// peek at the next char.
					switch (pattern.charAt(i)) {
					case ESCAPE_CHAR:
						currentLiteral.append(c);
						i++; // move pointer
						break;
					case 'n':
						currentLiteral.append(LINE_SEP);
						i++; // move pointer
						break;
					default:
						if (currentLiteral.length() != 0) {
							addToList(new LiteralPatternConverter(
									currentLiteral.toString()));
						}
						currentLiteral.setLength(0);
						currentLiteral.append(c); // append %
						state = CONVERTER_STATE;
						formattingInfo.reset();
					}
				} else {
					currentLiteral.append(c);
				}
				break;
			case CONVERTER_STATE:
				currentLiteral.append(c);
				switch (c) {
				case '-':
					formattingInfo.leftAlign = true;
					break;
				case '.':
					state = DOT_STATE;
					break;
				default:
					if (c >= '0' && c <= '9') {
						formattingInfo.min = c - '0';
						state = MIN_STATE;
					} else
						finalizeConverter(c);
				} // switch
				break;
			case MIN_STATE:
				currentLiteral.append(c);
				if (c >= '0' && c <= '9')
					formattingInfo.min = formattingInfo.min * 10 + (c - '0');
				else if (c == '.')
					state = DOT_STATE;
				else {
					finalizeConverter(c);
				}
				break;
			case DOT_STATE:
				currentLiteral.append(c);
				if (c >= '0' && c <= '9') {
					formattingInfo.max = c - '0';
					state = MAX_STATE;
				} else {
					DefLog.error("Error occured in position " + i
							+ ".\n Was expecting digit, instead got char \""
							+ c + "\".");
					state = LITERAL_STATE;
				}
				break;
			case MAX_STATE:
				currentLiteral.append(c);
				if (c >= '0' && c <= '9')
					formattingInfo.max = formattingInfo.max * 10 + (c - '0');
				else {
					finalizeConverter(c);
					state = LITERAL_STATE;
				}
				break;
			} // switch
		} // while
		if (currentLiteral.length() != 0) {
			addToList(new LiteralPatternConverter(currentLiteral.toString()));
		}
		return head;
	}

	protected void finalizeConverter(char c) {
		PatternConverter pc = null;
		switch (c) {
		case 'c':
			pc = new CategoryPatternConverter(formattingInfo,
					extractPrecisionOption());
			currentLiteral.setLength(0);
			break;
		case 'C':
			pc = new ClassNamePatternConverter(formattingInfo,
					extractPrecisionOption());
			currentLiteral.setLength(0);
			break;
		case 'd':
			String dateFormatStr = DATE_FORMAT;
			DateFormat df;
			String dOpt = extractOption();
			if (dOpt != null)
				dateFormatStr = dOpt;
			df = new SimpleDateFormat(dateFormatStr);
			pc = new DatePatternConverter(formattingInfo, df);
			currentLiteral.setLength(0);
			break;
		case 'l':
			pc = new LocationPatternConverter(formattingInfo,
					FULL_LOCATION_CONVERTER);
			currentLiteral.setLength(0);
			break;
		case 'L':
			pc = new LocationPatternConverter(formattingInfo,
					LINE_LOCATION_CONVERTER);
			currentLiteral.setLength(0);
			break;
		case 'm':
			pc = new BasicPatternConverter(formattingInfo, MESSAGE_CONVERTER);
			currentLiteral.setLength(0);
			break;
		case 'M':
			pc = new LocationPatternConverter(formattingInfo,
					METHOD_LOCATION_CONVERTER);
			currentLiteral.setLength(0);
			break;
		case 'p':
			pc = new BasicPatternConverter(formattingInfo, LEVEL_CONVERTER);
			// LogLog.debug("LEVEL converter.");
			// formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 't':
			pc = new BasicPatternConverter(formattingInfo, THREAD_CONVERTER);
			currentLiteral.setLength(0);
			break;
		case 'T':
			pc = new BasicPatternConverter(formattingInfo, THREAD_NO_CONVERTER);
			currentLiteral.setLength(0);
			break;
		case 's':
			String opt = extractOption();
			pc = new ThreadPatternConverter(formattingInfo, opt);
			addConverter(pc);
			break;
		default:
			DefLog.error("Unexpected char [" + c + "] at position " + i
					+ " in conversion patterrn.");
			pc = new LiteralPatternConverter(currentLiteral.toString());
			currentLiteral.setLength(0);
		}
		addConverter(pc);
	}

	protected void addConverter(PatternConverter pc) {
		currentLiteral.setLength(0);
		// Add the pattern converter to the list.
		addToList(pc);
		// Next pattern is assumed to be a literal.
		state = LITERAL_STATE;
		// Reset formatting info
		formattingInfo.reset();
	}

	private void addToList(PatternConverter pc) {
		if (head == null) {
			head = tail = pc;
		} else {
			tail.next = pc;
			tail = pc;
		}
	}

	static class ThreadPatternConverter extends PatternConverter {
		final String option;

		ThreadPatternConverter(FormattingInfo formattingInfo, String option) {
			super(formattingInfo);
			this.option = option;
		}

		public String convert(LoggingEvent event) {
			String s = null;
			s = String.valueOf(Thread.currentThread().getId());
			if (s == null)
				return UNKNOW_SESSION;
			else
				return s;
		}
	}

	private static class BasicPatternConverter extends PatternConverter {
		int type;

		BasicPatternConverter(FormattingInfo formattingInfo, int type) {
			super(formattingInfo);
			this.type = type;
		}

		public String convert(LoggingEvent event) {
			switch (type) {
			case THREAD_CONVERTER:
				return event.getThreadName();
			case THREAD_NO_CONVERTER:
				return String.valueOf(Thread.currentThread().getId());
			case LEVEL_CONVERTER:
				return event.getLevel();
			case MESSAGE_CONVERTER: {
				return event.getMessage();
			}
			default:
				return null;
			}
		}
	}

	private static class LiteralPatternConverter extends PatternConverter {
		private String literal;

		LiteralPatternConverter(String value) {
			literal = value;
		}

		public final void format(StringBuffer sbuf, LoggingEvent event) {
			sbuf.append(literal);
		}

		public String convert(LoggingEvent event) {
			return literal;
		}
	}

	private static class DatePatternConverter extends PatternConverter {
		private DateFormat df;
		private Date date;

		DatePatternConverter(FormattingInfo formattingInfo, DateFormat df) {
			super(formattingInfo);
			date = new Date();
			this.df = df;
		}

		public String convert(LoggingEvent event) {
			date.setTime(event.getTimeStamp());
			String converted = null;
			try {
				converted = df.format(date);
			} catch (Exception ex) {
				DefLog.error("Error occured while converting date.", ex);
			}
			return converted;
		}
	}

	private class LocationPatternConverter extends PatternConverter {
		int type;

		LocationPatternConverter(FormattingInfo formattingInfo, int type) {
			super(formattingInfo);
			this.type = type;
		}

		public String convert(LoggingEvent event) {
			LocationInfo locationInfo = event.getLocationInfo();
			switch (type) {
			case FULL_LOCATION_CONVERTER:
				return locationInfo.getFullInfo();
			case METHOD_LOCATION_CONVERTER:
				return locationInfo.getMethodName();
			case LINE_LOCATION_CONVERTER:
				return locationInfo.getLineNumber();
			default:
				return null;
			}
		}
	}

	private static abstract class NamedPatternConverter extends
			PatternConverter {
		int precision;

		NamedPatternConverter(FormattingInfo formattingInfo, int precision) {
			super(formattingInfo);
			this.precision = precision;
		}

		abstract String getFullyQualifiedName(LoggingEvent event);

		public String convert(LoggingEvent event) {
			String n = getFullyQualifiedName(event);
			if (precision <= 0)
				return n;
			else {
				int len = n.length();
				int end = len - 1;
				for (int i = precision; i > 0; i--) {
					end = n.lastIndexOf('.', end - 1);
					if (end == -1)
						return n;
				}
				return n.substring(end + 1, len);
			}
		}
	}

	private class ClassNamePatternConverter extends NamedPatternConverter {

		ClassNamePatternConverter(FormattingInfo formattingInfo, int precision) {
			super(formattingInfo, precision);
		}

		String getFullyQualifiedName(LoggingEvent event) {
			return event.getLocationInfo().getClassName();
		}
	}

	private class CategoryPatternConverter extends NamedPatternConverter {

		CategoryPatternConverter(FormattingInfo formattingInfo, int precision) {
			super(formattingInfo, precision);
		}

		String getFullyQualifiedName(LoggingEvent event) {
			return event.getLoggerName();
		}
	}
}
