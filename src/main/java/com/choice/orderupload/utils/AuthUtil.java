package com.choice.orderupload.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.choice.orderupload.config.ApplicationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * 加密（对key值排序）
 */
@Component
@SuppressWarnings("all")
public class AuthUtil {
    private static final Logger PLOG = LoggerFactory.getLogger(AuthUtil.class);

    @Autowired
    private ApplicationParameter applicationParameter;

    public Map<String, Object> postByAccessToken(String post_url, Map<String, Object> map, String accessToken) {
        Map<String, Object> m = rebuildAllParams(post_url, map, accessToken);
        JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(m, SerializerFeature.DisableCircularReferenceDetect));
        Map<String, Object> itemMap = JSONObject.toJavaObject(itemJSONObj, Map.class);
        return itemMap;
    }

    public Map<String, Object> rebuildAllParams(String requestUrl, Map<String, Object> params, String accessToken) {
        Long timestamp = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString().replace("-", "");
        params.put("appid", applicationParameter.getOauthAppId());
        params.put("timestamp", timestamp.toString());
        params.put("nonce", nonce);
        params.put("token", accessToken);
        String sign = CommonSignComputer.createSign(requestUrl, params, applicationParameter.getOauthSecret());
        System.out.println("sign:" + sign);
        params.put("sign", sign);
        return params;
    }
}