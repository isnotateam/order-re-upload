package com.choice.orderupload.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 林金成
 * @date 2018/8/20 12:54
 */
public class Common {
    public static String appendInterfaceUrl(String before, String after) {
        if (before.endsWith("/")) {
            return after.startsWith("/") ? before.concat(after.replaceFirst("/", "")) : before.concat(after);
        }
        return after.startsWith("/") ? before.concat(after) : before.concat("/").concat(after);
    }

    /**
     * 按逗号截取，返回list
     *
     * @param value
     * @return
     */
    public static List<String> split(String value, String regex) {
        List<String> result = new ArrayList<>();
        if (null != value && !"".equals(value)) {
            if (value.contains(regex)) {
                result.addAll(Arrays.asList(value.split(regex)));
            } else {
                result.add(value);
            }
        }
        return result;
    }
}