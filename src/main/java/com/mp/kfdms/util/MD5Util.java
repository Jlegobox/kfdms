package com.mp.kfdms.util;

import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/18
 * @Time 13:59
 */
public class MD5Util {
    public static String calMD5(File file) throws IOException {
        if (file.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                String md5 = DigestUtils.md5DigestAsHex(inputStream);
                return md5;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
        }
        return null;
    }
}
