package com.yss.acs.mock.server.model.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * WebService参数
 *
 * @author jiayy
 * @date 2020/7/2
 */
@Data
public class WebServiceSaveReqVO {

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
     * WSDL内容
     */
    @NotBlank(message = "WSDL内容不能为空")
    private String wsdlContent;

}
