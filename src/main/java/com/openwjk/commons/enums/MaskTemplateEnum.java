package com.openwjk.commons.enums;

/**
 * @author wangjunkai
 * @description
 * @date 2023/7/21 10:53
 */
public enum MaskTemplateEnum {
    DEFAULT_PHONE(3, 4),
    DEFAULT_CERTNO(0, 6);
    private Integer prefix;
    private Integer suffix;

    MaskTemplateEnum(Integer prefix, Integer suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public Integer getPrefix() {
        return prefix;
    }

    public Integer getSuffix() {
        return suffix;
    }
}
