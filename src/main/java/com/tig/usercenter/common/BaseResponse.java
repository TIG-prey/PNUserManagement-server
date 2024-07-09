package com.tig.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应类
 *
 * @author jsnlg
 * @date 2024-07-07 05:26:08
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = -4260319694499324609L;

    private int code;
    private T data;
    private String message;
    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }
    public BaseResponse(int code, T data, String message) {
        this(code,data,message,"");
    }
    public BaseResponse(int code, T data) {
        this(code,data,"","");
    }
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
}
