package com.yss.acs.mock.server.common.servlet;

import com.caucho.hessian.server.HessianServlet;
import lombok.extern.slf4j.Slf4j;
import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.exception.MockException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock Hessian Servlet
 *
 * @author jiayy
 * @date 2020/7/10
 */
@Slf4j
public class MockHessianServlet extends HessianServlet {

    private String uri;

    private Object implObj;

    private Class interfaceClass;

    private static Map<String, HessianServlet> servletMap = new ConcurrentHashMap<>();

    public MockHessianServlet() {

    }

    public MockHessianServlet(String uri, Object implObj, Class interfaceClass) {
        this.uri = uri;
        this.implObj = implObj;
        this.interfaceClass = interfaceClass;
    }

    public void initServlet() throws ServletException {
        HessianServlet hessianServlet = new HessianServlet();
        hessianServlet.setHome(implObj);
        hessianServlet.setHomeAPI(interfaceClass);
        hessianServlet.init(new ServletConfig() {
            @Override
            public String getServletName() {
                return null;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public String getInitParameter(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }
        });

        servletMap.put(Constants.HESSIAN_URI_PREFIX + uri, hessianServlet);
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String reqUri = req.getRequestURI();

        //调用hessian服务
        HessianServlet hessianServlet = servletMap.get(reqUri);
        if (hessianServlet != null) {
            hessianServlet.service(request, response);
        } else {
            log.error("未找到对应的Hessian服务, uri:{}", reqUri);
            throw new MockException("未找到对应的Hessian服务");
        }
    }

    /**
     * 删除服务Servlet
     *
     * @param uri
     */
    public static void removeServlet(String uri) {
        HessianServlet servlet = servletMap.get(uri);
        if (servlet != null) {
            servlet.destroy();
        }
        servletMap.remove(uri);
    }
}
