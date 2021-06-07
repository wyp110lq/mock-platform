package com.yss.acs.mock.server.model.base;

/**
 * 分页请求参数
 *
 * @author jiayy
 * @date 2020/6/29
 */
public class PageParam {

    /** 当前记录起始索引 */
    private int pageNum = 1;

    /** 每页显示记录数 */
    private int pageSize = 20;

    public int getPageNum() {
        return pageNum > 1 ? pageNum - 1 : 0;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
