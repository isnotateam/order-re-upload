package com.choice.orderupload.service;

import java.io.IOException;
import java.util.List;

/**
 * 解析activemq.log文件中的消息内容，补发到Uploaddata队列
 *
 * @author 林金成
 * @date 2018/9/14 13:49
 */
public interface FileAnalysisService {
    List<String> analysis(String date) throws IOException;
}