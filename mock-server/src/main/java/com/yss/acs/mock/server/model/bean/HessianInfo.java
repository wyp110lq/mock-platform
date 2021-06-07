package com.yss.acs.mock.server.model.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Hessian服务信息
 *
 * @author jiayy
 * @date 2020/7/11
 */
@Data
@AllArgsConstructor
public class HessianInfo {

    /**
     * 接口名（含包名）
     */
    private String interfaceClass;

    /**
     * 接口代码
     */
    private String interfaceCode;

    /**
     * Mock结果
     */
    private String mockResult;

}
