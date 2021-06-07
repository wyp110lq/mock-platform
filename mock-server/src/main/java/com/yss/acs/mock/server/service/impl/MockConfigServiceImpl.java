package com.yss.acs.mock.server.service.impl;

import com.yss.acs.mock.server.common.enums.ServiceStatus;
import com.yss.acs.mock.server.common.utils.CommonUtil;
import com.yss.acs.mock.server.service.IHessianService;
import com.yss.acs.mock.server.service.IMockConfigService;
import com.yss.acs.mock.server.service.IWebService;
import lombok.extern.slf4j.Slf4j;
import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.enums.ContentType;
import com.yss.acs.mock.server.common.enums.ServiceType;
import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.common.utils.JsonUtil;
import com.yss.acs.mock.server.common.utils.XmlUtil;
import com.yss.acs.mock.server.model.bean.HessianInfo;
import com.yss.acs.mock.server.model.entity.MockConfig;
import com.yss.acs.mock.server.model.vo.request.HessianSaveReqVO;
import com.yss.acs.mock.server.model.vo.request.MockConfigQueryReqVO;
import com.yss.acs.mock.server.model.vo.request.WebServiceSaveReqVO;
import com.yss.acs.mock.server.repository.MockConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Mock服务配置管理
 *
 * @author jiayy
 * @date 2020/6/28
 */
@Service
@Slf4j
public class MockConfigServiceImpl implements IMockConfigService {

    @Autowired
    private MockConfigRepository mockConfigRepository;

    @Autowired
    private IWebService webService;

    @Autowired
    private IHessianService hessianService;

    /**
     * 服务编号
     */
    public static int service_number_webservice;
    public static int service_number_hessian;

    @Override
    public Page<MockConfig> listPage(MockConfigQueryReqVO param) {
        Specification<MockConfig> specification = (Specification<MockConfig>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(param.getServiceName())) {
                Predicate queryStr = cb.like(root.get("serviceName").as(String.class), "%" + param.getServiceName().trim() + "%");
                predicates.add(queryStr);
            }
            if (!StringUtils.isEmpty(param.getServiceUri())) {
                Predicate queryStr = cb.like(root.get("serviceUri").as(String.class), "%" + param.getServiceUri().trim() + "%");
                predicates.add(queryStr);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        //排序
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC, "id"));

