package com.yss.acs.mock.server.common.exception;

/**
 * 自定义异常类
 *
 * @author jiayy
 * @date 2020/6/29
 */
public class MockException extends RuntimeException {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MockException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
