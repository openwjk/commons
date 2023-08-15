package com.openwjk.commons.domain;

import com.google.common.base.Objects;
import com.openwjk.commons.enums.ResponseEnum;

import java.io.Serializable;

/**
 * @author wangjunkai
 * @description
 * @date 2023/7/30 8:56
 */
public class ResponseVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String isSuccess;
    private String msg;
    private T data;

    public ResponseVO(T data) {
        this.isSuccess = ResponseEnum.SUCCESS.getCode();
        this.msg = ResponseEnum.SUCCESS.getMsg();
        this.data = data;
    }

    public ResponseVO(ResponseEnum responseEnum) {
        this.isSuccess = responseEnum.getCode();
        this.msg = responseEnum.getMsg();
    }

    public ResponseVO(String isSuccess, String msg) {
        this.isSuccess = isSuccess;
        this.msg = msg;
    }

    public ResponseVO() {
        this.isSuccess = ResponseEnum.SUCCESS.getCode();
        this.msg = ResponseEnum.SUCCESS.getMsg();
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseVO that = (ResponseVO) o;
        return Objects.equal(isSuccess, that.isSuccess) && Objects.equal(msg, that.msg) && Objects.equal(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isSuccess, msg, data);
    }

    @Override
    public String toString() {
        return "ResponseVO{" +
                "isSuccess='" + isSuccess + '\'' +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
