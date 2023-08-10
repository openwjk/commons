package com.openwjk.commons.enums;

/**
 * @author wangjunkai
 * @description
 * @date 2023/7/30 8:33
 */
public enum ResponseEnum {
    SUCCESS("00", "success."),

    UNAUTHORIZED("10", "unauthorized."),
    SIGN_CHECK_FAIL("11", "signature is invalid."),
    PARAM_FORMAT_ERROR("12", "check param error."),
    INSUFFICIENT_PERMISSION("13", "insufficient permissions."),
    IP_NOT_ALLOW_ACCESS("14", "ip don't allow access."),
    REPEAT_COMMIT("15", "don't repeat commit."),

    PARAM_CHECK_FAIL("20", "param is invalid."),
    PARAM_STRATEGY_INVALID("21", "request strategy is invalid."),
    RPC_FAIL("30", "call remote service occur error."),

    NOT_ALLOWED("40", "current status don't allow operation."),

    SYSTEM_ERROR("99", "error.");

    private String code;
    private String msg;

    ResponseEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
