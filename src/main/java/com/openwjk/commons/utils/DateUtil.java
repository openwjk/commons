package com.openwjk.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 13:51
 */
public class DateUtil {
    public static final String FORMAT_DATE_COMPACT = "yyyyMMdd";
    public static final String FORMAT_DATE_COMPACT_TILL_YEAR = "yyyy";
    public static final String FORMAT_DATE_NORMAL = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME_COMPACT = "yyyyMMddHHmmss";
    public static final String FORMAT_DATETIME_COMPACT_DAY = "yyyyMMdd";
    public static final String FORMAT_DATETIME_COMPACT_SPACE = "yyyyMMdd HHmmss";
    public static final String FORMAT_DATETIME_NORMAL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIME_TILL_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_TIMESTAMP_NORMAL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_DATE_COMPACT_TILL_MONTH = "yyyyMM";
    public static final String FORMAT_DATE_NORMAL_TILL_MONTH = "yyyy-MM";
    public static final String FORMAT_TIME_COMPACT = "HHmmss";

    static {
        System.setProperty("user.timezone", "Etc/GMT-8");
    }

    public static Date getNow() {
        return new Date();
    }

    public static String getCurrentTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static Integer getYear(Date date) {
        return dateToLocalDate(date).getYear();
    }

    public static Integer getMonth(Date date) {
        return dateToLocalDate(date).getMonth().getValue();
    }

    public static Integer getDayOfMonth(Date date) {
        return dateToLocalDate(date).getDayOfMonth();
    }

    private static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    public static Date parseDate(String dateStr, String formatPattern) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formatPattern);
        return fmt.parseDateTime(dateStr).toDate();
    }

    public static Date getNowAtStart() {
        DateTime dateTime = DateTime.now();
        dateTime = dateTime.withTimeAtStartOfDay();
        return dateTime.toDate();
    }

    public static Date getTomorrowAtStart() {
        DateTime dateTime = DateTime.now();
        dateTime = dateTime.plusDays(1);
        dateTime = dateTime.withTimeAtStartOfDay();
        return dateTime.toDate();
    }

    public static Date getYesterdayAtStart() {
        Date now = new Date();
        DateTime dateTime = new DateTime(now);
        dateTime = dateTime.minusDays(1);
        dateTime = dateTime.withTimeAtStartOfDay();
        return dateTime.toDate();
    }

    public static Date plusYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
    }

    public static Date plusMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    public static Date plusDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }

    public static Date plusHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

    public static Date plusMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    public static Date plusSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    public static String formatDate(Date dt, String formatPattern) {
        DateTime dateTime = new DateTime(dt);
        return dateTime.toString(formatPattern);
    }

    public static String formatNow(String formatPattern) {
        return formatDate(getNow(), formatPattern);
    }

    public boolean checkDateFormat(String value, String format) {
        try {
            DateUtil.parseDate(value, format);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
