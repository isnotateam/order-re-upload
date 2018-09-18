package com.choice.orderupload.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.choice.orderupload.baton.BatonController;
import com.choice.orderupload.common.Common;
import com.choice.orderupload.config.ApplicationParameter;
import com.choice.orderupload.service.OrderReUploadService;
import com.choice.orderupload.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单消息补发
 *
 * @author 林金成
 * @date 2018/9/14 13:57
 */
@Service
@SuppressWarnings("all")
public class OrderReUploadServiceImpl implements OrderReUploadService {
    @Autowired
    private ApplicationParameter applicationParameter;
    @Autowired
    private BatonController batonController;
    @Autowired
    private HttpUtil httpUtil;

    private static final String CLOUD_ORDER_NO_QUERY_INTERFACE = "/basemember/basic/findMongoFolios";

    /**
     * 从组装服务处理后的文件上传
     */
    @Override
    public int uploadOrderFileFromFileDir(String vscode, String date) {
        Map<String, Object> orderFiles = this.getOrderFilesFromFileDir(vscode, date);
        Map<String, String> orderPaths = (Map<String, String>) orderFiles.get("orderPaths");
        List<String> orderNos = (List<String>) orderFiles.get("orderNos");
        String tenantId = (String) orderFiles.get("tenantId");
        List<String> orderNoFromCloud = this.getOrderNoFromCloud(tenantId, vscode, date);
        if (orderNoFromCloud != null && orderNoFromCloud.size() > 0) {
            for (String orderNo : orderNoFromCloud) {
                if (orderNos.contains(orderNo)) {
                    orderNos.remove(orderNo);
                }
            }
        }
        // orderNos中剩余的单号是云平台没有的单号，全部补发至云平台
        if (orderNos != null && orderNos.size() > 0) {
            for (String orderNo : orderNos) {
                if (orderPaths.containsKey(orderNo)) {
                    batonController.sendFile(tenantId, vscode, date, orderPaths.get(orderNo));
                }
            }
        }
        return orderNos.size();
    }

    /**
     * 调云平台接口获取订单号
     * [vbcode:vorclass]
     */
    private List<String> getOrderNoFromCloud(String tenantId, String vscode, String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("vgroupcode", tenantId);
        params.put("vscode", vscode);
        params.put("dworkdate", date);
        JSONObject response = httpUtil.postAcrossGateway(Common.appendInterfaceUrl(applicationParameter.getChoiceGateway(), CLOUD_ORDER_NO_QUERY_INTERFACE), params);
        if (response != null && response.containsKey("code") && "200".equalsIgnoreCase(response.getString("code"))) {
            JSONObject responseData = response.getJSONObject("data");
            if (responseData != null && responseData.containsKey("data")) {
                JSONObject data = responseData.getJSONObject("data");
                if (data != null && data.containsKey("vbcodes")) {
                    String vbcodes = data.getString("vbcodes");
                    return Common.split(vbcodes, ",");
                }
            }
        }
        return null;
    }

    /**
     * 读取组装服务写到硬盘的文件
     */
    private Map<String, Object> getOrderFilesFromFileDir(String vscode, String date) {
        File fileDir = new File(applicationParameter.getFileDir());
        Map<String, Object> result = new HashMap<>();
        // 订单JSON文件路径集合
        Map<String, String> orderPaths = new HashMap<>();
        // 订单号集合
        List<String> orderNos = new ArrayList<>();
        // 企业号
        String tenantId = null;
        if (fileDir.exists()) {
            File[] files = fileDir.listFiles();
            if (files != null && files.length > 0) {
                File tenantDir = files[0];// 企业号目录
                tenantId = tenantDir.getName();
                if (tenantDir.exists()) {
                    File[] vscodeFileList = tenantDir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name != null && name.equalsIgnoreCase(vscode);
                        }
                    });
                    if (vscodeFileList != null && vscodeFileList.length > 0) {
                        File vscodeDir = vscodeFileList[0];// 门店编码目录
                        if (vscodeDir.exists()) {
                            File[] dateFileList = vscodeDir.listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name != null && name.equalsIgnoreCase(date);
                                }
                            });
                            if (dateFileList != null && dateFileList.length > 0) {
                                File dateDir = dateFileList[0];// 日期目录
                                if (dateDir.exists()) {
                                    for (File vorclassDir : dateDir.listFiles()) {// 遍历vorclass目录
                                        if (vorclassDir.exists()) {
                                            String vorclass = vorclassDir.getName();
                                            for (File order : vorclassDir.listFiles()) {
                                                String key = String.format("%s:%s", order.getName(), vorclass);
                                                orderNos.add(key);
                                                orderPaths.put(key, order.getAbsolutePath());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        result.put("orderNos", orderNos);
        result.put("orderPaths", orderPaths);
        result.put("tenantId", tenantId);
        return result;
    }

    /**
     * 从mq日志中解析上传
     */
    @Override
    public int uploadOrderFileFromMqLog(String vscode, String date) {
        return 0;
    }
}