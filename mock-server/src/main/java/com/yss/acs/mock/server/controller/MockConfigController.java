package com.yss.acs.mock.server.controller;

import com.yss.acs.mock.server.model.base.PageResult;
import com.yss.acs.mock.server.model.entity.MockConfig;
import com.yss.acs.mock.server.model.vo.request.MockConfigQueryReqVO;
import com.yss.acs.mock.server.model.vo.request.WebServiceSaveReqVO;
import com.yss.acs.mock.server.service.IMessageService;
import com.yss.acs.mock.server.service.IMockConfigService;
import com.yss.acs.mock.server.model.base.Result;
import com.yss.acs.mock.server.model.vo.request.HessianSaveReqVO;
import com.yss.acs.mock.server.model.vo.request.MessageSendReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Mock配置Controller
 *
 * @author jiayy
 * @date 2020/6/28
 */
@Controller
@RequestMapping("/acsmock/config")
public class MockConfigController {

    @Autowired
    private IMockConfigService mockConfigService;

    @Autowired
    private IMessageService messageService;

    @GetMapping("/toList")
    public String mockList() {
        return "mock/list";
    }

    @GetMapping("/toAdd")
    public String mockAdd() {
        return "mock/add";
    }

    @GetMapping("/toEdit/{id}")
    public String mockEdit(@PathVariable("id") String id, ModelMap mmap) {
        mmap.put("mockConfig", mockConfigService.findById(id));
        return "mock/edit";
    }

    @GetMapping("/toWsAdd")
    public String mockWsAdd() {
        return "mock/addws";
    }

    @GetMapping("/toHsAdd")
    public String pageHessian() {
        return "mock/addhs";
    }

    @GetMapping("/toMqAdd")
    public String toMqAdd() {
        return "mock/addmq";
    }

    /**
     * 查询Mock配置列表
     *
     * @param param
     * @return
     */
    @RequestMapping("/listPage")
    @ResponseBody
    public PageResult<MockConfig> listPage(MockConfigQueryReqVO param) {
        Page<MockConfig> page = mockConfigService.listPage(param);
        return PageResult.success(page.getTotalElements(), page.getContent());
    }

    /**
     * 保存Mock配置
     *
     * @param mockConfig
     */
    @RequestMapping("/save")
    @ResponseBody
    public Result save(@Valid MockConfig mockConfig) {
        mockConfigService.save(mockConfig);
        return Result.success();
    }

    /**
     * 删除Mock配置
     *
     * @param id
     */
    @RequestMapping("/delete/{id}")
    @ResponseBody
    public Result delete(@PathVariable("id") String id) {
        mockConfigService.deleteById(id);
        return Result.success();
    }

    /**
     * 新增WebService服务
     *
     * @param param
     */
    @RequestMapping("/createWebService")
    @ResponseBody
    public Result createWebService(@Valid WebServiceSaveReqVO param) {
        mockConfigService.createWebService(param);
        return Result.success();
    }

    /**
     * 新增Hessian服务
     *
     * @param param
     */
    @RequestMapping("/createHessian")
    @ResponseBody
    public Result createHessian(@Valid HessianSaveReqVO param) {
        mockConfigService.createHessian(param);
        return Result.success();
    }

    /**
     * 发送MQ消息
     *
     * @param param
     * @return
     */
    @RequestMapping("/sendMq")
    @ResponseBody
    public Result sendMq(@Valid MessageSendReqVO param) {
        messageService.sendMq(param);
        return Result.success();
    }

}
