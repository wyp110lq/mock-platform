package com.yss.acs.mock.server.service;

/**
 * WebService服务管理
 *
 * @author jiayy
 * @date 2020/7/3
 */
public interface IWebService {

    /**
     * 校验配置
     */
    void validConfig();

    /**
     * 生成代码
     *
     * @param serviceNumber
     * @param wsdlUrl
     * @return
     */
    String generate(int serviceNumber, String wsdlUrl);

    /**
     * 发布服务
     *
     * @param serviceNumber
     * @param uri
     * @param className
     * @param isNewService
     * @param resResult
     * @return
     */
    String publish(int serviceNumber, String uri, String className, boolean isNewService, String resResult);

    /**
     * 停止服务
     *
     * @param uri
     */
    void stop(String uri);

    /**
     * 删除代码文件
     *
     * @param serviceNumber
     */
    void deleteCode(int serviceNumber);
}
