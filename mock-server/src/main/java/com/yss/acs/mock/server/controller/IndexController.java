package com.yss.acs.mock.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页Controller
 *
 * @author jiayy
 * @date 2020/6/29
 */
@Controller
@Slf4j
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/acsmock")
    public String acsmock() {
        return "index";
    }

}
