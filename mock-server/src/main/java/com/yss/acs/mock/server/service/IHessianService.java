package com.yss.acs.mock.server.service;

import com.yss.acs.mock.server.model.bean.HessianInfo;

/**
 * Hessian服务管理
 *
 * @author jiayy
 * @date 2020/7/10
 */
public interface IHessianService {

    /**
     * 发布服务
     *
     * @param serviceNumber
     * @param uri
     * @param interfaceCode
     * @param isNewService
     * @return
     */
    HessianInfo publish(int serviceNumber, String uri, String interfaceCode, boolean isNewService);

    /**
     * 停止服务
     *
     * @param uri
     */
    void stop(String uri);
}
