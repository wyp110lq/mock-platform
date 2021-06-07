package com.yss.acs.mock.server.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yss.acs.mock.server.common.exception.MockException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Json转换工具
 *
 * @author jiayy
 * @date 2020/6/30
 */
@Slf4j
public class JsonUtil {

    /**
     * json转换成对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToObj(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Json转对象出现异常, json:{}", json, e);
            throw new MockException("Json转对象出现异常");
        }
    }

    /**
     * 对象转换成json
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String objToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new MockException("对象转Json出现异常");
        }
    }

    /**
     * json转换成对象List
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> List<T> json2List(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Json转对象列表出现异常, json:{}", json, e);
            throw new MockException("Json转对象列表出现异常");
        }
    }
}