        Pageable pageable = PageRequest.of(param.getPageNum(), param.getPageSize(), Sort.by(sorts));
        return mockConfigRepository.findAll(specification, pageable);
    }

    @Override
    public MockConfig save(MockConfig param) {
        //校验保存参数
        validMockConfig(param);

        if (StringUtils.isEmpty(param.getId())) {
            //新增操作
            param.setServiceStatus(ServiceStatus.ENABLE.getCode());
            preSave(param);
            return mockConfigRepository.save(param);
        } else {
            //更新操作
            MockConfig config = findById(param.getId());
            if (config == null) {
                throw new MockException("数据不存在，请刷新后再试");
            }
            config.setServiceName(param.getServiceName());
            config.setServiceType(param.getServiceType());
            config.setReqMethod(param.getReqMethod());
            config.setReqContentType(param.getReqContentType());
            config.setResContentType(param.getResContentType());
            config.setResStatus(param.getResStatus());
            config.setResResult(param.getResResult());
            config.setServiceTime(param.getServiceTime());

            boolean isUriModify = false;
            String oldUri = config.getServiceUri();
            if (!param.getServiceUri().equals(config.getServiceUri())) {
                isUriModify = true;
            }
            config.setServiceUri(param.getServiceUri());
            config.setUpdateTime(new Date());
            //保存配置
            config = mockConfigRepository.save(config);

            if (ServiceType.WebService.name().equals(config.getServiceType())) {
                //需要停止老服务，重新发布新服务
                webService.stop(oldUri);
                webService.publish(config.getServiceNumber(), config.getServiceUri(), config.getClassName(), false, config.getResResult());
            } else if (ServiceType.Hessian.name().equals(config.getServiceType()) && isUriModify) {
                //如果uri有修改，则需要停止老服务，重新发布新服务
                hessianService.stop(oldUri);
                hessianService.publish(config.getServiceNumber(), config.getServiceUri(), config.getInterfaceCode(), false);
            }
            return config;
        }
    }

    @Override
    public MockConfig findById(String id) {
        Optional<MockConfig> optional = mockConfigRepository.findById(id);
        return optional.isPresent() ? optional.get() : null;
    }

    @Override
    public MockConfig findByUri(String uri) {
        List<MockConfig> mockList = mockConfigRepository.findAll();
        MockConfig mockConfig = null;
        List<MockConfig> mockRestList = new ArrayList<>();
        for (MockConfig mock : mockList) {
            if (mock.getServiceUri().equals(uri)) {
                mockConfig = mock;
                break;
            }
            if (mock.getServiceUri().contains("{") && mock.getServiceUri().contains("}")) {
                mock.setServiceUri(mock.getServiceUri().substring(0, mock.getServiceUri().indexOf("{")));
                mockRestList.add(mock);
            }
        }
        if (mockConfig == null) {
            for (MockConfig mock : mockRestList) {
                if (uri.startsWith(mock.getServiceUri())) {
                    mockConfig = mock;
                    break;
                }
            }
        }
        return mockConfig;
    }

    @Override
    public void deleteById(String id) {
        MockConfig mockConfig = findById(id);
        Assert.notNull(mockConfig, "数据不存在，请刷新后再试");

        mockConfigRepository.deleteById(id);
        //删除服务
        if (ServiceType.WebService.name().equals(mockConfig.getServiceType())) {
            webService.stop(mockConfig.getServiceUri());
            webService.deleteCode(mockConfig.getServiceNumber());
        } else if (ServiceType.Hessian.name().equals(mockConfig.getServiceType())) {
            hessianService.stop(mockConfig.getServiceUri());
        }
        log.info("删除服务，服务类型:{}, 服务URI:{}", mockConfig.getServiceType(), mockConfig.getServiceUri());
    }

    @Override
    public synchronized void createWebService(WebServiceSaveReqVO param) {
        //校验WebService配置
        webService.validConfig();

        MockConfig mockConfig = new MockConfig();
        mockConfig.setServiceName(param.getServiceName().trim());
        mockConfig.setServiceUri(param.getServiceUri().trim());
        mockConfig.setWsdlContent(param.getWsdlContent());
        mockConfig.setServiceType(ServiceType.WebService.name());
        mockConfig.setResContentType(ContentType.JSON.name());
        mockConfig.setServiceStatus(ServiceStatus.UNABLE.getCode());
        mockConfig.setResStatus(200);
        //校验保存参数
        validMockConfig(mockConfig);

        //获取服务编号
        int serviceNumber = getNextServiceNumber(ServiceType.WebService);

        //保存webservice服务编号
        mockConfig.setServiceNumber(serviceNumber);
        preSave(mockConfig);
        mockConfig = mockConfigRepository.save(mockConfig);

        try {
            //生成WebService代码
            String className = webService.generate(serviceNumber, mockConfig.getServiceUri());
            if (StringUtils.isEmpty(className)) {
                throw new MockException("创建WebService服务失败");
            }

            //发布WebService服务
            String mockResult = webService.publish(serviceNumber, mockConfig.getServiceUri(), className, true, null);

            //更新服务状态为已发布
            mockConfig.setServiceStatus(ServiceStatus.ENABLE.getCode());
            mockConfig.setClassName(className);
            mockConfig.setResResult(mockResult);
            mockConfigRepository.save(mockConfig);
        } catch (Exception e) {
            log.info("创建WebService服务失败", e);
            deleteById(mockConfig.getId());
            throw new MockException("创建WebService服务失败");
        }
    }

    @Override
    public synchronized void createHessian(HessianSaveReqVO param) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.setServiceName(param.getServiceName().trim());
        mockConfig.setServiceUri(param.getServiceUri().trim());
        mockConfig.setServiceType(ServiceType.Hessian.name());
        mockConfig.setResContentType(ContentType.JSON.name());
        mockConfig.setReqMethod(RequestMethod.POST.name());
        mockConfig.setResStatus(200);
        //校验保存参数
        validMockConfig(mockConfig);

        //获取服务编号
        int serviceNum = getNextServiceNumber(ServiceType.Hessian);
        //发布Hessian服务
        HessianInfo hessianInfo = hessianService.publish(serviceNum, mockConfig.getServiceUri(), param.getInterfaceCode(), true);

        mockConfig.setServiceNumber(serviceNum);
        mockConfig.setServiceStatus(ServiceStatus.ENABLE.getCode());
        mockConfig.setClassName(hessianInfo.getInterfaceClass());
        mockConfig.setInterfaceCode(hessianInfo.getInterfaceCode());
        mockConfig.setResResult(hessianInfo.getMockResult());
        preSave(mockConfig);

        mockConfigRepository.save(mockConfig);
    }

    @Override
    public List<MockConfig> findByServiceType(ServiceType serviceType) {
        if (!serviceType.equals(ServiceType.Http)) {
            MockConfig mockConfig = mockConfigRepository.findFirstByServiceTypeOrderByServiceNumberDesc(serviceType.name());
            if (mockConfig != null && mockConfig.getServiceNumber() != null) {
                if (ServiceType.WebService.equals(serviceType)) {
                    service_number_webservice = mockConfig.getServiceNumber();
                } else if (ServiceType.Hessian.equals(serviceType)) {
                    service_number_hessian = mockConfig.getServiceNumber();
                }
            }
        }
        return mockConfigRepository.findByServiceTypeAndServiceStatus(serviceType.name(), ServiceStatus.ENABLE.getCode());
    }

    /**
     * 校验Mock配置保存参数
     *
     * @param mockConfig
     */
    private void validMockConfig(MockConfig mockConfig) {
        mockConfig.setServiceName(mockConfig.getServiceName().trim());
        mockConfig.setServiceUri(CommonUtil.formatUri(mockConfig.getServiceUri().trim()));

        if (!mockConfig.getServiceUri().startsWith(Constants.URI_ROOT_PATH)) {
            mockConfig.setServiceUri(Constants.URI_ROOT_PATH + mockConfig.getServiceUri());
        }
        if (mockConfig.getServiceUri().endsWith(Constants.URI_ROOT_PATH)) {
            mockConfig.setServiceUri(mockConfig.getServiceUri().substring(0, mockConfig.getServiceUri().length() - 1));
        }

        if (ServiceType.WebService.name().equals(mockConfig.getServiceType())) {
            if (!mockConfig.getServiceUri().endsWith(Constants.URI_WSDL_END)) {
                mockConfig.setServiceUri(mockConfig.getServiceUri() + Constants.URI_WSDL_END);
            }
        }

        if (ServiceType.Hessian.name().equals(mockConfig.getServiceType())) {
            mockConfig.setServiceUri(mockConfig.getServiceUri().split(Constants.STRING_QUESTION)[0]);
        }

        if (mockConfig.getServiceTime() != null) {
            if (mockConfig.getServiceTime() > Constants.MAX_SERVICE_TIME) {
                throw new MockException("服务耗时最大设置" + Constants.MAX_SERVICE_TIME + "秒");
            } else if (mockConfig.getServiceTime() < BigDecimal.ZERO.intValue()) {
                mockConfig.setServiceTime(BigDecimal.ZERO.intValue());
            }
        } else {
            mockConfig.setServiceTime(BigDecimal.ZERO.intValue());
        }

        if (!StringUtils.isEmpty(mockConfig.getResResult())) {
            mockConfig.setResResult(mockConfig.getResResult().replaceAll("\n", ""));
        }

        //校验返回结果格式
        validContentType(mockConfig.getResContentType(), mockConfig.getResResult());

        MockConfig queryResult = mockConfigRepository.findByServiceUri(mockConfig.getServiceUri());
        if (StringUtils.isEmpty(mockConfig.getId()) && queryResult != null) {
            throw new MockException("服务URI已存在");
        }
        if (!StringUtils.isEmpty(mockConfig.getId()) && queryResult != null && !queryResult.getId().equals(mockConfig.getId())) {
            throw new MockException("服务URI已存在");
        }

        if (!StringUtils.isEmpty(mockConfig.getWsdlContent())) {
            mockConfig.setWsdlContent(mockConfig.getWsdlContent().replaceAll("\n", ""));
        }
        try {
            String wsdlContent = mockConfig.getWsdlContent();
            validContentType(ContentType.XML.name(), wsdlContent);
        } catch (Exception e) {
            throw new MockException("WSDL内容格式有误");
        }
    }

    /**
     * 校验内容格式
     *
     * @param contentType
     */
    private void validContentType(String contentType, String content) {
        if (StringUtils.isEmpty(contentType)) {
            throw new MockException("返回类型不能为空");
        }
        ContentType type = ContentType.valueOf(contentType);
        if (type == null) {
            throw new MockException("返回类型有误");
        }
        if (StringUtils.isEmpty(content)) {
            return;
        }
        try {
            switch (type) {
                case JSON:
                    JsonUtil.jsonToObj(content, Object.class);
                    return;
                case XML:
                    XmlUtil.loadXml(content);
                    return;
                default:
                    return;
            }
        } catch (Exception e) {
            log.error("校验内容格式异常", e);
            throw new MockException("返回结果不符合" + contentType + "格式");
        }
    }

    /**
     * 获取下一个服务编号
     *
     * @param serviceType
     * @return
     */
    private int getNextServiceNumber(ServiceType serviceType) {
        //查询服务编号最大的一条数据
        MockConfig config = mockConfigRepository.findFirstByServiceTypeOrderByServiceNumberDesc(serviceType.name());
        int lastServiceNum = 0;
        if (config != null) {
            lastServiceNum = config.getServiceNumber();
        }
        int currMaxNumber;
        if (ServiceType.WebService.equals(serviceType)) {
            currMaxNumber = service_number_webservice;
        } else if (ServiceType.Hessian.equals(serviceType)) {
            currMaxNumber = service_number_hessian;
        } else {
            throw new MockException("服务类型有误");
        }
        if (lastServiceNum < currMaxNumber) {
            lastServiceNum = currMaxNumber;
        }
        int serviceNumber = lastServiceNum + 1;
        //设置全局最新服务编号，防止删掉再创建出现相同的类
        if (ServiceType.WebService.equals(serviceType)) {
            service_number_webservice = serviceNumber;
        } else if (ServiceType.Hessian.equals(serviceType)) {
            service_number_hessian = serviceNumber;
        }
        return serviceNumber;
    }

    private void preSave(MockConfig mockConfig) {
        if (mockConfig.getId() == null) {
            mockConfig.setId(CommonUtil.getNextId());
        }
        Date now = new Date();
        mockConfig.setCreateTime(now);
        mockConfig.setUpdateTime(now);
    }
}
