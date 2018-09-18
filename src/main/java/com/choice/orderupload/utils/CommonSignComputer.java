package com.choice.orderupload.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @classDesc: 功能描述:
 * @Author: wangmaoshuai
 * @createTime: Created in 下午1:28 18-2-28
 * @version: v1.0
 * @copyright: 北京辰森
 * @email: wms@choicesoft.com.cn
 */
public class CommonSignComputer {
    private static final Logger LOG = LoggerFactory.getLogger(CommonSignComputer.class);

    public static String createSign(String requestURI, Map<String, Object> params, String secret) {
        String result;
        try {
            List<Map.Entry> infoIds = new ArrayList<>(params.entrySet());
            Collections.sort(infoIds,new Comparator<Map.Entry>(){
                @Override
                public int compare(Map.Entry arg0, Map.Entry arg1) {
                    return ((String) arg0.getKey()).compareTo((String) arg1.getKey());
                }
            });

            StringBuilder sb = new StringBuilder();
            for (Map.Entry item : infoIds) {
                if (!"".equals(item.getKey()) && item.getKey()!=null) {
                    if (!item.getKey().equals("sign")) {
                        String key = (String) item.getKey();
                        String val ="";
                        Map mapLi = null;
                        List list = new ArrayList<>();
                        if("data".equals(key)){//数据： 网关系统级参数 +业务参数data
                        	mapLi = (Map) item.getValue();
                        	list.add(mapLi);
                        	 if (JSONUtil.isJsonArrayList(list)) {
                                 val = JSONUtil.sortJsonArray(JSONArray.parseArray(JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect))).toJSONString();
                        	 }
                        	 sb.append(key).append("=").append(val.substring(val.indexOf("[")+1,val.lastIndexOf("]"))).append("&");
                        	 val = "";
                        }else{
                        	val = item.getValue().toString();
                        }
                    	if (!"".equals(val) && val!=null) {
                            if (JSONUtil.isJsonArray(val)) {
                                val = JSON.toJSONString(JSONUtil.sortJsonArray(JSON.parseArray(val)), SerializerFeature.DisableCircularReferenceDetect);
                            } else if (JSONUtil.isJsonObject(val)) {
                                val = JSON.toJSONString(JSONUtil.sortJsonObject(JSON.parseObject(val)), SerializerFeature.DisableCircularReferenceDetect);
                            }
                            sb.append(key).append("=").append(val).append("&");
                        }
                    }
                }
            }
            sb.append("secret=").append(secret);
            result = requestURI+"?"+sb.toString();
            LOG.info("生成的url {}", result);
            result = DigestUtils.md5Hex(result);
            LOG.debug("计算的sign {}", result);
        } catch (Exception e) {
            LOG.error("CommonSignComputer 获取异常 ,errMsg = {}, stack info = {}",e.getMessage(),e);
            e.printStackTrace();
            return null;
        }
        return result;
    }
}