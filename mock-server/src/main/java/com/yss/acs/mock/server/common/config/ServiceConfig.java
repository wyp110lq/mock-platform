package com.yss.acs.mock.server.common.config;

import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.servlet.MockHessianServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务配置类
 *
 * @author jiayy
 * @date 2020/7/1
 */
@Configuration
@Slf4j
public class ServiceConfig {

    /**
     * WebService映射
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean disWebServiceServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CXFServlet(), Constants.WEBSERVICE_URI_PREFIX + "/*");
        return servletRegistrationBean;
    }

    /**
     * Hessian映射
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean disHessianServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new MockHessianServlet(), Constants.HESSIAN_URI_PREFIX + "/*");
        return servletRegistrationBean;
    }

}
