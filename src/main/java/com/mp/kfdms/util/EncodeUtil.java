package com.mp.kfdms.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/21
 * @Time 12:40
 */
public class EncodeUtil {
    private EncodeUtil(){

    }

    /**
     * <h2>文件名转码</h2>
     * <p>
     * 将文件名转码为UTF-8并确保特殊字符能够正确显示，建议在提供给用户文件之前对文件名进行本转码操作。
     * </p>
     * @param name
     * @return
     */
    public static String getFileNameByUTF8(String name) {
        try {
            return URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return name;
        }
    }
}
