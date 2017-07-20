package com.zlikun.fang.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/7/20 12:07
 */
@RestController
public class DefaultController {

    @GetMapping(value = "/" ,produces = "text/html; charset=UTF-8")
    public String index() {
        return "搜房网(www.fang.com)爬虫" ;
    }

}
