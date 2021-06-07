package com.yss.acs.mock.server.service.impl;

import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.enums.ContentType;
import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.common.utils.CommonUtil;
import com.yss.acs.mock.server.common.utils.MockUtil;
import com.yss.acs.mock.server.model.bean.MockRequest;
import com.yss.acs.mock.server.model.bean.MockResponse;
import com.yss.acs.mock.server.model.entity.MockConfig;
import com.yss.acs.mock.server.service.IMockConfigService;
import com.yss.acs.mock.server.service.IMockService;
import com.yss.acs.mock.util.MockResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Mock服务实现
 *
 * @author jiayy
 * @date 2020/6/28
 */
@Service
@Slf4j
public class MockServiceImpl implements IMockService {

    @Autowired
    private IMockConfigService mockConfigService;

    @Override
    public MockResponse getMockResponse(MockRequest mockRequest) {

        //查询Mock配置
        MockConfig config = mockConfigService.findByUri(mockRequest.getReqUri());
        if (config == null) {
            return null;
        }

        if (!StringUtils.isEmpty(config.getReqMethod()) && !StringUtils.isEmpty(mockRequest.getReqMethod()) && !config.getReqMethod().equals(mockRequest.getReqMethod())) {
            throw new MockException("该接口已指定请求方式为" + config.getReqMethod());
        }

        if (ContentType.JSON.name().equals(config.getReqContentType()) && !Constants.REQ_CONTENT_TYPE_JSON.equals(mockRequest.getReqContentType())) {
            throw new MockException("该接口已指定请求参数类型为JSON");
        }

        if (ContentType.XML.name().equals(config.getReqContentType()) && !Constants.REQ_CONTENT_TYPE_XML.equals(mockRequest.getReqContentType())) {
            throw new MockException("该接口已指定请求参数类型为XML");
        }

        MockResponse mockResponse = new MockResponse();
        mockResponse.setServiceType(config.getServiceType());
        mockResponse.setServiceStatus(config.getServiceStatus());
        mockResponse.setServiceTime(config.getServiceTime());
        mockResponse.setResType(config.getResContentType());
        mockResponse.setResStatus(config.getResStatus());
        mockResponse.setWsdlContent(config.getWsdlContent());

        //转换Mock结果内容
        mockResponse.setResResult(MockResultUtil.getMockResult(config.getResResult()));

        return mockResponse;
    }

    @Override
    public Map<String, Object> getDefaultMockResult(Class clazz) {
        return getMockResult(clazz, true, null);
    }

    @Override
    public Map<String, Object> getMockConfigResult(Class clazz, String resResult) {
        return getMockResult(clazz, false, resResult);
    }

    /**
     * 获取Mock结果
     *
     * @param clazz     处理类
     * @param isDefault 是否默认结果
     * @param resResult 配置返回结果
     * @return Map，key：方法名，value：方法返回值
     */
    private Map<String, Object> getMockResult(Class clazz, boolean isDefault, String resResult) {
        Map<String, Object> resultMap = new HashMap<>(10);
        try {
            Method[] methods = clazz.getMethods();
            Object mockResult;
            for (Method method : methods) {
                if (CommonUtil.isObjDefaultMethod(method.getName())) {
                    continue;
                }
                if (Constants.METHOD_GET_MOCK_RESULT.equals(method.getName())) {
                    continue;
                }
                //判断是否为静态方法
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                String returnTypeName = method.getReturnType().getName();
                if (Constants.JAVA_FUNCTION_VOID.trim().equals(returnTypeName)) {
                    //没有返回值的方法，不需要结果
                    continue;
                }

                if (isDefault) {
                    mockResult = getMethodDefaultReturn(method);
                } else {
                    mockResult = getMethodMockReturn(method, resResult);
                }
                resultMap.put(method.getName(), mockResult);
            }
        } catch (Exception e) {
            log.error("获取Mock结果异常, className:{}", clazz.getName(), e);
        }
        return resultMap;
    }

    /**
     * 获取方法默认返回
     *
     * @param method
     * @return
     */
    private Object getMethodDefaultReturn(Method method) {
        Object defaultResult = null;
        try {
            String returnTypeName = method.getReturnType().getName();
            if (List.class.getName().equals(returnTypeName)) {
                defaultResult = new ArrayList<>();
            } else if (Set.class.getName().equals(returnTypeName)) {
                defaultResult = new HashSet<>();
            } else if (Map.class.getName().equals(returnTypeName)) {
                defaultResult = new HashMap<>(0);
            } else {
                defaultResult = method.getReturnType().newInstance();
            }
        } catch (Exception e) {
            log.error("获取WebService方法默认Mock结果异常, method:{}", method.getName(), e);
        }
        return defaultResult;
    }

    /**
     * 获取方法Mock返回
     *
     * @param method
     * @return
     */
    private Object getMethodMockReturn(Method method, String resResult) {
        Object mockObj = null;
        try {
            mockObj = MockUtil.getMockResult(method, resResult);
            if (mockObj == null) {
                return getMethodDefaultReturn(method);
            }
        } catch (Exception e) {
            log.error("获取WebService方法Mock结果异常, method:{}", method.getName(), e);
        }
        return mockObj;
    }
}
