package com.sparrow.core.utils.date;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: chenlei
 * Date: 2010-3-21
 * Time: 18:12:01
 * Email: poison7@yeah.net
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATE_20 = "yy-MM-dd";
    public static final String PATTERN_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIME_20 = "yy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIME_20_NOS = "yy-MM-dd HH:mm";
    public static final String PATTERN_HOUR_TIME = "HH:mm:ss";
    public static final String PATTERN_CHINESE_HOUR_TIME = "HH时mm分ss秒";

    public static final String PATTERN_YEAR_HOUR_TIME = "yyyyMMdd_HH";
    public static final String PATTERN_YEAR_TIME = "yyyyMMdd";

    public static DateTime getCurrentDateTime() {
        DateTime d = new DateTime(System.currentTimeMillis());
        return d;
    }

    public static Date parseTime(String timeStr) {
        try {
            return parseDate(timeStr, new String[]{PATTERN_TIME});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseTime(String timeStr, String pattern) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat(pattern, Locale.US);
            Date d = parser.parse(timeStr);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseTime(String timeStr, String[] patterns) {
        try {
            return parseDate(timeStr, patterns);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseDate(String timeStr) {
        try {
            return parseDate(timeStr, new String[]{PATTERN_DATE});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date parseTime(String timeStr, String pattern, int pos) {
        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(true);
        parser.applyPattern(pattern);
        String str = timeStr;
        Date d = parser.parse(str, new ParsePosition(pos));
        return d;
    }

    public static String currentTime() {
        SimpleDateFormat fmt = new SimpleDateFormat(PATTERN_TIME_20);
        return fmt.format(new Date());
    }

    public static String currentTime(String pattern) {
        SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.US);
        return fmt.format(new Date());
    }

    public static String currentDate() {
        SimpleDateFormat fmt = new SimpleDateFormat(PATTERN_DATE);
        return fmt.format(new Date());
    }

    public static String formatDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat(PATTERN_DATE);
        return fmt.format(date);
    }

    public static String formatDate(long times, String pattern) {
        SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.US);
        return fmt.format(new Date(times));
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.US);

        return fmt.format(date);
    }

    public static String formatExpiredDate(Date date, String pattern) {
        SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.US);
        fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return fmt.format(date);
    }

    public static String formatTime(Date date) {
        return formatDate(date, PATTERN_TIME);
    }

    public static String formatHourTime(Date date) {
        return formatDate(date, PATTERN_HOUR_TIME);
    }

    public static String formatChineseHourTime(Date date) {
        return formatDate(date, PATTERN_CHINESE_HOUR_TIME);
    }

    public static String getMinuteScale(Date date, int interval) {
        Calendar begin = new GregorianCalendar();
        begin.setTime(date);
        int fieldNum = begin.get(Calendar.MINUTE);
        int scale = fieldNum / interval + 1;
        return "0" + scale;
    }

    public static Date[] getTimeSlice(Date now, int interval, int field) {
        Date[] d = new Date[2];
        Calendar begin = new GregorianCalendar();
        begin.setTime(now);

        int fieldNum = begin.get(field);
        int a = fieldNum / interval;
        begin.set(field, a * interval);
        //System.out.println(begin.getTime());
        switch (field) {
            case Calendar.MINUTE:
                begin.set(Calendar.SECOND, 0);
                begin.set(Calendar.MILLISECOND, 0);
                break;
            case Calendar.HOUR:
                begin.set(Calendar.MINUTE, 0);
                begin.set(Calendar.SECOND, 0);
                begin.set(Calendar.MILLISECOND, 0);
                break;

        }
        //System.out.println(begin.getTime());
        d[0] = begin.getTime();
        begin.add(field, interval);
        d[1] = begin.getTime();
        return d;
    }

    public static Date[] getTimeSlice(Date now, int interval) {
        if (interval > 60) {
            return getTimeSlice(now, interval / 60, Calendar.HOUR);
        }
        return getTimeSlice(now, interval, Calendar.MINUTE);
    }

    public static Integer getPassedHours(DateTime startTime, DateTime endTime) {
        Interval interval = new Interval(startTime, endTime);
        Period period = interval.toPeriod(PeriodType.hours());
        Integer passedHours = period.getHours();
        return passedHours;
    }

    public static void main(String[] args) {
        Date d[] = getTimeSlice(new Date(), 60 * 24);
        System.out.println(d[0]);
        System.out.println(d[1]);

        Integer passedHours = getPassedHours(new DateTime().minusDays(10), new DateTime());
        System.out.println("passedHours = " + passedHours);
    }

    public static Date afterMinutes(Date date, int counts) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, +counts);
        return calendar.getTime();
    }
}