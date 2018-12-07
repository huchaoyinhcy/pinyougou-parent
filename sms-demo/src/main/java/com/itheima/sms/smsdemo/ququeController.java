package com.itheima.sms.smsdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ququeController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/sendMap")
    public void sendSms() {
        Map map = new HashMap<>();
        map.put("mobile", "15025528605");
        map.put("template_code", "SMS_151579553");
        map.put("sign_name", "品优购");
        map.put("param", "{\"code\":\"102931\"}");
        jmsMessagingTemplate.convertAndSend("sms", map);
    }
}
