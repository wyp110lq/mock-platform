package com.yss.acs.mock.server.common.proxy;

import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.utils.BeanUtil;
import com.yss.acs.mock.server.common.utils.CommonUtil;
import com.yss.acs.mock.server.common.utils.LocalMap;
import com.yss.acs.mock.server.common.utils.MockUtil;
import com.yss.acs.mock.server.model.bean.MockRequest;
import com.yss.acs.mock.server.model.bean.MockResponse;
import com.yss.acs.mock.server.service.IMockService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Mock方法代理
 *
 * @author jiayy
 * @date 2020/7/11
 */
@Slf4j
public class MockMethodProxy implements InvocationHandler {

    @Override
    public Object invoke(Object obj, Method method, Object[] args) {
        String uri = LocalMap.get(Constants.CACHE_URI_KEY);
        if (uri.startsWith(Constants.WEBSERVICE_URI_PREFIX)) {
            uri = uri.substring(Constants.WEBSERVICE_URI_PREFIX.length());
        } else if (uri.startsWith(Constants.HESSIAN_URI_PREFIX)) {
            uri = uri.substring(Constants.HESSIAN_URI_PREFIX.length());
        }
        String methodName = method.getName();
        try {
            if (CommonUtil.isObjDefaultMethod(methodName)) {
                return defaultResult(obj, method, args);
            }
            if (CommonUtil.isProxyObjMethod(methodName)) {
                return defaultResult(obj, method, args);
            }
            //判断是否为静态方法
            if (Modifier.isStatic(method.getModifiers())) {
                return defaultResult(obj, method, args);
            }

            IMockService mockService = BeanUtil.getBean(IMockService.class);
            MockRequest mockRequest = new MockRequest(uri);
            MockResponse mockResponse = mockService.getMockResponse(mockRequest);
            if (mockResponse == null) {
                log.info("Mock返回默认结果, 请求URI:{}, 调用方法:{}, 返回结果:null", uri, methodName);
                return null;
            }

            //模拟服务耗时
            CommonUtil.mockWaiting(mockResponse.getServiceTime());

            Object mockObj = MockUtil.getMockResult(method, mockResponse.getResResult());
            log.info("Mock返回结果, 请求URI:{}, 调用方法:{}, 返回结果:{}", uri, methodName, mockObj);
            return mockObj;
        } catch (Throwable e) {
            log.error("Mock结果方法执行异常, 请求URI:{}, 调用方法:{}", uri, methodName, e);
            return null;
        }
    }

    /**
     * 返回默认实现
     *
     * @param obj
     * @param method
     * @param args
     * @return
     */
    private Object defaultResult(Object obj, Method method, Object[] args) {
        if (obj == null || "null".equals(obj)) {
            return null;
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            log.warn("method return default exception, method:{}", method, e);
            return null;
        }
    }
}
