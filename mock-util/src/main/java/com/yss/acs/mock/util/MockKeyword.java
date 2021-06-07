package com.yss.acs.mock.util;

/**
 * Mock结果关键字
 *
 * @author jiayy
 * @date 2020/6/29
 */
public enum MockKeyword {

    RANDOM_INT("@int", "随机整数"),
    RANDOM_STRING("@string", "随机字符串"),
    RANDOM_BOOLEAN("@boolean", "随机布尔值"),
    DATE("@date", "当前日期"),
    DATETIME("@datetime", "当前时间");

    private String code;
    private String name;

    MockKeyword(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
