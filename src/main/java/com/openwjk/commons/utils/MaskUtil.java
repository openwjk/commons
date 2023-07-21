package com.openwjk.commons.utils;

import com.openwjk.commons.enums.MaskTemplateEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangjunkai
 * @description
 * @date 2023/7/21 10:01
 */
public class MaskUtil {

    private static final String[] MASK = {"", "*", "**", "***", "****", "*****",
            "******", "*******", "********", "*********", "**********",
            "***********", "************", "*************", "**************",
            "***************", "****************", "*****************", "******************"};

    public static String mask(String src, int start, int end) {
        if (StringUtils.isEmpty(src)) {
            return src;
        }
        int length = StringUtils.length(src);
        int maskLen = length - (start + end);
        if (maskLen <= 0) {
            return src;
        }
        String prefix = StringUtils.substring(src, 0, start);
        String suffix = StringUtils.substring(src, length - end);
        return prefix + MASK[maskLen] + suffix;
    }

    public static String mask(String src, MaskTemplateEnum template) {
        return mask(src, template.getPrefix(), template.getSuffix());
    }


}
