package com.openwjk.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 14:01
 */
public class IdnUtil {
    private static String[] PROVINCE_CODE = {"11", "12", "13", "14", "15", "21",
            "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42",
            "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
            "63", "64", "65", "71", "81", "82", "91"};
    /**
     * 每位加权因子
     */
    private static int[] POWER = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
            8, 4, 2};

    public static int getAgeByIdn(String idn) {
        if (StringUtils.length(idn) == 15) {
            return getAgeByIdnAndDate(idn, new Date());
        } else if (StringUtils.length(idn) == 18) {
            return getAgeByIdnAndDate(idn, new Date());
        } else {
            throw new RuntimeException("idn.length is not correct");
        }
    }

    public static int getAgeByIdnAndDate(String idn, Date date) {
        if (StringUtils.length(idn) == 15) {
            int birthYear = calcYearFor15Idn(idn.substring(6, 8));
            int birthMonth = Integer.valueOf(idn.substring(8, 10));
            int birthDay = Integer.valueOf(idn.substring(10, 12));
            return calcAge(birthYear, birthMonth, birthDay, date);
        } else if (StringUtils.length(idn) == 18) {
            int birthYear = Integer.valueOf(idn.substring(6, 10));
            int birthMonth = Integer.valueOf(idn.substring(10, 12));
            int birthDay = Integer.valueOf(idn.substring(12, 14));
            return calcAge(birthYear, birthMonth, birthDay, date);
        } else {
            throw new RuntimeException("idn.length is not correct");
        }
    }

    /**
     * 计算15位身份证中的年份，年份(2位)>="05"则加上前缀"19"；否则加上前缀"20"
     * 备注：十八位身份证号码是中国大陆正式启用的第二代居民身份证，启用时间为2004年3月29日。
     *
     * @param yearStr 15位身份证中的年份(2位)
     * @return 计算后的年份(4位)
     */
    protected static int calcYearFor15Idn(String yearStr) {
        int year = Integer.valueOf(yearStr);
        if (year >= 5) {
            return Integer.valueOf("19" + yearStr);
        } else {
            return Integer.valueOf("20" + yearStr);
        }
    }

    private static int calcAge(int birthYear, int birthMonth, int birthDay, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int age = calendar.get(Calendar.YEAR) - birthYear;
        int nowMonth = calendar.get(Calendar.MONTH) + 1;
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (birthMonth > nowMonth
                || (birthMonth == nowMonth
                && birthDay > nowDay)) {
            age--;
        }
        return age;
    }

    public static boolean isIDNumber(String idNumber) {
        return validateIdnLength(idNumber) && isValidatedAllIdcard(idNumber);
    }

    public static boolean validateIdnLength(String idn) {
        int len = StringUtils.length(idn);
        if (len != 15 && len != 18) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidatedAllIdcard(String idcard) {
        if (idcard == null || "".equals(idcard)) {
            return false;
        }
        if (idcard.length() == 15) {
            return validate15IDCard(idcard);
        }
        return validate18Idcard(idcard);
    }

    private static boolean validate15IDCard(String idcard) {
        if (idcard == null) {
            return false;
        }
        // 非15位为假
        if (idcard.length() != 15) {
            return false;
        }
        // 15全部为数字
        if (!isDigital(idcard)) {
            return false;
        }
        String provinceid = idcard.substring(0, 2);
        // 校验省份
        if (!checkProvinceid(provinceid)) {
            return false;
        }
        String birthday = idcard.substring(6, 12);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            // 身份证日期错误
            if (!tmpDate.equals(birthday)) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static boolean validate18Idcard(String idcard) {
        if (idcard == null) {
            return false;
        }

        // 非18位为假
        if (idcard.length() != 18) {
            return false;
        }
        // 获取前17位
        String idcard17 = idcard.substring(0, 17);

        // 前17位全部为数字
        if (!isDigital(idcard17)) {
            return false;
        }

        String provinceid = idcard.substring(0, 2);
        // 校验省份
        if (!checkProvinceid(provinceid)) {
            return false;
        }

        // 校验出生日期
        String birthday = idcard.substring(6, 14);

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMAT_DATE_COMPACT);

        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            if (!tmpDate.equals(birthday)) {// 出生年月日不正确
                return false;
            }

        } catch (ParseException e1) {

            return false;
        }

        // 获取第18位
        String idcard18Code = idcard.substring(17, 18);

        char c[] = idcard17.toCharArray();

        int bit[] = converCharToInt(c);

        int sum17 = 0;

        sum17 = getPowerSum(bit);

        // 将和值与11取模得到余数进行校验码判断
        // 或者将身份证的第18位与算出来的校码进行匹配，不相等就为假
        String checkCode = getCheckCodeBySum(sum17);
        if (StringUtils.isBlank(checkCode)) {
            return false;
        } else if (!idcard18Code.equalsIgnoreCase(checkCode)) {
            return false;
        }

        return true;
    }

    /**
     * 将字符数组转为整型数组
     */
    private static int[] converCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c) {
            a[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return a;
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     */
    private static int getPowerSum(int[] bit) {
        int sum = 0;
        if (POWER.length != bit.length) {
            return sum;
        }
        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < POWER.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * POWER[j];
                }
            }
        }
        return sum;
    }

    /**
     * 将和值与11取模得到余数进行校验码判断
     *
     * @return 校验位
     */
    private static String getCheckCodeBySum(int sum17) {
        String checkCode = null;
        switch (sum17 % 11) {
            case 10:
                checkCode = "2";
                break;
            case 9:
                checkCode = "3";
                break;
            case 8:
                checkCode = "4";
                break;
            case 7:
                checkCode = "5";
                break;
            case 6:
                checkCode = "6";
                break;
            case 5:
                checkCode = "7";
                break;
            case 4:
                checkCode = "8";
                break;
            case 3:
                checkCode = "9";
                break;
            case 2:
                checkCode = "x";
                break;
            case 1:
                checkCode = "0";
                break;
            case 0:
                checkCode = "1";
                break;
            default:
                throw new RuntimeException("check code by sun is fail");
        }
        return checkCode;
    }

    private static boolean checkProvinceid(String provinceid) {
        for (String id : PROVINCE_CODE) {
            if (id.equals(provinceid)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDigital(String str) {
        return str.matches("^[0-9]*$");
    }

    /**
     * 根据idn返回出生日期
     *
     * @param idn
     * @return yyyy-MM-dd
     */
    public static String getBirthDateByIdn(String idn) {
        boolean idNumber = isIDNumber(idn);
        if (idNumber) {
            String birthDate = null;
            if (idn.length() == 18) {
                birthDate = new StringBuilder()
                        .append(idn.substring(6, 10))
                        .append("-")
                        .append(idn.substring(10, 12))
                        .append("-")
                        .append(idn.substring(12, 14))
                        .toString();
            } else if (idn.length() == 15) {
                birthDate = new StringBuilder()
                        .append("19")
                        .append(idn.substring(6, 8))
                        .append("-")
                        .append(idn.substring(8, 10))
                        .append("-")
                        .append(idn.substring(10, 12))
                        .toString();
            }
            return birthDate;
        } else {
            return null;
        }
    }

    /**
     * 根据身份证获取性别
     *
     * @param idn
     * @return 0 - 女, 1 - 男
     */
    public static String getGenderByIdn(String idn) {
        boolean idNumber = isIDNumber(idn);
        if (idNumber) {
            String gender = null;
            if (idn.length() == 18) {
                gender = Integer.parseInt(idn.substring(idn.length() - 4, idn.length() - 1)) % 2 == 0 ? "0" : "1";
            } else if (idn.length() == 15) {
                gender = Integer.parseInt(idn.substring(idn.length() - 3, idn.length())) % 2 == 0 ? "0" : "1";
            }
            return gender;
        } else {
            return null;
        }
    }

    /**
     * 根据身份证获取性别
     *
     * @param idn
     * @return 0 - 女, 1 - 男
     */
    public static Integer getGenderNumberByIdn(String idn) {
        String genderByIdn = getGenderByIdn(idn);
        if (StringUtils.isNotBlank(genderByIdn)) {
            return Integer.parseInt(genderByIdn);
        }
        return null;
    }
}
