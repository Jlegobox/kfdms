package com.mp.kfdms.util;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/16
 * @Time 22:06
 */

import com.google.gson.Gson;

/**
 * json工具类，基于gson
 */
public class GsonUtil {
    public static volatile Gson instance;

    public static Gson instance(){
        if(instance == null){
            synchronized (GsonUtil.class){
                instance = new Gson();
            }
        }
        return instance;
    }
}
