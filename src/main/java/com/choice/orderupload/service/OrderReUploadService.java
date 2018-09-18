package com.choice.orderupload.service;

/**
 * @author 林金成
 * @date 2018/9/14 13:57
 */
public interface OrderReUploadService {
    int uploadOrderFileFromFileDir(String vscode, String date);

    int uploadOrderFileFromMqLog(String vscode, String date);
}