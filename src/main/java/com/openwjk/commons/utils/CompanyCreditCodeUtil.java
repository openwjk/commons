package com.openwjk.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
/**
 * @author wangjunkai
 * @description
 * @date 2023/7/20 14:01
 */
public class CompanyCreditCodeUtil {

    private static final String BASE_CODE_STRING = "0123456789ABCDEFGHJKLMNPQRTUWXY";
    private static final char[] BASE_CODE_ARRAY = BASE_CODE_STRING.toCharArray();
    private static final List<Character> BASE_CODES = new ArrayList<>();
    private static final String BASE_CODE_REGEX = "[" + BASE_CODE_STRING + "]{18}";
    private static final int[] WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};

    static {
        for (char c : BASE_CODE_ARRAY) {
            BASE_CODES.add(c);
        }
    }
    /**
     * 是否是有效的统一社会信用代码
     *
     * @param socialCreditCode 统一社会信用代码
     * @return
     */
    public static boolean isValidSocialCreditCode(String socialCreditCode) {
        if (StringUtils.isBlank(socialCreditCode) || !Pattern.matches(BASE_CODE_REGEX, socialCreditCode)) {
            return false;
        }
        char[] businessCodeArray = socialCreditCode.toCharArray();
        char check = businessCodeArray[17];
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char key = businessCodeArray[i];
            sum += (BASE_CODES.indexOf(key) * WEIGHT[i]);
        }
        int value = 31 - sum % 31;
        return check == BASE_CODE_ARRAY[value % 31];
    }

}
