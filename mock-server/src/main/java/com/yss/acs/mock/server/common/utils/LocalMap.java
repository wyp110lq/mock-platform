package com.yss.acs.mock.server.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程本地临时存储数据
 *
 * @author jiayy
 * @date 2020/7/2
 */
public class LocalMap {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    /**
     * 向当前线程暂存数据
     *
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        Map<String, Object> map = contextMap();
        if (map != null) {
            map.put(key, value);
        }
    }

    /**
     * 从当前线程获取暂存数据
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        String value = null;
        Map<String, Object> map = contextMap();
        if (map != null && map.get(key) != null) {
            value = String.valueOf(map.get(key));
        }
        return value;
    }

    /**
     * 获取当前线程里暂存的数据
     *
     * @return
     */
    public static Map<String, Object> contextMap() {
        Map<String, Object> map = CONTEXT.get();
        if (map == null) {
            map = new HashMap<>(10);
            CONTEXT.set(map);
        }
        return map;
    }
}
