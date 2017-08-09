package com.sparrow.collect.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";

	public static String longToDateToString(long date) {
		Date temp = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(temp);
	}

	public static String getCurrentTime(String parrten) {
		String timestr;
		if (parrten == null || parrten.equals("")) {
			parrten = YYYY_MM_DD_FORMAT;
		}
		java.util.Date cday = new java.util.Date();

		SimpleDateFormat sdf = new SimpleDateFormat(parrten);
		timestr = sdf.format(cday);
		return timestr;
	}

}
