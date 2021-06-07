package com.yss.acs.mock.server.service;

import com.yss.acs.mock.server.model.vo.request.MessageSendReqVO;

/**
 * 发送消息服务
 *
 * @author jiayy
 * @date 2020/7/4
 */
public interface IMessageService {

    /**
     * 发送消息
     *
     * @param param
     */
    void sendMq(MessageSendReqVO param);
}
