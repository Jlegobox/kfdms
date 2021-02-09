package com.mp.kfdms.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/21
 * @Time 12:58
 */

/**
 * 处理requset提取属性的操作
 */
public class RequestUtil {
    /**
     * 提取request的parameter，抓换为int。不存在则返回0
     * @param request
     * @param parameterName
     * @return
     */
    public static int getInt(final HttpServletRequest request,final String parameterName){
        String parameter = request.getParameter(parameterName);
        return parameter==null?0:Integer.parseInt(parameter);
    }

    public static String getStr(final HttpServletRequest request,final String parameterName){
        String parameter = request.getParameter(parameterName);
        return parameter==null?"":parameter;
    }
}
