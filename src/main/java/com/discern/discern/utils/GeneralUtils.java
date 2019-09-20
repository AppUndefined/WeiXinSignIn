package com.discern.discern.utils;

import java.util.HashMap;
import java.util.Map;

public class GeneralUtils {

    public static final String OK_STATE = "成功";
    public static final String BAD_STATE = "失败";
    public static final String OK_CODE = "200";
    public static final String BAD_CODE = "500";

    /**
     *
     * @param code 状态码
     * @param msg 消息
     * @param resultData 返回数据
     * @return
     */
    public static Map resultMap(String code,String msg,Object resultData){
        Map map = new HashMap();
        if (code != null && code.trim().length() > 0){
            map.put("code",code);
        }
        if (msg != null && code.trim().length() > 0){
            map.put("msg",msg);
        }
        if (resultData != null){
            map.put("data",resultData);
        }
        return map;
    }
}
