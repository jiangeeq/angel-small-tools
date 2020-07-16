package com.jpsite.sensitiveJson2;

import lombok.Data;

@Data
public class Result<T> {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 不成功的原因
     */
    private String message;

    /**
     * 不成功的原因编码
     */
    private String errorCode;

    /**
     * 数据
     */
    private T data;

    /**
     * 错误实例
     */
    public static final Result<String> ERROR_STRING_RESULT = new Result(false, (String) null, "");

    /**
     * 成功实例
     */
    public static final Result<String> SUCCESS_STRING_RESULT = new Result(true, (String) null, "");


    public Result() {
        success = false;
        data = null;
    }

    /**
     * .ctor
     *
     * @param succ 是否成功
     * @param data 数据
     * @param msg  失败的原因
     */
    public Result(final boolean succ, final T data, final String msg) {
        this.success = succ;
        this.data = data;
        this.message = msg;
    }

    /**
     * .ctor
     *
     * @param succ 是否成功
     * @param data 数据
     * @param msg  失败的原因
     */
    public Result(final boolean succ, final T data, final String msg, final String errorCode) {
        this.success = succ;
        this.data = data;
        this.message = msg;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 是否成功
     *
     * @return true/false
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 静态构建成功实例
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 实例
     */
    public static <T> Result<T> buildSuccessResult(final T data) {
        return new Result<>(true, data, null);
    }

    /**
     * 静态构建成功实例
     *
     * @param <T> 数据类型
     * @return 实例
     */
    public static <T> Result<T> buildSuccessResult() {
        return new Result<>(true, null, null);
    }

    /**
     * 静态构建失败实例
     *
     * @param msg 失败原因
     * @param <T> 数据类型
     * @return 实例
     */
    public static <T> Result<T> buildFailedResult(final String msg) {
        return new Result<>(false, null, msg);
    }

    /**
     * 静态构建失败实例
     *
     * @param msg 失败原因
     * @param <T> 数据类型
     * @return 实例
     */
    public static <T> Result<T> buildFailedResult(final String errorCode, final String msg) {
        return new Result<>(false, null, msg, errorCode);
    }

    /**
     * 静态构建失败实例
     *
     * @param msg 失败原因
     * @param <T> 数据类型
     * @return 实例
     */
    public static <T> Result<T> buildFailedResult(final String errorCode, final String msg,final T data) {
        return new Result<>(false, data, msg, errorCode);
    }

    public static String getMessage(Result result) {
        return result == null ? "null" : result.getMessage();
    }

    @Override
    public String toString() {
        return success ? "成功" : "失败原因为:" + message;
    }
}
