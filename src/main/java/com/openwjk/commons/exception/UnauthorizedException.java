package com.openwjk.commons.exception;

/**
 * @author wangjunkai
 * @description
 * @date 2023/8/17 14:57
 */
public class UnauthorizedException extends CommonsException {
    private String code;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, String code, String responseMsg) {
        super(message);
        this.code = code;
        this.setResponseMsg(responseMsg);
    }


    public String getCode() {
        return code;
    }
}
