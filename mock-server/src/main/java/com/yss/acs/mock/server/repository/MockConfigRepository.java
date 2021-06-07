package com.yss.acs.mock.server.repository;

import com.yss.acs.mock.server.model.entity.MockConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Mock配置Repository
 *
 * @author jiayy
 * @date 2020/6/29
 */
public interface MockConfigRepository extends JpaRepository<MockConfig, String> {

    /**
     * 分页查询列表
     *
     * @param spec
     * @param pageable
     * @return
     */
    Page<MockConfig> findAll(Specification<MockConfig> spec, Pageable pageable);

    /**
     * 根据服务uri查询服务配置
     *
     * @param serviceUri
     * @return
     */
    MockConfig findByServiceUri(String serviceUri);

    /**
     * 根据服务类型，查询服务编号最大一条记录
     *
     * @param serviceType
     * @return
     */
    MockConfig findFirstByServiceTypeOrderByServiceNumberDesc(String serviceType);

    /**
     * 根据服务类型和服务状态查询服务配置列表
     *
     * @param serviceType
     * @param serviceStatus
     * @return
     */
    List<MockConfig> findByServiceTypeAndServiceStatus(String serviceType, Integer serviceStatus);
}
