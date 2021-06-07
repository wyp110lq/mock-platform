package com.yss.acs.mock.server.common.proxy;

import java.lang.reflect.Proxy;

/**
 * 创建接口实现代理对象
 *
 * @author jiayy
 * @date 2020/7/11
 */
public class MockInvoker {

    /**
     * 创建接口实现代理对象
     *
     * @param cls
     * @return
     */
    public Object getInstance(Class<?> cls) {
        MockMethodProxy invocationHandler = new MockMethodProxy();
        Object newProxyInstance = Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{cls},
                invocationHandler);
        return newProxyInstance;
    }
}
