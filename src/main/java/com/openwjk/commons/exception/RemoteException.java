package com.openwjk.commons.exception;


public class RemoteException extends CommonsException {
    public RemoteException(String msg) {
        super(msg);
    }

    public RemoteException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
