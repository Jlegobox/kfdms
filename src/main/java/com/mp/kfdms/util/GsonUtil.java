package com.mp.kfdms.util;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/16
 * @Time 22:06
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * json工具类，基于gson
 */
public class GsonUtil {
    public static volatile Gson instance;

    public static Gson instance(){
        if(instance == null){
            synchronized (GsonUtil.class){
                // 开启
                instance = new GsonBuilder().enableComplexMapKeySerialization().create();
            }
        }
        return instance;
    }

    /**
     * 从前端的表单到entity的映射，解决少字段or多字段问题
     * @param json
     * @return
     */
    public static Object fromJson(String json){
        // TODO: 2021/1/21 未完成
        // 判断是否符合json格式
        // 转换未Map
        // 利用反射拿到属性名和属性类别
        // 生成注入，没有的属性设置为默认值
        return "success";
    }
}
