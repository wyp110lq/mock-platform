package com.yss.acs.mock.server.model.base;

import java.util.List;

/**
 * 分页返回结果
 *
 * @author jiayy
 * @date 2020/6/29
 */
public class PageResult<T> {

    private int code;

    private String msg;

    private long total;

    private List<T> rows;

    public PageResult() {
    }

    public PageResult(int code, String msg, long total, List<T> rows) {
        this.code = code;
        this.msg = msg;
        this.total = total;
        this.rows = rows;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public static <T> PageResult success(long total, List<T> rows) {
        return new PageResult(0, "操作成功", total, rows);
    }

}
