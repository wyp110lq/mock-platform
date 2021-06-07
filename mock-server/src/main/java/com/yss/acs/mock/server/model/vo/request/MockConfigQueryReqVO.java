package com.yss.acs.mock.server.model.vo.request;

import com.yss.acs.mock.server.model.base.PageParam;
import lombok.Data;

/**
 * Mock配置查询参数
 *
 * @author jiayy
 * @date 2020/6/29
 */
@Data
public class MockConfigQueryReqVO extends PageParam {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务URI
     */
    private String serviceUri;

}
