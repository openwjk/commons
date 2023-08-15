package com.openwjk.commons.exception;

public class CommonsException extends RuntimeException {
    private String responseMsg;

    public CommonsException(String message) {
        super(message);
    }

    public CommonsException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}
