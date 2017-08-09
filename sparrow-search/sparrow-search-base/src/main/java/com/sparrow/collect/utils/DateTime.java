package com.sparrow.collect.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * <p>
 * Title: DateTime
 * </p>
 * <p>
 * Description: com.eweb.utils
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: HR
 * </p>
 * 
 * @author Yzc
 * @version 1.0
 * @date 2009-10-29上午01:39:21
 */
public class DateTime {
	public static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";

	public static String dateToStr(Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sf.format(date);
		return dateStr;
	}

	public static String dateToStr(Date date, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		String dateStr = sf.format(date);
		return dateStr;
	}

	public static Date strToDate(String dateStr) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date strToDate(String dateStr, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(
				format == null ? "yyyy-MM-dd HH:mm:ss" : format);
		Date date = null;
		try {
			date = sf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String curDate() {
		return curDate("yyyy-MM-dd");
	}

	public static String curDate(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static String curDateyyyymmdd() {
		return curDate("yyyyMMdd");
	}

	public static String curDateyymmdd() {
		return curDate("yyMMdd");
	}

	public static String curDateTime() {
		return curDateTime("yyyy-MM-dd HH:mm:ss");
	}

	public static String curDateTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static String fmtDateYYYYMMDD(String date) {
		String frmDate = "";
		try {
			SimpleDateFormat sfrm = new SimpleDateFormat("yyyy-MM-dd");
			frmDate = sfrm.format(sfrm.parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return frmDate;
	}
}
