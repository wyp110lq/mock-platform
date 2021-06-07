package com.yss.acs.mock.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Mock配置
 *
 * @author jiayy
 * @date 2020/6/28
 */
@Data
@Entity
@Table(name = "T_MOCK_CONFIG")
public class MockConfig {

    @Id
    @Column(
            name = "ID",
            unique = true,
            nullable = false
    )
    private String id;

    /**
     * 服务名称
     */
    @NotEmpty(message = "服务名称不能为空")
    @Column(name = "SERVICE_NAME")
    private String serviceName;

    /**
     * 服务类型
     */
    @NotEmpty(message = "服务类型不能为空")
    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    /**
     * 服务uri
     */
    @NotEmpty(message = "服务URI不能为空")
    @Column(name = "SERVICE_URI")
    private String serviceUri;

    /**
     * 服务状态
     */
    @Column(name = "SERVICE_STATUS")
    private Integer serviceStatus;

    /**
     * 请求方式GET/POST/PUT/DELETE
     */
    @Column(name = "REQ_METHOD")
    private String reqMethod;

    /**
     * 请求类型
     */
    @Column(name = "REQ_CONTENT_TYPE")
    private String reqContentType;

    /**
     * 返回类型
     */
    @NotEmpty(message = "返回类型不能为空")
    @Column(name = "RES_CONTENT_TYPE")
    private String resContentType;

    /**
     * 返回状态
     */
    @NotNull(message = "返回状态不能为空")
    @Column(name = "RES_STATUS")
    private Integer resStatus;

    /**
     * 返回结果
     */
    @Column(name = "RES_RESULT")
    private String resResult;

    /**
     * 服务耗时
     */
    @Column(name = "serviceTime")
    private Integer serviceTime;

    /**
     * 服务编号
     */
    @Column(name = "serviceNumber")
    private Integer serviceNumber;

    /**
     * 类名
     */
    @Column(name = "CLASS_NAME")
    private String className;

    /**
     * WSDL内容
     */
    @Column(name = "WSDL_CONTENT")
    private String wsdlContent;

    /**
     * 接口代码
     */
    @Column(name = "INTERFACE_CODE")
    private String interfaceCode;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

}
