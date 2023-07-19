package com.openwjk.commons.utils;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 14:55
 */
public class SystemUtil {
    private static final String OS_NAME = "os.name";

    public static String getOsName() {
        return System.getProperty(OS_NAME);
    }
}
