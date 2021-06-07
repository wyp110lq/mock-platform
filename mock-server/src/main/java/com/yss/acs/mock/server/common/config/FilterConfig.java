package com.yss.acs.mock.server.common.config;

import com.yss.acs.mock.server.common.filter.MockFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置类
 *
 * @author jiayy
 * @date 2020/6/28
 */

@Configuration
public class FilterConfig {

    @Autowired
    private MockFilter mockFilter;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(mockFilter);
        bean.setName("mockFilter");
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }

}
