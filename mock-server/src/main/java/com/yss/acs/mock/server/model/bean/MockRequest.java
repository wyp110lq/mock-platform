package com.yss.acs.mock.server.model.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Mock请求信息
 *
 * @author jiayy
 * @date 2020/6/28
 */
@Data
@AllArgsConstructor
public class MockRequest {

    private String reqUri;

    private String reqMethod;

    private String reqContentType;

    public MockRequest(String reqUri) {
        this.reqUri = reqUri;
    }

}
