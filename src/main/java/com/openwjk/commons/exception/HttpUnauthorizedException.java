package com.openwjk.commons.exception;


public class HttpUnauthorizedException extends CommonsException {
    public HttpUnauthorizedException() {
        super("receive httpStatusCode 401");
    }

    public HttpUnauthorizedException(String msg, Throwable cause) {
        super("receive httpStatusCode 401, " + msg, cause);
    }
}
