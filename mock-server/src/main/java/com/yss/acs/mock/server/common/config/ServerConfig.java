package com.yss.acs.mock.server.common.config;

import lombok.extern.slf4j.Slf4j;
import com.yss.acs.mock.server.common.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 服务器配置类
 *
 * @author jiayy
 * @date 2020/7/3
 */
@Component
@Slf4j
public class ServerConfig {

    @Value("${server.port}")
    private int serverPort;

    public String getUrl() {
        return "http://" + Constants.LOCALHOST_IP + ":" + serverPort;
    }
}
