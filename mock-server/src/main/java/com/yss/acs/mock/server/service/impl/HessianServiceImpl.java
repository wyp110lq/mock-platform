package com.yss.acs.mock.server.service.impl;

import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.common.proxy.MockInvoker;
import com.yss.acs.mock.server.common.servlet.MockHessianServlet;
import com.yss.acs.mock.server.service.IHessianService;
import com.yss.acs.mock.server.service.IMockService;
import lombok.extern.slf4j.Slf4j;
import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.utils.JdkCompiler;
import com.yss.acs.mock.server.common.utils.JsonUtil;
import com.yss.acs.mock.server.model.bean.HessianInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Hessian服务管理
 *
 * @author jiayy
 * @date 2020/7/10
 */
@Service
@Slf4j
public class HessianServiceImpl implements IHessianService {

    @Value("${mock.hessian.package}")
    private String hsPackage;

    @Autowired
    private IMockService mockService;

    @Override
    public HessianInfo publish(int serviceNumber, String uri, String interfaceCode, boolean isNewService) {
        uri = uri.split(Constants.STRING_QUESTION)[0];
        log.info("Hessian服务发布开始, uri:{}", uri);

        String packageName = getCodePackage(serviceNumber);
        String interfaceName = getInterfaceName(interfaceCode);

        if (isNewService) {
            interfaceCode = transformCodePackage(interfaceCode, packageName);
        }

        String interfaceFullName = packageName + Constants.STRING_POINT + interfaceName;
        Class clazz;
        try {
            JdkCompiler jdkCompiler = new JdkCompiler();
            clazz = jdkCompiler.doCompile(interfaceFullName, interfaceCode);
        } catch (Throwable e) {
            log.error("Hessian接口代码编译错误, uri:{}, interfaceName:{}", uri, interfaceFullName, e);
            throw new MockException("接口代码内容有误");
        }

        try {
            Object implObj = new MockInvoker().getInstance(clazz);
            MockHessianServlet servlet = new MockHessianServlet(uri, implObj, clazz);
            servlet.initServlet();
        } catch (Exception e) {
            log.error("Hessian服务发布失败, uri:{}, interfaceName:{}", uri, interfaceName, e);
            throw new MockException("Hessian服务发布失败");
        }

        String mockResult = "";
        if (isNewService) {
            mockResult = JsonUtil.objToJson(mockService.getDefaultMockResult(clazz));
        }

        log.info("Hessian服务发布成功, uri:{}, className:{}", uri, interfaceFullName);
        return new HessianInfo(interfaceFullName, interfaceCode, mockResult);
    }

    @Override
    public void stop(String uri) {
        uri = uri.split(Constants.STRING_QUESTION)[0];
        MockHessianServlet.removeServlet(uri);
    }

    /**
     * 转换包路径
     *
     * @param interfaceCode
     * @param packageName
     * @return
     */
    private String transformCodePackage(String interfaceCode, String packageName) {
        interfaceCode = interfaceCode.replaceAll("\n", "").trim();
        if (interfaceCode.startsWith(Constants.JAVA_PACKAGE_BEGIN)) {
            interfaceCode = interfaceCode.substring(interfaceCode.indexOf(";") + 1);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Constants.JAVA_PACKAGE_BEGIN).append(packageName).append(";\n");
        sb.append(interfaceCode);
        return sb.toString();
    }

    /**
     * 获取接口名
     *
     * @param interfaceCode
     * @return
     */
    private String getInterfaceName(String interfaceCode) {
        int index1 = interfaceCode.indexOf(Constants.JAVA_INTERFACE_KEYWORD);
        if (index1 < 0) {
            throw new MockException("接口代码内容有误");
        }
        int index2 = interfaceCode.indexOf(Constants.JAVA_BODY_BEGIN);
        if (index2 < 0) {
            throw new MockException("接口代码内容有误");
        }
        if (index2 <= index1) {
            throw new MockException("接口代码内容有误");
        }
        return interfaceCode.substring(index1 + Constants.JAVA_INTERFACE_KEYWORD.length(), index2).trim();
    }

    /**
     * 获取代码包名
     *
     * @param serviceNumber
     * @return
     */
    private String getCodePackage(int serviceNumber) {
        return hsPackage + Constants.STRING_POINT + Constants.HSCODE_PACKAGE_PREFIX + serviceNumber;
    }
}
