package com.choice.orderupload.activemq;

import com.choice.orderupload.service.OrderReUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * @author 林金成
 * @date 2018/7/12 17:40
 */
@Service
public class MqListener {
    @Autowired
    private OrderReUploadService orderReUploadService;

    @JmsListener(destination = "testqueue")
    public void onMessage(String msg) {
        orderReUploadService.uploadOrderFileFromFileDir("8051058", "2018-07-11");
    }
}