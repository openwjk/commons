package com.openwjk.commons.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChineseCalendar {
    // 阳历日期
    private Date date;
    // 农历日期
    private Date nDate;

    // 是否是闰月
    private boolean runyue;

    // 农历年份
    private int nYear;
    // 农历月份
    private int nMonth;
    // 农历日
    private int nDay;

    final static SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    final static String[] chineseNumber = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};

    final String[] animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    final static long[] lunarInfo = new long[]{0x04bd8, 0x04ae0, 0x0a570,
            0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
            0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
            0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
            0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
            0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
            0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
            0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
            0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
            0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
            0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
            0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
            0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
            0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
            0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
            0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
            0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
            0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};


    private ChineseCalendar() {
    }

    /**
     * 初始化
     *
     * @param dateStr     阴历年月日 yyyy-MM-dd
     * @param isLeapMonth 是否是润月中的后面的月
     * @return 农历
     */
    public static ChineseCalendar asChinaDate(String dateStr, Boolean isLeapMonth) {
        try {
            return asChinaDate(dateFormat.parse(dateStr), isLeapMonth);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChineseCalendar asChinaDate(Date date, Boolean isLeapMonth) {
        int year, month, day;
        ChineseCalendar calendar = new ChineseCalendar();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        int days = 0;
        // 年的总天数
        for (int iYear = 1900; iYear < year; iYear++) {
            days += yearDays(iYear);
        }
        // 月的总天数
        for (int iMonth = 1; iMonth < month; iMonth++) {
            days += monthDays(year, iMonth);
        }
        int leapMonth = leapMonth(year); // 闰哪个月,1-12 （双月）
        if ((isLeapMonth && leapMonth == month)) {
            days += monthDays(year, month);
        } else if (leapMonth < month) {
            days += leapDays(year);
        }
        days += day;
        Date baseDate = null;
        Date baseDate2 = null;
        try {
            baseDate = chineseDateFormat.parse("1900年1月31日");
            baseDate2 = chineseDateFormat.parse("1970年1月1日");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 求出1970年1月1日和1900年1月31日相差的天数
        int offset = (int) ((baseDate2.getTime() - baseDate.getTime()) / 86400000L);
        days = days - offset - 1;
        long time = 1000L * days * 24 * 60 * 60;
        calendar.date = new Date(time);
        calendar.nYear = year;
        calendar.nMonth = month;
        calendar.nDay = day;
        calendar.runyue = leapMonth == month;

        try {
            calendar.nDate = chineseDateFormat.parse(calendar.nYear + "年" + calendar.nMonth + "月" + calendar.nDay + "日");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static ChineseCalendar as(String dateStr) {
        try {
            return as(dateFormat.parse(dateStr));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChineseCalendar as(Date date) {

        ChineseCalendar chin = new ChineseCalendar();
        chin.date = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int leapMonth = 0;
        Date baseDate = null;
        try {
            baseDate = chineseDateFormat.parse("1900年1月31日");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 求出和1900年1月31日相差的天数
        assert baseDate != null;
        int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);

        int iYear, daysOfYear = 0;
        for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
            daysOfYear = yearDays(iYear);
            offset -= daysOfYear;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
        }
        // 农历年份
        chin.nYear = iYear;
        leapMonth = leapMonth(iYear); // 闰哪个月,1-12
        chin.runyue = false;
        // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            // 闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !chin.runyue) {
                --iMonth;
                chin.runyue = true;
                daysOfMonth = leapDays(chin.nYear);
            } else {
                daysOfMonth = monthDays(chin.nYear, iMonth);
            }
            offset -= daysOfMonth;
            // 解除闰月
            if (chin.runyue && iMonth == (leapMonth + 1)) {
                chin.runyue = false;
            }
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (chin.runyue) {
                chin.runyue = false;
            } else {
                chin.runyue = true;
                --iMonth;
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
        }
        chin.nMonth = iMonth;
        chin.nDay = offset + 1;

        try {
            chin.nDate = chineseDateFormat.parse(chin.nYear + "年" + chin.nMonth + "月" + chin.nDay + "日");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chin;
    }


    // ====== 传回农历 y年的总天数
    private static int yearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0) {
                sum += 1;
            }
        }
        return (sum + leapDays(y));
    }

    // ====== 传回农历 y年闰月的天数
    private static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0) {
                return 30;
            } else {
                return 29;
            }
        } else {
            return 0;
        }
    }

    // ====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
    private static int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    // ====== 传回农历 y年m月的总天数
    private static int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0) {
            return 29;
        } else {
            return 30;
        }
    }

    public static String getChinaDayString(int day) {
        String[] chineseTen = {"初", "十", "廿", "卅"};
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30) {
            return "";
        }
        if (day == 10) {
            return "初十";
        } else {
            return chineseTen[day / 10] + chineseNumber[n + 1];
        }
    }

    private String getChinaYearStr() {
        String yearStr = String.valueOf(nYear);
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < yearStr.length(); i++) {
            char s = yearStr.charAt(i);
            res.append(chineseNumber[Integer.parseInt(String.valueOf(s))]);
        }
        return res.toString();
    }


    /**
     * 获取年月日的中国说法
     *
     * @return 日期
     */
    public String getChinaString() {
        return getChinaYearStr() + "年" + (runyue ? "闰" : "") + chineseNumber[nMonth] + "月"
                + getChinaDayString(nDay);
    }

    /**
     * 获取阴历的年
     *
     * @return 年
     */
    public int getChinaYear() {
        return nYear;
    }

    /**
     * 获取阴历的月
     *
     * @return 月
     */
    public int getChinaMonth() {
        return nMonth;
    }

    /**
     * 获取阴历的日
     *
     * @return 日
     */
    public int getChinaDay() {
        return nDay;
    }

    /**
     * 获取阴历的生肖
     *
     * @return 生肖
     */
    public String getChinaZodiac() {
        return animals[(nYear - 4) % 12];
    }

    /**
     * 获取阳历日期
     *
     * @return 阳历日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 获取阴历日期
     *
     * @return 阳历日期
     */
    public Date getNDate() {
        return nDate;
    }

    /**
     * 获取是否为润月中的第二个月
     *
     * @return 阳历日期
     */
    public boolean isRunyue() {
        return runyue;
    }
}
