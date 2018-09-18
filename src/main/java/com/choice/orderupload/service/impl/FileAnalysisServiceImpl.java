package com.choice.orderupload.service.impl;

import com.choice.orderupload.config.ApplicationParameter;
import com.choice.orderupload.service.FileAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析activemq.log文件中的消息内容
 *
 * @author 林金成
 * @date 2018/9/14 13:49
 */
@Service
public class FileAnalysisServiceImpl implements FileAnalysisService {
    @Autowired
    private ApplicationParameter applicationParameter;

    /**
     * 根据日期和配置的日志文件目录，读取activemq日志文件并解析
     */
    @Override
    public List<String> analysis(String date) throws IOException {
        File logDir = new File(applicationParameter.getLogDir());
        List<String> result = new ArrayList<>();
        if (logDir.exists()) {
            File[] files = logDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name != null && name.equalsIgnoreCase(date);
                }
            });
            if (files != null && files.length > 0) {
                File targetDir = files[0];
                files = targetDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith("activemq") && name.contains(date.replaceAll("-", "")) && name.endsWith("log");
                    }
                });
                if (files != null && files.length > 0) {
                    File target = files[0];
                    FileInputStream fis = new FileInputStream(target);
                    InputStreamReader reader = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while (!StringUtils.isEmpty((line = bufferedReader.readLine()))) {
                        stringBuffer.append(line);
                    }
                    String messageStr = stringBuffer.toString();
                    result.add(messageStr);
                }
            }
        }
        return result;
    }
}