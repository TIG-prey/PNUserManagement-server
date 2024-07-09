package com.tig.usercenter.common;

/**
 * @author jsnlg
 * @version 1.0.0
 * @date 2024-07-07 04:52:56
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @return {@link BaseResponse }<{@link T }>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return {@link BaseResponse }
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static <T> BaseResponse<T> error(int code,String message,String description) {
        return new BaseResponse<>(code,null, message, description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message,String description) {
        return new BaseResponse<>(errorCode.getCode(),null, message, description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode,String description) {
        return new BaseResponse<>(errorCode.getCode(),null, errorCode.getMessage(), description);
    }
}
