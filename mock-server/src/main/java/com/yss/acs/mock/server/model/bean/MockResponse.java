package com.yss.acs.mock.server.model.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mock返回结果
 *
 * @author jiayy
 * @date 2020/6/28
 */
@Data
@NoArgsConstructor
public class MockResponse {

    private String serviceType;

    private int serviceStatus;

    private int serviceTime;

    private int resStatus;

    private String resType;

    private String resResult;

    private String wsdlContent;

}
