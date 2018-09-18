package com.choice.orderupload.utils;

import com.alibaba.fastjson.JSONObject;
import com.choice.orderupload.common.BasicConstant;
import com.choice.orderupload.config.ApplicationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 林金成
 * @date 2018/9/17 13:48
 */
@Component
public class HttpUtil {
    private static final Logger PLOG = LoggerFactory.getLogger(HttpUtil.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ApplicationParameter applicationParameter;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 通过网关调用
     */
    public JSONObject postAcrossGateway(String url, Map<String, Object> params) {
        return this.post(url, authUtil.postByAccessToken(url, params, this.getAccessToken()));
    }

    public JSONObject post(String url, Map<String, Object> params) {
        return this.post(url, params, MediaType.APPLICATION_FORM_URLENCODED);
    }

    public JSONObject post(String url, Map<String, Object> params, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.setAll(params);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = null;
        JSONObject jsonObject = new JSONObject();
        try {
            response = restTemplate.postForEntity(url, request, Object.class);
            jsonObject.put("success", true);
            jsonObject.put("data", response.getBody());
            jsonObject.put("code", "200");
            jsonObject.put("message", "请求成功");
        } catch (RestClientException e) {
            jsonObject.put("success", false);
            jsonObject.put("code", "500");
            jsonObject.put("message",
                    "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
        }
        return jsonObject;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public JSONObject get(String url) {
        JSONObject result = new JSONObject();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.getForEntity(url, String.class);
            if (response != null) {
                result.put("success", true);
                result.put("data", response.getBody());
                result.put("code", 200);
                result.put("msg", "请求成功");
            }
        } catch (RestClientException e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("msg", "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取AccessToken
     */
    private String getAccessToken() {
        if (redisUtil.exists(BasicConstant.ACCESS_TOKEN_CACHE_KEY)) {
            return redisUtil.get(BasicConstant.ACCESS_TOKEN_CACHE_KEY);
        } else {
            String accessToken = getAccessTokenByHttp();
            if (accessToken != null && !"".equals(accessToken)) {
                redisUtil.set(BasicConstant.ACCESS_TOKEN_CACHE_KEY, accessToken, 600);
            }
            return accessToken;
        }
    }

    /**
     * 调接口获取AccessToken
     */
    private String getAccessTokenByHttp() {
        Map<String, Object> map = new HashMap<>();
        map.put("appid", applicationParameter.getOauthAppId());
        map.put("secret", applicationParameter.getOauthSecret());
        String accessToken = null;
        JSONObject response = null;
        try {
            response = this.post(applicationParameter.getChoiceGateway() + "oauth2-service/oauth2/getToken", map);
            if (response != null && !"".equals(response) && "200".equalsIgnoreCase(response.getString("code"))) {
                accessToken = response.getJSONObject("data").getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            PLOG.error(MessageFormat.format(
                    "remote invoke {0} token fail, errMsg = {1}",
                    applicationParameter.getChoiceGateway() + "oauth2-service/oauth2/getToken", e.getMessage()));
            return null;
        }
        if (response == null || response.getJSONObject("data") == null) {
            PLOG.error(MessageFormat.format(
                    "code change token failed redirectUri = {0}, appid = {1}, secret = {2}, {3}",
                    applicationParameter.getChoiceGateway() + "oauth2-service/oauth2/getToken",
                    applicationParameter.getOauthAppId(),
                    applicationParameter.getOauthSecret(), response.getString("message")));
            return null;
        }
        if ("200".equalsIgnoreCase(response.getString("code"))) {
            return (String) response.getJSONObject("data").get("access_token");
        }
        PLOG.error(MessageFormat.format(
                "code change token failed redirectUri = {0}, appid = {1}, secret = {2}, {3}",
                applicationParameter.getChoiceGateway() + "oauth2-service/oauth2/getToken",
                applicationParameter.getOauthAppId(),
                applicationParameter.getOauthSecret(), "参数错误，请确认appid和secret配置"));
        return accessToken;
    }
}