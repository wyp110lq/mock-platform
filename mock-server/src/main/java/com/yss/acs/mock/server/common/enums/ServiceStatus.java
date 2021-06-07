package com.yss.acs.mock.server.common.enums;

/**
 * 服务状态
 *
 * @author jiayy
 * @date 2020/7/4
 */
public enum ServiceStatus {

    UNABLE(0, "不可用"),
    ENABLE(1, "可用");

    private int code;
    private String name;

    ServiceStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
