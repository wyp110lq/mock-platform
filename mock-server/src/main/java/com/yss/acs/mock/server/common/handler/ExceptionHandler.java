package com.yss.acs.mock.server.common.handler;

import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import com.yss.acs.mock.server.model.base.Result;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 异常处理
 *
 * @author jiayy
 * @date 2020/6/29
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandler {

    /**
     * 返回json或者错误页面
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @param ex
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public Object handlerRuntimeException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable ex) {
        if (isReturnJson(handlerMethod)) {
            Result result;
            if (ex instanceof MockException) {
                log.error("业务异常: ", ex);
                MockException me = (MockException) ex;
                result = Result.error(me.getMsg());
            } else if (ex instanceof BindException) {
                log.error("参数错误", ex);
                BindException be = (BindException) ex;
                BindingResult bindingResult = be.getBindingResult();
                result = Result.error(bindingResult.getAllErrors().get(0).getDefaultMessage());
            } else {
                log.error("系统错误", ex);
                result = Result.error(ex.getMessage());
            }
            PrintWriter out = null;
            try {
                response.setContentType("application/json;charset=utf-8");
                out = response.getWriter();
                out.print(JsonUtil.objToJson(result));
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if (out != null) {
                    out.flush();
                }
            }
        }
        return null;
    }

    /**
     * 判断是否返回json
     * spring 返回含有 ResponseBody 或者 RestController注解
     *
     * @param handlerMethod HandlerMethod
     * @return 是否返回json
     */
    private boolean isReturnJson(HandlerMethod handlerMethod) {

        ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
        if (null != responseBody) {
            return true;
        }
        // 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
        Class<?> beanType = handlerMethod.getBeanType();
        responseBody = AnnotationUtils.getAnnotation(beanType, ResponseBody.class);
        if (null != responseBody) {
            return true;
        }
        return false;
    }

}