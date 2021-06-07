package com.yss.acs.mock.server.model.base;

/**
 * 返回结果
 *
 * @author jiayy
 * @date 2020/6/29
 */
public class Result<T> {

    private int code;

    private String msg;

    private T data;

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Result success(T data) {
        return new Result(0, "success", data);
    }

    public static <T> Result success() {
        return new Result(0, "操作成功", null);
    }

    public static <T> Result error(String msg) {
        return new Result(1, msg, null);
    }

}
