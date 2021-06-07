package com.yss.acs.mock.server.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * Mock工具类
 *
 * @author jiayy
 * @date 2020/7/9
 */
@Slf4j
public class MockUtil {

    /**
     * 获取Mock结果
     *
     * @param method
     * @param resResult
     * @return
     */
    public static Object getMockResult(Method method, String resResult) {
        if (StringUtils.isEmpty(resResult)) {
            return null;
        }

        String methodName = method.getName();
        Map<String, Object> resultMap = JsonUtil.jsonToObj(resResult, Map.class);
        if (resultMap == null || StringUtils.isEmpty(resultMap.get(methodName))) {
            return null;
        }

        Object mockObj = null;
        String returnTypeName = method.getReturnType().getName();
        Object resultObj = resultMap.get(methodName);

        String resultJson = "";
        if (resultObj != null) {
            if (resultObj instanceof String) {
                resultJson = (String) resultObj;
            } else {
                resultJson = JsonUtil.objToJson(resultMap.get(methodName));
            }
        }
        if (!StringUtils.isEmpty(resultJson)) {
            if (String.class.getName().equals(returnTypeName)) {
                mockObj = resultJson;
            } else if (List.class.getName().equals(returnTypeName)) {
                //以下方法就是获取list类的泛形类数组的方法
                if (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments().length > 0) {
                    Class entityClass = (Class) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                    //如果list集合中不属于自定义类，则单独处理
                    mockObj = JsonUtil.json2List(resultJson, entityClass);
                } else {
                    mockObj = JsonUtil.json2List(resultJson, Object.class);
                }
            } else {
                mockObj = JsonUtil.jsonToObj(resultJson, method.getReturnType());
            }
        }
        return mockObj;
    }
}
