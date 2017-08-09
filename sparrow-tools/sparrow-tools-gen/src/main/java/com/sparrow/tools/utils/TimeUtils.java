package com.sparrow.tools.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class TimeUtils {
	public static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
	public static final String DATE_DEFAULT_FORMAT = "yyyyMMdd";
	public static final String DATE_STANDARD_FORMAT = "yyyy-MM-dd kk:mm:ss";
	public static final String DATE_D_FORMAT = "yyyy-MM-dd kk:mm:sss";

	// Default date long
	public static final long DEFAULT_DATE = -5364691200000L;

	public static final String DEFAULTFORMAT = "yyyy-MM-dd kk:mm:ss";

	// "1800-01-01 00:00:00.0"

	/**
     *
     */
	public static Calendar date2Calendar(Date date) {
		Calendar cal = null;
		if (date != null) {
			cal = GregorianCalendar.getInstance();
			cal.setTime(date);
		}
		return cal;
	}

	/**
	 * <p>
	 * <code>timestamp2Calendar</code>
	 * </p>
	 */
	public static Calendar timestamp2Calendar(Timestamp timestamp) {
		Calendar cal = null;
		if (timestamp != null) {
			cal = GregorianCalendar.getInstance();
			cal.setTime(timestamp);
		}
		return cal;
	}

	/**
	 * <p>
	 * <code>getDefaultTimestamp</code>
	 * </p>
	 */
	public static final Timestamp getDefaultTimestamp() {
		return new Timestamp(DEFAULT_DATE); // "1800-01-01 00:00:00.0"
	}

	/**
	 * <p>
	 * <code>getCurrentDate</code>
	 * </p>
	 */
	public static Calendar getCurrentDate() {
		return Calendar.getInstance();
	}

	/**
	 * <p>
	 * <code>getCurrentTimestamp</code>
	 * </p>
	 */
	public static Timestamp getCurrentTimestamp() {
		long time = System.currentTimeMillis();
		return new Timestamp(time);
	}

	/**
	 * <p>
	 * <code>getCurrentTimestamp</code>
	 * </p>
	 */
	public static Timestamp getCurrentTimestamp(String format) {
		if (format == null) {
			format = YYYY_MM_DD_FORMAT;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

		Date date = null;
		try {
			date = simpleDateFormat.parse(getCurrentTime(format));
		} catch (ParseException e) {
			return null;
		}
		return new Timestamp(date.getTime());
	}

	/**
	 * <p>
	 * <code>getCurrentTime</code>
	 * </p>
	 */
	public static String getCurrentTime(String parrten) {
		String timestr;
		if (parrten == null || parrten.equals("")) {
			parrten = YYYY_MM_DD_FORMAT;
		}
		Date cday = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat(parrten);
		timestr = sdf.format(cday);
		return timestr;
	}

	/**
	 * <p>
	 * <code>isDefaultTimestamp</code>
	 * </p>
	 */
	public static boolean isDefaultTimestamp(Timestamp time) {
		if (time.getTime() == DEFAULT_DATE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * <code>isDefaultTimestamp</code>
	 * </p>
	 */
	public static boolean isDefaultTimestamp(Object time) {
		if (time instanceof Timestamp) {
			Timestamp timeValue = (Timestamp) time;
			if (timeValue.getTime() == DEFAULT_DATE) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * <code>calendar2String</code>
	 * </p>
	 */
	public static final Timestamp calendar2Timestamp(Calendar cal) {

		Date date = null;
		date = cal.getTime();

		return new Timestamp(date.getTime());
	}

	/**
	 * <p>
	 * <code>date2String</code>
	 * </p>
	 */
	public static String date2String(Date date, String format) {
		if (format == null) {
			format = "yyyy-MM-dd";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);

		if (date == null) {
			return null;
		}
		return formatter.format(date);
	}

	public static String currentTime() {
		return date2String(new Date(), DATE_STANDARD_FORMAT);
	}

	public static String date2String(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_D_FORMAT);
		if (date == null) {
			return null;
		}
		return formatter.format(date);
	}

	/**
	 * <p>
	 * <code>timestamp2String</code>
	 * </p>
	 */
	public static String timestamp2String(String format, Timestamp time) {
		if (getDefaultTimestamp().equals(time)) {
			return "";
		}

		if (format == null) {
			format = YYYY_MM_DD_FORMAT;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);

		if (time == null) {
			return null;
		}
		return formatter.format(time);
	}

	/**
	 * <p>
	 * <code>string2Timestamp</code>
	 * </p>
	 */
	public static final Timestamp string2Timestamp(String format, String time) {
		if (format == null) {
			format = YYYY_MM_DD_FORMAT;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

		Date date = null;
		try {
			date = simpleDateFormat.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return new Timestamp(date.getTime());
	}

	/**
	 * <p>
	 * <code>string2Timestamp</code>
	 * </p>
	 */
	public static final Date string2Date(String format, String time) {
		if (format == null) {
			format = YYYY_MM_DD_FORMAT;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

		Date date = null;
		try {
			date = simpleDateFormat.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return date;
	}

	public static final String reFormatDateStr(String time) {
		return reFormatDateStr(time, DATE_STANDARD_FORMAT, null);
	}

	public static final String reFormatDateStr(String time, String format) {
		return reFormatDateStr(time, format, null);
	}

	/**
	 * <p>
	 * <code>string2Timestamp</code>
	 * </p>
	 */
	public static final String reFormatDateStr(String time, String format,
			String newformat) {
		if (format == null)
			format = YYYY_MM_DD_FORMAT;
		if (newformat == null)
			newformat = DATE_DEFAULT_FORMAT;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = simpleDateFormat.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return date2String(date, newformat);
	}

	/**
	 * <p>
	 * <code>string2Calendar</code>
	 * </p>
	 */
	public static final Calendar string2Calendar(String cal, String format) {
		if (format == null) {
			format = YYYY_MM_DD_FORMAT;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = simpleDateFormat.parse(cal);
		} catch (ParseException e) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * <p>
	 * <code>getMonthDays</code>
	 * </p>
	 */
	public static final int getMonthDays(int year, int month) {
		switch (month) {
		case 1:
			return 31;
		case 2:
			if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
				return 29;
			} else {
				return 28;
			}
		case 3:
			return 31;
		case 4:
			return 30;
		case 5:
			return 31;
		case 6:
			return 30;
		case 7:
			return 31;
		case 8:
			return 31;
		case 9:
			return 30;
		case 10:
			return 31;
		case 11:
			return 30;
		case 12:
			return 31;
		}
		return 0;
	}

	/**
	 * <p>
	 * <code>getDaysDiff</code>
	 * </p>
	 */
	public static int getDaysDiff(String sdate1, String sdate2, String format,
			java.util.TimeZone tz) {
		SimpleDateFormat df = new SimpleDateFormat(format);

		Date date1 = null;
		Date date2 = null;

		try {
			date1 = df.parse(sdate1);
			date2 = df.parse(sdate2);
		} catch (ParseException pe) {
			return -1;
		}

		Calendar cal1 = null;
		Calendar cal2 = null;

		if (tz == null) {
			cal1 = Calendar.getInstance();
			cal2 = Calendar.getInstance();
		} else {
			cal1 = Calendar.getInstance(tz);
			cal2 = Calendar.getInstance(tz);
		}

		// different date might have different offset
		cal1.setTime(date1);
		long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET)
				+ cal1.get(Calendar.DST_OFFSET);

		cal2.setTime(date2);
		long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET)
				+ cal2.get(Calendar.DST_OFFSET);

		// Use integer calculation, truncate the decimals
		int hr1 = (int) (ldate1 / 3600000); // 60*60*1000
		int hr2 = (int) (ldate2 / 3600000);

		int days1 = (int) hr1 / 24;
		int days2 = (int) hr2 / 24;
		int dateDiff = days2 - days1;
		return dateDiff;
		// int dateDiff = days2 - days1;
		// int weekOffset = (cal2.get(Calendar.DAY_OF_WEEK) -
		// cal1.get(Calendar.DAY_OF_WEEK)) < 0 ? 1 : 0;
		// int weekDiff = dateDiff / 7 + weekOffset;
		// int yearDiff = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
		// int monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) -
		// cal1.get(Calendar.MONTH);
	}

	public static int getDaysDiff(long sdate1, long sdate2,
			java.util.TimeZone tz) {

		Date date1 = new Date(sdate1);
		Date date2 = new Date(sdate2);

		Calendar cal1 = null;
		Calendar cal2 = null;

		if (tz == null) {
			cal1 = Calendar.getInstance();
			cal2 = Calendar.getInstance();
		} else {
			cal1 = Calendar.getInstance(tz);
			cal2 = Calendar.getInstance(tz);
		}

		// different date might have different offset
		cal1.setTime(date1);
		long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET)
				+ cal1.get(Calendar.DST_OFFSET);

		cal2.setTime(date2);
		long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET)
				+ cal2.get(Calendar.DST_OFFSET);

		int hr1 = (int) (ldate1 / 3600000); // 60*60*1000
		int hr2 = (int) (ldate2 / 3600000);

		int days1 = (int) hr1 / 24;
		int days2 = (int) hr2 / 24;
		int dateDiff = days2 - days1;
		return dateDiff;
	}

	public static int getSampleDaysDiff(long sdate1, long sdate2) {
		long ofs = sdate1 - sdate2;
		if (ofs < 0)
			ofs = -ofs;
		int dateDiff = (int) (ofs / 3600000);
		dateDiff /= 24;
		return dateDiff;
	}

	public static int getSampleHoursDiff(long sdate1, long sdate2) {
		long ofs = sdate1 - sdate2;
		if (ofs < 0)
			ofs = -ofs;
		int dateDiff = (int) (ofs / 3600000);
		return dateDiff;
	}

	/**
	 * <p>
	 * <code>getWeek</code>
	 * </p>
	 */
	public static Calendar getWeek(int num) {

		Calendar date = Calendar.getInstance();
		int weekOfYear = date.get(Calendar.WEEK_OF_YEAR);
		weekOfYear += num;
		date.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		return date;
	}

	/**
	 * <p>
	 * <code>getBeforeWeek</code>
	 * </p>
	 */
	public static Calendar getBeforeWeek(int num) {

		Calendar date = Calendar.getInstance();
		int weekOfYear = date.get(Calendar.WEEK_OF_YEAR);
		weekOfYear -= num;
		date.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		return date;
	}

	/**
	 * <p>
	 * <code>getMonth</code>
	 * </p>
	 */
	public static Calendar getMonth(int num) {

		Calendar date = Calendar.getInstance();
		int monthOfYear = date.get(Calendar.MONTH);
		monthOfYear += num;
		date.set(Calendar.MONTH, monthOfYear);
		return date;
	}

	/**
	 * <p>
	 * <code>getBeforeMonth</code>
	 * </p>
	 */
	public static Calendar getBeforeMonth(int num) {

		Calendar date = Calendar.getInstance();
		int monthOfYear = date.get(Calendar.MONTH);
		monthOfYear += num;
		date.set(Calendar.MONTH, monthOfYear);
		return date;
	}

	/**
	 * <p>
	 * <code>getDay</code>
	 * </p>
	 */
	public static Calendar getDay(int num) {

		Calendar date = Calendar.getInstance();
		int dayOfYear = date.get(Calendar.DATE);
		dayOfYear += num;
		date.set(Calendar.DATE, dayOfYear);
		return date;
	}

	/**
	 * <p>
	 * <code>date2TimestampStart</code>
	 * </p>
	 */
	public static Timestamp date2TimestampStart(Date date) {
		return new Timestamp(date.getTime());
	} // end date2TimestampStart

	/**
	 * <p>
	 * <code>getDateString</code>
	 * </p>
	 */
	public static String getDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_FORMAT);
		return format.format(date);
	} // end getDateString

	public static Date startDatePerDay() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String dastr = formatter.format(new Date());
		formatter = new SimpleDateFormat(DEFAULTFORMAT);
		try {
			return formatter.parse(dastr);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	public static String startDatePerDay(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return formatter.format(date);
	}

	public static String endDatePerDay() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		return formatter.format(new Date());
	}

	public static String endDatePerDay(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		return formatter.format(date);
	}

	public static String datetimeBeforeWeeks(Date date, int weeks) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, -weeks);
		// calendar.set(Calendar.MINUTE, 0);
		// calendar.set(Calendar.SECOND, 0);
		return startDatePerDay(calendar.getTime());
	}

	public static String datetimeBeforeDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -days); // 得到前一天
		// calendar.add(Calendar.MONTH, -1); //得到前一个月
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH)+1;
		return startDatePerDay(calendar.getTime());
	}

	public static String datetimeBeforeMonthes(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -months); // 得到前几月
		return startDatePerDay(calendar.getTime());
	}

	public static int getDays(Date date, String period) {
		if (StringUtils.isEmpty(period))
			return 0;
		period = period.toLowerCase();
		String numstr;
		char u;
		int pos = period.indexOf('/'), num;

		if (pos != -1) {
			numstr = period.substring(0, pos);
			u = period.charAt(pos + 1);
		} else {
			numstr = period;
			u = 'd';
		}

		try {
			num = Integer.parseInt(numstr);
		} catch (Exception e) {
			e.printStackTrace();
			num = 2;
		}
		Calendar calendar = Calendar.getInstance();
		switch (u) {
		case 'w':
			calendar.setTime(date);
			calendar.add(Calendar.WEEK_OF_YEAR, -num);
			break;
		case 'd':
			return num;
		case 'm':
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, -num); // 得到前几月
		}

		int hr1 = (int) (calendar.getTime().getTime() / 3600000); // 60*60*1000
		int hr2 = (int) (date.getTime() / 3600000);

		int days1 = (int) hr1 / 24;
		int days2 = (int) hr2 / 24;
		int dateDiff = days2 - days1;
		return dateDiff;
	}

	public static String getBeforeDateByPeriod(Date date, String period) {
		if (StringUtils.isEmpty(period))
			return date2String(date, DEFAULTFORMAT);
		period = period.toLowerCase();
		String numstr;
		char u;
		int pos = period.indexOf('/'), num;

		if (pos != -1) {
			numstr = period.substring(0, pos);
			u = period.charAt(pos + 1);
		} else {
			numstr = period;
			u = 'd';
		}

		try {
			num = Integer.parseInt(numstr);
		} catch (Exception e) {
			e.printStackTrace();
			num = 2;
		}

		switch (u) {
		case 'w':
			return datetimeBeforeWeeks(date, num);
		case 'd':
			return datetimeBeforeDays(date, num);
		case 'm':
			return datetimeBeforeMonthes(date, num);
		}
		return date2String(date, DEFAULTFORMAT);
	}

	public static String beforeWeeks(Date date, int weeks) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, -weeks);
		// calendar.add(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// calendar.add(Calendar.MONTH, -1); //得到前一个月
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH)+1;
		return date2String(calendar.getTime(), DEFAULTFORMAT);
	}

	public static void main(String args[]) {
		System.out.println(datetimeBeforeWeeks(new Date(), 4));
		System.out.println(getBeforeDateByPeriod(new Date(), "1/w"));
		System.out.println(timestamp2String(DEFAULTFORMAT,
				beforeDayToTimestamp(new Date(), 4)));
	}

	public static String beforeDay(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -days); // 得到前一天
		// calendar.add(Calendar.MONTH, -1); //得到前一个月
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH)+1;
		return date2String(calendar.getTime(), DEFAULTFORMAT);
	}

	public static Timestamp beforeDayToTimestamp(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -days); // 得到前一天

		return new Timestamp(calendar.getTimeInMillis());
	}

	public static long getBeforeMonth(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -months); // 得到前一天
		// calendar.add(Calendar.MONTH, -1); //得到前一个月
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH)+1;
		return calendar.getTimeInMillis();
	}

	public static Date beforeMonth(int months) {
		Calendar calendar = Calendar.getInstance();
		// calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -months); // 得到前一天
		// calendar.add(Calendar.MONTH, -1); //得到前一个月
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH)+1;
		return calendar.getTime();
	}

	public static String beforeMonthDates(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -months); // 得到前几月
		return date2String(calendar.getTime(), DEFAULTFORMAT);
	}

	public static String beforeMonthDates(int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -months); // 得到前一天
		return date2String(calendar.getTime(), DEFAULTFORMAT);
	}
}