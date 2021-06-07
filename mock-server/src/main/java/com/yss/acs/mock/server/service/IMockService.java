package com.yss.acs.mock.server.service;

import com.yss.acs.mock.server.model.bean.MockRequest;
import com.yss.acs.mock.server.model.bean.MockResponse;

import java.util.Map;

/**
 * Mock服务接口
 *
 * @author jiayy
 * @date 2020/6/28
 */
public interface IMockService {

    /**
     * 根据请求获取Mock结果
     *
     * @param mockRequest
     * @return
     */
    MockResponse getMockResponse(MockRequest mockRequest);

    /**
     * 获取类默认的Mock结果
     *
     * @param clazz
     * @return
     */
    Map<String, Object> getDefaultMockResult(Class clazz);

    /**
     * 获取配置的Mock结果
     *
     * @param clazz
     * @param resResult
     * @return
     */
    Map<String, Object> getMockConfigResult(Class clazz, String resResult);

}
