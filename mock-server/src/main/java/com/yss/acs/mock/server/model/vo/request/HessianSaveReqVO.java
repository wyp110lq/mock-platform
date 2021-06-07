package com.yss.acs.mock.server.model.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Hessian参数
 *
 * @author jiayy
 * @date 2020/7/2
 */
@Data
public class HessianSaveReqVO {

    /**
     * 服务名称
     */
    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    /**
     * 请求uri
     */
    @NotBlank(message = "服务URI不能为空")
    private String serviceUri;

    /**
     * 接口代码
     */
    @NotBlank(message = "接口代码不能为空")
    private String interfaceCode;

}
