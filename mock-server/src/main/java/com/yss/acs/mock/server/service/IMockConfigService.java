package com.yss.acs.mock.server.service;

import com.yss.acs.mock.server.common.enums.ServiceType;
import com.yss.acs.mock.server.model.entity.MockConfig;
import com.yss.acs.mock.server.model.vo.request.HessianSaveReqVO;
import com.yss.acs.mock.server.model.vo.request.MockConfigQueryReqVO;
import com.yss.acs.mock.server.model.vo.request.WebServiceSaveReqVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Mock配置服务
 *
 * @author jiayy
 * @date 2020/6/28
 */
public interface IMockConfigService {

    /**
     * 分页查询Mock配置列表
     *
     * @param param
     * @return
     */
    Page<MockConfig> listPage(MockConfigQueryReqVO param);

    /**
     * 保存Mock配置
     *
     * @param mockConfig
     * @return
     */
    MockConfig save(MockConfig mockConfig);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    MockConfig findById(String id);

    /**
     * 根据uri查询Mock配置
     *
     * @param uri
     * @return
     */
    MockConfig findByUri(String uri);

    /**
     * 根据id删除
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 创建WebService服务
     *
     * @param param
     */
    void createWebService(WebServiceSaveReqVO param);

    /**
     * 创建Hessian服务
     *
     * @param param
     */
    void createHessian(HessianSaveReqVO param);

    /**
     * 根据服务类型查询服务配置（已发布状态）
     *
     * @param serviceType
     * @return
     */
    List<MockConfig> findByServiceType(ServiceType serviceType);
}
