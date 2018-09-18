package com.choice.orderupload.controller;

import com.choice.orderupload.service.FileAnalysisService;
import com.choice.orderupload.service.OrderReUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 林金成
 * @date 2018/9/14 13:52
 */
@RestController
public class FileAnalysisController {
    @Autowired
    private FileAnalysisService fileAnalysisService;
    @Autowired
    private OrderReUploadService orderReUploadService;

    @PostMapping("/orderReUpload")
    public String orderReUpload() {
        return "";
    }
}