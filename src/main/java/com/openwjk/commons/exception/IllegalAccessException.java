package com.openwjk.commons.exception;

/**
 * @author wangjunkai
 * @description
 * @date 2023/8/17 15:36
 */
public class IllegalAccessException extends CommonsException{
    public IllegalAccessException(String message) {
        super(message);
    }

    public IllegalAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
