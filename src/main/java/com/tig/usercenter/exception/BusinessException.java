package com.tig.usercenter.exception;

import com.tig.usercenter.common.ErrorCode;
import lombok.Getter;

/**
 * 异常抛出处理
 *
 * @author jsnlg
 * @date 2024-07-07 06:30:54
 * @version 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 6088091435661655180L;

    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
}
