package com.openwjk.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 13:54
 */
public class ParamCheckUtil {
    public static boolean checkIsNumber(String number) {
        try {
            if (StringUtils.isNotBlank(number) && (number.contains("e") || number.contains("E")))
                return false;
            new BigDecimal(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
