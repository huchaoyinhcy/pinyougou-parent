package com.itheima.sms.smsdemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Demo {
    @RequestMapping("/demo")
    public String demo() {
        return "hello world";
    }
}
