package com.yss.acs.mock.server.common.filter;

import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.enums.ContentType;
import com.yss.acs.mock.server.common.enums.ServiceStatus;
import com.yss.acs.mock.server.common.enums.ServiceType;
import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.common.utils.CommonUtil;
import com.yss.acs.mock.server.common.utils.LocalMap;
import com.yss.acs.mock.server.model.bean.MockRequest;
import com.yss.acs.mock.server.model.bean.MockResponse;
import com.yss.acs.mock.server.service.IMockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * Mock过滤器
 *
 * @author jiayy
 * @date 2020/6/28
 */
@Component
@Slf4j
public class MockFilter implements Filter {

    @Autowired
    private IMockService mockService;

    @Value("${mock.exclude.uri}")
    private String excludeUri;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = getMockUri(request);
        LocalMap.set(Constants.CACHE_URI_KEY, uri);

        //匹配uri
        if (isMatch(uri)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        log.info("请求URI:{}", uri);

        //构造Mock请求信息
        MockRequest mockRequest = new MockRequest(uri, request.getMethod(), request.getContentType());
        //根据请求获取Mock结果
        MockResponse mockResponse;
        try {
            mockResponse = mockService.getMockResponse(mockRequest);
        } catch (MockException e) {
            printResponse(response, false, e.getMsg());
            return;
        }
        if (mockResponse != null && ServiceType.WebService.name().equals(mockResponse.getServiceType()) && ServiceStatus.ENABLE.getCode() == mockResponse.getServiceStatus()) {
            //模拟服务耗时
            CommonUtil.mockWaiting(mockResponse.getServiceTime());
            request.getRequestDispatcher(Constants.WEBSERVICE_URI_PREFIX + uri).forward(request, response);
            return;
        }
        if (mockResponse != null && ServiceType.Hessian.name().equals(mockResponse.getServiceType()) && ServiceStatus.ENABLE.getCode() == mockResponse.getServiceStatus()) {
            request.getRequestDispatcher(Constants.HESSIAN_URI_PREFIX + uri).forward(request, response);
            return;
        }
        returnMockResult(uri, response, mockResponse);
    }

    /**
     * 过滤URL匹配
     *
     * @param requestUrl
     */
    private boolean isMatch(String requestUrl) {
        if (StringUtils.isEmpty(requestUrl) || StringUtils.isEmpty(excludeUri)) {
            return false;
        }
        if (Constants.URI_ROOT_PATH.equals(requestUrl)) {
            return true;
        }

        // 多个URL用逗号分隔
        String[] excludeUrlArray = excludeUri.split(",");

        // 判断是否匹配URL
        for (String excludeUrl : excludeUrlArray) {
            if (StringUtils.isEmpty(excludeUrl)) {
                continue;
            }
            // 正则匹配
            if (Pattern.matches(excludeUrl.trim(), requestUrl)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取Mock的Uri, 如果是webservice请求带wsdl参数的话要拼上
     *
     * @param request
     * @return
     */
    private String getMockUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        uri = CommonUtil.formatUri(uri);
        boolean isWsdl = request.getParameterMap().containsKey(Constants.URI_WSDL_PARAM);
        if (isWsdl && StringUtils.isEmpty(request.getParameter(Constants.URI_WSDL_PARAM))) {
            uri += Constants.URI_WSDL_END;
        }
        return uri;
    }

    /**
     * 返回Mock结果
     *
     * @param reqUri
     * @param response
     * @param mockResponse
     */
    private void returnMockResult(String reqUri, HttpServletResponse response, MockResponse mockResponse) {
        String result;
        if (mockResponse != null) {
            response.setStatus(mockResponse.getResStatus());
            if (ContentType.JSON.name().equals(mockResponse.getResType())) {
                response.setContentType(Constants.CONTENT_TYPE_JSON);
            } else if (ContentType.XML.name().equals(mockResponse.getResType())) {
                response.setContentType(Constants.CONTENT_TYPE_XML);
            } else {
                response.setContentType(Constants.CONTENT_TYPE_TEXT);
            }
            //未发布的WebService服务返回wsdl内容
            if (ServiceType.WebService.name().equals(mockResponse.getServiceType()) && mockResponse.getServiceStatus() == ServiceStatus.UNABLE.getCode()) {
                response.setContentType(Constants.CONTENT_TYPE_XML);
                result = mockResponse.getWsdlContent();
            } else {
                result = mockResponse.getResResult();
                //模拟服务耗时
                CommonUtil.mockWaiting(mockResponse.getServiceTime());
            }
            log.info("Mock返回结果, 请求URI:{}, 返回结果:{}", reqUri, result);

            printResponse(response, true, result);
        } else {
            log.warn("未找到匹配的Mock服务配置, 请求URI:{}", reqUri);
            printResponse(response, false, "未找到匹配的Mock服务配置");
        }
    }

    /**
     * 输出结果
     *
     * @param response
     * @param isSuccess
     * @param result
     */
    private void printResponse(HttpServletResponse response, boolean isSuccess, String result) {
        PrintWriter out = null;
        try {
            if (!isSuccess) {
                response.setStatus(500);
                response.setContentType(Constants.CONTENT_TYPE_JSON);
                result = "{\"errmsg\":\"" + "Mock平台返回错误：" + result + "\"}";
            }
            out = response.getWriter();
            out.print(result);
        } catch (IOException e) {
            log.error("Mock返回结果失败", e);
        } finally {
            out.flush();
        }
    }
}
