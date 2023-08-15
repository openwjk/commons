package com.openwjk.commons.exception;


public class ParamInvalidException extends CommonsException {
    private String paramName;
    private Object paramValue;

    public ParamInvalidException(String message) {
        super(message);
    }

    public ParamInvalidException(String message, String paramName, Object paramValue) {
        super(message);
        this.paramName = paramName;
        this.paramValue = paramValue;
    }
    public ParamInvalidException(String message, String paramName, Object paramValue, String responseMsg) {
        super(message);
        this.paramName = paramName;
        this.paramValue = paramValue;
        setResponseMsg(responseMsg);
    }

    public String getParamName() {
        return paramName;
    }

    public Object getParamValue() {
        return paramValue;
    }

}
