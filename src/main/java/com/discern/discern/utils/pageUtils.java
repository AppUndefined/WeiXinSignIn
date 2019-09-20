package com.discern.discern.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class pageUtils {
    /**
     *
     * @param pageNumber 第几页
     * @param pageSize 分页大小
     * @param url 第三方地址
     * @param param 查询条件
     * @param multiple 分页倍数
     * @return
     */
    public static JSONObject adaption(Integer pageNumber, Integer pageSize, String url, HashMap<String,String> param, int multiple) {
        JSONObject jsonObject = new JSONObject();
        JSONObject temp = new JSONObject();
        JSONArray results = new JSONArray();
        for(int i = 0; i < multiple;i++) {
            String page = getPage(i,pageNumber+1,multiple);

                     param.put("page",page);
                      String s = HttpUtils.sendGet(url, param);
                      if(s==null){
                          break;
                      }
                      temp = JSONObject.parseObject(s);
                      results.addAll(temp.getJSONArray("results"));
        }
        jsonObject = temp;
        jsonObject.put("results",results);
        return jsonObject;

    }

    private static String getPage(int index, int pageNumber, Integer multiple) {
        int page = (index + 1) + (pageNumber - 1) * multiple;
        return page+"";
    }
}
