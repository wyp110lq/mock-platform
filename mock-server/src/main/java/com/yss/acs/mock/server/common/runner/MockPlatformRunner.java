package com.yss.acs.mock.server.common.runner;

import com.yss.acs.mock.server.common.enums.ServiceType;
import com.yss.acs.mock.server.common.classloader.MockClassLoader;
import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.model.entity.MockConfig;
import com.yss.acs.mock.server.service.IHessianService;
import com.yss.acs.mock.server.service.IMockConfigService;
import com.yss.acs.mock.server.service.IWebService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;

/**
 * 服务器启动监听
 *
 * @author jiayy
 * @date 2020/7/4
 */
@Component
@Slf4j
public class MockPlatformRunner implements ApplicationRunner {

    @Autowired
    private IMockConfigService mockConfigService;

    @Autowired
    private IWebService webService;

    @Autowired
    private IHessianService hessianService;

    @Autowired
    private MockClassLoader mockClassLoader;

    @Value("${mock.lib.path}")
    private String libPath;


    @Override
    public void run(ApplicationArguments args) {

        log.info("******系统启动发布服务开始******");

        //加载外部jar，webservice服务mock结果用到
        loadOuterJar();
        //发布WebService
        publishWebService();
        //发布hessian服务
        publishHessianService();

        log.info("******系统启动发布服务完成******");
    }

    /**
     * 加载外部jar
     */
    private void loadOuterJar() {
        if (StringUtils.isEmpty(libPath)) {
            throw new MockException("未配置依赖的jar包路径");
        }
        File file = new File(libPath);
        if (!file.exists()) {
            throw new MockException("依赖jar包路径配置有误");
        }
        if (file.listFiles().length == 0) {
            throw new MockException("依赖jar包配置路径下未找到文件");
        }
        try {
            for (File jarFile : file.listFiles()) {
                if (!jarFile.isFile() || !jarFile.getName().toLowerCase().endsWith(".jar")) {
                    continue;
                }
                mockClassLoader.loadJar(jarFile);
            }
        } catch (Exception e) {
            log.error("加载外部jar异常", e);
        }
    }

    /**
     * 发布WebService
     */
    private void publishWebService() {
        log.info("系统启动发布WebService服务开始...");
        List<MockConfig> configList = mockConfigService.findByServiceType(ServiceType.WebService);
        if (!CollectionUtils.isEmpty(configList)) {
            for (MockConfig config : configList) {
                if (StringUtils.isEmpty(config.getServiceUri()) || StringUtils.isEmpty(config.getClassName())) {
                    continue;
                }
                String serviceUri = config.getServiceUri();
                String className = config.getClassName();
                try {
                    webService.publish(config.getServiceNumber(), serviceUri, className, false, config.getResResult());
                } catch (Exception e) {
                    log.error("系统启动发布WebService失败, uri:{}, 类名:{}", serviceUri, className);
                }
            }
        } else {
            log.info("没有可以发布的WebService服务...");
        }
        log.info("系统启动发布WebService服务完成...");
    }

    /**
     * 发布hessian服务
     */
    private void publishHessianService() {
        log.info("系统启动发布Hessian服务开始...");
        List<MockConfig> configList = mockConfigService.findByServiceType(ServiceType.Hessian);
        if (!CollectionUtils.isEmpty(configList)) {
            for (MockConfig config : configList) {
                if (StringUtils.isEmpty(config.getServiceUri()) || StringUtils.isEmpty(config.getClassName()) || StringUtils.isEmpty(config.getInterfaceCode())) {
                    continue;
                }
                String serviceUri = config.getServiceUri();
                String className = config.getClassName();
                try {
                    hessianService.publish(config.getServiceNumber(), serviceUri, config.getInterfaceCode(), false);
                } catch (Exception e) {
                    log.error("Hessian服务发布失败, uri:{}, className:{}", serviceUri, className, e);
                }
            }
        } else {
            log.info("没有可以发布的Hessian服务...");
        }
        log.info("系统启动发布Hessian服务完成...");
    }
}
