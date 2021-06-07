package com.yss.acs.mock.server.common.utils;

import com.yss.acs.mock.server.common.constants.Constants;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 通用工具类
 *
 * @author jiayy
 * @date 2020/6/29
 */
public class CommonUtil {

    /**
     * 特殊方法名
     */
    private static final Set<String> DEFAULT_METHOD_NAMES = new HashSet<>(Arrays.asList("toString", "hashCode", "equals", "wait", "notify", "notifyAll", "getClass", "main"));
    private static final Set<String> PROXY_METHOD_NAMES = new HashSet<>(Arrays.asList("setCallback", "setCallbacks", "getCallback", "getCallbacks", "newInstance"));

    /**
     * 构造id,共20位
     *
     * @return
     */
    public static String getNextId() {
        return System.currentTimeMillis() + "" + getRandomInt(7);
    }

    /**
     * 获取指定位数随机数
     *
     * @param len
     * @return
     */
    public static long getRandomInt(int len) {
        return (long) ((Math.random() * 9 + 1) * Math.pow(10, len - 1));
    }

    /**
     * 是否为对象默认方法
     *
     * @param methodName
     * @return
     */
    public static boolean isObjDefaultMethod(String methodName) {
        return DEFAULT_METHOD_NAMES.contains(methodName);
    }

    /**
     * 是否为代理类默认方法
     *
     * @param methodName
     * @return
     */
    public static boolean isProxyObjMethod(String methodName) {
        return PROXY_METHOD_NAMES.contains(methodName);
    }

    /**
     * 模拟服务耗时
     *
     * @param waitSeconds
     */
    public static void mockWaiting(Integer waitSeconds) {
        if (waitSeconds == null) {
            return;
        }
        if (waitSeconds > 0) {
            try {
                waitSeconds = waitSeconds < Constants.MAX_SERVICE_TIME ? waitSeconds : Constants.MAX_SERVICE_TIME;
                Thread.sleep(waitSeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化uri，去掉多余的/
     *
     * @param uri
     * @return
     */
    public static String formatUri(String uri) {
        return uri.replaceAll("/+", "/");
    }

}
